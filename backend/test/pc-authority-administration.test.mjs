import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { HostedAuthorizationError, HostedObjectGoneError, HostedObjectNotFoundError } from "../src/store.ts";

const ACTOR_ID = "10000000-0000-4000-8000-000000000701";
const OWNER_ID = "10000000-0000-4000-8000-000000000702";
const CONTROLLER_ID = "10000000-0000-4000-8000-000000000703";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000701";
const PC_ID = "40000000-0000-4000-8000-000000000701";

function auth() {
  return {
    async verify() {
      return { subject: "pc-authority-dm", displayName: "PC Authority DM", claims: {} };
    },
  };
}

function campaigns(setPcAuthority) {
  return {
    async resolveUser(subject, displayName) {
      return { id: ACTOR_ID, externalSubject: subject, displayName };
    },
    setPcAuthority,
  };
}

test("PC authority route forwards explicit owner and controller replacement", async () => {
  let received = null;
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(async (input) => {
      received = input;
      return {
        authority: {
          pcId: PC_ID,
          campaignId: CAMPAIGN_ID,
          ownerUserId: OWNER_ID,
          controllerUserId: CONTROLLER_ID,
        },
        applied: true,
      };
    }),
  });

  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}/authority`, {
    method: "PUT",
    headers: { "content-type": "application/json", authorization: "Bearer test" },
    body: JSON.stringify({
      campaignId: CAMPAIGN_ID,
      ownerUserId: OWNER_ID,
      controllerUserId: CONTROLLER_ID,
    }),
  }));

  assert.equal(response.status, 200);
  assert.deepEqual(received, {
    actorUserId: ACTOR_ID,
    pcId: PC_ID,
    campaignId: CAMPAIGN_ID,
    ownerUserId: OWNER_ID,
    controllerUserId: CONTROLLER_ID,
  });
  assert.deepEqual(await response.json(), {
    authority: {
      pcId: PC_ID,
      campaignId: CAMPAIGN_ID,
      ownerUserId: OWNER_ID,
      controllerUserId: CONTROLLER_ID,
    },
    applied: true,
  });
});

test("PC authority route supports explicit owner/controller unassignment", async () => {
  let received = null;
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(async (input) => {
      received = input;
      return {
        authority: {
          pcId: PC_ID,
          campaignId: CAMPAIGN_ID,
          ownerUserId: null,
          controllerUserId: null,
        },
        applied: true,
      };
    }),
  });

  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}/authority`, {
    method: "PUT",
    headers: { "content-type": "application/json", authorization: "Bearer test" },
    body: JSON.stringify({
      campaignId: CAMPAIGN_ID,
      ownerUserId: null,
      controllerUserId: null,
    }),
  }));

  assert.equal(response.status, 200);
  assert.equal(received.ownerUserId, null);
  assert.equal(received.controllerUserId, null);
});

test("authority request requires both nullable fields explicitly", async () => {
  let calls = 0;
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(async () => {
      calls += 1;
      throw new Error("not expected");
    }),
  });

  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}/authority`, {
    method: "PUT",
    headers: { "content-type": "application/json", authorization: "Bearer test" },
    body: JSON.stringify({
      campaignId: CAMPAIGN_ID,
      ownerUserId: OWNER_ID,
    }),
  }));

  assert.equal(response.status, 400);
  assert.equal(calls, 0);
  assert.deepEqual(await response.json(), {
    code: "VALIDATION_FAILED",
    message: "controllerUserId must be explicitly provided as a UUID or null.",
    details: { field: "controllerUserId" },
  });
});

test("authority authorization failure stays generic", async () => {
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(async () => {
      throw new HostedAuthorizationError();
    }),
  });

  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}/authority`, {
    method: "PUT",
    headers: { "content-type": "application/json", authorization: "Bearer test" },
    body: JSON.stringify({
      campaignId: CAMPAIGN_ID,
      ownerUserId: OWNER_ID,
      controllerUserId: CONTROLLER_ID,
    }),
  }));

  assert.equal(response.status, 403);
  assert.deepEqual(await response.json(), {
    code: "FORBIDDEN",
    message: "The authenticated account is not allowed to perform this operation.",
  });
});

test("missing and tombstoned PCs receive typed non-success responses", async () => {
  for (const [error, expectedStatus, expectedCode] of [
    [new HostedObjectNotFoundError(), 404, "NOT_FOUND"],
    [new HostedObjectGoneError(), 410, "GONE"],
  ]) {
    const handler = createApiHandler({
      auth: auth(),
      campaigns: campaigns(async () => {
        throw error;
      }),
    });

    const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}/authority`, {
      method: "PUT",
      headers: { "content-type": "application/json", authorization: "Bearer test" },
      body: JSON.stringify({
        campaignId: CAMPAIGN_ID,
        ownerUserId: null,
        controllerUserId: null,
      }),
    }));

    assert.equal(response.status, expectedStatus);
    assert.equal((await response.json()).code, expectedCode);
  }
});
