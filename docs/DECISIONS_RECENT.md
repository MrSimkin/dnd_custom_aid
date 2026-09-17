# Recent Decision Index — D-0068 through D-0075

Detailed decision records under `docs/decisions/` are authoritative; this file is navigation only.

## D-0068 — DM live Workspace, Desk model and Dungeon direction

**Status:** Approved current product/design direction  
**Detailed record:** `docs/decisions/D-0068_DM_LIVE_WORKSPACE_DESKS_AND_DUNGEON_DIRECTION.md`

Persistent Campaign Workspace, flat Desk model, no formal technical Session object, Dungeon/Zone live direction, clocks/counters, Encounter Readiness and dirty-live improvisation lifecycle.

## D-0069 — DM Desk family, Stage Desk and DM Screen

**Status:** Approved  
**Detailed record:** `docs/decisions/D-0069_DM_DESK_FAMILY_STAGE_DESK_AND_DM_SCREEN.md`

Approves DM Screen, Stage Desk, Dungeon Desk and Combat Desk plus retrieval/orientation semantics.

## D-0070 — Shared Player/DM rules question capability

**Status:** Approved  
**Detailed record:** `docs/decisions/D-0070_RULES_QUESTION_SHARED_PLAYER_DM_CAPABILITY.md`

Natural-language rules clarification is shared by Player and DM; MVP corpus remains official SRD 5.1 / 5.2.1 only.

## D-0071 — Integrated MVP architecture

**Status:** Approved current product/architecture direction  
**Detailed record:** `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`

Controls the integrated Player + hosted/shared + DM architecture, paper/local/server authority, bounded project-specific sync, Desktop fallback, combat handoff, reusable content, history/recovery and backup direction.

## D-0072 — DM Desktop product and authoring/management surfaces

**Status:** Approved  
**Detailed record:** `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`

Defines Live/Workspace, Prepare/Manage and Administration; Personal -> Campaign explicit independent-copy/provenance semantics; Monster, NPC, Homebrew/Rules, Place/Zone, Encounter, PC, Campaign, System and Media/Handout surfaces.

## D-0073 — Integrated MVP boundary and implementation governance

**Status:** Approved  
**Detailed record:** `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`

Protects integrated-MVP scope, dependency waves and owner-vs-technical responsibility. Wave 6 is reusable/persistent content architecture; Wave 7 is rich authoring Managers. Routine implementation details are delegated.

## D-0074 — PC Sheet PDF export

**Status:** Approved  
**Detailed record:** `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`

Defines cross-surface PC Sheet PDF export behavior and visual families.

## D-0075 — zero-budget provider policy and owner guidance

**Status:** Approved / controlling  
**Detailed record:** `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`

Hard external-service budget USD $0; current provider revalidation; intentionally public repository; secret hygiene; object-storage provider selection deferred; owner guidance contract.

## Current exact continuation

Read `docs/checkpoints/LATEST.md` and its referenced checkpoint.

Wave 5 is complete/integrated through PR #44. The next normal implementation direction is Wave 6 reusable/persistent content architecture. Reuse the existing Shared scope/provenance/revision/tombstone spine and establish local reusable-content persistence before Wave 7 Manager UI. Do not repeat provider activation or Wave 5 deployment/QA without new evidence.