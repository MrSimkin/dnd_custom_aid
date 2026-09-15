import test from "node:test";
import assert from "node:assert/strict";
import worker from "../src/index.ts";

test("Worker health endpoint stays available without hosted provider secrets", async () => {
  const response = await worker.fetch(new Request("https://example.test/health"), {});

  assert.equal(response.status, 200);
  assert.deepEqual(await response.json(), { status: "ok", service: "dnd-custom-aid-api" });
});

test("protected routes fail closed when hosted provider configuration is absent", async () => {
  const response = await worker.fetch(new Request("https://example.test/v1/me"), {});

  assert.equal(response.status, 503);
  assert.deepEqual(await response.json(), {
    code: "TRANSIENT_FAILURE",
    message: "Hosted API is not configured.",
  });
});
