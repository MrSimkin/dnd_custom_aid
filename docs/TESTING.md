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
- UI/component tests where valuable;
- end-to-end/instrumented tests for critical workflows;
- CI checks for merge candidates.

This is a quality framework, not a requirement to create every test type for every feature.

## 3. Phone, tablet and desktop/laptop verification

Because Android phone and tablet support are approved requirements, user-visible mobile features must eventually be checked against representative configurations for both form factors.

The exact Android device/API matrix is **pending** and should be chosen after the architecture/minimum Android version decision.

For responsive/adaptive mobile UI work, verification should record at minimum whether relevant layouts were checked in representative compact and larger-screen configurations.

Desktop/laptop administration is also an approved MVP surface. Once its implementation form is selected, relevant administration workflows must be verified in representative supported desktop/laptop environments. The exact operating-system/browser/runtime matrix remains pending until that architecture choice is approved.

Cross-surface workflows—such as shared campaign data, character visibility, encounter preparation and synchronization—should be verified end-to-end across the applicable surfaces when those capabilities exist.

## 4. Feature acceptance criteria

Every substantial user-visible feature should have explicit acceptance criteria before it is called complete.

Acceptance criteria should describe observable behavior rather than implementation details.

Example format:

- Given [starting state]
- When [user action]
- Then [observable result]

Detailed feature-level criteria may be authored incrementally with each implementation slice rather than exhaustively during product discovery.

## 5. Regression rule

When fixing a reproducible defect, add an automated regression test when practical. If no automated regression test is practical, document why and record the manual verification used.

## 6. Test failures

A failing test is project state, not chat trivia.

If a branch is handed off with known failures, `docs/PROJECT_STATE.md` must identify:

- failing command/test;
- relevant error summary;
- whether the failure existed before the change;
- current diagnosis if known;
- next recommended action.

## 7. Manual verification

Manual device/emulator/browser/desktop checks may be necessary for UI, accessibility, interaction, layout, or platform-specific behavior. They should complement, not silently replace, reasonable automated coverage.

Record manual verification in concise form, including the configuration used when relevant.

## 8. Release readiness

A concrete release checklist will be created once the distribution path and architecture are approved. The MVP boundary is already approved; the future checklist may include build reproducibility, regression status, data safety, privacy/security, accessibility, supported mobile/desktop coverage, offline/recovery behavior, synchronization, and packaging/signing checks as applicable.
