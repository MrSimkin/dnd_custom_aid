# Project State — global repository navigation

**Last verified:** 2026-09-14 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Validated convergence branch:** `integration/mvp-baseline-convergence`  
**Semantic convergence commit:** `5bed85cbb3e86ae63eac79149fadc5e56e61b256`  
**Convergence Actions:** `34917259324` / #1694 — **SUCCESS**  
**Canonical trunk after promotion:** `main`  
**Historical Player successor:** `implementation/phase4a-successor-cycle` at `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

## 1. Current authority/topology

The former two-authority state has been reconciled on the dedicated convergence branch.

The merge deliberately preserves:

- Player runtime, SQLDelight migrations, Player tests/guards and Player implementation evidence from the successor line;
- current integrated product/architecture/governance from `main`;
- current PC Sheet PDF-export/template direction from `main`;
- historical Player checkpoints needed for traceability.

After promotion of the validated convergence branch, **`main` is the single normal integrated-MVP development trunk**. The old Player successor remains historical/frozen evidence and should not receive ordinary new development.

Do not recreate permanent Player/Server/Desktop mainline silos. Use short-lived outcome-oriented branches from current `main` and integrate frequently.

## 2. Convergence verification

Semantic convergence commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

GitHub Actions run `34917259324` / #1694 completed successfully with:

- all permanent Player guard scripts;
- `:shared:desktopTest`;
- `:androidApp:assembleDebug`;
- `:desktopApp:build`;
- Android debug APK artifact upload;
- backend `npm install` + `npm run check`.

This is the validated integrated technical baseline. It does **not** retroactively convert historical Player physical QA into a PASS.

Historical frozen Player candidate remains:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- run `34801612526` / #1630 — SUCCESS;
- targeted cross-device physical revalidation was pending at that historical boundary.

## 3. Controlling integrated-MVP product direction

The approved product is one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Current controlling decisions define:

- integrated Player/Server/DM architecture and paper/local/server authority;
- full DM Desktop product and authoring/management surfaces;
- exact integrated-MVP boundary and owner-vs-technical implementation governance;
- complete cross-surface PC Sheet PDF export.

In ordinary conversation prefer descriptive names such as “the PDF export decision” rather than repeatedly referring to numeric decision IDs unless the exact repository reference matters.

## 4. Protected integrated-MVP scope

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

## 5. Current technical baseline

The mature asset now present in the integrated baseline is the Player/shared Kotlin + SQLDelight implementation, including its migrations, backup serialization, tests and guard scripts.

Current hosted/Desktop reality remains deliberately early:

- backend: health-only TypeScript Cloudflare Worker scaffold;
- hosted PostgreSQL migrations: scaffold-only;
- Desktop: Compose Multiplatform placeholder shell.

Preferred delegated engineering direction remains:

- Ktor Client for shared Android/Desktop HTTP;
- small versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic expected revisions;
- SQLDelight outbox + scoped push/pull synchronization;
- Neon serverless driver from Worker initially;
- explicit SQL migrations;
- hosted PC current state as versioned JSONB snapshot plus relational authorization/index/public-projection metadata;
- Descope identity proof plus application-owned domain authorization;
- R2 Standard as current object-storage recommendation, pending owner/service activation;
- versioned app-owned JSON import/export;
- versioned on-demand full backup archive with manifest/integrity data;
- one canonical PC/export snapshot + shared PDF-export semantics with platform-specific rendering.

Exact low-level implementation choices remain delegated unless they create a material owner-level product/security/privacy/cost/lock-in consequence.

## 6. Exact next implementation package

Proceed to the **Shared Integrated-MVP Spine**.

Required semantic foundation:

- global account/identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object IDs;
- monotonic revisions and stale-write rejection;
- deletion/tombstone/non-resurrection semantics;
- Personal/Campaign/System-or-Official scope semantics where valid;
- provenance for independent copies;
- basic audit/sync metadata and invariant tests.

Do not pre-model every Monster/NPC/Zone/Encounter/Combat/SRD field in this first package and do not force everything into a giant universal `SyncEntity` abstraction.

After the shared spine, continue into hosted foundation and Player↔Server integration in dependency order.

## 7. Owner-vs-technical responsibility

The owner decides product behavior/workflow, visibility/privacy, MVP-vs-later scope, destructive/safety behavior and meaningful cost/security/convenience/lock-in tradeoffs.

Technical agents decide routine schema/table layout, type decomposition, endpoint/request shapes, migration mechanics, sync structures, serialization, PDF rendering internals, tests and package/branch granularity.

Do not pause for ceremonial owner approval of routine engineering.

## 8. External-service boundary

No external provider was activated by convergence.

R2 activation, provider project secrets or similar account/service steps should be requested from the owner only when implementation actually reaches the relevant dependency. Secrets must remain outside Git and should be configured through secure provider/runtime mechanisms.

## 9. Release/acceptance status

The project remains development/debug and is not release-ready. Integrated implementation authorization does not fabricate historical physical acceptance or replace future integrated owner-facing QA.
