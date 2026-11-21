import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { InvitationBannedError, InvitationExpiredError, invitationCode } from "../src/invitationStore.ts";
import { HostedAuthorizationError, HostedObjectNotFoundError } from "../src/store.ts";

// Owner, 2026-11-18 («A»): a campaign could be moderated but nobody could join it.

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000002";

function fixture(invitations) {
  const auth = { async verify() { return { subject: "descope-user", displayName: "Gustavo", claims: {} }; } };
  const campaigns = {
    async resolveUser(subject, displayName) {
      return { id: ACCOUNT_ID, externalSubject: subject, displayName };
    },
  };
  return createApiHandler({ auth, campaigns, invitations });
}

function request(path, method = "GET", body) {
  return new Request(`https://api.example.test${path}`, {
    method,
    headers: { authorization: "Bearer token", "content-type": "application/json" },
    body: body == null ? undefined : JSON.stringify(body),
  });
}

const invitation = {
  code: "MARFIL",
  campaignId: CAMPAIGN_ID,
  campaignName: "Serpiente Vieja",
  expiresAtEpochSeconds: 1_800_000_000,
};

test("a code is six letters that cannot be read as digits", () => {
  const sequence = [0, 0.99, 0.5, 0.2, 0.7, 0.1];
  let at = 0;
  const code = invitationCode(() => sequence[at++]);
  assert.match(code, /^[A-HJ-NP-Z]{6}$/);
  assert.ok(!code.includes("I") && !code.includes("O"), code);
});

test("the DM asks for the live invitation and gets null when there is none", async () => {
  const handler = fixture({ async current() { return null; } });
  const response = await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/invitation`));
  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), { invitation: null });
});

test("creating one answers the code with its expiry", async () => {
  const calls = [];
  const handler = fixture({
    async create(actor, campaignId, days) {
      calls.push([actor, campaignId, days]);
      return invitation;
    },
  });
  const response = await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/invitation`, "POST", { daysValid: 7 }));
  assert.equal(response.status, 201);
  assert.deepEqual((await response.json()).invitation, invitation);
  assert.deepEqual(calls, [[ACCOUNT_ID, CAMPAIGN_ID, 7]]);
});

test("seven days is the default", async () => {
  const calls = [];
  const handler = fixture({ async create(actor, campaignId, days) { calls.push(days); return invitation; } });
  await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/invitation`, "POST", {}));
  assert.deepEqual(calls, [7]);
});

test("only the DM of the campaign may look or create", async () => {
  const handler = fixture({
    async current() { throw new HostedAuthorizationError(); },
    async create() { throw new HostedAuthorizationError(); },
  });
  assert.equal((await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/invitation`))).status, 403);
  assert.equal((await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/invitation`, "POST", {}))).status, 403);
});

test("a player joins with the code", async () => {
  const handler = fixture({
    async redeem(actor, code) {
      assert.equal(actor, ACCOUNT_ID);
      assert.equal(code, "MARFIL");
      return { campaignId: CAMPAIGN_ID, campaignName: "Serpiente Vieja", role: "PLAYER", alreadyMember: false };
    },
  });
  const response = await handler(request("/v1/invitations/MARFIL", "POST"));
  assert.equal(response.status, 200);
  assert.equal((await response.json()).joined.campaignName, "Serpiente Vieja");
});

test("a code that does not exist, one that expired and a banned account each say why", async () => {
  const missing = fixture({ async redeem() { throw new HostedObjectNotFoundError(); } });
  assert.equal((await missing(request("/v1/invitations/MARFIL", "POST"))).status, 404);

  const expired = fixture({ async redeem() { throw new InvitationExpiredError(); } });
  const expiredResponse = await expired(request("/v1/invitations/MARFIL", "POST"));
  assert.equal(expiredResponse.status, 410);
  assert.match((await expiredResponse.json()).message, /venció/);

  const banned = fixture({ async redeem() { throw new InvitationBannedError(); } });
  assert.equal((await banned(request("/v1/invitations/MARFIL", "POST"))).status, 403);
});

test("the invitation routes answer 503 until the server has them configured", async () => {
  const handler = fixture(undefined);
  const response = await handler(request(`/v1/campaigns/${CAMPAIGN_ID}/invitation`));
  assert.equal(response.status, 503);
});

test("the route only takes six letters", async () => {
  const handler = fixture({ async redeem() { return { campaignId: CAMPAIGN_ID, campaignName: "x", role: "PLAYER", alreadyMember: false }; } });
  assert.equal((await handler(request("/v1/invitations/MARFI", "POST"))).status, 404);
  assert.equal((await handler(request("/v1/invitations/MARFIL1", "POST"))).status, 404);
});
