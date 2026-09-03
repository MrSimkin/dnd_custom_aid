# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

After reading this README, continue with these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented code areas.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/DECISIONS.md` — chronological significant-decision log; later detailed approved Phase 4 decisions under `docs/decisions/` also remain authoritative while the master log is being reconciled.
5. `docs/CONVENTIONS.md` — approved recurring project conventions.
6. `docs/PRODUCT.md` — current approved product direction and MVP.
7. `docs/ROADMAP.md` — development phases and current phase.
8. `docs/WORKFLOW.md` — how changes are designed, implemented, tested, documented, reviewed, and merged.
9. `docs/ARCHITECTURE.md` — current approved architecture record and rationale.
10. `docs/TESTING.md` — verification rules, commands, and current test status.
11. Relevant `docs/decisions/`, `docs/checkpoints/`, and feature-specific files for the active work.

## Canonical source of truth

- `main` is the canonical accepted project state (D-0007).
- Git is the project's operative memory (D-0012).
- Repository files, not chat memory, determine durable project truth.
- `docs/PROJECT_STATE.md` is the authoritative current-state/next-action snapshot.
- Discovery notes preserve exploratory reasoning but do not override approved product/decision records.
- Substantial in-progress work remains on focused branches until explicit owner merge approval.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/architecture choices.

Meaningful technical work must be explained. New durable conventions are discussed with the owner when they first arise, then recorded and followed consistently.

C-0009 is controlling: **this is a personal/small-scale project. Prefer the simplest safe solution that satisfies real requirements and do not add enterprise machinery without a concrete reason.**

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Android is explicitly designed for **phone and tablet**, portrait and landscape.
- Desktop DM administration: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where offline/local behavior provides real value.
- Desktop MVP: **Save locally + explicit Sync**; failed Sync never discards local work.
- Hosted database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, implemented in TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- HTTP/request-response and simple polling/refresh come before realtime infrastructure.
- Provider replaceability means sensible code locality, not provider-abstraction frameworks.

See `docs/ARCHITECTURE.md` for the full record.

## Current implemented foundation

Phases 0–3 are complete. Phase 4 has already implemented a substantial local Android character foundation beyond the original scaffold.

Current Phase 4 character work includes, among other things:

- campaign-scoped persistent characters with UUID identity and lifecycle state;
- multiclass entries, derived ability/skill/save/proficiency behavior and explicit adjustment escape paths;
- General, Habilidades, Combate, Equipo, Trasfondo, Rasgos, conditional Conjuros and Notas;
- PC Settings with spellcasting hide-not-delete behavior;
- persistent equipment/currencies, traits, notes, spells/sources/prepared state/shared slots and background identity fields;
- SQLDelight migrations and persistence regression coverage;
- an owner-tested correction APK lineage;
- a focused closure branch with preliminary schema-6 class/subclass/provenance, Inspiration/death saves, proficiencies, Weapon Mastery, Resources, Forms and Companions.

The current accepted closure scope is D-0047. It expands/fixes the complete character foundation before any DM-feature implementation begins.

## Current Phase 4 work

Focused branch:

`implementation/phase4-character-closure`

Primary execution documents:

- `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`;
- `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md`;
- `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md`;
- `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`.

The closure work is intentionally divided into recoverable batches. Every meaningful batch leaves a Git checkpoint and passes its proportionate gate before dependent work proceeds.

**DM feature implementation is blocked until the Phase 4 character closure package is implemented, passes final phone + tablet QA, and is owner-accepted.**

## Build and verification commands

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install
npm run check
```

Current CI uses JDK 17, Gradle 9.5, Android SDK platform 36 and Node.js 22.

See `docs/TESTING.md` for the current gate/QA rules and exact verified revisions.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise real migrations. It is not a production/release identity and must never be reused for a release. See `AGENTS.md` and `docs/TESTING.md` for the governing distinction between non-secret development debug identity and protected release/private credentials.
