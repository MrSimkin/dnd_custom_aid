import test from "node:test";
import assert from "node:assert/strict";
import { createApiHandler } from "../src/app.ts";
import { RulesAnswerer } from "../src/rulesAnswer.ts";

const ACCOUNT_ID = "10000000-0000-4000-8000-000000000001";
const PASSAGE = { version: "5.2.1", page: 177, heading: "Cobertura", text: "Media cobertura: +2 a la CA y a las salvaciones de Destreza." };

function fixture({ ai } = {}) {
  const calls = [];
  const handler = createApiHandler({
    auth: { async verify() { return { subject: "s", displayName: "DM", claims: {} }; } },
    campaigns: { async resolveUser() { return { id: ACCOUNT_ID, externalSubject: "s", displayName: "DM" }; } },
    rules: ai === undefined ? undefined : new RulesAnswerer({
      async run(model, input) {
        calls.push({ model, input });
        return ai(input);
      },
    }, "@cf/test/model"),
  });
  return { handler, calls };
}

function ask(handler, body) {
  return handler(new Request("https://api.test/v1/rules/answer", {
    method: "POST",
    headers: { "content-type": "application/json", authorization: "Bearer t" },
    body: JSON.stringify(body),
  }));
}

test("a rules question is answered from the SRD passages the App found", async () => {
  const { handler, calls } = fixture({ ai: () => ({ response: "Con media cobertura sumas +2 a la CA [1]." }) });
  const response = await ask(handler, { question: "¿Cuánto da la media cobertura?", passages: [PASSAGE] });
  assert.equal(response.status, 200);
  const body = await response.json();
  assert.equal(body.answer, "Con media cobertura sumas +2 a la CA [1].");
  assert.equal(body.passageCount, 1);
  assert.equal(calls[0].model, "@cf/test/model");
  const user = calls[0].input.messages[1].content;
  assert.match(user, /\[1\] SRD 5\.2\.1, p\. 177 · Cobertura/);
  assert.match(user, /Pregunta: ¿Cuánto da la media cobertura\?/);
  assert.match(calls[0].input.messages[0].content, /solamente los fragmentos/);
});

test("without the AI binding the route says it is not available and the App keeps its offline search", async () => {
  const { handler } = fixture();
  const response = await ask(handler, { question: "¿Cobertura?", passages: [PASSAGE] });
  assert.equal(response.status, 503);
  assert.equal((await response.json()).code, "TRANSIENT_FAILURE");
});

test("a failing model or exhausted quota is a 503, never a 500", async () => {
  const { handler } = fixture({ ai: () => { throw new Error("quota"); } });
  const response = await ask(handler, { question: "¿Cobertura?", passages: [PASSAGE] });
  assert.equal(response.status, 503);
});

test("questions and passages are validated and bounded", async () => {
  const { handler, calls } = fixture({ ai: () => ({ response: "ok" }) });
  assert.equal((await ask(handler, { question: "", passages: [PASSAGE] })).status, 400);
  assert.equal((await ask(handler, { question: "x".repeat(501), passages: [PASSAGE] })).status, 400);
  assert.equal((await ask(handler, { question: "¿?", passages: [] })).status, 400);
  assert.equal((await ask(handler, { question: "¿?", passages: Array(9).fill(PASSAGE) })).status, 400);
  assert.equal((await ask(handler, { question: "¿?", passages: [{ ...PASSAGE, version: "4e" }] })).status, 400);
  assert.equal((await ask(handler, { question: "¿?", passages: [{ ...PASSAGE, page: 0 }] })).status, 400);
  assert.equal(calls.length, 0, "nothing reaches the model before validation passes");

  const long = { ...PASSAGE, text: "a".repeat(5000) };
  assert.equal((await ask(handler, { question: "¿?", passages: [long] })).status, 200);
  assert.ok(calls[0].input.messages[1].content.length < 2000, "passages are cut to a bounded size");
});

test("only POST is allowed", async () => {
  const { handler } = fixture({ ai: () => ({ response: "ok" }) });
  const response = await handler(new Request("https://api.test/v1/rules/answer", { method: "GET", headers: { authorization: "Bearer t" } }));
  assert.equal(response.status, 405);
});
