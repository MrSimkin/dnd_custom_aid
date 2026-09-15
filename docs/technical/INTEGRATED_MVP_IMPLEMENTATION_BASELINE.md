# Integrated MVP implementation baseline — technical handoff

**Date:** 2026-09-14  
**Status:** ACTIVE implementation handoff  
**Owner implementation authorization:** GRANTED  
**Validated convergence commit:** `5bed85cbb3e86ae63eac79149fadc5e56e61b256`  
**Convergence validation:** Actions `34917259324` / #1694 — SUCCESS

This file is the compact engineering handoff for implementation Workers. It deliberately avoids owner-facing approval gates for routine low-level technical matters.

## 1. Repository baseline

The former `main` + Player-successor split has been semantically reconciled.

The convergence preserves:

- authoritative Player runtime, SQLDelight migrations, tests, permanent guard scripts and historical Player evidence from `implementation/phase4a-successor-cycle`;
- current integrated product/architecture/governance and PDF-export direction from `main`.

After promotion, use current `main` as the normal integrated trunk. Keep the old Player successor as historical evidence rather than continuing ordinary development there.

Convergence CI passed all Player guards, `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend `npm run check` and APK artifact upload.

## 2. Next package — Shared Integrated-MVP Spine

Implement the minimum common semantics needed by later Player/Server/Desktop/DM work.

Required concepts/invariants:

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
basic audit/sync metadata
```

Do not pre-model the entire future product and do not force every entity into one giant universal `SyncEntity`.

A Personal -> Campaign copy receives a new object ID and independent revision lifecycle while retaining provenance. Later Personal edits never silently update the Campaign copy.

Tests should prove the invariants, not merely class construction.

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
  Android/JVM Ktor engine
  Android auth/session integration

desktopMain
  JVM Ktor engine
  Desktop auth/session integration
```

Keep platform UI/session acquisition outside common domain logic.

## 4. API contract

Initial API family:

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
ALREADY_DELETED / GONE where useful
TRANSIENT_FAILURE
INTERNAL_ERROR
```

Display strings are not protocol semantics.

## 5. Hosted identity/authorization spine

Conceptual relational foundation — exact names are delegated:

```text
app_user
campaign
campaign_membership
campaign_invite / moderation state
pc
mutation_receipt / idempotency
sync_change / scoped cursor
```

Important invariants:

- Descope subject maps to stable internal user;
- campaign role is not global identity;
- DM authority does not imply PC ownership;
- PC owner and controller may differ;
- authorization is server-side on every protected operation;
- membership removal stops future hosted access;
- global account freeze is distinct from campaign kick/ban.

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
record scoped change
return authoritative result
```

Pull:

```text
scope + afterCursor
-> ordered relevant changes/tombstones
-> transactional local application
```

Desktop transmits on explicit Sync. Android may opportunistically retry while retaining manual Sync. No generalized CRDT/auto-merge platform.

## 8. PostgreSQL / Neon

Initial Worker database access: `@neondatabase/serverless`.

Prefer the simplest serverless path. Do not introduce Hyperdrive until measured behavior demonstrates a real need.

Hosted schema is owned by explicit SQL migrations under `database/migrations/`.

## 9. Authentication

Android: Descope Kotlin/Android integration.

Desktop: standards-based native OIDC, preferably Authorization Code + PKCE using system browser/local callback.

Backend:

- validate session/access token;
- validate issuer/audience/signature/expiry as appropriate;
- map external subject to internal user;
- apply application-owned campaign/domain authorization.

Perform a bounded Worker-runtime compatibility spike before spreading a specific Descope backend package. JWT/JWKS validation is acceptable if cleaner in Worker runtime.

## 10. Object storage

Preferred first provider: **Cloudflare R2 Standard**, pending owner account/subscription activation.

Application records store stable logical asset identity; provider keys stay infrastructure-specific. Native clients never receive durable R2 credentials.

Choose Worker-proxy vs short-lived authorized direct upload only when concrete asset-size/workflow evidence makes the tradeoff real. Do not pre-build multipart complexity.

No R2 activation occurred during convergence.

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
parse -> validate -> preview warnings/errors -> explicit destination scope -> commit
```

No overwrite-by-name. Bulk arrays/packages where useful. CSV/plain text may be convenience adapters later.

## 12. Backup/export

First complete server backup should be an on-demand versioned archive, for example:

```text
manifest.json
relational data JSON/NDJSON
asset manifest
assets or sufficient recovery material
checksums/integrity data
```

Do not require queues unless real generation time/runtime limits prove a need.

## 13. PC Sheet PDF export

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

## 14. CI evolution

The integrated baseline already preserves and passes successor Player guards.

Add invariant-focused tests incrementally for:

- shared serialization/contracts;
- hosted SQL migrations;
- auth/authorization;
- stale revision rejection;
- idempotent retry;
- tombstone non-resurrection;
- sync round-trip/conflict;
- asset authorization/integrity;
- backup completeness;
- PDF export completeness/overflow;
- later combat authority generation/sequence.

Avoid generic coverage targets as substitutes for behavior tests.

## 15. Branch/package strategy

After baseline promotion:

- branch from current `main`;
- use short-lived outcome-oriented branches;
- merge shared contracts early when downstream work depends on them;
- keep `main` coherent/buildable at normal integration points;
- do not maintain permanent Player/Server/Desktop silos;
- update durable docs/checkpoints when operational truth materially changes.

## 16. Escalation rule

Do not ask the owner to approve routine implementation details in this file.

Escalate only choices that materially change:

- product behavior/workflow;
- security/privacy;
- cost/billing;
- irreversible provider lock-in;
- user-visible destructive behavior;
- approved MVP scope.

External account actions such as enabling R2 or configuring provider secrets may still require owner action even when the engineering choice is delegated.
