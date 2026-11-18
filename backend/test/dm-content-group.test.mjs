import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { HostedObjectGoneError, StaleRevisionError } from "../src/store.ts";

// Owner's server plan A (2026-11-18): a group of changes travels in one request, so the first
// upload of a whole package stops spending one request per object.

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000002";
const DM_CONTENT_FORMAT = "dnd-custom-aid.dm-content";

function id(n) {
  return `50000000-0000-4000-8000-${String(n).padStart(12, "0")}`;
}

function snapshot(contentId, overrides = {}) {
  return {
    format: DM_CONTENT_FORMAT,
    version: 1,
    content: {
      id: contentId,
      family: "CREATURE",
      displayName: "Goblin",
      scope: { kind: "CAMPAIGN", ref: CAMPAIGN_ID },
      createdAtEpochSeconds: 10,
      updatedAtEpochSeconds: 10,
      ...overrides,
    },
    payload: { size: "Small" },
  };
}

function hostedContent(contentId, revision) {
  return {
    id: contentId,
    scopeKind: "CAMPAIGN",
    scopeRef: CAMPAIGN_ID,
    family: "CREATURE",
    displayName: "Goblin",
    revision,
    deletedAtEpochSeconds: null,
    snapshotFormat: DM_CONTENT_FORMAT,
    snapshotVersion: 1,
    snapshot: snapshot(contentId),
  };
}

function fixture({ putFails = () => null, deleteFails = () => null } = {}) {
  const calls = [];
  const auth = { async verify() { return { subject: "descope-user", displayName: "Gustavo", claims: {} }; } };
  const campaigns = {
    async resolveUser(subject, displayName) {
      return { id: ACCOUNT_ID, externalSubject: subject, displayName };
    },
  };
  const dmContent = {
    async listCampaignContent() { return []; },
    async listPersonalContent() { return []; },
    async putContent(input) {
      calls.push(["putContent", input.contentId]);
      const failure = putFails(input);
      if (failure != null) throw failure;
      return { content: hostedContent(input.contentId, input.expectedRevision + 1), applied: true };
    },
    async deleteContent(input) {
      calls.push(["deleteContent", input.contentId]);
      const failure = deleteFails(input);
      if (failure != null) throw failure;
      return { content: hostedContent(input.contentId, input.expectedRevision + 1), applied: true };
    },
  };
  return { calls, handler: createApiHandler({ auth, campaigns, dmContent }) };
}

function groupRequest(items) {
  return new Request("https://api.example.test/v1/content:batch", {
    method: "POST",
    headers: { authorization: "Bearer token", "content-type": "application/json" },
    body: JSON.stringify({ items }),
  });
}

function putItem(n) {
  return { mutationId: id(900 + n), contentId: id(n), expectedRevision: 0, snapshot: snapshot(id(n)) };
}

test("a group applies every change in one request and answers one result per item", async () => {
  const { calls, handler } = fixture();
  const response = await handler(groupRequest([putItem(1), putItem(2), { mutationId: id(903), contentId: id(3), expectedRevision: 4, delete: true }]));

  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.results.length, 3);
  assert.deepEqual(body.results.map((r) => r.contentId), [id(1), id(2), id(3)]);
  assert.equal(body.results[0].content.revision, 1);
  assert.equal(body.results[2].content.revision, 5);
  assert.deepEqual(calls, [
    ["putContent", id(1)],
    ["putContent", id(2)],
    ["deleteContent", id(3)],
  ]);
});

test("one refused change does not stop the others and comes back with its code", async () => {
  const { calls, handler } = fixture({
    putFails: (input) => (input.contentId === id(2) ? new StaleRevisionError("Revision is stale.", 7) : null),
    deleteFails: () => new HostedObjectGoneError("Already deleted."),
  });
  const response = await handler(groupRequest([
    putItem(1),
    putItem(2),
    { mutationId: id(903), contentId: id(3), expectedRevision: 1, delete: true },
    putItem(4),
  ]));

  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.results[0].content.revision, 1);
  assert.equal(body.results[1].errorCode, "CONFLICT_STALE_REVISION");
  assert.equal(body.results[2].errorCode, "GONE");
  assert.equal(body.results[3].content.revision, 1);
  assert.equal(calls.length, 4, "every item was attempted");
});

test("a group is bounded and must carry something", async () => {
  const { handler } = fixture();
  const empty = await handler(groupRequest([]));
  assert.equal(empty.status, 400);

  const tooMany = await handler(groupRequest(Array.from({ length: 101 }, (_, index) => putItem(index + 1))));
  assert.equal(tooMany.status, 400);
  assert.equal((await tooMany.json()).code, "VALIDATION_FAILED");
});

test("an invalid snapshot inside a group is refused as a validation problem for that item", async () => {
  const { handler } = fixture();
  const response = await handler(groupRequest([
    putItem(1),
    { mutationId: id(902), contentId: id(2), expectedRevision: 0, snapshot: snapshot(id(2), { family: "not a family" }) },
  ]));

  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.results[0].applied, true);
  assert.equal(body.results[1].errorCode, "VALIDATION_FAILED");
});

test("a server failure inside a group fails the whole request, so the App keeps the queue", async () => {
  const { handler } = fixture({ putFails: (input) => (input.contentId === id(2) ? new Error("database is down") : null) });
  const response = await handler(groupRequest([putItem(1), putItem(2)]));

  assert.equal(response.status, 500);
  assert.equal((await response.json()).code, "INTERNAL_ERROR");
});

test("the group route only answers POST", async () => {
  const { handler } = fixture();
  const response = await handler(new Request("https://api.example.test/v1/content:batch", {
    method: "GET",
    headers: { authorization: "Bearer token" },
  }));
  assert.equal(response.status, 405);
});
