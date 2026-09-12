# Phase 4A — P6 direct-drag reorder implementation CLOSED

Date: 2026-09-11 / 2026-09-12 CI completion
Branch: `implementation/phase4a-successor-cycle`
Status: **IMPLEMENTATION CLOSED / READY FOR OWNER QA IN THE REPAIRED BUILD**

## Authority

This checkpoint closes implementation of the already-approved P6 repair contract. It does not supersede the P6 design decision checkpoint or the consolidated owner-phone QA evidence.

Accepted P6 interaction contract remains:
- direct long-press + drag on the item/card body;
- child controls retain their normal actions and are excluded from pickup regions;
- actual spatial/row-major reorder where the normal layout is multi-column;
- one durable final-order transaction on successful drop;
- no separate reorder mode or `Done` step;
- manual order remains explicit; A–Z/other presentation orders do not silently overwrite it;
- search/filter states disable reorder where those collection controls exist;
- subtle lifted/reflow/haptic feedback;
- PC Settings tab order follows the same interaction grammar;
- responsive implementation applies to phone and tablet surfaces, without claiming physical tablet acceptance.

## Shared implementation foundation

The repair uses stable-id final-order transactions, a single-owner reorder coordinator, measured item geometry, lifted overlay support, optional auto-scroll, accessibility/semantic reorder actions, and group constraints where a collection has independent reorder domains.

Relevant foundation commits in the P6 sequence include:
- `ed6dcac37624d1ce9f1e20cd5f33ca6e1492602c` — reorder transaction policy;
- `9f1f65c232ec6ddb5eef4098fd15d866224bd58c` — transaction policy tests;
- `711dea38c141fa52c5aca1527447fe4969c12b0f` — hardened reorder session;
- `f923b5943fe70ca44c38f747c93f0708cc7e78a7` — hardened interaction modifiers;
- `f2814894bd600157691f2648398ac25c09af8452` — lifted overlay host;
- `bc3a4adc8202444c2f0a65797dacb9adf2a7fe69` — constrained/grouped reorder-session support.

## Reachable Player manual-order surfaces audited

### PC Settings tab order

Active route:
`CharacterPcSettingsClosureV4.kt` → `TAB_ORDER` → `CharacterTabOrderDragSettingsV4(...)`.

Result:
- direct long-press drag;
- lifted overlay;
- one final commit;
- no reachable up/down controls;
- Table Mode keeps the structural order page read-only.

The older `CharacterTabOrderSettingsV4()` arrow-based helper still exists as dormant/unrouted source in `CharacterPcSuccessorSettingsV4.kt`; it is not the active PC Settings route and therefore is not a reachable P6 product surface. It is deliberately not deleted as part of this repair-only cleanup.

### Equipment

Migrated to the hardened spatial reorder path.
- manual/search/filter gating preserved;
- responsive multi-column placement preserved;
- final order commits once on drop.

Relevant implementation commits include `c214b158…` and `36adff86…`.

### Notes

Migrated to the hardened direct-drag path.
Relevant implementation commit: `06ad46b1…`.

### Techniques / Metamagic / Pacts

The shared class-option module now uses stable-id subset transactions and the hardened reorder session, preserving unrelated option positions.
Relevant implementation commit: `3198ae61…`.

### Artifice

Migrated to the hardened final-order/subset reorder path.
Relevant implementation commit: `8e920be5…`.

### Forms

Migrated to direct long-press drag with stable-id final-order commit, lifted overlay and auto-scroll.
Relevant implementation commit: `74c02833…`.

### Combat

Migrated to the hardened P6 path while keeping operational controls separate from pickup behavior.
Relevant implementation commit: `773f1278…`.

### Spells

Completed:
- manual order remains per spell level;
- source-filtered reorder preserves hidden spell positions;
- cross-level movement is prevented structurally;
- legacy repeated threshold writes removed from the active spell-list path;
- auto-scroll/lifted overlay retained;
- one final transaction applies on drop.

Supporting commits:
- `723ae6d32ee9022f44ba259aba01c0be0bedb8c3` — atomic spell manual-order helper;
- `988fe43774244b1017c1ca115baf8d147239c229` — spell-list migration.

Validation:
- workflow `34660678265`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

### Spell-source manager

Migrated independently from the spell list:
- direct drag;
- edit/delete controls outside pickup;
- one source-order commit on drop.

Commit:
- `58f6ff45386d2f9ac00e595ca6eab276226f4d53`.

Validation:
- workflow `34660760220`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

### Traits

Completed:
- removed the active separate `Reordenar` / `Listo` interaction mode;
- retained the normal responsive multi-column trait layout during reorder;
- direct 2D drag;
- grouping domains constrain valid placement;
- unrelated group positions remain canonical;
- operational use/recovery controls stay outside pickup;
- one grouped final-order transaction on drop.

Supporting commits:
- `42be755ecdaa46d7b5bdf77360d60d8e2e3e7dbd` — grouped trait order policy;
- `0ad04af7a107bcaaecfe0d0f30473894b9b26e50` — Traits UI migration.

Validation:
- workflow `34660997245`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

### Custom skills — active PC Settings manager

The audit corrected an earlier false target: `CharacterCustomSkillsV4.kt` is not the live structural editor. The active route is PC Settings → `Habilidades personalizadas` → `CharacterCustomSkillsSettingsV4(...)`.

Completed:
- stable-id direct long-press reorder in the active manager;
- pickup is restricted to the primary skill body;
- Edit/Delete remain independent child actions;
- only final `sortOrder` is rewritten on drop;
- ability mappings, training and every non-order field are preserved;
- Table Mode disables structural reorder.

Commit:
- `b3d0d885ecf5962b3bc5e03729f41cf69d2d6808`.

Validation:
- workflow `34661616937`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

### Companions

The final residue sweep found this as a real live P6 miss: the active Companions module still used `characterMeasuredReorderDragV4(...)` and repeated `moveCharacterCompanionManual(...)` writes during drag.

Completed:
- replaced repeated threshold movement with the hardened stable-id reorder session;
- direct long-press pickup is limited to the primary companion body;
- Favorite / Duplicate / Delete remain independent child controls;
- lifted overlay and auto-scroll follow the same one-column pattern already validated for Forms;
- Manual/A–Z/search/filter behavior is preserved;
- one normalized companion order is committed only on successful drop.

Commit:
- `914345bd09fae86051bbe34f976a1a87ecc1e85c`.

Validation:
- workflow `34662090966`: backend SUCCESS; Kotlin build/tests SUCCESS; debug APK upload SUCCESS.

## Audit classifications that intentionally did not become new P6 UX

The audit did **not** interpret every `sortOrder` field as a manual-order feature.

Examples such as custom attributes, custom markers, management resources, conditions and temporary effects currently use stable insertion/display ordering without exposing a user manual-order control. They were therefore not given a new drag interaction merely because they store an order value.

Likewise:
- `CharacterCustomSkillsV4.kt` is an older/non-active projection/editor path and was not modified after the active route was identified;
- older threshold-based helpers remain defined in `CharacterCardInteractionV4.kt`, but the audited reachable manual-order Player surfaces above no longer depend on them for their active reorder UX;
- dormant legacy helpers are a code-cleanup concern, not a reason to expand this repair or claim a reachable P6 defect where none exists.

## Full-app P6 audit conclusion

For the reachable Player surfaces identified as manual-order collections or explicit move/reorder settings:
- no active dedicated reorder mode remains where P6 requires direct drag;
- no active PC tab up/down UI remains in the routed settings page;
- manual reorder is disabled under search/filter where applicable;
- child operational/edit controls remain separate from pickup regions;
- spatial/group constraints are preserved where required;
- persistent state changes occur as final-order transactions rather than repeated writes during pointer movement.

The P6 implementation audit is therefore **CLOSED**.

## Acceptance boundary

This is an implementation/automated-validation closure, **not owner visual/interaction acceptance**.

P6 still requires representative owner QA in the repaired build. Physical tablet QA remains **UNTESTED / DEFERRED** under P17; this checkpoint makes no tablet PASS claim. The implementation nevertheless follows the shared responsive contract for phone and tablet surfaces.

## Prior progress checkpoint

Earlier implementation progress/correction record:
`docs/checkpoints/2026-09-11_PHASE4A_P6_REORDER_IMPLEMENTATION_PROGRESS.md`

Progress-document commits:
- `41dd6bba624d3c84a07139e86e6eb5ef1ae45efd`
- `6d214ff917996471ac86e545606d70e5a0cc28d8`

## Branch state immediately before this closure document

`914345bd09fae86051bbe34f976a1a87ecc1e85c`
