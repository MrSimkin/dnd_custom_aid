# Hosted/sync foundation checkpoint — 2026-09-15

**Verified trunk before this documentation refresh:** `main` at `734477b4e276810de1581dbc2d0a8458ad953f85`  
**Post-merge validation:** Actions run `34979121449` — **SUCCESS**  
**Integrated-MVP implementation authorization:** **GRANTED**

## 1. Current operational truth

The integrated baseline convergence is complete and `main` is the sole normal implementation trunk.

Wave 2 — Shared Integrated-MVP Spine — is implemented and integrated. The repository now contains the shared account/campaign/membership/role/PC-authority/scope/revision/tombstone/provenance/sync-metadata foundation needed by later hosted and client work.

Wave 3 — Hosted foundation — is **IN PROGRESS** and already materially implemented. PRs #15–#21 established, in dependency order:

- the shared integrated-MVP spine;
- hosted `/v1` API/auth/domain foundations;
- hosted PostgreSQL schema/contracts and CI validation;
- shared Android/Desktop HTTP transport contracts;
- a durable SQLDelight hosted outbox;
- atomic local campaign creation plus hosted delivery with idempotent retry semantics;
- authenticated account/campaign bootstrap back into the local spine with revision/tombstone conflict protection.

PR #21 merged as `734477b4e276810de1581dbc2d0a8458ad953f85`. Its post-merge run `34979121449` passed backend checks, hosted database contracts, shared/Kotlin tests, Android build, Desktop build, APK upload and all preserved Player guard scripts.

## 2. Current sync behavior established

Campaign creation is local-first. Local campaign creation and durable outbox enqueue are atomic. Hosted delivery reuses the same mutation identity across retries. Confirmed idempotent replay is acknowledged as success; transient/auth failures preserve local data and the outbox entry; permanent conflicts are not silently discarded.

Authenticated hosted bootstrap imports the current account and hosted campaign membership state into the existing local spine. It preserves locally newer revisions and tombstones, rejects same-revision divergent campaign state, and does not infer membership removal status merely because a campaign is absent from a list response.

## 3. Exact next implementation package

Continue Wave 3 with **explicit hosted campaign/membership lifecycle and scoped change semantics**.

The next package should make membership/removal changes explicit enough that clients can distinguish real hosted lifecycle events from simple absence in a current-list/bootstrap response. It should remain project-specific and should not become a generalized sync/event platform.

After that, continue in dependency order toward hosted PC snapshot/current-state sync and the Wave 4 Player↔Server end-to-end path.

## 4. External-provider activation gate

**No external provider account or project must be activated yet.** Local contracts, migrations, transport, reconciliation and CI can continue without creating provider resources early.

The first provider activation gate is the first package that needs a **real authenticated end-to-end hosted development environment**, after the remaining local hosted campaign/membership change semantics are stable and before remembered Player authentication/real hosted PC sync is wired into the owner-facing Player flow.

At that gate, activate only the providers actually needed for the first real hosted round trip:

1. **Cloudflare** — development Worker/API runtime.
2. **Neon** — development PostgreSQL database.
3. **Descope** — development authentication/identity project.

**Cloudflare R2 is not part of that first activation gate.** Activate R2 later when Media/Handouts/assets actually reach object-storage integration.

Provider setup rule:

- accounts/resources should remain owner-controlled;
- start with development/test resources, not a production environment;
- choose plan/region/project settings deliberately at activation time rather than creating speculative resources now;
- secrets/tokens/connection credentials must never be committed to Git;
- runtime secrets belong in provider/runtime secret stores or local ignored development configuration;
- public/non-secret identifiers may be documented when useful;
- before any account/resource activation that has cost, privacy, security or lock-in consequences, present the owner with the exact choices and required actions.

## 5. Repository rule

Branch from current `main`, use short-lived outcome-oriented branches, validate proportionately, and reintegrate early. Historical Player and convergence branches remain evidence only.

Do not restart old Player repair work from historical pending language, and do not treat this technical progress as retroactive physical acceptance of the historical Player candidate.
