# Phase 4A — preqa.8 — P16 Round 1

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** P16 ROUND 1 ACCEPTED / DESIGN OPEN  
**Implementation authorization:** NOT YET

## P16 — landscape / fixed-sticky vertical-space policy

The owner accepted the first P16 round in full. The defect is transversal and is classified **FULL APP AUDIT REQUIRED**.

The repair must not be implemented as scattered `landscape` exceptions. Persistent/fixed/sticky UI must react to **actual usable vertical height** and to the **combined persistent footprint** of the screen.

### Accepted decisions

1. Persistent UI reacts to actual usable vertical height, not simply to orientation labels.
   - Account for available viewport after system bars/navigation and, where relevant, IME/keyboard.
   - A shallow split-screen portrait viewport may require the same compaction behavior as phone landscape.
   - A tall landscape tablet need not receive the same compromises as a shallow phone.

2. Use automatic progressive compaction rather than a user-facing landscape-mode setting.
   - Normal usable height: normal compact persistent presentation.
   - Reduced usable height: tighter/reflowed persistent presentation.
   - Severely constrained height: nonessential persistent regions cease being persistent or move into scrollable content.
   - This is separate from the P10 global density preference; users must not have to change global density to repair a bad viewport.

3. Combate must exploit available horizontal width to reduce persistent HUD height.
   - P5 remains authoritative for HUD content and operational grammar.
   - In shallow/wide contexts, high-frequency data may reflow into fewer horizontal rows, conceptually:
     `CA 18 | Inic. +4 | Vel. 30 ft (9 m) | PV 37/52 | Temp. 8`
     followed by the compact `Daño | cantidad | Curar` row.
   - CA, initiative and speed remain permanently available as required by P5, but the persistent HUD must consume the minimum reasonable vertical height.
   - Death saves remain outside the persistent HUD as previously decided.

4. Conjuros spell-level headers become conditionally sticky.
   - Enough usable vertical room: sticky level headers are allowed.
   - Too little usable vertical room: level headers scroll normally with content.
   - The current unconditional sticky-header behavior is therefore not acceptable across all viewport heights.

5. Persistent layers are governed by one total vertical-space budget.
   - Header, tabs/navigation, contextual toolbar, HUD, sticky section headers and similar layers cannot independently reserve height without regard to the remaining content viewport.
   - Repair/audit must evaluate the **combined persistent footprint**, not only each component in isolation.
   - A screen is unacceptable when technically small individual persistent components combine to leave an impractically small primary-content viewport.

6. Preserve the phone interaction/navigation model in phone landscape.
   - Do not solve shallow-height problems by automatically switching a landscape phone to tablet navigation/side rail.
   - Compact/reflow persistent content and use horizontal space intelligently while retaining the phone interaction model.
   - Existing tablet-specific navigation decisions remain governed separately by responsive layout rules.

## Evidence / root cause noted during design

The current shared layout context primarily exposes coarse form-factor/orientation categories (`PHONE_PORTRAIT`, `PHONE_LANDSCAPE`, `TABLET_PORTRAIT`, `TABLET_LANDSCAPE`) rather than a reusable usable-height budget. Conjuros currently uses unconditional sticky headers for spell levels. Combate keeps its operational card entirely above the scrollable attacks/actions list. These structures explain why portrait can remain usable while shallow landscape becomes dominated by persistent UI.

## Audit classification

**FULL APP AUDIT REQUIRED.**

The repair pass must inventory all Player surfaces containing fixed, sticky, pinned, persistent, top-of-scroll, permanently reserved, or viewport-consuming regions and verify them against:

- actual usable width/height;
- combined persistent footprint;
- minimum useful content viewport;
- progressive compaction/reflow behavior;
- legitimate persistence priority;
- phone/tablet navigation-model boundaries;
- interaction with P5 fixed-HUD rules, P9 adaptive editors and P10 density.

This round does **not** yet close P16; the next round may freeze priority/order behavior for severely constrained viewports and other remaining edge semantics.
