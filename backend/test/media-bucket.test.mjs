import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { BucketMediaStore } from "../src/mediaStore.ts";

// Owner's «N» (2026-11-18): the images can live in object storage instead of KV, and the API in
// front does not change, so the Apps never learn where the bytes are.

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CONTENT_ID = "50000000-0000-4000-8000-000000000005";
const PNG = new Uint8Array([137, 80, 78, 71, 13, 10, 26, 10]).buffer;
// SHA-256 of those eight bytes.
const SHA = "1d1f4b4b2b02ad2a4d57e4b41ee8d0dcb2b15dcd06d1e3c0b47e46e5b0b4b7f7";

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

test("the bucket stores a file once, by its checksum", async () => {
  const bucket = fakeBucket();
  const store = new BucketMediaStore(bucket);

  assert.equal(await store.put(SHA, "image/png", PNG), true);
  assert.equal(await store.put(SHA, "image/png", PNG), false, "the same file is not written twice");
  assert.equal(bucket.objects.size, 1);
  assert.equal([...bucket.objects.keys()][0], `media/${SHA}`);
});

test("what comes back keeps its kind of image", async () => {
  const store = new BucketMediaStore(fakeBucket());
  await store.put(SHA, "image/webp", PNG);

  const stored = await store.get(SHA);
  assert.equal(stored.mediaType, "image/webp");
  assert.deepEqual(new Uint8Array(stored.bytes), new Uint8Array(PNG));
});

test("something that is not an image kind does not come back as one", async () => {
  const bucket = fakeBucket();
  await bucket.put(`media/${SHA}`, PNG, { httpMetadata: { contentType: "text/html" } });

  const stored = await new BucketMediaStore(bucket).get(SHA);
  assert.equal(stored.mediaType, "application/octet-stream");
});

test("a file that is not there answers nothing", async () => {
  assert.equal(await new BucketMediaStore(fakeBucket()).get(SHA), null);
});

test("the API serves images from the bucket exactly as from KV", async () => {
  const bucket = fakeBucket();
  const store = new BucketMediaStore(bucket);
  await store.put(SHA, "image/png", PNG);

  const handler = createApiHandler({
    auth: { async verify() { return { subject: "descope-user", displayName: "Gustavo", claims: {} }; } },
    campaigns: {
      async resolveUser(subject, displayName) {
        return { id: ACCOUNT_ID, externalSubject: subject, displayName };
      },
    },
    media: store,
    mediaAuthorization: {
      async isActiveDm() { return true; },
      async mayReadMedia() { return true; },
      async listSharedHandouts() { return []; },
    },
  });

  const response = await handler(
    new Request(`https://api.example.test/v1/media/${SHA}?content=${CONTENT_ID}`, {
      headers: { authorization: "Bearer token" },
    }),
  );

  assert.equal(response.status, 200);
  assert.equal(response.headers.get("content-type"), "image/png");
  assert.deepEqual(new Uint8Array(await response.arrayBuffer()), new Uint8Array(PNG));
});
