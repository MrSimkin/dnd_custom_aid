import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { HostedAuthorizationError } from "../src/store.ts";

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000501";
const CAMPAIGN_ID = "20000000-0000-4000-8000-000000000501";
const MUTATION_ID = "30000000-0000-4000-8000-000000000501";
const PC_ID = "40000000-0000-4000-8000-000000000501";

function handlerWithRevokedPcWrite() {
  const auth = {
    async verify() {
      return { subject: "revoked-user", displayName: "Revoked User", claims: {} };
    },
  };
  const campaigns = {
    async resolveUser(subject, displayName) {
      return { id: ACCOUNT_ID, externalSubject: subject, displayName };
    },
    async putPcSnapshot() {
      throw new HostedAuthorizationError();
    },
  };
  return createApiHandler({ auth, campaigns });
}

test("revoked PC write is surfaced as stable FORBIDDEN without hosted-state details", async () => {
  const handler = handlerWithRevokedPcWrite();
  const response = await handler(new Request(`https://example.test/v1/pcs/${PC_ID}`, {
    method: "PUT",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      mutationId: MUTATION_ID,
      campaignId: CAMPAIGN_ID,
      expectedRevision: 9,
      snapshot: {
        format: "dnd-custom-aid.character-backup",
        version: 2,
        character: {
          id: PC_ID,
          campaignId: CAMPAIGN_ID,
          name: "Preserved Local PC",
        },
      },
    }),
  }));

  assert.equal(response.status, 403);
  assert.deepEqual(await response.json(), {
    code: "FORBIDDEN",
    message: "The authenticated account is not allowed to perform this operation.",
  });
});
