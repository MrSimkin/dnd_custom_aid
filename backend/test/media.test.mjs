import test from "node:test";
import assert from "node:assert/strict";
import { createHash } from "node:crypto";
import { createApiHandler } from "../src/app.ts";
import { MediaStore } from "../src/mediaStore.ts";

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CONTENT_ID = "50000000-0000-4000-8000-000000000005";
const IMAGE = new Uint8Array([137, 80, 78, 71, 1, 2, 3, 4, 5]);
const SHA = createHash("sha256").update(IMAGE).digest("hex");

function fakeKv() {
  const entries = new Map();
  const writes = [];
  return {
    entries,
    writes,
    async get(key) {
      return entries.get(key)?.value ?? null;
    },
    async getWithMetadata(key) {
      const entry = entries.get(key);
      return { value: entry?.value ?? null, metadata: entry?.metadata ?? null };
    },
    async put(key, value, options) {
      writes.push(key);
      entries.set(key, { value, metadata: options?.metadata ?? null });
    },
  };
}

function fixture({ dm = true, readable = true, withMedia = true, member = true } = {}) {
  const kv = fakeKv();
  const calls = [];
  const handler = createApiHandler({
    auth: { async verify() { return { subject: "s", displayName: "DM", claims: {} }; } },
    campaigns: { async resolveUser() { return { id: ACCOUNT_ID, externalSubject: "s", displayName: "DM" }; } },
    media: withMedia ? new MediaStore(kv) : undefined,
    mediaAuthorization: withMedia
      ? {
          async isActiveDm(actor) {
            calls.push(["isActiveDm", actor]);
            return dm;
          },
          async mayReadMedia(actor, contentId, sha) {
            calls.push(["mayReadMedia", actor, contentId, sha]);
            return readable;
          },
          async listSharedHandouts(actor, campaignId) {
            calls.push(["listSharedHandouts", actor, campaignId]);
            return member
              ? [{ id: CONTENT_ID, name: "Cartel de la Sierpe", caption: "Baños", kind: "HANDOUT", sha256: SHA, mediaType: "image/png", revision: 2 }]
              : null;
          },
        }
      : undefined,
  });
  return { kv, calls, handler };
}

function put(body = IMAGE, type = "image/png", sha = SHA) {
  return new Request(`https://api.example.test/v1/media/${sha}`, {
    method: "PUT",
    headers: { authorization: "Bearer t", "content-type": type },
    body,
  });
}

function get(content = CONTENT_ID, sha = SHA) {
  return new Request(`https://api.example.test/v1/media/${sha}?content=${content}`, {
    headers: { authorization: "Bearer t" },
  });
}

test("an active DM uploads an image once; a second upload writes nothing", async () => {
  const { kv, handler } = fixture();
  const first = await handler(put());
  assert.equal(first.status, 201);
  assert.deepEqual(await first.json(), { sha256: SHA, stored: true });
  const second = await handler(put());
  assert.equal(second.status, 200);
  assert.equal((await second.json()).stored, false);
  assert.deepEqual(kv.writes, [`media/${SHA}`], "KV free-plan writes are spent once per image");
});

test("uploads are refused for non-DMs, wrong types, wrong checksums and oversize files", async () => {
  assert.equal((await fixture({ dm: false }).handler(put())).status, 403);
  const { handler, kv } = fixture();
  assert.equal((await handler(put(IMAGE, "application/pdf"))).status, 400);
  assert.equal((await handler(put(new Uint8Array([1, 2, 3])))).status, 400, "checksum mismatch");
  const big = new Uint8Array(5 * 1024 * 1024 + 1);
  const bigSha = createHash("sha256").update(big).digest("hex");
  assert.equal((await handler(put(big, "image/png", bigSha))).status, 400);
  assert.equal(kv.writes.length, 0);
});

test("a readable handout image comes back with its type; others look missing", async () => {
  const { handler, calls } = fixture();
  await handler(put());
  const response = await handler(get());
  assert.equal(response.status, 200);
  assert.equal(response.headers.get("content-type"), "image/png");
  assert.deepEqual(new Uint8Array(await response.arrayBuffer()), IMAGE);
  assert.deepEqual(calls.at(-1), ["mayReadMedia", ACCOUNT_ID, CONTENT_ID, SHA]);

  assert.equal((await fixture({ readable: false }).handler(get())).status, 404);
  const empty = fixture();
  assert.equal((await empty.handler(get())).status, 404, "authorized but never uploaded");
  assert.equal((await empty.handler(get("not-a-uuid"))).status, 400);
});

test("without the KV binding the media routes answer 503 and nothing else breaks", async () => {
  const { handler } = fixture({ withMedia: false });
  const response = await handler(put());
  assert.equal(response.status, 503);
  assert.equal((await response.json()).code, "TRANSIENT_FAILURE");
});


// Redesign A7 (owner observation G2): the players' App lists the handouts the DM sent.
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000901";

test("an active member lists the campaign's shared handouts", async () => {
  const { handler, calls } = fixture();
  const response = await handler(new Request(`https://api.example.test/v1/campaigns/${CAMPAIGN_ID}/handouts`, {
    headers: { authorization: "Bearer t" },
  }));
  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.handouts.length, 1);
  assert.equal(body.handouts[0].name, "Cartel de la Sierpe");
  assert.equal(body.handouts[0].notes, undefined, "DM notes never reach players");
  assert.deepEqual(calls.at(-1), ["listSharedHandouts", ACCOUNT_ID, CAMPAIGN_ID]);
});

test("someone outside the campaign gets not found", async () => {
  const { handler } = fixture({ member: false });
  const response = await handler(new Request(`https://api.example.test/v1/campaigns/${CAMPAIGN_ID}/handouts`, {
    headers: { authorization: "Bearer t" },
  }));
  assert.equal(response.status, 404);
});
