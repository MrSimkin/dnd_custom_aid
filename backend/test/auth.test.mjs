import test from "node:test";
import assert from "node:assert/strict";
import { AuthenticationError, extractBearerToken } from "../src/auth.ts";

test("Bearer token parsing is case-insensitive and trims the header", () => {
  const request = new Request("https://example.test/v1/me", {
    headers: { authorization: "  bearer token-value  " },
  });

  assert.equal(extractBearerToken(request), "token-value");
});

test("missing authorization is rejected", () => {
  assert.throws(
    () => extractBearerToken(new Request("https://example.test/v1/me")),
    AuthenticationError,
  );
});

test("non-Bearer authorization is rejected", () => {
  const request = new Request("https://example.test/v1/me", {
    headers: { authorization: "Basic abc" },
  });

  assert.throws(() => extractBearerToken(request), AuthenticationError);
});
