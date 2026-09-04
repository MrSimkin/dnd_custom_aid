# Architecture Record

## Current status

**Phase:** Phase 4 — Character Foundation Closure  
**Architecture state:** foundational choices approved; current work is additive character/adaptive implementation, not a stack redesign.  
**Active branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** remains the latest accepted merged state.

The foundational architecture under D-0034 through D-0043 remains controlling. D-0044 through D-0047 define the current character-foundation direction and closure scope. C-0009 remains controlling: use the simplest safe implementation that satisfies real approved requirements.

Batches A1 through H3 are implemented. The current execution position is Batch I1 adaptive-shell completion; do not reopen schema/domain architecture merely because the closure has moved into holistic responsive work.

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
- Batch I1 may introduce an adaptive navigation shell/rail using ordinary Compose state/layout primitives; this does not require a new navigation framework or architecture layer.

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
- Character-owned companions are durable sheet/reference records. They are not future DM live-combat participants/authority merely because they store reference HP/AC/state.

### Character closure data direction

The current Phase 4 closure is intentionally structured around reusable character domains rather than one bespoke data model per class/subclass.

Schema 6 provides the initial closure prototype additions including:

- class/subclass source/provenance/catalog identity;
- Inspiration and death saves;
- structured proficiencies;
- Weapon Mastery;
- generic Resources;
- generic class options;
- Forms;
- Companions.

Additive schema 7 now represents the remaining D-0047 durable domains, including conditions/exhaustion, defenses, senses/movement, concentration, recovery metadata, custom skills, temporary effects, module overrides, portrait/reference metadata, reconciliation checkpoints and related settings.

No further schema work is currently implied by Batch I1/I2. H1/H2/H3 confirmed that Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros can use the existing reusable durable domains without one hard-coded persistence subsystem per subclass.

### UI/adaptive state boundary

Character presentation/navigation state must remain conceptually separate from character mechanics.

For Batch I1:

- available-width shell decisions are UI behavior, not character-domain data;
- per-character last-open-tab state may be persisted as local UI preference/navigation state rather than added to the character rules/domain schema;
- conditional-tab restoration must resolve safely through existing module/spell visibility rules if the previously open tab is no longer available;
- existing list search/filter/sort/selection context should be preserved by the UI-state mechanisms already introduced under B2/D16 rather than copied into durable character records;
- existing F/G/H master-detail implementations should be reused, not replaced by a new generalized UI framework.

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

**DM-feature implementation remains blocked until the Phase 4 character closure is complete and owner-accepted.** Architecture notes about combat are future constraints, not permission to start that work now.

### PDF and SRD clarification

Approved architecture remains:

- local character PDF generation on Android/Desktop using the approved PDFBox variants when that feature is implemented;
- versioned SRD 5.1 / SRD 5.2.1 PostgreSQL chunks with PostgreSQL full-text retrieval and replaceable LLM integration when SRD clarification is implemented.

Neither area should be activated merely as housekeeping for the current character-closure batches unless the approved batch explicitly reaches it.

## Current implementation consequence

The remaining Phase 4 closure work should primarily exercise:

- Android Compose adaptive shell/state handling in I1/I2;
- existing shared Kotlin/SQLDelight persistence as regression-protected foundations rather than new architecture work;
- own-format local backup/import in Batch J using the existing character repositories/domain model;
- Desktop compilation as a shared-API regression check;
- the existing simple CI workflow;
- focused migration/integration stabilization in K before freezing L.

Do not introduce a new service, synchronization layer, realtime mechanism, navigation framework or architecture framework to solve these local character-sheet completion tasks when ordinary shared Kotlin + SQLDelight + Compose is sufficient.

## Architecture gate consequence

The architecture-selection gate is complete. Routine reversible implementation details may be chosen under D-0008 and existing conventions.

A genuinely new consequential architecture choice must still be surfaced to the owner instead of being silently embedded in implementation.