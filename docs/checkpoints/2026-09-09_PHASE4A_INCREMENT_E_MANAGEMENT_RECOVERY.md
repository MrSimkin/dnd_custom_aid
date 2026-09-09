# Phase 4A — Increment E: Gestión, Markers, Resources and recovery

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** COMPLETE / AUTOMATED GATE GREEN  
**Active Gestión wiring source commit:** `4b3ab53faada5af7b50f73ce951fe767c13ff63a`  
**Authoritative integrated validation commit:** `0587db5e65d89e809f138e83d053903659216886`  
**Authoritative workflow:** `34307068166` — SUCCESS

## Scope completed

Increment E implements the planned Gestión/live-state family without merging logically distinct domain concepts.

### E1 — compact operational state

- `Estado operativo` is replaced by a compact fixed surface rather than the prior full-height card.
- PG/current/temp HP remain canonical `CharacterSheet` state.
- Death saves use one compact horizontal row when current PG is zero.
- Inspiration uses the same canonical value already projected in General and respects the per-character visibility preference.
- If General contains unsaved HP draft changes, Gestión explicitly shows saved-vs-draft coherence information instead of silently persisting unrelated General edits.

### E2 — Custom Markers

- Custom Markers remain logically distinct from Resources.
- Gestión projects the same persisted Marker values configured from PC Settings.
- Binary, counter and current/max interactions use compact controls appropriate to their configured type.

### E3 — Resources across domains

- Resource successor configuration controls value kind and presentation placements.
- Gestión displays only Resources whose canonical configuration includes `MANAGEMENT` (legacy/unconfigured resources retain the compatibility MANAGEMENT default).
- Resource editing supports placement among General / Gestión / Equipo / Rasgos from one resource identity/value.
- Generic `Fuente` is not exposed in the successor Resource editor; legacy stored source text is retained for compatibility rather than destroyed.

### E4 — cross-domain rest recovery

Shared operations provide one typed recovery preview/apply engine for Resources and Custom Markers while preserving distinct identities.

- Resource/Marker target identity includes kind + UUID, preventing collisions.
- automatic recovery occurs only from explicit structured recovery rules;
- manual/free-text recovery remains review-only;
- ambiguous `TO_MAX` without a known maximum remains review-only;
- binary/current-max values are clamped to their valid bounds;
- only explicitly selected automatic proposals are applied;
- the previous Resource-only API remains available as a compatibility wrapper over the shared engine.

Focused tests cover mixed previews, collision safety, clamping, selected-only application, placement independence and legacy/manual semantics.

### E5 — conditions and concentration

- custom conditions remain available;
- successor condition editor no longer exposes a new generic free-text `Fuente` field;
- condition catalog infrastructure supports stable key, source identity and contextual help;
- the approved predefined catalog is intentionally empty until an approved corpus is supplied;
- this architecture can later host supported official/SRD entries and the owner's Sandy Petersen Cthulhu Mythos family without copying proprietary Spanish text now;
- concentration surfaces explain the standard check reference through the shared contextual-help mode: Constitution save, DC 10 or half damage received, whichever is higher, while keeping table variants non-automated.

## Automated evidence

### Shared trackable/recovery foundation

- source/tests through commit `87e4a213da42f19ba45ca001270e7a1006241fe2`;
- workflow `34305427239` — SUCCESS.

### Standalone successor Gestión compile/build

- source commit `3c5be8d499d176c37496df59ed5ae1219e4a572b`;
- workflow `34306095086` — SUCCESS;
- artifact ID `10086746569`;
- artifact digest `sha256:8210319be7a508d2eef8192dc68c5ff89041530a502bc3cc1ded13a96b831559`.

### Active wiring

The first exact-string activation guard failed harmlessly because whitespace differed; it made no source change. The corrected exactly-one-match guard:

- matched exactly one legacy Gestión call;
- replaced it with `CharacterManagementSuccessorTabV4`;
- passed `git diff --check`;
- verified successor call count one and legacy call count zero;
- committed active wiring as `4b3ab53faada5af7b50f73ce951fe767c13ff63a`;
- removed its temporary workflow in the same commit.

### Final integrated gate

Workflow `34307068166` — **SUCCESS** on validation commit `0587db5e65d89e809f138e83d053903659216886`.

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10087074946`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:55bba08d09918a3f6102e4e2694da50c0ee5bb6e9bf3b1ac4c8849f9203ac50`.

## Acceptance boundary

Increment E is technically complete. This remains a development/debug boundary, **not owner-device visual acceptance**. The compact operational footprint, death-save row, mixed rest flow and card density remain targeted owner-audition items in the successor interaction build.

## Next increment

Increment F — compact Conjuros source-context redesign and the early targeted phone portrait/landscape owner retest.
