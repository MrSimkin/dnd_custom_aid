# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

After reading this README, continue with these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented code areas.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/checkpoints/LATEST.md` — stable current resume pointer; it names the exact active checkpoint/build and next action.
5. `docs/DECISIONS.md` — chronological significant-decision log; later detailed approved Phase 4 decisions under `docs/decisions/` also remain authoritative while the master log is being reconciled.
6. `docs/CONVENTIONS.md` — approved recurring project conventions.
7. `docs/PRODUCT.md` — current approved product direction and MVP.
8. `docs/ROADMAP.md` — development phases and current phase.
9. `docs/WORKFLOW.md` — how changes are designed, implemented, tested, documented, reviewed, and merged.
10. `docs/ARCHITECTURE.md` — current approved architecture record and rationale.
11. `docs/TESTING.md` — verification rules, current pre-QA review identity, and the formal owner-QA matrix that will apply after a replacement candidate is frozen.
12. Relevant `docs/decisions/`, `docs/checkpoints/`, and feature-specific files for the active work.

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

Phases 0–3 are complete. Phase 4 Character Foundation Closure implementation is complete through the pre-QA consolidation stage.

The current character foundation includes, among other things:

- campaign-scoped persistent characters with UUID identity and lifecycle state;
- multiclass entries, official/custom class/subclass provenance, derived values and explicit adjustment escape paths;
- General, Habilidades, Combate, Gestión, Equipo/Monedas, Trasfondo, Rasgos, conditional Conjuros and Notas;
- structured proficiencies/languages, defenses/senses/movement, conditions/exhaustion, concentration, resources, rest support, temporary effects and death saves;
- equipment locations/containers/consumables/ammunition, richer traits/spells and Quick Access/Favorites;
- all six approved reusable conditional modules: Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros;
- PC Settings, Application Settings, Supercompact and Table mode;
- adaptive phone/tablet behavior, context preservation and last-open-tab restoration;
- app-owned backup/export and import-as-copy with reconciliation;
- SQLDelight migration coverage including the prior owner schema lineage;
- bounded pre-QA dead-code/compiler-warning cleanup.

The approved closure scope is D-0047. After the historical M5 freeze, the owner explicitly reopened pre-QA implementation and the focused UX repair line completed Pass 03–07. Automated repair gates are green through review build `0.4.0-preqa.7` / `40700`. The next stage is staged owner phone/tablet visual audition; formal M6 remains deferred until the owner explicitly freezes a replacement candidate.

## Current Phase 4 position — pre-QA repair line stable; owner audition next

Active owner-requested repair branch:

`implementation/phase4-preqa-ux-repair`

Current technically verified review identity:

- version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- checkpoint/branch head `4c6da4577b57e472819e096ecff55bd6750e026d`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK size `36,161,616` bytes;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Current resume pointer:

`docs/checkpoints/LATEST.md`

Detailed current checkpoint:

`docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`

Owner audition guide:

`docs/PREQA_OWNER_VISUAL_AUDITION.md`

The September 4 M5 frozen candidate remains immutable historical evidence, but it is **not the active owner-QA target** after the owner explicitly reopened implementation. Build `40700` is a pre-QA visual-audition build, not yet a frozen formal M6 candidate.

Further speculative UX implementation is stopped. The owner should now perform the staged phone/tablet audition; concrete blocking findings may produce a focused successor build. Formal M6 resumes only after the owner explicitly says an exact replacement build is ready to freeze.

**DM feature implementation remains blocked until Phase 4 owner QA is accepted, final governance/merge-boundary housekeeping is complete, and the owner explicitly approves closure/merge.**

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

See `docs/TESTING.md` for the current gate/QA rules and exact verified candidate.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise real migrations. It is not a production/release identity and must never be reused for a release. See `AGENTS.md` and `docs/TESTING.md` for the governing distinction between non-secret development debug identity and protected release/private credentials.
