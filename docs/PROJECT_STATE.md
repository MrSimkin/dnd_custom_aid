# Project State — global repository navigation

**Last verified:** 2026-09-15 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Provider-neutral checkpoint Actions:** `34985799585` — **SUCCESS**  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / VERIFIED / OWNER-PHYSICAL PASS**  
**Current implementation checkpoint:** `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md`

## 1. Current authority/topology

`main` is the single normal integrated-MVP development trunk.

The old Player successor and convergence lines remain historical/frozen evidence. New work uses short-lived outcome-oriented branches from current `main` and reintegrates early. Do not recreate permanent Player/Server/Desktop silos.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle.

## 2. Current verified implementation baseline

Wave 2 — **Shared Integrated-MVP Spine** — is complete and integrated, including:

- account/global identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object identities;
- monotonic revisions and stale-write semantics;
- tombstones/non-resurrection semantics;
- Personal/Campaign/System/Official scope models where valid;
- independent-copy provenance;
- local sync metadata and invariant tests.

Provider-neutral hosted foundation is integrated through PR #25 and includes:

- hosted `/v1` API/auth/domain foundations;
- explicit PostgreSQL schema/migrations/contracts and PostgreSQL CI validation;
- shared Android/Desktop Ktor transport;
- provider-neutral token acquisition/verification seams;
- durable SQLDelight hosted outbox;
- local-first campaign creation + hosted delivery using stable mutation identity;
- authenticated hosted account/campaign bootstrap;
- explicit campaign membership lifecycle reconciliation (`ACTIVE`, `KICKED`, `BANNED`) and campaign soft-deletion state;
- hosted PC current-state snapshot read/write using versioned app-owned Player serialization as JSONB;
- server-side PC authorization using active membership plus DM authority or Player owner/controller authority;
- optimistic PC revisions, mutation idempotency, stale-write rejection and tombstone handling;
- durable PC snapshot outbox delivery with authoritative revision acknowledgement;
- same-identity hosted reconciliation distinct from user-facing backup restore-as-copy;
- guards against equal-revision overwrite, local-ahead overwrite and tombstone resurrection;
- non-destructive local recovery when hosted PC state is deleted.

PR #25 merged as `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`; Actions `34985799585` passed backend, PostgreSQL contracts, shared/Kotlin tests, Android, Desktop, APK upload and preserved Player guards.

## 3. Hosted DEV activation — complete

The first real external-provider activation has been completed successfully.

Verified DEV architecture:

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Current DEV resources:

- Neon project `dnd-custom-aid-dev`, project ID `holy-meadow-19010740`, São Paulo region, database `dnd-custom-aid-dev`;
- Descope project `dnd-custom-aid-dev`, project ID `P3JNKAUazZAxRXF4uM7nKzaAiy7Y`, current DEV base URL `https://api.descope.com`;
- Cloudflare Worker `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

The current Worker runtime contract uses `DATABASE_URL` plus `DESCOPE_PROJECT_ID`; `DESCOPE_BASE_URL` is optional and defaults to the current US Descope base URL.

### Verified real-provider evidence

Completed successfully:

- real Neon migration `database/migrations/0001_integrated_mvp_spine.sql`;
- real Neon contract tests 0001–0004, executed transactionally and rolled back;
- real email OTP login through Descope DEV;
- deployed Cloudflare Worker health response;
- `/v1/me` without auth -> 401 `UNAUTHENTICATED`;
- `/v1/me` with valid Descope JWT -> 200;
- application identity persisted/resolved through real Neon;
- repeated authenticated `/v1/me` requests observed at about 1 ms Worker CPU per visible invocation with no benchmark errors.

The representative Workers Free CPU/runtime gate is therefore **PASS** for the tested authenticated path. Materially heavier future endpoints should still be profiled.

See `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` for exact provider evidence and public identifiers.

## 4. Android hosted authentication/session edge — complete

PR #30 connected Android to the already-existing provider-neutral hosted-client seam rather than creating a second auth/network architecture.

Integrated behavior includes:

- Descope Android SDK setup during application startup;
- Descope-managed remembered session/refresh lifecycle;
- Android implementation of `HostedAccessTokenProvider` that refreshes when needed and exposes the active short-lived session JWT to the shared hosted client;
- Android INTERNET permission for real hosted traffic;
- a separate debug-only `DnD Aid - Hosted DEV Auth` verification activity so the temporary DEV email-OTP harness does not become the final product login UX by inertia.

PR #30 merged to `main` as:

`bf5f843066a7c2f8674a4577918156e8a8d2c139`

Post-merge Actions `35020281492` / #1898 completed **SUCCESS**.

The owner physically verified on Android:

- real email OTP login;
- authenticated `/v1/me` through the shared hosted client;
- session persistence across full app close/reopen;
- remembered-session reuse without another login;
- DEV logout;
- no remembered session after another full close/reopen.

The Android session lifecycle gate is therefore **PASS**.

The ordinary Player launcher remains the normal application and is not yet product-login-gated. The debug harness is verification infrastructure, not a final authentication UX decision.

See `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` for exact evidence.

## 5. Current implementation boundary

Provider activation and Android session/token acquisition are no longer dependencies.

Wave 4 — Player <-> Server end-to-end — is active.

The next primary package is:

**owner-facing hosted account/campaign bootstrap**

Proceed in dependency order from the already-integrated session edge:

1. **COMPLETE** — remembered Android Descope session/token acquisition at the platform edge;
2. **COMPLETE** — feed the session token into the existing `HostedAccessTokenProvider` seam;
3. **NEXT** — wire hosted account/campaign bootstrap into the owner-facing Player flow;
4. wire campaign create/select + durable hosted delivery while preserving local-first behavior;
5. wire PC snapshot push/pull through the existing sync foundation;
6. prove second-device observation;
7. prove offline edit/reconnect/convergence;
8. prove membership revoke enforcement and Player/DM authorization boundaries;
9. only then deepen DM Android/tablet/Desktop hosted integration in dependency order.

Do not invent another auth/network/sync abstraction simply because the providers are now real.

## 6. Controlling integrated-MVP product direction

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Paper-first Player play, local-first saves, bounded project-specific sync, complete DM Desktop fallback, explicit combat authority resume/handoff, object storage/media, meaningful recovery/backup and official-SRD clarification remain protected integrated-MVP direction.

## 7. Protected integrated-MVP scope

Do not silently demote the following to stretch goals:

- Player hosted/shared integration, remembered auth and campaign switching;
- project-specific revisions/idempotency/outbox/tombstone/conflict sync;
- object storage and Media/Handouts;
- complete DM live Workspace on Android/tablet and Desktop;
- explicit DM combat authority resume/handoff;
- Monster + Creature Creator Assistant;
- NPC Manager/helper;
- Homebrew & Rules Manager;
- Stage/Place/Scene Spine preparation;
- Dungeon/Zone/Encounter Readiness/clocks/triggers preparation;
- Encounter Manager;
- PC Manager/Audit/correction;
- PC Sheet PDF export on Player Android, DM Android/tablet and DM Desktop;
- Campaign Manager + System Administration;
- meaningful audit/history/recovery;
- full verifiable server backup/export;
- official SRD storage/retrieval/grounded Player+DM clarification.

The project remains paper-first and intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

## 8. Current technical direction

The approved technical direction remains:

- Kotlin/Compose Android + Kotlin/Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence;
- TypeScript Cloudflare Worker API;
- Neon PostgreSQL hosted persistence;
- Descope identity proof with application-owned authorization;
- Ktor Client shared Android/Desktop HTTP;
- versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic revisions;
- durable SQLDelight outbox + scoped push/pull synchronization;
- explicit hosted SQL migrations and contract validation;
- hosted PC current state as versioned JSONB snapshot plus relational authorization/revision/lifecycle metadata;
- versioned app-owned JSON import/export;
- versioned on-demand backup archives with manifest/integrity data;
- one canonical PC/export snapshot + shared PDF-export semantics with platform-specific rendering.

Object storage remains required for the MVP but provider selection/activation is **deferred** until Media/Handouts/assets reach real integration. Do not assume R2 by inertia.

Workers AI remains a later approved official-SRD clarification direction only while it can be used safely under the hard `$0` policy.

## 9. External-service and security state

D-0075 remains controlling:

- hard external-service operating budget = **USD $0** unless owner changes it;
- repository visibility is intentionally public;
- never commit secrets;
- paid plans/overage/billing commitments require explicit owner approval.

Current provider activation and Android session integration succeeded within the `$0` policy.

Important security residuals carried forward:

- review JWT/fail-closed verification robustness;
- extend object-level authorization regression coverage;
- maintain SQL/query safety;
- prevent token/secret leakage through errors/logs;
- review replay/idempotency authorization;
- inspect the locally reported **3 high severity npm vulnerabilities** before remediation; never run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role instead of the current project-owner runtime credential;
- reassess Descope region/settings before production release.

Completion of the current gates is not a claim that security work is permanently finished.

## 10. Owner/local development notes

Owner project root: `D:\DnD_Aid`  
Local clone: `D:\DnD_Aid\repo\dnd_custom_aid`  
Owner credential file: `D:\DnD_Aid\dnd_custom_aid_dev_credentials.md`

The credential file is intentionally outside Git and plaintext by explicit owner choice. Do not read/copy/commit its contents or replace the workflow with a vault/password-manager migration unless requested.

A local Windows SChannel issue prevents PowerShell `Invoke-RestMethod`/Windows `curl.exe` from negotiating TLS with the Worker URL on the owner's machine, while Node `fetch()` and Vivaldi work. Treat Node/browser as the known-good local endpoint-test path unless the Windows TLS issue is separately investigated.

## 11. Release/acceptance status

The project remains development/debug and is not release-ready.

The Android hosted-session path has passed its specific owner/manual gate. That does not retroactively convert historical Player physical QA into a PASS.

Historical frozen Player candidate remains `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`; targeted cross-device physical revalidation was pending at that historical boundary.

## 12. Resume rule

For exact continuation, read `docs/checkpoints/LATEST.md` and `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md`.

Use `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` when exact provider/environment evidence is needed.

Older current-state documents that still describe provider activation or Android session acquisition as pending are superseded for those operational points by these newer records.
