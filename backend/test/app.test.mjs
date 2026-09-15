import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { AuthenticationError } from "../src/auth.ts";
import { MutationReuseError } from "../src/store.ts";

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000002";
const MUTATION_ID = "30000000-0000-4000-8000-000000000003";

function fixture() {
  const calls = [];
  const auth = {
    async verify() {
      return { subject: "descope-user", displayName: "Gustavo", claims: {} };
    },
  };
  const campaigns = {
    async resolveUser(subject, displayName) {
      calls.push(["resolveUser", subject, displayName]);
      return { id: ACCOUNT_ID, externalSubject: subject, displayName };
    },
    async listCampaigns(userId) {
      calls.push(["listCampaigns", userId]);
      return [{ id: CAMPAIGN_ID, name: "Terramore", role: "DM", revision: 0 }];
    },
    async listCampaignMemberships(userId) {
      calls.push(["listCampaignMemberships", userId]);
      return [{
        campaignId: CAMPAIGN_ID,
        name: "Terramore",
        role: "PLAYER",
        status: "KICKED",
        revision: 4,
        deletedAtEpochSeconds: null,
      }];
    },
    async createCampaign(input) {
      calls.push(["createCampaign", input]);
      return {
        created: true,
        campaign: { id: input.campaignId, name: input.name, role: "DM", revision: 0 },
      };
    },
  };
  return { calls, auth, campaigns, handler: createApiHandler({ auth, campaigns }) };
}

test("health remains public and does not resolve an account", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request("https://example.test/health"));

  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), { status: "ok", service: "dnd-custom-aid-api" });
  assert.deepEqual(calls, []);
});

test("me resolves provider identity into the app account", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request("https://example.test/v1/me"));

  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), {
    account: { id: ACCOUNT_ID, displayName: "Gustavo" },
  });
  assert.deepEqual(calls, [["resolveUser", "descope-user", "Gustavo"]]);
});

test("campaign listing is scoped to the authenticated app user", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request("https://example.test/v1/campaigns"));

  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), {
    campaigns: [{ id: CAMPAIGN_ID, name: "Terramore", role: "DM", revision: 0 }],
  });
  assert.deepEqual(calls.at(-1), ["listCampaigns", ACCOUNT_ID]);
});

test("membership lifecycle listing exposes explicit inactive state", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request("https://example.test/v1/campaign-memberships"));

  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), {
    memberships: [{
      campaignId: CAMPAIGN_ID,
      name: "Terramore",
      role: "PLAYER",
      status: "KICKED",
      revision: 4,
      deletedAtEpochSeconds: null,
    }],
  });
  assert.deepEqual(calls.at(-1), ["listCampaignMemberships", ACCOUNT_ID]);
});

test("membership lifecycle endpoint is read only", async () => {
  const { handler } = fixture();
  const response = await handler(new Request("https://example.test/v1/campaign-memberships", {
    method: "POST",
  }));

  assert.equal(response.status, 405);
  assert.equal(response.headers.get("allow"), "GET");
  assert.equal((await response.json()).code, "VALIDATION_FAILED");
});

test("campaign creation preserves client identities and trims the display name", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request("https://example.test/v1/campaigns", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      mutationId: MUTATION_ID.toUpperCase(),
      campaignId: CAMPAIGN_ID.toUpperCase(),
      name: "  Terramore  ",
    }),
  }));

  assert.equal(response.status, 201);
  assert.deepEqual(await response.json(), {
    created: true,
    campaign: { id: CAMPAIGN_ID, name: "Terramore", role: "DM", revision: 0 },
  });
  assert.deepEqual(calls.at(-1), ["createCampaign", {
    actorUserId: ACCOUNT_ID,
    mutationId: MUTATION_ID,
    campaignId: CAMPAIGN_ID,
    name: "Terramore",
  }]);
});

test("invalid campaign identity is rejected before persistence", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request("https://example.test/v1/campaigns", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ mutationId: MUTATION_ID, campaignId: "not-a-uuid", name: "Terramore" }),
  }));

  assert.equal(response.status, 400);
  assert.equal((await response.json()).code, "VALIDATION_FAILED");
  assert.equal(calls.some(([name]) => name === "createCampaign"), false);
});

test("authentication failures use the stable API error envelope", async () => {
  const { campaigns } = fixture();
  const handler = createApiHandler({
    campaigns,
    auth: {
      async verify() {
        throw new AuthenticationError();
      },
    },
  });

  const response = await handler(new Request("https://example.test/v1/me"));
  assert.equal(response.status, 401);
  assert.equal(response.headers.get("www-authenticate"), "Bearer");
  assert.equal((await response.json()).code, "UNAUTHENTICATED");
});

test("mutation identity reuse is surfaced as a conflict", async () => {
  const { auth, campaigns } = fixture();
  campaigns.createCampaign = async () => {
    throw new MutationReuseError();
  };
  const handler = createApiHandler({ auth, campaigns });

  const response = await handler(new Request("https://example.test/v1/campaigns", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ mutationId: MUTATION_ID, campaignId: CAMPAIGN_ID, name: "Terramore" }),
  }));

  assert.equal(response.status, 409);
  assert.equal((await response.json()).code, "CONFLICT_MUTATION_REUSE");
});
