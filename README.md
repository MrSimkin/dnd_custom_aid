# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current state and next action;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md` — controlling current Player QA findings;
6. `docs/BRANCH_STATUS.md` — branch interpretation/cleanup status;
7. `docs/DECISIONS.md` plus relevant detailed records under `docs/decisions/`;
8. `docs/CONVENTIONS.md`;
9. `docs/PRODUCT.md`;
10. `docs/ROADMAP.md`;
11. `docs/WORKFLOW.md`;
12. `docs/ARCHITECTURE.md`;
13. `docs/TESTING.md`;
14. relevant current checkpoints/feature files.

## Canonical source of truth

- `main` is the canonical repository baseline under D-0066.
- Git is the project's operative memory.
- Repository files, not chat memory, determine durable project truth.
- `docs/PROJECT_STATE.md` is the authoritative current-state snapshot.
- `docs/checkpoints/LATEST.md` is the exact resume pointer.
- `implementation/phase4a-successor-cycle` is the active continuation branch carrying the current Phase 4A Player implementation, `preqa.8 / 40800` QA evidence, and the acceptance-repair discussion line.
- Historical implementation branches are not competing current state.
- Obsolete non-frozen `tmp/*` refs may be archived/removed; explicitly frozen QA-evidence branches remain immutable.

D-0066 established that **canonical does not mean release-ready**. Repository ordering and owner acceptance are separate decisions.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/UX/game-semantic/data/privacy/service/compatibility choices.

Meaningful work must be explained and persisted in Git. C-0009 remains controlling: this is a personal/small-scale project, so use the simplest safe implementation that satisfies real requirements and do not import enterprise machinery without a concrete reason.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Android targets **phone and tablet**, portrait and landscape.
- Desktop DM administration: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where offline/local behavior provides real value.
- Desktop MVP direction: **Save locally + explicit Sync** when hosted sync is implemented.
- Hosted database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- HTTP/request-response and simple polling/refresh come before realtime infrastructure.
- Provider replaceability means sensible code locality, not provider-abstraction frameworks.

See `docs/ARCHITECTURE.md` for the full record.

## Current Player implementation reality

Phases 0–3 are complete. Phase 4A Character Foundation Closure remains open.

The entire planned successor sequence **A–I is implemented and automated-green**, followed by a post-A–I stabilization pass. There is no planned Increment J.

The current technically verified Player build is:

- `0.4.0-preqa.8` / build `40800` / `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — SUCCESS;
- artifact `10134364621` / `dnd-custom-aid-debug-apk`;
- APK SHA-256 `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

This is **not** an accepted or release-ready build.

## Current owner QA status

A consolidated real-device owner QA pass was completed on **build 40800** using the Redmi Note 11 Pro 5G.

The build passed important boundaries including update-in-place/data preservation, persistence, normal portrait navigation, representative editor/IME behavior, currency, Conjuros portrait, Notas, Application Settings functionality, representative conditional modules, backup/export, phone-landscape retention of the phone interaction model, and representative larger text.

It also exposed Phase 4A acceptance blockers. The authoritative detailed record is:

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`

Physical tablet QA is intentionally deferred until shared/systemic defects found during phone QA are repaired. Shared defects must be repaired across phone and tablet where they share state/components/primitives, but tablet PASS/FAIL remains untested until a repaired build is physically audited.

Build `40700` is historical evidence only. It is **not** the current QA target or resume point.

## Current next action

Do **not** restart old QA, do **not** begin a speculative Increment J, and do **not** begin DM implementation.

The immediate Player task is **design reconciliation of the 40800 QA findings, one point at a time**.

For each QA point:

1. discuss the exact observed problem and intended behavior with the owner;
2. resolve only the design/interaction details needed for that point;
3. once the owner closes the point, consolidate the decision durably in the repository;
4. move to the next QA point;
5. do not implement the repair pass until the discussion sequence has produced an accepted bounded repair specification, unless the owner explicitly changes that instruction.

After the discussion pass, define and implement the bounded Phase 4A acceptance-repair package, validate it, produce a monotonic successor QA build, retest affected phone boundaries, then perform physical tablet portrait/landscape QA before freezing a replacement M6 candidate and explicitly closing Phase 4A.

**DM feature implementation remains blocked until explicit Phase 4A closure.** DM design/discovery documentation may continue independently when the owner chooses, but it is not implementation authorization.

## Build and verification commands

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install --no-package-lock
npm run check
```

Current CI uses JDK 17, Gradle 9.5, Android SDK platform 36 and Node.js 22.

See `docs/TESTING.md` for verification rules.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise real migrations. It is not a production/release identity and must never be reused for a real release.
