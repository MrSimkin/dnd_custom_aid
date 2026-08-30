# Testing and Verification

## Current status

There is no application code or build system yet, so there are no executable project tests at this stage.

This document defines how testing information must be recorded once implementation begins.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation change should state:

- what was tested;
- how it was tested;
- what passed;
- what failed;
- what was not tested;
- relevant environment/device information when it matters.

## 2. Future automated verification layers

The exact tools depend on the approved architecture, but the project should aim to cover appropriate layers such as:

- compilation/build checks;
- static analysis/lint;
- unit tests for business logic;
- data/persistence tests;
- synchronization/reconciliation tests;
- authorization/permission tests;
- UI/component tests where valuable;
- end-to-end/instrumented tests for critical workflows;
- CI checks for merge candidates.

This is a quality framework, not a requirement to create every test type for every feature.

## 3. Phone, tablet and desktop/laptop verification

Because Android phone and tablet support are approved requirements, user-visible mobile features must eventually be checked against representative configurations for both form factors.

The exact Android device/API matrix is **pending** and should be chosen after the architecture/minimum Android version decision.

For responsive/adaptive mobile UI work, verification should record at minimum whether relevant layouts were checked in representative compact and larger-screen configurations.

Desktop/laptop DM administration is also an approved MVP surface. Once its implementation form is selected, relevant administration workflows must be verified in representative supported desktop/laptop environments. The exact operating-system/browser/runtime matrix remains pending until that architecture choice is approved.

Desktop testing must reflect the approved asymmetric scope: MVP verification does **not** require player desktop behavior, Android/desktop feature parity, or desktop combat tracking unless a later decision adds those features.

Cross-surface workflows—such as shared campaign data, character visibility, encounter preparation and synchronization—should be verified end-to-end across the applicable surfaces when those capabilities exist.

## 4. Multicampaign verification

Because multicampaign is part of the MVP, testing must cover relevant isolation and selection behavior once implemented, including as applicable:

- multiple campaigns can coexist and remain independently usable;
- one account can participate in multiple campaigns concurrently;
- campaign selection/switching opens the intended campaign context;
- campaign-scoped roles and permissions do not leak across campaigns;
- Kick/Ban/Freeze PC affects only the intended campaign/character scope;
- a campaign ban does not affect unrelated campaign membership;
- global account freeze is evaluated separately from campaign moderation;
- campaign-scoped characters/NPCs/encounters/history remain associated with the correct campaign;
- invitation codes/links enroll only into their owning campaign.

## 5. Local-first combat and synchronization verification

The combat tracker is the most important live-table MVP validation surface and needs explicit failure/reconnection testing.

Once implemented, representative tests should verify as applicable:

- DM combat changes commit locally before requiring server acknowledgement;
- active combat survives app close/reopen and device restart on the same DM device;
- the DM can continue combat during network loss;
- sync status distinguishes at least locally saved, synchronized and waiting-to-sync states;
- reconnecting cannot replace newer authoritative DM combat state with an older remote snapshot;
- reusable monster/NPC definitions or saved encounter edits do not silently mutate an already-running live encounter;
- combat changes do not automatically mutate persistent character sheets and persistent sheet edits do not silently rewrite active combat;
- player devices receive only the approved public combat projection;
- an offline player may maintain provisional local tracker/view changes without becoming authoritative;
- after reconnection, provisional player state yields to/reconciles to authoritative DM state and cannot overwrite it;
- simultaneous authoritative DM editing from multiple devices is not accidentally enabled by ordinary sync behavior.

If explicit future DM-device transfer/resume is implemented, it requires its own authority-handoff tests; that behavior is not part of the MVP baseline.

## 6. Character paper/digital workflow verification

Where relevant, character workflow tests should verify:

- last-updated/freshness information reflects the latest saved/reconciled digital state;
- digital state is not presented as proof that unobserved paper-only changes do not exist;
- using the app as the active sheet works without requiring a paper copy;
- end-of-session/deliberate reconciliation creates the new durable digital baseline;
- no automatic paper/digital merge is implied or attempted;
- Save and Export remain distinct, including the approved unsaved-export warning path.

## 7. Rules/content verification

Where relevant, tests should verify:

- campaign data can contain mixed D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew content without legality rejection;
- the application does not enforce SRD legality merely because the rules assistant uses SRD sources;
- MVP rules clarification is grounded only in supported official SRD material;
- source/version provenance distinguishes SRD 5.1 from SRD 5.2.1 when relevant;
- campaign house rules are not silently injected into MVP clarification answers;
- complete monster stat blocks can be represented/displayed while individual action/trait mechanics remain formatted text where no deeper structure exists.

## 8. Feature acceptance criteria

Every substantial user-visible feature should have explicit acceptance criteria before it is called complete.

Acceptance criteria should describe observable behavior rather than implementation details.

Example format:

- Given [starting state]
- When [user action]
- Then [observable result]

Detailed feature-level criteria may be authored incrementally with each implementation slice rather than exhaustively during product discovery.

## 9. Regression rule

When fixing a reproducible defect, add an automated regression test when practical. If no automated regression test is practical, document why and record the manual verification used.

## 10. Test failures

A failing test is project state, not chat trivia.

If a branch is handed off with known failures, `docs/PROJECT_STATE.md` must identify:

- failing command/test;
- relevant error summary;
- whether the failure existed before the change;
- current diagnosis if known;
- next recommended action.

## 11. Manual verification

Manual device/emulator/browser/desktop checks may be necessary for UI, accessibility, interaction, layout, network-loss/reconnection behavior, or platform-specific behavior. They should complement, not silently replace, reasonable automated coverage.

Record manual verification in concise form, including the configuration used when relevant.

## 12. Release readiness

A concrete release checklist will be created once the distribution path and architecture are approved. The MVP boundary is already approved; the future checklist may include build reproducibility, regression status, data safety, privacy/security, accessibility, supported mobile/desktop coverage, multicampaign isolation, offline/recovery behavior, synchronization/reconciliation, backup/export behavior, and packaging/signing checks as applicable.
