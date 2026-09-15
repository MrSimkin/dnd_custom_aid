import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { AuthenticationError } from "../src/auth.ts";
import {
  HostedAuthorizationError,
  MutationReuseError,
  StaleRevisionError,
} from "../src/store.ts";

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000002";
const MUTATION_ID = "30000000-0000-4000-8000-000000000003";
const PC_ID = "40000000-0000-4000-8000-000000000004";
const CHARACTER_BACKUP_FORMAT = "dnd-custom-aid.character-backup";

function snapshot(name = "Simkin") {
  return {
    format: CHARACTER_BACKUP_FORMAT,
    version: 2,
    exportedAtEpochSeconds: 100,
    character: {
      id: PC_ID,
      campaignId: CAMPAIGN_ID,
      name,
    },
    closureState: {},
    successorState: {},
  };
}

function hostedPc(overrides = {}) {
  return {
    id: PC_ID,
    campaignId: CAMPAIGN_ID,
    ownerUserId: ACCOUNT_ID,
    controllerUserId: ACCOUNT_ID,
    name: "Simkin",
    revision: 0,
    deletedAtEpochSeconds: null,
    snapshotFormat: CHARACTER_BACKUP_FORMAT,
    snapshotVersion: 2,
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
    async listPcSnapshots(userId, campaignId) {
      calls.push(["listPcSnapshots", userId, campaignId]);
      return [hostedPc()];
    },
    async putPcSnapshot(input) {
      calls.push(["putPcSnapshot", input]);
      return {
        applied: true,
        pc: hostedPc({
          id: input.pcId,
          campaignId: input.campaignId,
          name: input.name,
          revision: input.expectedRevision === 0 ? 0 : input.expectedRevision + 1,
          snapshotFormat: input.snapshotFormat,
          snapshotVersion: input.snapshotVersion,
          snapshot: input.snapshot,
        }),
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

test("campaign PC listing is explicitly scoped by authenticated account and campaign", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request(`https://example.test/v1/campaigns/${CAMPAIGN_ID}/pcs`));

  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), { pcs: [hostedPc()] });
  assert.deepEqual(calls.at(-1), ["listPcSnapshots", ACCOUNT_ID, CAMPAIGN_ID]);
});

test("PC snapshot mutation derives relational identity from the versioned snapshot envelope", async () => {
  const { calls, handler } = fixture();
  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}`, {
    method: "PUT",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      mutationId: MUTATION_ID.toUpperCase(),
      campaignId: CAMPAIGN_ID.toUpperCase(),
      expectedRevision: 0,
      snapshot: snapshot("  Simkin  "),
    }),
  }));

  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.applied, true);
  assert.equal(body.pc.id, PC_ID);
  assert.equal(body.pc.snapshotFormat, CHARACTER_BACKUP_FORMAT);
  assert.deepEqual(calls.at(-1), ["putPcSnapshot", {
    actorUserId: ACCOUNT_ID,
    mutationId: MUTATION_ID,
    pcId: PC_ID,
    campaignId: CAMPAIGN_ID,
    expectedRevision: 0,
    name: "Simkin",
    snapshotFormat: CHARACTER_BACKUP_FORMAT,
    snapshotVersion: 2,
    snapshot: snapshot("  Simkin  "),
  }]);
});

test("PC snapshot identity mismatch is rejected before persistence", async () => {
  const { calls, handler } = fixture();
  const wrong = snapshot();
  wrong.character.id = "40000000-0000-4000-8000-000000000099";
  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}`, {
    method: "PUT",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      mutationId: MUTATION_ID,
      campaignId: CAMPAIGN_ID,
      expectedRevision: 0,
      snapshot: wrong,
    }),
  }));

  assert.equal(response.status, 400);
  assert.equal((await response.json()).code, "VALIDATION_FAILED");
  assert.equal(calls.some(([name]) => name === "putPcSnapshot"), false);
});

test("PC stale revision is surfaced as a stable conflict", async () => {
  const { auth, campaigns } = fixture();
  campaigns.putPcSnapshot = async () => {
    throw new StaleRevisionError(7);
  };
  const handler = createApiHandler({ auth, campaigns });
  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}`, {
    method: "PUT",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      mutationId: MUTATION_ID,
      campaignId: CAMPAIGN_ID,
      expectedRevision: 2,
      snapshot: snapshot(),
    }),
  }));

  assert.equal(response.status, 409);
  assert.deepEqual(await response.json(), {
    code: "CONFLICT_STALE_REVISION",
    message: "The hosted object revision no longer matches the expected revision.",
    details: { currentRevision: 7 },
  });
});

test("PC authorization failures do not leak hosted state", async () => {
  const { auth, campaigns } = fixture();
  campaigns.listPcSnapshots = async () => {
    throw new HostedAuthorizationError();
  };
  const handler = createApiHandler({ auth, campaigns });
  const response = await handler(new Request(`https://example.test/v1/campaigns/${CAMPAIGN_ID}/pcs`));

  assert.equal(response.status, 403);
  assert.equal((await response.json()).code, "FORBIDDEN");
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
