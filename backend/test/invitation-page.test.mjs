import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";

// Owner's A (2026-11-20): the invitation link and the QR used to point at nothing.

function handler() {
  return createApiHandler({
    auth: {
      async verify() {
        throw new Error("the page must not ask for an account");
      },
    },
    campaigns: {
      async resolveUser() {
        throw new Error("the page must not touch the database");
      },
    },
  });
}

function open(path) {
  return handler()(new Request(`https://api.example.test${path}`));
}

test("the link opens a page that hands over the code, with no account and no database", async () => {
  const response = await open("/unirme/MARFIL");
  assert.equal(response.status, 200);
  assert.match(response.headers.get("content-type"), /text\/html/);
  const body = await response.text();
  assert.ok(body.includes("MARFIL"), body.slice(0, 200));
  assert.ok(body.includes("Te invitaron a una campaña"));
  assert.ok(body.includes("dndcustomaid://unirme/MARFIL"), "it offers to open the App");
});

test("lower case in the link still shows the code as the DM dictated it", async () => {
  const body = await (await open("/unirme/marfil")).text();
  assert.ok(body.includes("MARFIL"), body.slice(0, 200));
});

test("the page says nothing about the campaign, so a forwarded link leaks nothing", async () => {
  const body = await (await open("/unirme/MARFIL")).text();
  assert.ok(!body.toLowerCase().includes("serpiente"), "no campaign name");
  assert.ok(!body.toLowerCase().includes("vence el"), "no exact expiry, only that it expires");
});

test("something that is not six letters is not a page", async () => {
  assert.equal((await open("/unirme/MARFI")).status, 404);
  assert.equal((await open("/unirme/MARFIL1")).status, 404);
});

test("the page only answers GET", async () => {
  const response = await handler()(new Request("https://api.example.test/unirme/MARFIL", { method: "POST" }));
  assert.equal(response.status, 405);
});
