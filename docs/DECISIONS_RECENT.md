# Recent Decision Index — D-0068 through D-0075

This file indexes the later detailed Approved decision records under `docs/decisions/`.

**Authority rule:** the detailed decision records are authoritative. This file is navigation only.

## D-0068 — DM live Workspace, Desk model and Dungeon Desk direction

**Status:** Approved current product/design direction  
**Date:** 2026-09-10  
**Detailed record:** `docs/decisions/D-0068_DM_LIVE_WORKSPACE_DESKS_AND_DUNGEON_DIRECTION.md`

Establishes the DM Attention Budget, Campaign -> persistent Workspace -> flat Desk model, no formal technical Session object, concurrent Desks, Dungeon direction, Zone/Area Briefs, contextual Notes, Dungeon Turns beta/log, generic clocks/counters, Encounter Readiness and the dirty-live improvisation lifecycle.

## D-0069 — DM Desk family, Stage Desk and DM Screen direction

**Status:** Approved current product/design direction  
**Date:** 2026-09-10  
**Detailed record:** `docs/decisions/D-0069_DM_DESK_FAMILY_STAGE_DESK_AND_DM_SCREEN.md`

Approves the live DM Desk family: DM Screen, Stage Desk, Dungeon Desk and Combat Desk; defines Party Lens/reference direction, Stage retrieval-first behavior, Place/Shop/NPC relations, Scene Spine and Combat guidance direction.

## D-0070 — Shared Player/DM rules-question capability

**Status:** Approved  
**Date:** 2026-09-10  
**Detailed record:** `docs/decisions/D-0070_RULES_QUESTION_SHARED_PLAYER_DM_CAPABILITY.md`

Natural-language rules clarification is one shared Player/DM capability. MVP corpus remains official SRD 5.1 / SRD 5.2.1 only.

## D-0071 — Integrated MVP: Player + Server + DM Desktop/App architecture

**Status:** Approved current product/architecture direction  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`  
**Checkpoint:** `docs/checkpoints/2026-09-14_MVP_INTEGRATION_ARCHITECTURE_CONSOLIDATION.md`

Consolidates Player + hosted/shared + DM as one product, paper/local/server authority, bounded project-specific sync, full DM Desktop fallback, explicit combat authority resume/handoff, object storage, meaningful history/recovery, full backup/export and official-SRD clarification.

## D-0072 — DM Desktop App product definition and authoring/management surfaces

**Status:** Approved  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`

Defines the complete Desktop workbench: Live/Workspace, Prepare/Manage and Administration; DM live parity, personal/campaign/live content semantics, Monster/Creature, NPC, Homebrew/Rules, Stage/Place/Scene, Dungeon/Zone, Encounter, PC, Campaign, System Administration and Media/Handouts surfaces.

## D-0073 — Integrated MVP boundary and implementation governance

**Status:** Approved product/scope and implementation-governance direction  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`

Protects integrated-MVP scope, establishes dependency-driven implementation waves and records the owner-vs-technical responsibility boundary: owner decides consequential product/scope/risk; routine low-level implementation remains delegated unless it changes behavior, security/privacy, cost, lock-in or approved scope.

## D-0074 — PC Sheet PDF export product definition

**Status:** Approved  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`

Protects cross-surface PC Sheet PDF export and defines Classic/v1/v2 visual families, custom-stat completeness, design-specific extension families, portrait behavior, Permanent vs Current Snapshot, offline/static output, overflow/readability and optional complete Spellbook appendix.

## D-0075 — Zero-budget provider policy, provider revalidation and owner guidance

**Status:** Approved / controlling  
**Date:** 2026-09-15  
**Detailed record:** `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`

Makes the external-service operating budget **USD $0** a hard constraint; requires current verification of payment/overage/quota/region/lock-in behavior before provider activation; revalidates Cloudflare Workers + Neon PostgreSQL + Descope as the first hosted-development direction with explicit free-tier gates; keeps Workers AI conditionally at `$0`; defers object-storage provider selection; records that the GitHub repository is intentionally public; and defines the mandatory owner-guidance style for a technically oriented power user who is not a professional developer and wants clear, educational, step-by-step explanations with diagrams when useful.

D-0075 supersedes older wording that merely says to "consider cost" without recording the hard `$0` constraint, and supersedes any older statement treating `private: false` as an unexpected security/privacy discrepancy.

## Current exact continuation

Read `docs/checkpoints/LATEST.md` and the current checkpoint it references.

The first Cloudflare + Neon + Descope DEV activation has been completed and verified. If no later decision/checkpoint supersedes the current state, the next primary implementation package is **real authenticated Player <-> Server development integration** using the already active DEV environment.

Do not repeat provider activation, do not silently enable paid infrastructure, and do not infer that activation success closes later security hardening or physical owner acceptance gates.
