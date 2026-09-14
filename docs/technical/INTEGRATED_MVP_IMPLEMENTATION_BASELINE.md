# Integrated MVP implementation baseline — technical handoff

**Date:** 2026-09-14  
**Status:** Technical recommendation / handoff; implementation not yet authorized  
**Controlling product/architecture:** D-0071, D-0072, D-0073  
**Readiness evidence:** `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`

This file is a compact engineering handoff for the first implementation Workers. It deliberately avoids owner-facing approval gates for low-level technical matters.

## 1. First operation after owner implementation authorization

Do **not** start backend/DM feature coding directly on either current authority line.

1. refresh `main` and `implementation/phase4a-successor-cycle`;
2. create a focused convergence branch from current `main`;
3. reconcile the Player successor deliberately;
4. preserve successor Player runtime, SQLDelight migrations, tests, guard scripts and evidence;
5. preserve current `main` product/architecture/governance;
6. reconcile CI/navigation manually;
7. run convergence gates;
8. merge coherent baseline to `main`;
9. update branch status so `main` is the integrated trunk.

## 2. Convergence verification

Required technical gate before merge:

```text
all permanent Player guard scripts
+ :shared:desktopTest
+ :androidApp:assembleDebug
+ :desktopApp:build
+ backend npm check
+ local SQLDelight migration-history audit
+ documentation/authority audit
```

Do not infer semantic safety from a conflict-free Git merge.

## 3. Native shared networking

Preferred stack:

```text
shared/commonMain
  Ktor Client core
  JSON serialization/contracts
  auth-token attachment abstraction
  API/error models
  sync client logic

androidMain
  Android/JVM-supported Ktor engine
  Android auth/session integration

desktopMain
  JVM-supported Ktor engine
  Desktop auth/session integration
```

Keep UI/platform session acquisition outside common domain logic.

## 4. API contract

Initial API family:

```text
/v1/...
```

Use JSON request/response contracts.

Mutation requests that update synchronizable durable state should normally carry:

```text
mutationId       stable client-generated UUID
expectedRevision revision the client believes current
payload           domain mutation/current-state data as appropriate
```

Server response should include authoritative resulting revision/state metadata.

Minimum machine-readable error families:

```text
UNAUTHENTICATED
FORBIDDEN
NOT_FOUND
VALIDATION_FAILED
CONFLICT_STALE_REVISION
ALREADY_DELETED / GONE where useful
TRANSIENT_FAILURE
INTERNAL_ERROR
```

Display strings are not protocol semantics.

## 5. Hosted identity/authorization spine

Conceptual relational foundation — exact names may change during implementation:

```text
app_user
campaign
campaign_membership
campaign_invite / moderation state
pc
mutation_receipt / idempotency record
sync_change / scoped change cursor mechanism
```

Important invariants:

- Descope subject maps to stable internal application user;
- campaign role is not global identity;
- DM != PC owner;
- PC owner != current controller;
- authorization is checked server-side for every protected operation;
- membership removal stops future hosted access;
- global account freeze is separate from campaign kick/ban.

## 6. PC hosted persistence

Do not reproduce the complete local SQLDelight character table graph in hosted PostgreSQL for symmetry.

Preferred current-state representation:

```text
pc relational metadata
  id
  campaign id
  owner user id nullable as allowed
  controller user id nullable as allowed
  revision
  status/frozen/deleted metadata
  public identity metadata / portrait asset reference
  reconciled-data timestamp
  created/updated timestamps
  snapshot format/version
  snapshot JSONB
```

Snapshot content should use/evolve the existing application-owned versioned Player serialization family (`CharacterBackupDocument` aggregates) rather than creating a second complete character model.

Keep grouped audit/history/recovery records separate from current snapshot.

Never return the full snapshot through the tiny Player-to-Player public identity projection.

## 7. Synchronization

Use a project-specific outbox/revision design.

Client:

```text
local Save
-> local state committed
-> pending mutation/outbox record
-> later Sync
```

Push:

```text
mutationId + objectId + expectedRevision + mutation/payload
```

Server:

```text
validate identity/authorization
check idempotency
check revision
apply transaction
advance revision
record scoped change
return authoritative result
```

Pull:

```text
scope + afterCursor/changeSequence
-> ordered relevant changes/tombstones
-> transactional local application
```

Desktop sends on explicit Sync. Android may opportunistically retry while retaining manual Sync.

Do not build generalized CRDT/merge infrastructure.

## 8. PostgreSQL / Neon

Initial Worker database access:

`@neondatabase/serverless`

Prefer the simplest HTTP/serverless path appropriate to the operation. Add more connection infrastructure only if measured behavior requires it.

Do not introduce Hyperdrive initially merely because it is available.

Hosted schema is owned by explicit SQL migrations under `database/migrations/`.

## 9. Authentication

Android:

- Descope Kotlin/Android integration.

Desktop:

- standards-based native OIDC flow, preferably Authorization Code + PKCE using the system browser/local callback rather than embedded credentials.

Backend:

- validate session/access token on protected requests;
- validate relevant audience/issuer/signature/expiry;
- map external subject to internal user;
- apply application-owned campaign/domain authorization.

Perform a bounded Worker-runtime compatibility spike before committing broadly to a specific Descope backend package. Standards-based JWT/JWKS validation is acceptable if it is cleaner in the Worker runtime.

## 10. Object storage

Preferred first provider:

**Cloudflare R2 Standard**, pending owner account/subscription activation.

Reason: direct Worker integration, current free included tier appropriate to personal scale, and free egress.

Domain model stores stable application asset identity; provider key is infrastructure metadata.

Native clients must not receive durable R2 credentials.

Choose Worker-proxy vs temporary direct/presigned upload only when actual file-size/media requirements make the tradeoff concrete.

## 11. Import/export

Canonical app-owned format: versioned JSON document.

Common envelope concepts:

```text
format
version
contentType
exportedAt
records/payload
```

Import flow:

```text
parse
-> validate
-> preview recognized/warning/error state
-> explicit destination scope
-> commit
```

No overwrite-by-name. Bulk arrays/packages where useful. CSV/plain text may be adapters later.

## 12. Backup/export

First complete server backup should be an on-demand versioned archive, e.g. ZIP-like packaging with:

```text
manifest.json
relational data JSON/NDJSON
asset manifest
assets or sufficient complete recovery material according to final storage design
checksums/integrity data
```

Do not require a queue platform unless real generation time/limits prove it necessary.

## 13. CI evolution

Preserve successor Player guards.

Add tests incrementally for:

- shared serialization/contracts;
- PostgreSQL migrations;
- auth/authorization;
- stale revisions;
- idempotent retry;
- tombstone non-resurrection;
- sync round-trip and conflict;
- asset authorization/integrity;
- backup completeness;
- later combat authority generation/sequence.

Avoid generic coverage targets as substitutes for invariant tests.

## 14. Escalation rule

Do not ask the owner to approve the implementation details in this file.

Escalate only if implementation discovers a choice that materially changes:

- product behavior/workflow;
- security/privacy;
- cost/billing;
- irreversible provider lock-in;
- user-visible destructive behavior;
- approved MVP scope.

External account actions (for example enabling R2 or configuring provider secrets) may require owner action even when the technical design does not.