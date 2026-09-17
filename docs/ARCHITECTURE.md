# Architecture Record

**Updated:** 2026-09-17  
**State:** integrated Player + hosted/shared + DM MVP architecture approved and implementation in progress  
**Normal trunk:** `main`  
**Wave 5:** COMPLETE / INTEGRATED  
**Next architecture wave:** Wave 6 reusable/persistent content

D-0034 through D-0043 remain foundational stack decisions. D-0068 through D-0070 control DM Workspace/Desk behavior. D-0071 through D-0075 control the integrated MVP, Desktop product, scope/governance, PDF export and `$0` provider policy. C-0009 proportionality remains controlling.

## Product topology

```text
Player Android
      |
      v
shared Kotlin domain/data/network/persistence
      |
      v
Cloudflare Worker/API <---- Descope identity proof
      |
      v
Neon PostgreSQL
      ^
      |
DM Android/tablet + DM Desktop
```

Android and Desktop share domain/network/sync/persistence logic where useful, not necessarily UI/navigation. Desktop is both a rich authoring/admin surface and complete operational DM fallback.

## Technologies

- Android: Kotlin + Jetpack Compose, minSdk 30;
- Desktop: Kotlin + Compose Multiplatform;
- local durable state: SQLite/SQLDelight where local-first/offline behavior matters;
- shared native HTTP: Ktor Client;
- backend: TypeScript Cloudflare Worker/API;
- hosted relational store: Neon PostgreSQL;
- identity proof: Descope; application owns campaign/domain authorization;
- native clients do not connect directly to Neon.

Official SRD retrieval direction remains PostgreSQL full-text search first with replaceable LLM integration; Workers AI is conditional on safe `$0` availability. Object-storage provider selection remains deferred until asset integration.

## Authority/sync invariants

Paper remains authoritative during ordinary paper-first play. Local Save is network-independent. The server is a durable shared exchange/recovery point, not an always-live game-session engine.

Project-specific sync uses stable IDs, explicit revisions, stale-write rejection, mutation IDs/idempotency, durable outbox, tombstones/non-resurrection, scoped pull/push and explicit conflicts. Do not replace this with a generalized CRDT/sync platform without a concrete need.

Campaign membership/role, PC existence, PC ownership and current control are distinct. DM campaign authority does not imply PC ownership.

## Reusable content architecture

D-0071/D-0072 approve these semantics:

```text
PERSONAL LIBRARY
  | explicit use/copy
  v
CAMPAIGN CONTENT (new independent identity + provenance)
  | preparation/live use
  v
LIVE / WORKING STATE
```

Personal masters and Campaign copies do not silently update one another after copy. Official content is canonical/read-only; customization creates an independent custom copy with provenance.

The existing Shared spine already implements `ContentScope`, `CopyProvenance`, `ScopedObjectIdentity.independentCampaignCopy()`, `Revision`, `SyncMetadata` and stale/tombstone primitives. Wave 6 should extend these into reusable-content persistence rather than creating a parallel identity/versioning model.

The first Wave 6 package should use a bounded shared envelope/catalog for identity/family/scope/provenance/revision/deletion and allow domain-specific Monster/NPC/Homebrew/Place/Zone/Encounter payload models to remain appropriately structured. Avoid a giant universal executable content model.

## Provider boundary

Current real DEV path is Cloudflare Worker + Descope + Neon. The existing Worker is `dnd-custom-aid-api`; Wave 5 deployment and real owner/provider QA are already verified. Do not redeploy for documentation-only work.

An agent without authenticated provider capability stops alternate connection probing after establishing the boundary, completes safe repo/CI work, and gives one exact owner-action packet. Never request or persist secrets.

## Combat, audit and backup direction

Combat remains local-first with exactly one authoritative DM device; explicit resume/handoff advances an authority generation so stale prior authority cannot retake control. Full verifiable server backup/export remains MVP. History is meaningful grouped domain history/recovery, not exhaustive event sourcing.

## Security/proportionality

External-service budget is USD $0. Repository is intentionally public. Preserve fail-closed/object-level authorization, least privilege, replay/idempotency safety, stale-write/tombstone guarantees and error/log hygiene.

Do not add generalized realtime/WebSockets, queues, arbitrary ACL frameworks, generic sync/CRDT, microservices or enterprise observability without concrete approved need.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities; investigate deliberately rather than running forced audit fixes.

## Current implementation organization

```text
completed baseline/shared/hosted foundations
-> completed Wave 4 Player <-> Server
-> completed Wave 5 Desktop + Campaign Administration
-> NOW Wave 6 reusable/persistent content architecture
-> Wave 7 Managers
-> DM Live Workspace
-> combat exchange/handoff
-> SRD clarification
-> backup/operator completion
-> integrated owner QA
```

Use `docs/checkpoints/LATEST.md` for the exact practical continuation.