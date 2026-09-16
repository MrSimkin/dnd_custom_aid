import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { HostedAuthorizationError } from "../src/store.ts";

const ACTOR_ID = "10000000-0000-4000-8000-000000000601";
const PLAYER_ID = "10000000-0000-4000-8000-000000000602";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000601";

function auth() {
  return {
    async verify() {
      return { subject: "campaign-admin-dm", displayName: "Campaign Admin DM", claims: {} };
    },
  };
}

function campaigns() {
  return {
    async resolveUser(subject, displayName) {
      return { id: ACTOR_ID, externalSubject: subject, displayName };
    },
  };
}

test("active DM can retrieve Campaign Administration member roster", async () => {
  let receivedActor = null;
  let receivedCampaign = null;
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(),
    campaignAdministration: {
      async listMembers(actorUserId, campaignId) {
        receivedActor = actorUserId;
        receivedCampaign = campaignId;
        return {
          campaignId,
          campaignRevision: 12,
          members: [
            { userId: ACTOR_ID, displayName: "Campaign Admin DM", role: "DM", status: "ACTIVE" },
            { userId: PLAYER_ID, displayName: "Player One", role: "PLAYER", status: "KICKED" },
          ],
        };
      },
      async moderateMember() {
        throw new Error("not expected");
      },
    },
  });

  const response = await handler(new Request(`https://example.test/v1/campaigns/${CAMPAIGN_ID}/members`, {
    headers: { authorization: "Bearer test" },
  }));

  assert.equal(response.status, 200);
  assert.equal(receivedActor, ACTOR_ID);
  assert.equal(receivedCampaign, CAMPAIGN_ID);
  assert.deepEqual(await response.json(), {
    campaignId: CAMPAIGN_ID,
    campaignRevision: 12,
    members: [
      { userId: ACTOR_ID, displayName: "Campaign Admin DM", role: "DM", status: "ACTIVE" },
      { userId: PLAYER_ID, displayName: "Player One", role: "PLAYER", status: "KICKED" },
    ],
  });
});

test("moderation route forwards explicit action and returns lifecycle result", async () => {
  let receivedInput = null;
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(),
    campaignAdministration: {
      async listMembers() {
        throw new Error("not expected");
      },
      async moderateMember(input) {
        receivedInput = input;
        return {
          member: { userId: PLAYER_ID, displayName: "Player One", role: "PLAYER", status: "BANNED" },
          campaignRevision: 13,
          applied: true,
        };
      },
    },
  });

  const response = await handler(new Request(
    `https://example.test/v1/campaigns/${CAMPAIGN_ID}/members/${PLAYER_ID}/moderation`,
    {
      method: "POST",
      headers: { "content-type": "application/json", authorization: "Bearer test" },
      body: JSON.stringify({ action: "BAN" }),
    },
  ));

  assert.equal(response.status, 200);
  assert.deepEqual(receivedInput, {
    actorUserId: ACTOR_ID,
    campaignId: CAMPAIGN_ID,
    targetUserId: PLAYER_ID,
    action: "BAN",
  });
  assert.deepEqual(await response.json(), {
    member: { userId: PLAYER_ID, displayName: "Player One", role: "PLAYER", status: "BANNED" },
    campaignRevision: 13,
    applied: true,
  });
});

test("invalid moderation action is rejected before store mutation", async () => {
  let moderationCalls = 0;
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(),
    campaignAdministration: {
      async listMembers() {
        return { campaignId: CAMPAIGN_ID, campaignRevision: 0, members: [] };
      },
      async moderateMember() {
        moderationCalls += 1;
        throw new Error("not expected");
      },
    },
  });

  const response = await handler(new Request(
    `https://example.test/v1/campaigns/${CAMPAIGN_ID}/members/${PLAYER_ID}/moderation`,
    {
      method: "POST",
      headers: { "content-type": "application/json", authorization: "Bearer test" },
      body: JSON.stringify({ action: "UNBAN_AND_REJOIN" }),
    },
  ));

  assert.equal(response.status, 400);
  assert.equal(moderationCalls, 0);
  const body = await response.json();
  assert.equal(body.code, "VALIDATION_FAILED");
  assert.deepEqual(body.details, { field: "action" });
});

test("unauthorized roster request stays generic and leaks no member data", async () => {
  const handler = createApiHandler({
    auth: auth(),
    campaigns: campaigns(),
    campaignAdministration: {
      async listMembers() {
        throw new HostedAuthorizationError();
      },
      async moderateMember() {
        throw new HostedAuthorizationError();
      },
    },
  });

  const response = await handler(new Request(`https://example.test/v1/campaigns/${CAMPAIGN_ID}/members`, {
    headers: { authorization: "Bearer test" },
  }));

  assert.equal(response.status, 403);
  const body = await response.json();
  assert.deepEqual(body, {
    code: "FORBIDDEN",
    message: "The authenticated account is not allowed to perform this operation.",
  });
  assert.equal(JSON.stringify(body).includes("Player One"), false);
});
