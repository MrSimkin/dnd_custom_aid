import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import {
  BucketMediaStore,
  FallbackMediaStore,
  MediaStore,
  migrateMediaToBucket,
} from "../src/mediaStore.ts";

// Owner's K (2026-11-20): the images move from Workers KV to object storage without a day on
// which an old image stops being seen, and without a single request that has to move them all.

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CONTENT_ID = "50000000-0000-4000-8000-000000000005";
const PNG = new Uint8Array([137, 80, 78, 71, 13, 10, 26, 10]).buffer;
const SHA = "1d1f4b4b2b02ad2a4d57e4b41ee8d0dcb2b15dcd06d1e3c0b47e46e5b0b4b7f7";
const OTHER_SHA = "2d1f4b4b2b02ad2a4d57e4b41ee8d0dcb2b15dcd06d1e3c0b47e46e5b0b4b7f8";

function fakeBucket() {
  const objects = new Map();
  return {
    objects,
    async head(key) {
      return objects.has(key) ? { key } : null;
    },
    async put(key, value, options) {
      objects.set(key, { value, contentType: options?.httpMetadata?.contentType });
      return { key };
    },
    async get(key) {
      const stored = objects.get(key);
      if (stored == null) return null;
      return {
        async arrayBuffer() { return stored.value; },
        httpMetadata: { contentType: stored.contentType },
      };
    },
  };
}

function fakeKv(entries = []) {
  const values = new Map(entries);
  return {
    values,
    async get(key) {
      return values.get(key)?.value ?? null;
    },
    async getWithMetadata(key) {
      const stored = values.get(key);
      return { value: stored?.value ?? null, metadata: stored?.metadata ?? null };
    },
    async put(key, value, options) {
      values.set(key, { value, metadata: options?.metadata });
    },
    async list({ prefix = "", limit = 1000, cursor } = {}) {
      const names = [...values.keys()].filter((name) => name.startsWith(prefix)).sort();
      const from = cursor == null ? 0 : names.indexOf(cursor);
      const page = names.slice(from, from + limit);
      const next = from + limit;
      return {
        keys: page.map((name) => ({ name })),
        list_complete: next >= names.length,
        cursor: next >= names.length ? undefined : names[next],
      };
    },
  };
}

test("an image that is only in KV is still served, and moves across as it is read", async () => {
  const kv = fakeKv();
  await new MediaStore(kv).put(SHA, "image/png", PNG);
  const bucket = fakeBucket();
  const store = new FallbackMediaStore(new BucketMediaStore(bucket), new MediaStore(kv));

  const stored = await store.get(SHA);

  assert.equal(stored.mediaType, "image/png");
  assert.deepEqual(new Uint8Array(stored.bytes), new Uint8Array(PNG));
  assert.equal(bucket.objects.size, 1, "reading it once put it in the bucket");
  assert.equal(bucket.objects.get(`media/${SHA}`).contentType, "image/png");
});

test("what is new goes to the bucket and never to KV", async () => {
  const kv = fakeKv();
  const bucket = fakeBucket();
  const store = new FallbackMediaStore(new BucketMediaStore(bucket), new MediaStore(kv));

  assert.equal(await store.put(OTHER_SHA, "image/webp", PNG), true);

  assert.equal(bucket.objects.size, 1);
  assert.equal(kv.values.size, 0, "KV stops growing the day the bucket is bound");
});

test("a bucket that refuses the copy still gives the reader the image", async () => {
  const kv = fakeKv();
  await new MediaStore(kv).put(SHA, "image/png", PNG);
  const bucket = fakeBucket();
  bucket.put = async () => { throw new Error("storage is busy"); };
  const store = new FallbackMediaStore(new BucketMediaStore(bucket), new MediaStore(kv));

  const stored = await store.get(SHA);

  assert.deepEqual(new Uint8Array(stored.bytes), new Uint8Array(PNG));
});

test("the move walks KV a page at a time and says where to continue", async () => {
  const kv = fakeKv();
  const shas = [SHA, OTHER_SHA, "3d1f4b4b2b02ad2a4d57e4b41ee8d0dcb2b15dcd06d1e3c0b47e46e5b0b4b7f9"];
  for (const sha of shas) await new MediaStore(kv).put(sha, "image/png", PNG);
  // Something that is not an image's checksum must not stop the pass.
  await kv.put("media/not-a-checksum", PNG, { metadata: { mediaType: "image/png" } });
  const bucket = fakeBucket();

  const first = await migrateMediaToBucket(kv, bucket, { limit: 2 });
  assert.equal(first.examined, 2);
  assert.equal(first.moved, 2);
  assert.equal(first.done, false);
  assert.notEqual(first.cursor, null);

  const second = await migrateMediaToBucket(kv, bucket, { limit: 2, cursor: first.cursor });
  assert.equal(second.done, true);
  assert.equal(second.cursor, null);
  assert.equal(second.moved + second.missing, second.examined);
  assert.equal(bucket.objects.size, 3, "every real image moved, and the odd key was skipped");
  assert.equal(kv.values.size, 4, "nothing is deleted from KV: the old copy stays");

  const again = await migrateMediaToBucket(kv, bucket, { limit: 100 });
  assert.equal(again.moved, 0, "a second pass moves nothing");
  assert.equal(again.alreadyThere, 3);
});

test("only an active DM may ask for the move, and the answer says where it got to", async () => {
  const kv = fakeKv();
  await new MediaStore(kv).put(SHA, "image/png", PNG);
  const bucket = fakeBucket();
  let dm = false;
  const handler = createApiHandler({
    auth: { async verify() { return { subject: "descope-user", displayName: "Gustavo", claims: {} }; } },
    campaigns: {
      async resolveUser(subject, displayName) {
        return { id: ACCOUNT_ID, externalSubject: subject, displayName };
      },
    },
    media: new FallbackMediaStore(new BucketMediaStore(bucket), new MediaStore(kv)),
    mediaAuthorization: {
      async isActiveDm() { return dm; },
      async mayReadMedia() { return true; },
      async listSharedHandouts() { return []; },
    },
    mediaMigration: (options) => migrateMediaToBucket(kv, bucket, options),
  });
  const ask = (query = "") =>
    handler(new Request(`https://api.example.test/v1/media:migrate${query}`, {
      method: "POST",
      headers: { authorization: "Bearer token" },
    }));

  assert.equal((await ask()).status, 403);

  dm = true;
  const response = await ask("?limit=10");
  assert.equal(response.status, 200);
  const report = await response.json();
  assert.equal(report.moved, 1);
  assert.equal(report.done, true);
  assert.equal(report.cursor, null);

  assert.equal((await ask("?limit=0")).status, 400);
  assert.equal((await ask("?limit=500")).status, 400);

  // The image is served afterwards, from the bucket.
  const image = await handler(
    new Request(`https://api.example.test/v1/media/${SHA}?content=${CONTENT_ID}`, {
      headers: { authorization: "Bearer token" },
    }),
  );
  assert.equal(image.status, 200);
  assert.equal(image.headers.get("content-type"), "image/png");
});

test("without a bucket there is nothing to move", async () => {
  const handler = createApiHandler({
    auth: { async verify() { return { subject: "descope-user", displayName: "Gustavo", claims: {} }; } },
    campaigns: {
      async resolveUser(subject, displayName) {
        return { id: ACCOUNT_ID, externalSubject: subject, displayName };
      },
    },
    media: new MediaStore(fakeKv()),
    mediaAuthorization: {
      async isActiveDm() { return true; },
      async mayReadMedia() { return true; },
      async listSharedHandouts() { return []; },
    },
  });

  const response = await handler(
    new Request("https://api.example.test/v1/media:migrate", {
      method: "POST",
      headers: { authorization: "Bearer token" },
    }),
  );

  assert.equal(response.status, 503);
});
