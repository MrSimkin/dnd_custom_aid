# Testing and Verification

## Current status

Phases 0–3 are complete. Phase 4 Character Foundation Closure is current on `implementation/phase4-character-closure`.

The previous correction APK/artifact `9876725270` is historical evidence only and is no longer the acceptance target. The next manual QA target must be a newly frozen closure APK with exact commit/workflow/artifact identity.

The schema-6 closure prototype checkpoint `89aad12a094476c7b6798f6f0626bf978a5d0831` passed GitHub Actions run `33779104922`. The later documentation head `997340eadf0f7b3cd648d04932dca45c4c5434bd` also passed run `33782368536`. Green CI is technical evidence, not owner acceptance.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation batch should state:

- what was tested;
- how;
- what passed/failed;
- what was not tested when material;
- relevant device/environment information when material.

Automated verification and manual real-device acceptance are separate gates.

## 2. Standard automated verification

### Kotlin / Android / Desktop / SQLDelight

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Current CI uses JDK 17, Gradle 9.5 and Android SDK platform 36.

### Backend

```bash
cd backend
npm install
npm run check
```

The established full gate runs both surfaces even when one area is unchanged, unless a smaller intermediate batch gate is explicitly documented.

## 3. Current Phase 4 gate strategy

The closure is intentionally split into recoverable batches under:

`docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`.

For shared/schema batches, protect the material data risks:

- schema create-from-scratch;
- migration from relevant earlier schema versions;
- preservation of existing campaign/character data;
- repository round trips for new durable domains;
- derived-value regressions where relevant;
- Desktop compilation to catch shared API breakage;
- Android assembly.

For Android UI batches, use proportionate automated tests for state/helper logic plus Android assembly. Real IME, visual density, drag feedback, orientation and tablet usefulness still require real-device QA.

## 4. Migration rules for the closure

The owner must be able to install the future closure APK over the currently used Phase 4 development APK lineage without losing existing data.

The current plan deliberately keeps the already-tested schema-6 prototype migration and adds the remaining durable D-0047 state through schema 7 rather than rewriting migration history.

Minimum migration regression coverage before the final candidate:

- prior owner-APK schema -> current;
- schema 5 -> 6 -> 7;
- existing campaigns/characters;
- Background including Raza and Religión / Fe;
- Combat entries;
- Equipment/currencies;
- Traits;
- Spells/sources/prepared/shared slots;
- Notes;
- class rows with safe default subclass/provenance state;
- new fields initialized safely;
- module hide/show settings never delete module records.

## 5. Intended-device rule for this closure

C-0010 still applies, but D-0047 makes the current character closure explicitly a **phone + tablet acceptance package**.

Therefore final owner acceptance requires:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. a representative larger application text scale where practical.

Tablet is **not secondary** for this closure. The APK must materially exploit available width where appropriate instead of merely stretching phone components.

## 6. Global real-device invariants

Every user-visible closure batch should preserve or move toward:

- keyboard/IME action reachability;
- Android Back hierarchy;
- screen-off/recreation state;
- save/reopen persistence;
- portrait/landscape sanity;
- phone/tablet responsiveness;
- app text scaling;
- accessible touch targets;
- D16 parent/list/tab/search/filter/sort/selection context preservation where technically feasible;
- no automatic D&D legality/rules enforcement.

## 7. Final closure QA focus

The final pass is a focused closure acceptance matrix rather than a verbatim rerun of every historical check.

It must cover at least:

- update-in-place migration/data preservation;
- all editors with IME visible;
- consistent actions and unsaved-change guard;
- sorting/search/filter/drag behavior;
- lifecycle/module/haptic/Supercompact/Table-mode PC Settings;
- Gestión, Resources, Rest, Conditions, Concentration and effects;
- General/Habilidades/Combat improvements;
- compact Equipment/Monedas and containers/consumables;
- Traits/Spells/Notes/Background regressions;
- Artífice/Formas/Técnicas/Metamagia/Pactos/Compañeros;
- multiclass module union and manual override/hide-not-delete;
- Supercompact visual usefulness on phone/tablet;
- own-format backup/import round trip;
- rotation, screen-off/on, full app close/reopen and representative larger text.

## 8. Development APK signing

Development CI APKs use a stable **debug-only** signing identity so successive QA APKs can update one another in place. This exists specifically to test realistic SQLite migration/persistence behavior on owner devices.

The development identity is not a production/release trust boundary and is never to be reused for a real release. A future real release signing identity must remain private and be designed/handled separately.

CI currently reconstructs the development debug keystore from tracked development-only material before building. Do not expose/reproduce that material in chat or docs merely because it is non-production.

## 9. CI proportionality

Use the existing simple GitHub Actions workflow as a safety gate, not a deployment platform.

Do not add coverage gates, emulator farms, SonarQube, staging, automated production deployment or giant screenshot suites without a concrete requirement.

## 10. Regression rule

When fixing a reproducible defect, add a focused automated regression test when practical/useful. Otherwise record the exact manual verification used.

## 11. Current exact continuation

Housekeeping is followed by Batch A1 catalog reconciliation, then A2 schema 7. Each batch gets its own checkpoint/gate before dependent work proceeds.

No DM-feature testing or implementation begins until the Phase 4 character closure exit gate is satisfied.
