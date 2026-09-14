# Recent Decision Index — D-0068 through D-0071

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

Consolidates the expanded integrated-MVP direction. Important consequences include:

- next build targets the real Player + Server/shared + DM MVP, not an isolated DM/server mini-product;
- paper/local/server authority model is explicit and no formal technical Game Session object is required;
- synchronization/revisions/idempotency/tombstones/conflicts/freshness are consolidated;
- remembered login, campaign-scoped role switching and practical permissions are defined;
- PC default campaign-visible identity is limited to name + portrait/avatar + current controlling Player/display identity;
- object storage is required but provider remains Pending;
- meaningful history/recovery and full server backup/export are MVP foundations;
- explicit DM combat authority resume/handoff to another device is now MVP because Desktop must replace an unavailable tablet;
- DM Desktop App becomes both a full operational DM client containing all approved Desks and the richer authoring/management/system-administration client;
- required Desktop authoring includes at least Monster, NPC, Homebrew Rules, Zone and Encounter creator/manager surfaces;
- SRD storage/provenance foundations begin early, while the official-SRD retrieval + grounded AI feature is implemented last among the substantial user-facing features of the cycle;
- work is organized through coordinated Player/Server/shared/Desktop/live-exchange/SRD workstreams and dependency waves;
- the next major owner-facing QA is intended to exercise the complete integrated MVP.

D-0071 explicitly supersedes older MVP wording where it conflicts about desktop combat/fallback, explicit DM-device handoff/resume, and the next-cycle integrated implementation philosophy. It does not supersede paper-first play, the assistant/non-VTT boundary, local-first DM combat authority, proportionality, or official-SRD-only MVP AI clarification.

## Current exact continuation

Read `docs/checkpoints/LATEST.md`.

The current discussion resume point is:

1. **7D — detailed DM Desktop App product definition**;
2. **7E — exact outside-MVP boundary**;
3. derive final Git/development topology and implementation gates;
4. obtain explicit coding authorization.

No code implementation was authorized merely by the D-0071 documentation checkpoint.
