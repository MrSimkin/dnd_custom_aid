# Integrated MVP implementation baseline — technical handoff

**Updated:** 2026-09-15  
**Status:** ACTIVE implementation handoff  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified hosted/sync implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Checkpoint validation:** Actions `34985799585` — SUCCESS

This file is the compact engineering handoff for implementation Workers. Routine low-level engineering is delegated; owner escalation is for material product/scope/security/privacy/cost/lock-in/destructive behavior, external-service activation and manual/physical QA gates.

## 1. Repository baseline

`main` is the sole normal integrated-MVP development trunk. The former Player successor and convergence branch are historical evidence only.

The integrated baseline preserves the mature Player runtime, SQLDelight migrations, tests and permanent guard scripts together with current integrated product/architecture/governance and PDF-export direction.

Wave 2 Shared Integrated-MVP Spine is complete. Provider-neutral Wave 3 hosted work is integrated through PR #25.

The verified implementation checkpoint `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3` passed all Player guards, shared/Kotlin tests, Android build, Desktop build, APK artifact upload, backend checks and hosted PostgreSQL contracts in Actions run `34985799585`.

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
local-only static backend verifier seam
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

## 3. Current package boundary

The next meaningful package is **real authenticated Player↔Server development integration**.

Provider-neutral prerequisites are sufficiently complete. Android owner-facing composition is still intentionally local-only: it does not yet acquire a real remembered Descope session or invoke hosted campaign/PC sync from the Player UI.

That is now the external-provider activation boundary. Do not add a second auth/network/sync architecture merely to avoid activation.

Before owner-facing Player hosted wiring, activate owner-controlled development resources for Cloudflare + Neon + Descope. See `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md`.

## 4. Native shared networking

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
  Android auth/session integration after provider activation

desktopMain
  JVM Ktor engine
  Desktop auth/session integration when Desktop hosted work reaches it
```

Keep platform UI/session acquisition outside common domain logic.

Native clients normally talk to the Worker/API, not directly to Neon.

## 5. API/sync contract

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

Client behavior remains local-first. Local Save commits local state and durable pending mutation before network delivery. Desktop sync is explicit; Android may opportunistically retry while retaining manual Sync. A failed network request must not erase local work.

Do not introduce generalized CRDTs, event sourcing, queues, WebSockets or a generic synchronization framework without a concrete requirement.

## 6. Campaign lifecycle

Hosted campaign reconciliation explicitly carries lifecycle state rather than forcing clients to infer removal from absence.

Preserve distinctions including active membership, `KICKED`, `BANNED` and campaign soft deletion. The active-campaign projection is separate from retained lifecycle state.

Membership removal/revocation must stop future hosted access server-side.

## 7. Hosted PC persistence and authorization

Hosted current PC state uses the existing versioned application-owned Player serialization family as the JSONB snapshot payload rather than mirroring the entire SQLDelight character graph relationally.

Relational hosted metadata remains responsible for concepts such as:

```text
PC id
campaign id
owner user id nullable
controller user id nullable
revision
deletion/lifecycle metadata
snapshot format/version
current snapshot JSONB
reconciled timestamp
created/updated timestamps
```

Important invariants:

- campaign role is not global identity;
- DM authority does not imply PC ownership;
- PC owner and current controller may differ;
- a Player's first hosted upload may bind that Player as owner/controller;
- a DM-created PC remains owner/controller-unassigned until an explicit later PC-management action assigns it;
- server authorization applies on every protected read/write;
- stale revision cannot silently overwrite newer hosted state;
- equal hosted/local revision must not silently overwrite potential unsent local edits;
- local-ahead state is not overwritten by an older hosted snapshot;
- hosted tombstones cannot be resurrected by stale clients;
- hosted deletion does not destructively remove the local recovery copy.

Keep audit/history/recovery separate from current snapshot. Never expose the full PC snapshot through a small public Player-to-Player identity projection.

## 8. Authentication

Backend identity proof remains provider-separated from application authorization.

Current backend provides:

- a token verifier abstraction;
- local-only static bearer verification for local development/tests;
- Descope/JWKS verification path for real hosted mode;
- mapping from external subject to stable application user;
- application-owned campaign/PC authorization.

Shared native transport already isolates token acquisition behind `HostedAccessTokenProvider`.

Android direction after activation: Descope Android/Kotlin integration with remembered session/token acquisition at the platform edge, feeding the existing shared token-provider seam.

Desktop direction when reached: standards-based native OIDC, preferably Authorization Code + PKCE via system browser/local callback unless current provider capabilities indicate a simpler equally safe native path.

Do not spread provider-specific session objects through common domain logic.

## 9. PostgreSQL / Neon

Hosted PostgreSQL schema is owned by explicit SQL migrations under `database/migrations/` and contract tests under `database/tests/`.

Preferred first real Worker database access remains `@neondatabase/serverless`. Prefer the simplest serverless path; do not introduce Hyperdrive without measured need.

The first real development Neon project is now required for the next Player↔Server package, but creation remains an owner-controlled external action.

## 10. External-provider activation gate

The gate has been reached.

Required first development providers:

1. Cloudflare — Worker/API runtime;
2. Neon — PostgreSQL;
3. Descope — authentication/identity.

Immediately before activation, verify current provider plans/options, available regions/data locations, pricing/quotas and relevant security implications. Do not encode stale commercial assumptions into source control.

Use development/test resources first. Provider accounts/resources remain owner-controlled. Never commit secrets/tokens/database credentials.

R2 is **not** part of this activation. Activate R2 later when Media/Handouts/assets actually require object storage.

## 11. First real Player↔Server proof after activation

Continue in dependency order:

```text
configure Worker + Neon + Descope
-> apply/verify migrations
-> Android remembered auth/session
-> hosted account/campaign bootstrap
-> local campaign create/select + hosted delivery
-> PC snapshot push/pull
-> second-device observation
-> offline edit/reconnect/converge
-> authorization/revoke tests
```

Definition of done includes no silent data loss under conflicting/offline conditions, correct owner/controller/DM authority, and membership revoke enforced by the server.

## 12. Object storage

Preferred first object-storage provider remains Cloudflare R2 Standard, but activation is deferred until Media/Handouts/assets reach integration.

Application records store stable logical asset identity; provider keys remain infrastructure-specific. Native clients never receive durable R2 credentials.

Choose Worker proxy vs short-lived authorized direct upload only when concrete asset-size/workflow evidence makes the tradeoff real.

## 13. Import/export and backup

Canonical app-owned import/export remains versioned JSON with validation/preview and explicit destination scope. Do not overwrite by name.

User-facing character backup restore remains independent-copy behavior. Hosted synchronization uses the distinct trusted same-identity reconciliation path.

First complete server backup remains an on-demand versioned archive direction, for example manifest + relational data + asset manifest/recovery material + integrity checksums. Do not require queues until real runtime limits demonstrate need.

## 14. PC Sheet PDF export

PDF export remains one protected semantic capability across Player Android, DM Android/tablet and DM Desktop.

Preferred decomposition remains:

```text
PC/domain state
-> canonical export snapshot
-> shared export semantics/render plan
-> platform renderer
```

Support approved Classic/custom families, Permanent vs Current Snapshot, custom-stat completeness, portrait behavior, continuation/overflow and optional Spellbook. Source templates under `assets/character-sheets/templates/` remain visual authorities for owner v1/v2 where applicable.

Do not shrink content below readability floors merely to avoid continuation pages.

## 15. CI evolution

The integrated scaffold currently validates:

- permanent Player regression guards;
- shared/Kotlin tests;
- Android build + debug APK;
- Desktop build;
- backend type-check/tests where wired;
- PostgreSQL migrations/contracts including campaign lifecycle and PC snapshot authorization/revision contracts.

Continue adding invariant-focused tests for real auth/session behavior, real hosted round-trip, two-device sync, authorization/revoke, assets, backup, PDF and later combat-authority sequence.

Avoid generic coverage percentages as substitutes for behavioral contracts.

## 16. Security visibility note

At the current checkpoint, GitHub repository metadata reports `private: false`. The owner should verify intended visibility before provider integration. Do not change visibility autonomously.

Never store provider credentials in Git regardless of repository visibility.

## 17. Branch/package strategy

- branch from current remote `main`;
- use short-lived outcome-oriented branches;
- merge shared contracts early when downstream work depends on them;
- keep `main` coherent/buildable at normal integration points;
- merge only after exact-head validation;
- verify post-merge `main`;
- update durable docs/checkpoints when operational truth materially changes;
- do not maintain permanent Player/Server/Desktop silos.

For exact current resume state, `docs/checkpoints/LATEST.md` and `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md` supersede older operational “next package” wording.
