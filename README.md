# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current state and next action;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer;
5. `docs/BRANCH_STATUS.md` — branch interpretation/cleanup status;
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
- `implementation/phase4a-successor-cycle` is the focused continuation branch for the active Phase 4A successor cycle and is aligned with `main` at night-close boundaries.
- Historical implementation branches are not competing current state.
- Obsolete non-frozen `tmp/*` refs may be archived/removed from the visible branch list; explicitly frozen QA-evidence branches remain immutable.

D-0066 established that **canonical does not mean release-ready**. Development work may be consolidated into `main` before owner acceptance when the owner explicitly approves that repository-ordering boundary.

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

## Current implementation reality

Phases 0–3 are complete. Phase 4A Character Foundation Closure remains open, but the successor repair/refinement cycle has now completed **increments A–E of nine**.

Completed successor areas include:

- additive schema/domain/storage foundation and migration/backup compatibility;
- custom attributes and optional saves;
- per-source spellcasting configuration;
- Custom Markers + Resource placement/recovery foundations;
- explicit phone/tablet/orientation layout context;
- shared compact toolbar, drag, IME, numeric and contextual-help primitives;
- character-first startup and Back hierarchy;
- PC Settings administration, tab order, custom skills/attributes/Markers and haptics;
- compact General projections and inline standard/custom Habilidades;
- structured Combat damage and character-aware Dice flow;
- compact Gestión with canonical Inspiration/Markers/Resources, mixed rest recovery, conditions infrastructure and concentration help.

Increment E's fully wired automated gate is green:

- validation commit `0587db5e65d89e809f138e83d053903659216886`;
- workflow `34307068166` — SUCCESS;
- artifact `10087074946` / `dnd-custom-aid-debug-apk`;
- ZIP digest `55bba08d09918a3f6102e4e2694da50c0ee5bb6e9bf3b1ac4c8849f9203ac50`.

This is **development verification, not owner visual acceptance**.

## Owner-audition status

The last owner-auditioned practical build remains:

- `0.4.0-preqa.7` / build `40700` / `debug`;
- product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Its owner phone audition did not pass visual acceptance; it generated the repair backlog now driving the successor cycle.

Primary owner test device: **Redmi Note 11 Pro 5G**.

Physical tablet acceptance remains pending and the old tablet/wide presentation is itself a redesign target.

## Current next action

Resume at **Increment F — Conjuros compact source-context redesign**.

Protected direction:

- one compact sticky source-context bar rather than stacked permanent source/filter blocks;
- selected source visibly owns ability, `CD salv. conjuro` and `Mod. ataque mágico`;
- `Todos los conjuros` remains compact;
- expanded filters/source details are transient;
- useful level/slot context remains without erasing the spell list;
- phone landscape remains a phone interaction model and must keep practical spell content visible.

After F, produce the planned early targeted Redmi portrait/landscape interaction build and audition before continuing blindly.

Four planned increments remain after the completed A–E boundary: **F, G, H and I**.

**DM feature implementation remains blocked until Phase 4A is later repaired through the remaining successor increments, accepted through the required owner gates, and explicitly closed.**

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
