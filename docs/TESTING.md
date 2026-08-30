# Testing and Verification

## Current status

There is no application code or build system yet, so there are no executable project tests at this stage.

D-0043 defines the initial testing attitude: protect the failures that would materially hurt this personal project, keep CI small, and rely on practical real-device/manual verification for visual UX rather than building an enterprise test program.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation change should state concisely:

- what was tested;
- how it was tested;
- what passed or failed;
- what was not tested when that matters;
- relevant environment/device information when it matters.

## 2. Initial automated verification scope

At scaffolding/early implementation, automated tests should concentrate on:

- shared Kotlin domain logic where mistakes could corrupt or misrepresent state;
- SQLDelight schema/migration correctness;
- outbox, idempotent-mutation, revision/conflict and ordinary synchronization behavior that actually exists in MVP;
- DM live-combat sequence/authority behavior;
- consequential Cloudflare backend authentication/authorization and synchronization behavior;
- compilation/build checks for Android, desktop and backend code.

This is deliberately **not** a requirement to automate every UI interaction, every feature, or every implementation detail.

## 3. CI

Use one simple GitHub Actions workflow for relevant pushes/pull requests. It should perform the practical Kotlin/Gradle and TypeScript/backend build/test checks available at that stage.

CI is a safety check, not a deployment system.

Initial CI does **not** require:

- coverage-percentage gates;
- SonarQube or similar enterprise quality tooling;
- Android emulator/device farms;
- large API-version matrices;
- staging infrastructure;
- automatic production deployment;
- automated release/installer publishing before it provides real value.

## 4. Android device verification

The approved Android minimum is **Android 11 / API 30** under D-0042.

Android UX is a priority, so visual/layout/interaction work should be checked manually on the actual relevant phone and tablet devices whenever practical. Representative compact/phone and larger/tablet layouts matter more than hypothetical support for obsolete Android versions.

Automated UI tests may be added for a workflow when they solve a concrete regression problem; they are not a blanket requirement.

## 5. Desktop verification

The DM desktop client is native Kotlin + Compose Multiplatform Desktop. Verify the actual supported desktop environment(s) used by the project and the keyboard/mouse workflows that matter.

Desktop synchronization tests should reflect the approved simple model:

- **Save** persists locally even when offline;
- **Sync** sends pending local changes and retrieves applicable remote changes;
- failed Sync does not lose local saved work;
- a later Sync can retry pending changes safely.

MVP verification does **not** require player desktop behavior, Android/desktop feature parity, desktop combat tracking, or a continuous background-sync service.

## 6. Multicampaign verification

Relevant implementation must verify campaign isolation and selection, including as applicable:

- multiple campaigns coexist independently;
- one account can participate in multiple campaigns;
- campaign-scoped roles/permissions do not leak;
- character/NPC/encounter/history data remains attached to the correct campaign;
- Kick/Ban/Freeze PC affects only its intended campaign/character scope;
- global account freeze remains separate from campaign moderation;
- invitations enroll only into their owning campaign.

## 7. Local-first synchronization and combat verification

These behaviors are high-value automated-test targets because failure can lose or corrupt useful state:

- local mutation and outbox persistence remain coherent where the outbox is used;
- duplicate/idempotent mutations do not apply twice;
- stale ordinary revisions are detected rather than blindly overwriting newer data;
- active DM combat survives same-device interruption/restart once that persistence exists;
- network loss does not prevent the DM from continuing combat;
- the DM combat sequence/version rejects delayed older updates and older hosted state cannot replace newer authoritative DM state;
- no authority-generation/handoff machinery is accidentally treated as an MVP requirement;
- reusable monster/NPC/template edits do not silently mutate an already-running live encounter;
- combat state and durable character-sheet state remain separate.

### Player offline combat convenience

Player offline behavior is intentionally tiny and should be verified as such:

- while offline, the player can locally advance the displayed turn and add/remove visible conditions;
- those local changes are never uploaded and never enter the sync outbox;
- they never affect DM-authoritative combat state;
- reconnecting replaces/discards the temporary local view with the latest DM public projection;
- durability across player-app restart is not required.

## 8. Character/PDF workflow verification

Where relevant, verify:

- last-updated/freshness reflects the latest saved/reconciled digital character state;
- Save and Export remain distinct;
- the approved unsaved-export warning path works;
- exporting unsaved values does not commit/save them;
- Android and desktop PDF export work locally/offline against the approved template mapping;
- paper-only changes are not falsely claimed to be observed or automatically merged.

## 9. SRD clarification verification

Where relevant, verify:

- retrieval is grounded in the supported official Spanish SRD 5.1 / SRD 5.2.1 corpus;
- source/version provenance is preserved in answers;
- campaign homebrew is not silently presented as official SRD content;
- ordinary PostgreSQL full-text retrieval is measured against real questions before adding vector/embedding machinery.

## 10. Infrastructure proportionality verification

The initial implementation should remain simple enough that verification does **not** assume infrastructure we deliberately deferred.

- ordinary HTTP/request-response and simple polling/refresh are the default;
- WebSockets, Durable Objects, queues, R2 or other Cloudflare services are tested only if a later concrete feature actually introduces them;
- provider-specific code may be localized, but tests should not require generalized provider-abstraction frameworks that do not exist.

## 11. Regression rule

When fixing a reproducible defect, add an automated regression test when that test is practical and useful. If it is not, record the manual verification used.

## 12. Test failures are project state

If a branch is handed off with known failures, `docs/PROJECT_STATE.md` must identify the failing check/command, a useful error summary, current diagnosis if known, and the recommended next action.

## 13. Proportionality

C-0009 controls this document as much as the rest of the architecture: testing exists to protect this application and its data, not to simulate the process requirements of a commercial enterprise product.
