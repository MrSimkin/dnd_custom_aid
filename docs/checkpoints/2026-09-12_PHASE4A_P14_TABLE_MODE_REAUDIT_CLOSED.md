# Phase 4A — P14 Table Mode Re-audit Closed

Date: 2026-09-12

## Status

P14 implementation re-audit is complete at source level.

- Authoritative repaired source candidate: `13a2e0ef5475ef78500330a4b84a947ea1b4307a`
- Normal automated closure gate: **PENDING** at creation of this checkpoint. This checkpoint commit is intentionally used to trigger the normal `Scaffold checks` workflow against the same production source plus documentation only.
- Owner/device QA: **PENDING**.
- `main`: untouched.
- DM-side implementation: not started.

P14 must not be considered automation-closed until the normal Scaffold gate is observed GREEN.

## Authority preserved

This re-audit follows the accepted P14 contract in:

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P14_CLOSED.md`

The contract remains:

- requesting Table Mode must not become a dead/disabled action merely because structural drafts are dirty;
- dirty activation must review pending structural edits before changing mode;
- review must offer Save and activate / Discard and activate / Cancel;
- structural editing must be blocked at callback/persistence boundaries, not only visually;
- intentional live/session actions remain usable and persist immediately;
- structural PC configuration is unavailable while Table Mode is active;
- the classification must hold app-wide, including hidden callback paths.

## Re-audit findings

The first visible repair supplied the three-way activation transition, but the broader audit found additional contract violations:

1. the transition warning was generic and did not expose the required meaningful pending-change list;
2. several structural draft callbacks remained callable without the Table Mode guard, including equipment, currencies, traits and provenance;
3. spellcasting enablement and lifecycle/status persistence were still structurally writable from PC Settings paths;
4. spell-slot spend, equipment quick-use and trait-use counters were presented as operational actions but were mutating edit drafts rather than canonical live state;
5. Management reused one whole-sheet callback for both resource-definition edits and live resource-value changes;
6. PC Settings still exposed structural configuration while Table Mode was active;
7. successor-state persistence could accept a whole proposed state from an operational surface;
8. background navigation handlers remained active during the Table Mode review transition.

These were treated as real P14 defects rather than optional polish.

## Repairs

### Activation transition

The Table Mode control remains requestable when structural drafts are dirty.

Dirty activation now opens a bounded, scrollable review that includes:

- representative scalar `before → after` values;
- concise domain/collection summaries for larger edits;
- Save and activate;
- Discard and activate;
- Cancel.

The existing canonical save pipeline is reused, including the required-number confirmation path. Cancelling that secondary confirmation cannot activate Table Mode accidentally.

Discard resets every tracked structural draft to its persisted counterpart before activation.

Background Back handlers are disabled while the transition review is active.

### Shared persistence boundaries

`CharacterTableModePolicy.kt` now contains explicit operational merge boundaries.

`mergeCharacterOperationalState(...)` preserves the persisted structural `CharacterSheet` definition and accepts only live/session values:

- current HP;
- temporary HP;
- Inspiration;
- death-save successes/failures;
- spell-slot spent count while preserving persisted slot level/total;
- inventory quantity while preserving item definition;
- trait spent uses while preserving trait definition;
- resource current value while preserving resource definition.

`mergeCharacterOperationalClosureState(...)` accepts only Table/session controls such as conditions, concentration, exhaustion, temporary effects, haptics, reconciliation checkpoints and Table Mode itself while preserving structural closure configuration.

`mergeCharacterOperationalSuccessorState(...)` accepts only persisted custom-marker live values and Inspiration visibility while preserving marker definitions, custom attributes/skills, ordering, origins, provenance and other successor configuration.

### Android callback classification

Structural guards were added to remaining editor draft paths for:

- equipment items;
- currencies;
- equipment structural draft;
- traits;
- trait provenance;
- spellcasting enablement;
- lifecycle/status.

Operational persistence was centralized through the shared operational merge. Whole-sheet operational callbacks can therefore no longer overwrite structural fields.

Management now separates structural resource-definition persistence from operational live-value persistence.

Equipment quick-use persists live quantity immediately.

Trait use persists live spent-use count immediately.

Spell-slot use persists live spent count immediately.

Operational persistence synchronizes only the corresponding live projections back into edit drafts instead of replacing unrelated pending structural edits.

H1 module closure writes are routed through the structural closure guard as defense in depth.

### PC Settings

When Table Mode is active, structural PC settings are disabled/unavailable for:

- spellcasting enablement;
- lifecycle/status;
- tab ordering;
- custom attributes;
- custom skills;
- custom-marker configuration;
- module configuration.

A previously remembered structural subpage is forced back to the main PC Settings page while Table Mode is active.

Intentional Table/presentation actions remain available, including turning Table Mode off, haptics, Inspiration visibility, Supercompact, Application Settings and backup/export.

### Successor-state protection

`CharacterPcSettingsContextV4` now receives the closure repository and checks the persisted Table Mode state before successor-state persistence. While Table Mode is active, proposed successor changes are reduced through the operational successor merge before saving.

## Tests

Added:

`shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterTableModePolicyTest.kt`

Coverage proves that:

- live `CharacterSheet` values cross the operational boundary while structural overwrite attempts are rejected;
- closure session state crosses while structural closure configuration is preserved;
- custom-marker live value and Inspiration visibility cross while successor definitions/configuration remain unchanged.

## Source commits

- Initial transition repair: `ffca5fd10143bf5b7ec9650ab07da27382e4a06a`
- App-wide boundary hardening: `9c0893a0b5840ad3336c8b7939e5230cf740a340`
- Final transition navigation guard: `13a2e0ef5475ef78500330a4b84a947ea1b4307a`

Temporary repair helpers used during implementation are absent from the final source tree.

## Gate to close P14 automation

Observe the normal Scaffold workflow triggered by this documentation-only checkpoint commit.

Required evidence before declaring automated P14 closure:

1. backend/type-check success;
2. exact Kotlin/shared gate success:
   `gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace`;
3. Android debug assemble success;
4. APK artifact upload success.

If the gate fails, repair only the demonstrated defect and re-run the same authoritative gate.

## Remaining acceptance

Even after an automated GREEN gate, P14 still requires owner/device QA as part of the final repaired-build acceptance. Automation must not be represented as physical-device acceptance.
