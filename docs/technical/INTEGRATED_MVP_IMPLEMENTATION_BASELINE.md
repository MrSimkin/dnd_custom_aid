# Integrated MVP implementation baseline — technical handoff

**Updated:** 2026-09-15  
**Status:** ACTIVE implementation handoff  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified hosted/sync checkpoint:** `734477b4e276810de1581dbc2d0a8458ad953f85`  
**Checkpoint validation:** Actions `34979121449` — SUCCESS

This file is the compact engineering handoff for implementation Workers. It deliberately avoids owner-facing approval gates for routine low-level technical matters.

## 1. Repository baseline

The former `main` + Player-successor split has been semantically reconciled and promoted. Use current `main` as the normal integrated trunk. Keep the old Player successor and convergence branch as historical evidence rather than continuing ordinary development there.

The integrated baseline preserves authoritative Player runtime, SQLDelight migrations, tests, permanent guard scripts and historical Player evidence, together with current integrated product/architecture/governance and PDF-export direction.

Implementation has progressed beyond convergence. Wave 2 Shared Integrated-MVP Spine is complete, and Wave 3 hosted foundation is in progress through PR #21.

Current verified checkpoint `734477b4e276810de1581dbc2d0a8458ad953f85` passed all Player guards, `:shared:desktopTest`, Android build, Desktop build, backend checks, hosted PostgreSQL migration/contracts and APK artifact upload in Actions run `34979121449`.

## 2. Current package — hosted campaign/membership lifecycle changes

The Shared Integrated-MVP Spine is already implemented. Existing shared semantics include:

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
basic sync metadata
```

Hosted/client groundwork already integrated includes:

```text
/v1 API/auth/domain foundation
hosted PostgreSQL contracts + migration validation
shared Ktor transport contracts
durable SQLDelight hosted outbox
local campaign create + atomic outbox enqueue
idempotent hosted campaign delivery/retry
hosted account/campaign bootstrap into local state
revision/tombstone/conflict protection during bootstrap
```

**Next implementation package:** explicit hosted campaign/membership lifecycle and scoped change semantics.

Clients must not infer `KICKED`, `BANNED`, deletion or other lifecycle state merely because an object/campaign is absent from a bootstrap/list response. Add only the explicit project-specific change/removal semantics required for safe reconciliation.

Do not build generalized event sourcing, CRDTs, a generic sync platform, queues or realtime infrastructure without concrete evidence.

After this package, continue in dependency order toward hosted PC current-state/snapshot sync and Wave 4 Player↔Server end-to-end integration.

## 3. Native shared networking

Current shared networking direction:

```text
shared/commonMain
  Ktor Client core
  JSON serialization/contracts
  auth-token attachment abstraction
  API/error models
  sync client logic

androidMain
  Android/JVM Ktor engine
  Android auth/session integration

desktopMain
  JVM Ktor engine
  Desktop auth/session integration
```

Keep platform UI/session acquisition outside common domain logic.

## 4. API contract

API family:

```text
/v1/...
```

Use JSON request/response contracts.

Durable synchronizable mutations should normally carry:

```text
mutationId        stable client-generated UUID
expectedRevision  revision the client believes current
payload           domain mutation/current-state data
```

Server advances authoritative revision and returns resulting state/metadata.

Machine-readable error families should include at least:

```text
UNAUTHENTICATED
FORBIDDEN
NOT_FOUND
VALIDATION_FAILED
CONFLICT_STALE_REVISION
CONFLICT_MUTATION_REUSE
GONE where useful
TRANSIENT_FAILURE
INTERNAL_ERROR
```

Display strings are not protocol semantics.

Campaign creation already proves stable mutation identity/idempotent replay behavior. Preserve those semantics as further mutations are added.

## 5. Hosted identity/authorization spine

Current relational foundation includes stable application users, campaigns, campaign memberships, PCs and mutation receipts. Extend only as concrete lifecycle/sync requirements need it.

Important invariants:

- Descope subject maps to stable internal user;
- campaign role is not global identity;
- DM authority does not imply PC ownership;
- PC owner and controller may differ;
- authorization is server-side on every protected operation;
- membership removal stops future hosted access;
- global account freeze is distinct from campaign kick/ban;
- removal/lifecycle semantics must be explicit rather than inferred from list absence.

## 6. PC hosted persistence

Do not mirror the entire local SQLDelight character graph into hosted PostgreSQL merely for symmetry.

Preferred current-state representation:

```text
pc relational metadata
  id
  campaign id
  owner user id nullable where allowed
  controller user id nullable where allowed
  revision
  lifecycle/frozen/deleted metadata
  public identity / portrait asset reference
  reconciled-data timestamp
  created/updated timestamps
  snapshot format/version
  snapshot JSONB
```

Snapshot content should evolve the existing versioned application-owned Player serialization family rather than creating a second complete character model.

Keep grouped audit/history/recovery separate from current snapshot. Never expose the full snapshot through the tiny Player-to-Player public identity projection.

## 7. Synchronization

Use a project-specific outbox/revision design.

Client:

```text
local Save
-> local transaction commits state + pending mutation
-> later Sync
```

Campaign create already follows this durable pattern.

Push:

```text
mutationId + objectId + expectedRevision + payload
```

Server:

```text
validate identity/authorization
check idempotency
check revision
apply transaction
advance revision
record/return required scoped change semantics
return authoritative result
```

Pull/reconciliation direction:

```text
bootstrap/current state
+ explicit scoped lifecycle/change information
-> transactional local application
```

A cursor/change-feed representation may be introduced when the concrete package requires it, but do not generalize beyond project needs.

Desktop transmits on explicit Sync. Android may opportunistically retry while retaining manual Sync. No generalized CRDT/auto-merge platform.

## 8. PostgreSQL / Neon

Preferred first real Worker database access remains `@neondatabase/serverless`.

Prefer the simplest serverless path. Do not introduce Hyperdrive until measured behavior demonstrates a real need.

Hosted schema is owned by explicit SQL migrations under `database/migrations/` and is already CI-validated against PostgreSQL contracts.

**No Neon project needs to exist yet.** Activate the development Neon environment only at the first real authenticated end-to-end hosted-environment gate described in section 10.

## 9. Authentication

Android direction: Descope Kotlin/Android integration.

Desktop direction: standards-based native OIDC, preferably Authorization Code + PKCE using system browser/local callback.

Backend:

- validate session/access token;
- validate issuer/audience/signature/expiry as appropriate;
- map external subject to internal user;
- apply application-owned campaign/domain authorization.

Current API/client contracts already isolate access-token acquisition behind a provider abstraction, so local tests do not require an active Descope project.

Perform any remaining Worker-runtime compatibility spike before spreading a provider-specific backend package. JWT/JWKS validation is acceptable if cleaner in Worker runtime.

## 10. External-provider activation gate

**Do not activate provider resources speculatively.** Continue local/shared/backend/database implementation and CI first.

The first activation gate is the first package that needs a **real authenticated end-to-end hosted development environment**, after explicit campaign/membership lifecycle/change semantics are stable and before remembered Player authentication/real hosted PC sync reaches the owner-facing Player flow.

At that gate activate development resources for:

1. **Cloudflare** — Worker/API runtime;
2. **Neon** — PostgreSQL;
3. **Descope** — authentication/identity.

Provider setup rules:

- accounts/resources remain owner-controlled;
- use development/test resources first, not production;
- choose plan/region/project settings at activation time from current requirements;
- never commit secrets/tokens/connection credentials to Git;
- use provider/runtime secret stores or ignored local development configuration;
- public/non-secret identifiers may be documented when useful;
- escalate material cost/security/privacy/lock-in choices immediately before activation.

## 11. Object storage

Preferred first provider: **Cloudflare R2 Standard**.

**R2 is not part of the first Cloudflare/Neon/Descope activation gate.** Activate it later when Media/Handouts/assets actually reach object-storage integration.

Application records store stable logical asset identity; provider keys stay infrastructure-specific. Native clients never receive durable R2 credentials.

Choose Worker-proxy vs short-lived authorized direct upload only when concrete asset-size/workflow evidence makes the tradeoff real. Do not pre-build multipart complexity.

## 12. Import/export

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
parse -> validate -> preview warnings/errors -> explicit destination scope -> commit
```

No overwrite-by-name. Bulk arrays/packages where useful. CSV/plain text may be convenience adapters later.

## 13. Backup/export

First complete server backup should be an on-demand versioned archive, for example:

```text
manifest.json
relational data JSON/NDJSON
asset manifest
assets or sufficient recovery material
checksums/integrity data
```

Do not require queues unless real generation time/runtime limits prove a need.

## 14. PC Sheet PDF export

Treat PDF export as one semantic capability across Player Android, DM Android/tablet and Desktop.

Preferred decomposition:

```text
PC/domain state
-> canonical export snapshot
-> shared export semantics/render plan
   - selected family/variant
   - Permanent vs Current Snapshot
   - custom-stat mode
   - portrait mode
   - overflow/extension decisions
   - spellbook inclusion/content
-> platform renderer
```

Renderers must support:

1. faithful template overlay for owner v1/v2 fixed pages where appropriate;
2. generated/adapted drawing for Classic, App Modified, design-specific Extended pages and the application-designed Spellbook.

The source PDFs under `assets/character-sheets/templates/` are visual authorities for v1/v2.

Exact PDF library, coordinate model, font/image primitives and pagination are delegated. Existing PdfBox-Android / Apache PDFBox may remain if technically suitable, but local/offline static export and approved visual behavior are the invariant.

Use deterministic layout/overflow tests plus rendered references where practical. Do not shrink text below a readability floor merely to avoid continuation pages.

## 15. CI evolution

The integrated baseline preserves and passes successor Player guards. Current hosted/database/shared contracts are also validated in the scaffold workflow.

Continue adding invariant-focused tests incrementally for:

- shared serialization/contracts;
- hosted SQL migrations;
- auth/authorization;
- membership lifecycle/removal semantics;
- stale revision rejection;
- idempotent retry;
- tombstone non-resurrection;
- sync round-trip/conflict;
- asset authorization/integrity;
- backup completeness;
- PDF export completeness/overflow;
- later combat authority generation/sequence.

Avoid generic coverage targets as substitutes for behavior tests.

## 16. Branch/package strategy

- branch from current `main`;
- use short-lived outcome-oriented branches;
- merge shared contracts early when downstream work depends on them;
- keep `main` coherent/buildable at normal integration points;
- do not maintain permanent Player/Server/Desktop silos;
- update durable docs/checkpoints when operational truth materially changes.

## 17. Escalation rule

Do not ask the owner to approve routine implementation details in this file.

Escalate only choices that materially change:

- product behavior/workflow;
- security/privacy;
- cost/billing;
- irreversible provider lock-in;
- user-visible destructive behavior;
- approved MVP scope.

External account actions such as creating the first Cloudflare/Neon/Descope development environment or later enabling R2 require owner participation even when the engineering choice is delegated.
