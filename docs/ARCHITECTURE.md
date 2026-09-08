# Architecture Record

## Current status

**Phase:** Phase 4A — Character Foundation Closure  
**Canonical branch:** `main` under D-0048  
**Architecture state:** foundational choices approved; no stack redesign is active  
**Product state:** debug/pre-QA; known owner-observed UX defects remain

The foundational architecture under D-0034 through D-0043 remains controlling. D-0044 through D-0047 define the character-foundation direction and closure scope. D-0048 changes the repository consolidation boundary only; it does not change the approved stack or mark the current UX accepted.

C-0009 remains controlling: use the simplest safe implementation that satisfies real approved requirements.

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
- Phone and tablet are first-class targets.
- Portrait and landscape matter on both form factors.
- Responsive behavior must not equate "wide window" with "tablet interaction model" blindly.
- A physical phone in landscape should remain a phone-appropriate interaction model unless an explicitly designed responsive behavior says otherwise.
- Tablet/wide layouts should exploit width only where doing so demonstrably improves use.
- The owner has explicitly rejected the current wide/tablet presentation as sufficient; this is a UX/layout repair requirement, not a reason to introduce a new navigation architecture/framework.

### Desktop

- Kotlin + Compose Multiplatform Desktop.
- Primarily DM preparation/administration.
- Local Save + explicit Sync is the intended MVP model when hosted sync exists.
- Android/Desktop UI parity is not required.

### Local persistence

- Android and Desktop use SQLite via SQLDelight where local/offline behavior provides real value.
- Stable UUIDs are used for mutable domain identity.
- Migrations are explicit and data-preservation risk is tested proportionately.
- Do not rewrite already-tested historical migrations merely to make numbering prettier; use additive migrations when safe.

### Domain boundaries

- Characters belong to one campaign; existence, ownership and current control are distinct.
- Durable character-sheet state remains separate from future live combat working state.
- Saved encounters, live encounters, durable character state and audit/history remain distinct concepts.
- Character data remains permissive for D&D 5e, D&D 5.5e and custom/homebrew content; the application is not a legality engine.
- Character-owned companions are durable sheet/reference records, not future DM live-combat authority merely because they store reference HP/AC/state.

## Character closure data direction

The Phase 4 closure remains structured around reusable character domains rather than one bespoke persistence subsystem per class/subclass.

The current tested closure line is schema 9. No schema migration was added by pre-QA UX repair Pass 03–07 or by D-0048 documentation consolidation.

Reusable durable domains support the six approved conditional module families:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

## Provenance/source modeling direction after owner audition

The owner audition identified a cross-cutting UI/information-architecture problem: a generic `source` property has often been exposed as a full `Fuente` field even where that provenance does not earn independent user-facing space.

This does **not** require destructive schema cleanup merely for visual tidiness.

Current preferred UI direction:

- preserve stored source/provenance data where compatibility/future migration benefits from doing so;
- expose provenance only when it has a concrete user-facing purpose;
- where provenance is useful, prefer a structured compact origin model with **origin type + specific origin** on one row when space allows;
- default origin type may be `Clase`, with other user-facing categories such as `Dote`, `Pacto`, `Objeto`, `Raza`, `Trasfondo`, `Otro`, etc.;
- do not confuse this generic provenance concept with the real Conjuros spellcasting-source domain.

The Conjuros source system remains a functional domain relationship:

- a spellcasting source is a named object;
- it may be linked to a class or be completely custom/non-class;
- spell-source associations drive filtering/prepared-state behavior;
- that relationship must be preserved.

A later repair may adapt UI/data mapping without inventing a new generalized provenance framework.

## UI/adaptive state boundary

Character presentation/navigation state remains separate from character mechanics.

The existing boundary remains:

- window/form-factor layout decisions are UI behavior, not character-domain data;
- per-character last-open-tab state is local UI preference/navigation state rather than character rules data;
- conditional-tab restoration resolves through existing module/spell visibility rules;
- list search/filter/sort/selection context should remain UI state rather than durable character mechanics;
- existing master-detail/list components may be repaired/reused where useful, but the current tablet/wide UX is not protected merely because it exists.

The owner phone audition adds these repair constraints:

- rotation should preserve useful scroll/context where practical;
- phone landscape must not be promoted to tablet behavior solely from a width breakpoint;
- fixed/sticky regions must leave a practical primary-content viewport;
- Conjuros currently violates that rule in phone landscape;
- app-wide compactness should be solved through shared layout primitives where safe rather than per-screen arbitrary shrinkage.

## Hosted authorization/sync boundary

- Native clients never connect directly to Neon or hold PostgreSQL credentials.
- Hosted reads/writes go through Cloudflare.
- Descope establishes identity; application logic owns campaign/domain authorization.
- Ordinary durable synchronization remains small/application-specific when implemented.
- Rare conflicts may be surfaced to humans instead of requiring a generalized merge engine.

## Future live combat

Existing decisions remain unchanged:

- one authoritative DM device for an active encounter in MVP;
- local-first DM combat actions;
- simple increasing sequence/version;
- no speculative authority-generation machinery until actual device handoff exists;
- HTTP/request-response/polling before realtime infrastructure.

**DM-feature implementation remains blocked until Phase 4A is owner-accepted and explicitly closed.** Architecture notes about combat are future constraints, not permission to begin that work.

## PDF and SRD clarification

Approved architecture remains:

- local character PDF generation on Android/Desktop using the approved PDFBox variants when that feature is implemented;
- versioned SRD 5.1 / SRD 5.2.1 PostgreSQL chunks with PostgreSQL full-text retrieval and replaceable LLM integration when SRD clarification is implemented.

Neither area should be activated merely as housekeeping for the current character-repair cycle.

## Current implementation consequence

The architecture consequence of the D-0048 state is deliberately small:

- `main` becomes the canonical current development baseline;
- keep the existing shared Kotlin + SQLDelight + Compose foundation unless a concrete owner-observed defect demonstrates a need to change it;
- treat build `0.4.0-preqa.7` / `40700` as the latest technically verified product build, not as an accepted UX baseline;
- collect the owner's remaining non-QA observations before designing the successor repair batch;
- repair known UI families using the simplest shared mechanisms that solve them safely;
- do not introduce a new service, synchronization layer, realtime mechanism, navigation framework or architecture framework to solve presentation defects;
- after a successor build is technically green, use targeted real-device retesting before any later formal M6 freeze.

## Architecture gate consequence

The architecture-selection gate is complete. Routine reversible implementation details may be chosen under D-0008 and existing conventions.

A genuinely new consequential architecture choice must still be surfaced to the owner instead of being silently embedded in implementation.
