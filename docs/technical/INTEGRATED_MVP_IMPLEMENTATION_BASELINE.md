# Integrated MVP implementation baseline — technical handoff

**Updated:** 2026-09-15  
**Status:** ACTIVE implementation handoff  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Provider-neutral hosted/sync checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Checkpoint validation:** Actions `34985799585` — SUCCESS  
**Hosted DEV provider activation:** COMPLETE / VERIFIED

This file is the compact engineering handoff for implementation Workers. Routine low-level engineering is delegated; owner escalation is for material product/scope/security/privacy/cost/lock-in/destructive behavior, new external-service actions and manual/physical QA gates.

## 1. Repository baseline

`main` is the sole normal integrated-MVP development trunk. Historical Player/convergence branches remain evidence only.

Wave 2 Shared Integrated-MVP Spine is complete. Provider-neutral hosted work is integrated through PR #25.

The verified implementation checkpoint `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3` passed Player guards, shared/Kotlin tests, Android build, Desktop build, APK upload, backend checks and hosted PostgreSQL contracts in Actions `34985799585`.

The first real Cloudflare + Neon + Descope DEV activation has also been completed and verified. See `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`.

## 2. Implemented shared/hosted spine

Implemented semantics include:

```text
Account / global identity
Campaign
CampaignMembership + campaign Role
PC owner != PC controller
stable object IDs
monotonic revisions / stale-write rejection
deletion tombstones / stale non-resurrection
Personal / Campaign / System-or-Official scope where valid
independent-copy provenance
local hosted object revision metadata
durable hosted mutation outbox
```

Implemented hosted/client foundations include:

```text
/v1 API/auth/domain foundation
explicit hosted PostgreSQL migration/contracts
shared Ktor Android/Desktop transport
provider-neutral access-token acquisition boundary
Descope/JWKS backend verifier path
local campaign create + atomic outbox enqueue
idempotent hosted campaign delivery/retry
hosted account/campaign bootstrap
explicit membership lifecycle reconciliation
hosted PC JSONB current-state snapshots
PC authorization / owner-controller semantics
PC optimistic revision + mutation idempotency
PC durable outbox delivery
same-identity hosted PC reconciliation
stale/equal/local-ahead conflict protection
hosted tombstone non-resurrection / non-destructive local recovery
```

Do not restart or redesign these foundations without a concrete defect or approved requirement.

## 3. Real hosted DEV environment

Current DEV path:

```text
Android / Desktop
       |
       v
Cloudflare Worker/API
       |
       +---- Descope identity proof
       |
       v
Neon PostgreSQL
```

Verified:

- Neon migration applied;
- real Neon contract tests 0001–0004 passed transactionally and were rolled back;
- real Descope OTP login succeeded;
- Worker `/health` returned 200;
- unauthenticated `/v1/me` returned 401;
- authenticated `/v1/me` returned 200;
- application identity persisted/resolved through Neon;
- repeated authenticated `/v1/me` calls showed about 1 ms Worker CPU per visible invocation with no observed benchmark errors.

The representative Workers Free CPU/runtime gate is therefore **PASS** for the tested path. Profile materially heavier future routes separately.

## 4. Current package boundary

The next meaningful package is **real authenticated Player <-> Server development integration**.

Provider activation prerequisites are satisfied. Android owner-facing composition remains intentionally local-only: it does not yet acquire a remembered real Descope session or invoke the hosted campaign/PC sync paths from Player UI.

Do not add a second auth/network/sync architecture. Reuse the existing boundaries.

Dependency order:

```text
remembered Android Descope session/token
-> existing HostedAccessTokenProvider
-> hosted account/campaign bootstrap
-> campaign create/select + durable hosted delivery
-> PC snapshot push/pull
-> second-device observation
-> offline edit/reconnect/convergence
-> revoke + authorization tests
```

## 5. Native shared networking

Current shared networking direction:

```text
shared/commonMain
  Ktor Client core
  JSON serialization/contracts
  auth-token attachment abstraction
  API/error models
  campaign + PC sync logic

androidMain
  Android/JVM Ktor engine
  real Descope session integration at platform edge

desktopMain
  JVM Ktor engine
  Desktop auth/session integration when Desktop hosted work reaches it
```

Keep provider-specific session objects outside common domain logic.

Native clients normally talk to the Worker/API, not directly to Neon.

## 6. API/sync contract

API family:

```text
/v1/...
```

Durable synchronizable mutations use stable client identity and optimistic revisions, conceptually:

```text
mutationId        stable client-generated UUID
objectId          stable global object UUID
expectedRevision  revision the client believes current
payload           project-specific mutation/current-state data
```

Server validates identity/authorization/idempotency/revision, applies transactionally, advances authoritative revision and returns authoritative state/metadata.

Machine-readable error families include the established set such as:

```text
UNAUTHENTICATED
FORBIDDEN
NOT_FOUND
VALIDATION_FAILED
CONFLICT_STALE_REVISION
CONFLICT_MUTATION_REUSE
GONE
TRANSIENT_FAILURE
INTERNAL_ERROR
```

Display strings are not protocol semantics.

Client behavior remains local-first. Local Save commits local state and durable pending mutation before network delivery. Failed network requests must not erase local work.

Do not introduce generalized CRDTs, event sourcing, queues, WebSockets or a generic synchronization framework without a concrete requirement.

## 7. Campaign lifecycle

Hosted campaign reconciliation explicitly carries lifecycle state rather than forcing clients to infer removal from absence.

Preserve active membership, `KICKED`, `BANNED` and campaign soft deletion. Membership removal/revocation must stop future hosted access server-side.

## 8. Hosted PC persistence and authorization

Hosted current PC state uses the existing versioned application-owned Player serialization family as the JSONB snapshot payload rather than mirroring the full SQLDelight character graph relationally.

Relational hosted metadata owns PC ID, campaign ID, owner/controller IDs, revision, deletion/lifecycle state, snapshot format/version and timestamps.

Important invariants:

- campaign role is not global identity;
- DM authority does not imply PC ownership;
- PC owner and current controller may differ;
- a Player's first hosted upload may bind that Player as owner/controller;
- a DM-created PC remains owner/controller-unassigned until explicit later assignment;
- server authorization applies on every protected read/write;
- stale revision cannot silently overwrite newer hosted state;
- equal hosted/local revision must not silently overwrite potential unsent local edits;
- local-ahead state is not overwritten by older hosted state;
- hosted tombstones cannot be resurrected by stale clients;
- hosted deletion does not destructively remove the local recovery copy.

Keep audit/history/recovery separate from current snapshot. Never expose the full PC snapshot through the tiny public Player-to-Player identity projection.

## 9. Authentication

Backend identity proof remains provider-separated from application authorization.

Current backend provides:

- bearer extraction and verifier abstraction;
- Descope/JWKS verification;
- external subject -> stable application user mapping;
- application-owned campaign/PC authorization.

Current deployed Worker configuration contract:

- `DATABASE_URL` required, secret;
- `DESCOPE_PROJECT_ID` required;
- `DESCOPE_BASE_URL` optional, defaulting to `https://api.descope.com`.

Older pre-activation binding-family references are superseded by the current code.

Shared native transport already isolates token acquisition behind `HostedAccessTokenProvider`.

Android direction now: integrate remembered Descope session/token acquisition at the platform edge and feed the existing token-provider seam.

Desktop direction when reached: standards-based native identity/session flow consistent with current Descope capabilities and the same project-owned authorization boundary.

## 10. PostgreSQL / Neon

Hosted PostgreSQL schema is owned by explicit SQL migrations under `database/migrations/` and contract tests under `database/tests/`.

Current real DEV database is `dnd-custom-aid-dev` in Neon project `holy-meadow-19010740`.

The current Worker DB credential is associated with the project owner role. A later security hardening pass should evaluate a dedicated least-privilege runtime role; do not casually alter live privileges/credentials without a deliberate migration plan.

Prefer the current simple `@neondatabase/serverless` path; do not introduce Hyperdrive without measured need.

## 11. Security residuals

Activation success is not equivalent to a completed security audit.

Carry forward:

- JWT/fail-closed claim/signature/key-rotation review;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log token/secret leakage prevention;
- replay/idempotency authorization;
- request/API hardening;
- dependency audit review;
- least-privilege Neon runtime access;
- production Descope region/configuration review.

Known dependency item: local backend install reported **3 high severity vulnerabilities**. Inspect exact packages/reachability/fixed versions before remediation; do **not** run `npm audit fix --force` blindly.

## 12. Object storage

Object storage remains required by the MVP, but provider selection/activation is deferred until Media/Handouts/assets reach integration.

R2 is a candidate, not an assumption. Perform a fresh D-0075 `$0` review before activation.

Application records use stable logical asset identity; provider keys remain infrastructure-specific. Native clients never receive durable storage-provider credentials.

## 13. Import/export and backup

Canonical app-owned import/export remains versioned JSON with validation/preview and explicit destination scope. Do not overwrite by name.

User-facing character backup restore remains independent-copy behavior. Hosted synchronization uses the distinct trusted same-identity reconciliation path.

Complete server backup remains an on-demand versioned archive direction with relational data, future asset recovery material, manifest/version data and integrity checksums.

## 14. PC Sheet PDF export

PDF export remains one protected semantic capability across Player Android, DM Android/tablet and DM Desktop.

Preferred decomposition:

```text
PC/domain state
-> canonical export snapshot
-> shared export semantics/render plan
-> platform renderer
```

Support approved Classic/custom families, Permanent vs Current Snapshot, custom-stat completeness, portrait behavior, continuation/overflow and optional Spellbook.

## 15. CI and real-environment verification

The integrated scaffold validates:

- permanent Player regression guards;
- shared/Kotlin tests;
- Android build + debug APK;
- Desktop build;
- backend checks/tests;
- PostgreSQL migrations/contracts.

Real DEV activation additionally verified the actual Neon migration/contracts and real Descope -> Worker -> Neon authentication/persistence path.

Continue adding invariant-focused tests for remembered auth/session behavior, real Player hosted round trips, two-device sync, authorization/revoke, assets, backup, PDF and later combat authority.

## 16. Branch/package strategy

- branch from current remote `main`;
- use short-lived outcome-oriented branches;
- merge shared contracts early when downstream work depends on them;
- keep `main` coherent/buildable at normal integration points;
- merge only after exact-head validation;
- verify post-merge `main`;
- update durable docs/checkpoints when operational truth changes;
- do not maintain permanent Player/Server/Desktop silos.

For exact current resume state, `docs/checkpoints/LATEST.md` and `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` control.
