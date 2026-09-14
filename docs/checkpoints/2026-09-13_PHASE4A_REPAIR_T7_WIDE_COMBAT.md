# Phase 4A — T7 wide Combat adaptive composition repair

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical baseline:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Product commit:** `5f00bc006a26600a5d03582fbfbdf2e266259673`  
**Final steady-state HEAD:** `e567750a529238a2b45722b6f5ed726dd9123d88`  
**Status:** T7 IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## Scope

This bounded family repairs the tablet/wide Combat composition defect documented by P17 without changing the frozen physical candidate.

The active path is `CharacterCombatSuccessorV4.kt`. Before T7, the successor Combat screen reused the narrow one-dimensional composition on wide/tablet layouts: attack/action cards lived in one `LazyColumn` and expanded to the full available width. The repair deliberately changes only the wide composition while preserving the proven narrow-phone path.

## Implemented behavior

### Narrow / phone path preserved

When `wide == false`:

- Combat continues to use `CharacterReorderSessionV4`;
- `CharacterReorderOverlayHostV4` remains the lifted-card host;
- attack/action entries remain a one-column `LazyColumn` using `itemsIndexed`;
- existing narrow reorder, edit, favorite, delete and persisted-order behavior remain on the same interaction path.

### Wide / tablet composition

When `wide == true`:

- the persistent operational Combat HUD is centered and bounded with `widthIn(max = 840.dp)` instead of stretching indefinitely across the display;
- death saves, section header and empty state remain full-width list sections;
- only the attack/action collection becomes an adaptive `CharacterSpatialGridV4`;
- Combat consumes the repaired T3 responsive-card semantics through `constrainedCardColumnsV4(wide = true, phoneMax = 1, wideMax = 3)`;
- effective columns can therefore reduce automatically under insufficient width, larger text scale or spacing pressure rather than promising a fixed count.

The explicit Combat maximum is three wide columns because these cards can contain damage/effect text and operational action controls. The T3 helper remains responsible for reducing that maximum when the real layout cannot safely support it.

### Spatial reorder / persistence

Wide Combat reuses the stabilized Round 2 `CharacterSpatialReorderStateV4` foundation rather than introducing another drag system.

Both narrow and wide paths commit through one shared `commitEntryOrder(...)` function using `applyCharacterReorderResult(...)`; final entries continue to persist `sortOrder` in canonical order.

The wide path uses:

- stable spatial bounds registration;
- spatial preview order;
- stable drag-target behavior inherited from T1;
- spatial auto-scroll translation;
- direct lifted-card visual tracking.

During a wide spatial drag, card-body edit, favorite, Edit and Delete interactions are disabled so the drag gesture cannot accidentally trigger a structural/operational child action.

## Scope purity

Verified product commit `5f00bc006a26600a5d03582fbfbdf2e266259673` changed exactly one product file:

`androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatSuccessorV4.kt`

No storage/schema/import/export model was changed.

A temporary idempotent migration writer was used only to make the bounded CI transformation auditable. It was deleted before the authoritative steady-state run. The normal Scaffold is read-only again.

## Permanent guard

`scripts/check_player_wide_combat.py` remains in the normal Scaffold and verifies:

- the active successor Combat path is the repaired target;
- the narrow one-dimensional reorder/list path remains present;
- the wide path uses T3-driven adaptive columns;
- wide Combat uses the shared spatial reorder state, viewport, auto-scroll and grid;
- the operational HUD is deliberately bounded on wide layouts;
- narrow and wide reordering converge on one canonical persisted-order commit path;
- spatial dragging suppresses favorite/edit/delete interactions;
- the reused responsive/spatial foundations retain their minimum-width and stable-geometry contracts.

## Automation evidence

### Writable bounded validation

Workflow `Scaffold checks`, run `34797216805` / #1590 — **SUCCESS**.

The transformed source passed:

- bounded T7 migration;
- all pre-existing permanent Player guards;
- the new T7 wide-Combat guard;
- backend type-check;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload;
- verified product commit.

This run produced product commit `5f00bc006a26600a5d03582fbfbdf2e266259673`.

### Authoritative clean steady-state proof

- Workflow: `Scaffold checks`
- Run ID: `34797403737`
- Run number: `1592`
- Head: `e567750a529238a2b45722b6f5ed726dd9123d88`
- Conclusion: **SUCCESS**

The final tree had the temporary writer removed and the workflow restored to read-only operation. It passed:

- backend install/type-check;
- compact Player geometry guard;
- reorder stability guard;
- checkbox consistency guard;
- spellcasting-bootstrap guard;
- class-editor-controls guard;
- application-settings semantics guard;
- wide-Combat composition guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

Artifact:

- ID `10330505672`;
- name `dnd-custom-aid-debug-apk`;
- size `13,651,587` bytes;
- digest `sha256:582d455aea9dad0f0898fa43368d6bd69e6ed8994914522008ada85abe46c4c9`.

## Physical status

T7 is **not physically PASS**. On the future consolidated candidate, targeted revalidation should verify at minimum:

1. tablet landscape Combat no longer stretches each attack/action card across the full display;
2. the operational Combat HUD remains usable and deliberately bounded;
3. adaptive multi-column composition responds appropriately to density/text pressure;
4. wide multi-column reorder does not target-chase or jump during preview movement;
5. wide reorder auto-scroll behaves correctly where applicable;
6. final order persists after leave/reopen;
7. favorite/Edit/Delete remain usable normally and do not fire accidentally during drag;
8. one narrow-phone Combat sanity check confirms the one-column path remains behaviorally intact.

Do not replay the full historical phone/tablet suites.

## Next bounded family — T8 Table Mode

T8 is an implementation/affordance repair, not an unresolved product-design choice. The shared `CharacterTableModePolicy` already defines the contract: structural character/configuration editing is disabled in Table Mode while genuine operational/session actions remain enabled.

Next implementation must audit the major tab boundaries and ensure structural Edit/Add/Delete/reorder affordances are hidden or clearly disabled/read-only while preserving allowed HP/current/spent/session controls. Add focused policy/UI guards/tests and pass the normal read-only Scaffold before checkpointing.

Phase 4A remains OPEN. The frozen physical baseline remains `preqa.12`. DM implementation remains blocked until explicit Phase 4A owner closure.
