import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { DmContentIdentityError, mapDatabaseError } from "../src/dmContentStore.ts";
import {
  HostedAuthorizationError,
  HostedObjectGoneError,
  HostedObjectNotFoundError,
  MutationReuseError,
  StaleRevisionError,
} from "../src/store.ts";

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000002";
const MUTATION_ID = "30000000-0000-4000-8000-000000000003";
const CONTENT_ID = "50000000-0000-4000-8000-000000000005";
const DM_CONTENT_FORMAT = "dnd-custom-aid.dm-content";

function snapshot(overrides = {}) {
  return {
    format: DM_CONTENT_FORMAT,
    version: 1,
    content: {
      id: CONTENT_ID,
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

function hostedContent(overrides = {}) {
  return {
    id: CONTENT_ID,
    scopeKind: "CAMPAIGN",
    scopeRef: CAMPAIGN_ID,
    family: "CREATURE",
    displayName: "Goblin",
    revision: 0,
    deletedAtEpochSeconds: null,
    snapshotFormat: DM_CONTENT_FORMAT,
    snapshotVersion: 1,
    snapshot: snapshot(),
    ...overrides,
  };
}

function fixture() {
  const calls = [];
  const auth = {
    async verify() {
      return { subject: "descope-user", displayName: "Gustavo", claims: {} };
    },
  };
  const campaigns = {
    async resolveUser(subject, displayName) {
      return { id: ACCOUNT_ID, externalSubject: subject, displayName };
    },
  };
  const dmContent = {
    async listCampaignContent(actor, campaignId) {
      calls.push(["listCampaignContent", actor, campaignId]);
      return [hostedContent()];
    },
    async listPersonalContent(actor) {
      calls.push(["listPersonalContent", actor]);
      return [];
    },
    async putContent(input) {
      calls.push(["putContent", input]);
      return { content: hostedContent({ revision: input.expectedRevision }), applied: true };
    },
    async deleteContent(input) {
      calls.push(["deleteContent", input]);
      return {
        content: hostedContent({ revision: input.expectedRevision + 1, deletedAtEpochSeconds: 99 }),
        applied: true,
      };
    },
  };
  return { calls, dmContent, handler: createApiHandler({ auth, campaigns, dmContent }) };
}

function request(path, method = "GET", body) {
  return new Request(`https://api.example.test${path}`, {
    method,
    headers: { authorization: "Bearer token", "content-type": "application/json" },
    body: body == null ? undefined : JSON.stringify(body),
  });
}

test("campaign DM content list is scoped to the authenticated user", async () => {
  const { calls, handler } = fixture();
  const response = await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/content`));
  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.content.length, 1);
  assert.deepEqual(calls.at(-1), ["listCampaignContent", ACCOUNT_ID, CAMPAIGN_ID]);
});

test("personal DM content list uses the authenticated user", async () => {
  const { calls, handler } = fixture();
  const response = await handler(request("/v1/me/content"));
  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), { content: [] });
  assert.deepEqual(calls.at(-1), ["listPersonalContent", ACCOUNT_ID]);
});

test("PUT content forwards scope, family and snapshot from the document", async () => {
  const { calls, handler } = fixture();
  const response = await handler(request(`/v1/content/${CONTENT_ID}`, "PUT", {
    mutationId: MUTATION_ID,
    expectedRevision: 0,
    snapshot: snapshot({ displayName: "  Goblin  " }),
  }));
  assert.equal(response.status, 200);
  const [name, input] = calls.at(-1);
  assert.equal(name, "putContent");
  assert.equal(input.actorUserId, ACCOUNT_ID);
  assert.equal(input.scopeKind, "CAMPAIGN");
  assert.equal(input.scopeRef, CAMPAIGN_ID);
  assert.equal(input.family, "CREATURE");
  assert.equal(input.displayName, "Goblin");
  assert.equal(input.snapshotFormat, DM_CONTENT_FORMAT);
  assert.equal(input.snapshotVersion, 1);
});

test("PUT content rejects mismatched identity, bad family, bad scope and wrong format", async () => {
  const { calls, handler } = fixture();
  const cases = [
    snapshot({ id: "50000000-0000-4000-8000-000000000099" }),
    snapshot({ family: "creature" }),
    snapshot({ scope: { kind: "SYSTEM", ref: CAMPAIGN_ID } }),
    snapshot({ scope: { kind: "CAMPAIGN", ref: "nope" } }),
    { ...snapshot(), format: "dnd-custom-aid.character-backup" },
  ];
  for (const body of cases) {
    const response = await handler(request(`/v1/content/${CONTENT_ID}`, "PUT", {
      mutationId: MUTATION_ID,
      expectedRevision: 0,
      snapshot: body,
    }));
    assert.equal(response.status, 400, JSON.stringify(body));
    assert.equal((await response.json()).code, "VALIDATION_FAILED");
  }
  assert.equal(calls.some(([name]) => name === "putContent"), false);
});

test("DELETE content tombstones with the expected revision", async () => {
  const { calls, handler } = fixture();
  const response = await handler(request(`/v1/content/${CONTENT_ID}`, "DELETE", {
    mutationId: MUTATION_ID,
    expectedRevision: 3,
  }));
  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.content.revision, 4);
  assert.equal(body.content.deletedAtEpochSeconds, 99);
  assert.deepEqual(calls.at(-1), ["deleteContent", {
    actorUserId: ACCOUNT_ID,
    mutationId: MUTATION_ID,
    contentId: CONTENT_ID,
    expectedRevision: 3,
  }]);
});

test("store failures map to typed hosted errors", async () => {
  const { dmContent, handler } = fixture();
  const expectations = [
    [new HostedAuthorizationError(), 403, "FORBIDDEN"],
    [new StaleRevisionError(7), 409, "CONFLICT_STALE_REVISION"],
    [new HostedObjectGoneError(), 410, "GONE"],
    [new HostedObjectNotFoundError(), 404, "NOT_FOUND"],
    [new MutationReuseError(), 409, "CONFLICT_MUTATION_REUSE"],
    [new DmContentIdentityError("DM content scope cannot change."), 400, "VALIDATION_FAILED"],
  ];
  for (const [error, status, code] of expectations) {
    dmContent.putContent = async () => {
      throw error;
    };
    const response = await handler(request(`/v1/content/${CONTENT_ID}`, "PUT", {
      mutationId: MUTATION_ID,
      expectedRevision: 0,
      snapshot: snapshot(),
    }));
    assert.equal(response.status, status);
    assert.equal((await response.json()).code, code);
  }
});

test("other methods on content are rejected", async () => {
  const { handler } = fixture();
  const response = await handler(request(`/v1/content/${CONTENT_ID}`, "POST", {
    mutationId: MUTATION_ID,
    expectedRevision: 0,
  }));
  assert.equal(response.status, 405);
});

test("private SQLSTATE codes map to hosted error types", () => {
  assert.ok(mapDatabaseError({ code: "DC403" }) instanceof HostedAuthorizationError);
  assert.ok(mapDatabaseError({ code: "DC404" }) instanceof HostedObjectNotFoundError);
  const stale = mapDatabaseError({ code: "DC409", detail: "5" });
  assert.ok(stale instanceof StaleRevisionError);
  assert.equal(stale.currentRevision, 5);
  assert.ok(mapDatabaseError({ code: "DC410" }) instanceof HostedObjectGoneError);
  assert.ok(mapDatabaseError({ code: "DC412" }) instanceof MutationReuseError);
  assert.ok(mapDatabaseError(Object.assign(new Error("scope"), { code: "DC422" })) instanceof DmContentIdentityError);
  const other = { code: "23505" };
  assert.equal(mapDatabaseError(other), other);
});
