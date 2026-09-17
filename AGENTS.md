# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent or other AI working in this repository.

## 1. Repository authority

Git is the project's durable source of truth and operative memory. Chat context is navigation only. Before substantial work, verify the current remote repository rather than trusting remembered state.

`main` is the sole normal integrated-MVP trunk. Use short-lived outcome-oriented branches. Branch existence alone never establishes authority.

Authority/navigation order:

1. `README.md`;
2. `AGENTS.md`;
3. `MANIFEST.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/checkpoints/LATEST.md`;
6. the checkpoint referenced by `LATEST.md`;
7. `docs/BRANCH_STATUS.md`;
8. `docs/DECISIONS.md`, `docs/DECISIONS_RECENT.md`, relevant detailed decisions;
9. `docs/CONVENTIONS.md`;
10. `docs/PRODUCT.md`;
11. `docs/ROADMAP.md`;
12. `docs/WORKFLOW.md`;
13. `docs/ARCHITECTURE.md`;
14. `docs/TESTING.md`.

Historical checkpoints remain evidence; do not follow old `next` instructions when newer authority supersedes them.

## 2. Current stage

Integrated-MVP implementation is authorized and in progress.

Wave 4 Player <-> Server is complete/integrated for its recorded scope. Wave 5 Desktop shell + Campaign Administration is complete, owner-QA accepted and integrated through PR #44 merge commit `306377df1a453f531af4b670d2b231c88a3c9419`; post-merge Scaffold `35168920031` passed.

Wave 6 reusable/persistent content architecture is active. Its first bounded reusable-content persistence foundation is integrated through PR #46 merge commit `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` passed.

Normal continuation remains Wave 6, but the next bounded package must be determined from current architecture, decisions and roadmap. Do not restart PR #42/#43/#44/#46, completed Wave 4 packages, provider activation, Wave 5 owner QA or the integrated Wave 6 foundation without new evidence of a real defect.

## 3. Product/architecture invariants

Preserve:

- paper-first Player experience;
- `Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop`;
- local-first operation;
- Kotlin/Compose Android and Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence where appropriate;
- Ktor native HTTP;
- Cloudflare Worker API + Neon PostgreSQL + Descope identity proof;
- application-owned authorization; native clients do not connect directly to Neon;
- stable object IDs, optimistic revisions, stale-write rejection, mutation IDs/idempotency, durable outbox, tombstones/non-resurrection and explicit conflict handling;
- DM campaign authority distinct from PC ownership; PC owner distinct from current controller;
- provider-neutral Shared seams where useful.

For reusable DM content, approved semantics include Personal reusable material, explicit Personal -> Campaign copy, a new independent campaign object identity, retained provenance, and no automatic inheritance/update relationship after copy. The integrated Wave 6 foundation now persists and tests these core local semantics for its recorded scope.

Do not add generalized CRDT/sync engines, microservices, event buses, enterprise IAM/RBAC/ACL, exhaustive event sourcing, WebSockets/realtime, or enterprise observability without a concrete approved need.

## 4. Owner vs technical responsibility

The owner decides consequential product behavior, workflow/UX, game semantics, privacy/visibility, MVP scope, destructive behavior and meaningful cost/security/compatibility/lock-in tradeoffs.

Technical agents normally decide schema layout, class/type decomposition, endpoint/request shapes, migration mechanics, internal sync structures, serialization, tests and bounded branch/package granularity. Do not ask the owner to rubber-stamp routine engineering.

Explain meaningful work: what changed, why, practical approach, consequences/tradeoffs, verification, limitations and exact next action.

## 5. External-provider capability boundary

Cloudflare, Descope, Neon and future providers are explicit authenticated capability boundaries.

When a task reaches a provider:

1. determine whether the current environment actually has authenticated capability for the required action;
2. if yes, proceed only within approved scope/security/cost constraints;
3. if no, establish the limitation once and stop trying alternate plugins, MCPs, browser logins, API-token paths or dashboard workarounds;
4. finish all safe repository/code/test/CI work first;
5. stop at the first unavoidable authenticated owner action;
6. provide one bounded owner-action packet;
7. resume from returned non-secret evidence without repeating completed investigation.

Provider inability is a capability boundary, not a reason to fragment safe engineering into tiny tasks.

## 6. Owner-action packet standard

When provider/owner handoff is necessary, include:

- what must be done and why;
- exact working directory/page/provider location;
- ordered commands/clicks;
- expected successful result;
- secrets that must not be shared;
- exact non-secret evidence to return;
- explicit stop/error conditions;
- billing/security/destructive-change warnings.

Prefer one provider handoff per task.

## 7. Secret handling

Never request or commit passwords, OTPs, Cloudflare tokens, DB passwords, Neon connection strings, Descope/session/access/refresh JWTs, private keys, signing credentials, provider credential files or secret environment-variable values.

Use secure local/CI/provider secret stores and non-secret evidence.

## 8. Provider evidence discipline

Green repository CI does not prove a Worker is deployed, a route is live, provider configuration is accepted, or real authentication works. Provider-side behavior requires provider/owner evidence.

Conversely, do not repeat a completed provider action merely because documentation later changes.

For the verified Wave 5 state, the existing DEV Worker is `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`. Do not create a replacement Worker. Do not redeploy unless Worker code materially changes or newer evidence specifically requires it. Deployment must preserve configured secrets and fail safely when required secrets are absent.

## 9. Canonical DEV identity

Normal hosted DEV owner/DM testing uses the Outlook-backed application identity. Gmail is historical/inactive by default and may be deliberately reused only when a future test needs a secondary real user. Historical Gmail mutation/audit evidence remains historical truth and must not be rewritten.

## 10. Cost/security

External-service operating budget is **USD $0** unless the owner explicitly changes it. Do not enable paid plans, automatic overage, usage-based commitments, paid add-ons or paid AI services without explicit approval.

Before activating any new provider/resource, verify current free-tier/payment/quota/hard-cap/overage/region/lock-in terms. Object-storage provider selection remains deferred until Media/Handouts/assets actually require it.

The repository is intentionally public. Preserve fail-closed authorization, least privilege, object-level authorization, replay/idempotency safety, error/log hygiene, local recovery data, stale-write protection and deletion/tombstone guarantees.

Known residual: an owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; investigate exact packages/reachability/upgrade consequences when a relevant hardening package is taken up.

## 11. Development workflow

For substantial work:

1. verify remote branch/topology/current CI;
2. read current authority files;
3. identify already-approved semantics and implemented constraints;
4. surface only genuine owner-level ambiguities;
5. create a short-lived branch from current `main`;
6. implement the smallest coherent technical batch;
7. run focused checks and aggregate CI proportionate to risk;
8. update operative-memory documentation;
9. leave clean Git evidence and open/update PR as appropriate;
10. continue autonomously until a genuine owner/manual/provider boundary.

Do not stop simply because one safe subtask completed if the coherent package can continue, and do not expand speculatively beyond it.