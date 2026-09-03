# Architecture Record

## Current status

**Phase:** Phase 4 — Character Foundation Closure  
**Architecture state:** foundational choices approved; current work is additive feature/domain implementation, not a stack redesign.  
**Active branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** remains the latest accepted merged state.

The foundational architecture under D-0034 through D-0043 remains controlling. D-0044 through D-0047 define the current character-foundation direction and closure scope. C-0009 remains controlling: use the simplest safe implementation that satisfies real approved requirements.

## Approved architecture

### Hosted providers

- **Neon PostgreSQL** — durable hosted relational database when hosted/shared features are implemented.
- **Cloudflare Worker/API** — project-owned hosted application gateway/backend.
- **Descope** — authentication only.
- **Cloudflare Workers AI** — initial LLM provider only for approved SRD clarification when implemented.
- Do not activate R2, Durable Objects, WebSockets, queues or other services merely because Cloudflare offers them.
- Keep vendor-specific code localized without generalized provider-abstraction frameworks.

Initial hosted path when those features are activated:

```text
Android / Desktop
       │
       ▼
Cloudflare Worker/API
       │
       ▼
Neon PostgreSQL
```

### Android

- Native Kotlin + Jetpack Compose.
- `minSdk 30 / Android 11`.
- **Phone and tablet are first-class targets.**
- Responsive behavior should react to available width rather than simply stretching one phone layout or using one coarse tablet boolean.
- Portrait and landscape matter on both phone and tablet.

### Desktop

- Kotlin + Compose Multiplatform Desktop.
- Primarily DM preparation/administration.
- Local Save + explicit Sync is the intended MVP model when hosted sync exists.
- Android/Desktop UI parity is not required.

### Local persistence

- Android and Desktop use SQLite via SQLDelight where local/offline behavior provides real value.
- Stable UUIDs are used for mutable domain identity.
- Migrations are explicit and data-preservation risk is tested proportionately.
- Do not rewrite already-tested historical migrations merely to make migration numbering prettier; use additive migrations when safe.

### Domain boundaries

- Characters belong to one campaign; existence, ownership and current control are distinct.
- Durable character-sheet state remains separate from future live combat working state.
- Saved encounters, live encounters, durable character state and audit/history remain distinct concepts.
- Character data remains permissive for D&D 5e, D&D 5.5e and custom/homebrew content; the application is not a legality engine.

### Character closure data direction

The current Phase 4 closure is intentionally structured around reusable character domains rather than one bespoke data model per class/subclass.

The schema-6 prototype already adds:

- class/subclass source/provenance/catalog identity;
- Inspiration and death saves;
- structured proficiencies;
- Weapon Mastery;
- generic Resources;
- generic class options;
- Forms;
- Companions.

The next additive schema-7 work will represent the remaining D-0047 durable domains such as conditions/exhaustion, defenses, senses/movement, concentration, recovery metadata, custom skills, temporary effects, module overrides, portrait/reference metadata, reconciliation checkpoints and related settings.

This deliberately supports conditional reusable surfaces such as Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros without creating a separate hard-coded persistence subsystem for every subclass.

### Hosted authorization/sync boundary

- Native clients never connect directly to Neon or hold PostgreSQL credentials.
- Hosted reads/writes go through Cloudflare.
- Descope establishes identity; application logic owns campaign/domain authorization.
- Ordinary durable synchronization remains small/application-specific when implemented.
- Rare conflicts may be surfaced to humans instead of requiring a generalized merge engine.

### Future live combat

Existing decisions remain unchanged:

- one authoritative DM device for an active encounter in MVP;
- local-first DM combat actions;
- simple increasing sequence/version;
- no speculative authority-generation machinery until actual device handoff exists;
- HTTP/request-response/polling before realtime infrastructure.

**However, DM-feature implementation is currently blocked until the Phase 4 character closure is complete and owner-accepted.** Architecture notes about combat are future constraints, not permission to start that work now.

### PDF and SRD clarification

Approved architecture remains:

- local character PDF generation on Android/Desktop using the approved PDFBox variants when that feature is implemented;
- versioned SRD 5.1 / SRD 5.2.1 PostgreSQL chunks with PostgreSQL full-text retrieval and replaceable LLM integration when SRD clarification is implemented.

Neither area should be activated merely as housekeeping for the current character-closure batches unless the approved batch explicitly reaches it.

## Current implementation consequence

The current closure work should primarily exercise:

- shared Kotlin character/domain logic;
- SQLDelight/SQLite migrations and persistence;
- Android Compose UI/adaptive layout/state handling;
- Desktop compilation as a shared-API regression check;
- the existing simple CI workflow.

Do not introduce a new service, synchronization layer, realtime mechanism or architecture framework to solve a local character-sheet problem when ordinary shared Kotlin + SQLDelight + Compose is sufficient.

## Architecture gate consequence

The architecture-selection gate is complete. Routine reversible implementation details may be chosen under D-0008 and existing conventions.

A genuinely new consequential architecture choice must still be surfaced to the owner instead of being silently embedded in implementation.
