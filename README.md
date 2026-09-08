# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current state and next action;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/BRANCH_STATUS.md` — how to interpret the many historical branch refs;
6. `docs/DECISIONS.md` plus relevant detailed records under `docs/decisions/`;
7. `docs/CONVENTIONS.md`;
8. `docs/PRODUCT.md`;
9. `docs/ROADMAP.md`;
10. `docs/WORKFLOW.md`;
11. `docs/ARCHITECTURE.md`;
12. `docs/TESTING.md`;
13. relevant current checkpoints/feature files.

## Canonical source of truth

- `main` is the canonical current project baseline.
- Git is the project's operative memory.
- Repository files, not chat memory, determine durable project truth.
- `docs/PROJECT_STATE.md` is the authoritative current-state snapshot.
- `docs/checkpoints/LATEST.md` is the exact resume pointer.
- Old implementation/tmp branches are historical evidence and must not be treated as competing current state; see `docs/BRANCH_STATUS.md`.
- Frozen QA-evidence branches remain immutable historical evidence.

D-0066 explicitly consolidated the current in-progress Phase 4 development state into `main` before formal QA/closure. **Canonical does not mean release-ready.** The current product is still a debug/pre-QA build with known defects.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/UX/game-semantic/data/privacy/service/compatibility choices.

Meaningful work must be explained and persisted in Git. C-0009 is controlling: this is a personal/small-scale project, so use the simplest safe implementation that satisfies real requirements and do not import enterprise machinery without a concrete reason.

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

## Current implementation reality

Phases 0–3 are complete. Phase 4A Character Foundation Closure contains the broad character foundation described by D-0047, including:

- persistent campaign-scoped characters and multiclass identity;
- General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros and Notas;
- structured proficiencies/languages, defenses/senses/movement, conditions/exhaustion, concentration, resources, rest support, temporary effects and death saves;
- richer equipment, traits and spell workflows plus Favorites/Quick Access;
- conditional Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros modules;
- PC Settings, Application Settings, Supercompact and Table mode;
- local backup/export and import-as-copy;
- schema/migration coverage through the current character closure line.

The implementation is **not accepted as finished UX**. The September 2026 owner phone audition found substantial cross-cutting issues in density, editor/IME behavior, card interaction, landscape/wide behavior, tablet/wide design, and several information-architecture/terminology areas. Those findings are canonical input for the next repair cycle.

## Latest technically verified product build

Latest full automated-gate product identity:

- version `0.4.0-preqa.7`;
- build `40700`;
- type `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Later commits through the D-0066 consolidation are documentation/governance changes only unless a newer checkpoint explicitly says otherwise.

Build `40700` is **not** a frozen formal M6 candidate and is not release-ready.

## Current next action

The owner is compiling additional observations outside the formal QA exercise so they can be included in the same upcoming development cycle.

Do not start a broad repair from partial remembered observations. First read `docs/checkpoints/LATEST.md`, collect the remaining owner input, reconcile it with the existing audition backlog, then create a new focused repair branch from canonical `main`.

**DM feature implementation remains blocked until Phase 4A is later repaired, accepted through the required owner gates, and explicitly closed.**

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
