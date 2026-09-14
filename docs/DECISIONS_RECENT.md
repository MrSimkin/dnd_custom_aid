# Recent Decision Index — D-0068 through D-0073

This file closes the indexing gap between the historical chronological `docs/DECISIONS.md` master log and the later detailed Approved decision records already stored under `docs/decisions/`.

**Authority rule:** the detailed decision records linked below are the authoritative text. This index is navigation only and does not replace their full rationale/contracts.

## D-0068 — DM live Workspace, Desk model and Dungeon Desk direction

**Status:** Approved current product/design direction, with explicitly marked Proposed/Pending items in the detailed record  
**Date:** 2026-09-10  
**Detailed record:** `docs/decisions/D-0068_DM_LIVE_WORKSPACE_DESKS_AND_DUNGEON_DIRECTION.md`

Establishes the DM Attention Budget, Campaign -> persistent Workspace -> flat Desk model, no formal technical Session object, multiple concurrent Desks, Dungeon Desk direction, topological/flowchart exploration model, rich Zone/Area Briefs, contextual Notes, Dungeon Turns beta/log, generic clocks/counters, Encounter Readiness/staging, dirty-live improvisation lifecycle and non-VTT/non-rules-engine boundaries.

## D-0069 — DM Desk family, Stage Desk and DM Screen direction

**Status:** Approved current product/design direction, with explicitly marked future/pending items in the detailed record  
**Date:** 2026-09-10  
**Detailed record:** `docs/decisions/D-0069_DM_DESK_FAMILY_STAGE_DESK_AND_DM_SCREEN.md`

Approves the current live DM Desk family:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

Defines DM Screen/Party Lens/reference direction, Stage Desk retrieval-first behavior, Places/Shops/NPC relations, Adventure/Scene Spine, Dungeon refinements and Combat Desk as both tracker and tactical-guidance assistant. Journey remains a future candidate rather than a fifth approved Desk.

## D-0070 — Shared Player/DM rules-question capability and provisional naming

**Status:** Approved  
**Date:** 2026-09-10  
**Detailed record:** `docs/decisions/D-0070_RULES_QUESTION_SHARED_PLAYER_DM_CAPABILITY.md`

Confirms that natural-language rules clarification is one shared capability available to both Player and DM experiences. `Quick Rules Question` is only a working/discovery label. MVP corpus remains official SRD 5.1 / SRD 5.2.1 only; broader house-rule-aware clarification remains later scope.

## D-0071 — Integrated MVP: Player + Server + DM Desktop/App architecture and execution direction

**Status:** Approved current product/architecture direction, with explicitly marked Pending items in the detailed record  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`  
**Checkpoint:** `docs/checkpoints/2026-09-14_MVP_INTEGRATION_ARCHITECTURE_CONSOLIDATION.md`

Consolidates the expanded integrated-MVP direction: Player + hosted/shared + DM as one product, paper/local/server authority, bounded project-specific sync, full DM Desktop operational fallback, explicit combat authority resume/handoff, object storage, meaningful history/recovery, full backup/export, and official-SRD clarification inside the integrated MVP.

D-0071 supersedes older wording where it conflicts about desktop combat/fallback, explicit DM-device handoff/resume, and the next-cycle integrated implementation philosophy. It does not supersede paper-first play, the assistant/non-VTT boundary, local-first DM combat authority, proportionality, or official-SRD-only MVP AI clarification.

## D-0072 — DM Desktop App product definition and authoring/management surfaces

**Status:** Approved  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`

Closes the detailed 7D Desktop product-design pass. Defines one Desktop workbench with Live/Workspace, Prepare/Manage and Administration; full live Desk parity with DM Android/tablet; common Personal -> Campaign -> Live content semantics; Monster Manager plus Creature Creator Assistant/import-export; Quick/Developed NPC helper; expanded Homebrew & Rules Manager including races/classes/backgrounds/feats/spells/items; Stage/Place/Scene Spine and Dungeon/Zone authoring; Encounter Manager; PC Manager/Audit; Campaign Manager; sole-admin System Administration; Media & Handouts; and cross-cutting search/sync/navigation facilities.

## D-0073 — Integrated MVP boundary, implementation governance and branch convergence direction

**Status:** Approved product/scope and implementation-governance direction  
**Date:** 2026-09-14  
**Detailed record:** `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`  
**Checkpoint:** `docs/checkpoints/2026-09-14_DM_DESKTOP_MVP_SCOPE_AND_IMPLEMENTATION_GOVERNANCE_CONSOLIDATION.md`

Closes 7E and the current implementation-planning pass. Protects the substantial integrated-MVP scope from later accidental trimming, records explicit deferred/generalized directions, establishes dependency-driven integration waves, defines the planned `main` + Player-successor convergence strategy, and records the owner-vs-technical responsibility boundary: the owner decides product/scope/user-facing risk; routine low-level implementation design is delegated unless it materially changes behavior, security/privacy, cost, lock-in or approved scope.

## Current exact continuation

Read `docs/checkpoints/LATEST.md`.

Current resume point:

1. the 7D Desktop product definition is closed;
2. the 7E integrated-MVP boundary is closed;
3. 8A implementation-wave strategy, 8B Git convergence strategy and 8C first shared-spine technical package direction are closed;
4. discussion intentionally pauses before expanding the next hosted-foundation technical package;
5. before product coding, verify branch state and execute the planned convergence under explicit coding authorization;
6. low-level technical package design is delegated and should not be repeatedly pushed to the owner for rubber-stamp approval.
