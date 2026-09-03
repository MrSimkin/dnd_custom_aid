# Phase 4 Character Foundation Closure Expansion — Design and Implementation Gate

**Date:** 2026-09-03
**Branch target:** isolated Phase 4 closure branch; `main` remains untouched
**Status:** implementation-authorized by owner in chat; this document records the consolidated scope before code changes

## 1. Goal

Use the forced post-QA build to close the player-character foundation with substantial new functionality rather than another narrow correction APK, while preserving the project boundary that DM live-combat state remains a later phase.

The build must include both the owner's newly reported correction requirements and genuinely new character-sheet functionality that will remain useful to the upcoming DM surfaces.

## 2. Non-negotiable correction/UX scope

- Replace isolated dialog fixes with one reusable app-wide IME-safe editor/dialog pattern. Required actions must remain reachable with the Android keyboard visible everywhere.
- Standardize interaction grammar: tap row/card to open/edit; `+` to add; drag handle to reorder in Manual mode; overflow/secondary actions for duplicate/delete; `Guardar` persists; `Cancelar` abandons editor-local draft.
- Real drag-and-drop feedback: lifted/highlighted active item, visible destination/reflow, and haptic feedback where practical.
- Make ordinary Equipo materially denser; it represents ordinary objects, not rich special-item cards.
- Make Monedas materially more compact and responsive.
- Independent Manual/A–Z modes for ordinary Equipo and Equipo especial. A–Z must not destroy the stored manual order; drag is disabled/hidden while alphabetical display mode is active.
- Conjuros supports Manual/A–Z inside each spell level. Level grouping remains authoritative; alphabetical display must not destroy manual order.
- Replace the coarse `wide >= 700dp` concept over time with component-specific available-width layouts. Phone, tablet portrait, tablet landscape, and split/wide windows must use sensible column counts based on useful minimum component width.
- Add an application-settings navigation entry inside PC Settings while retaining application-wide ownership of theme/font/text-scale preferences.
- Normalize Add/Edit/Delete iconography, wording, semantics, touch targets, and accessibility descriptions app-wide.

## 3. General new PC-sheet features in this closure build

1. Unsaved-change protection when leaving a dirty character (`Guardar`, `Descartar`, `Seguir editando`).
2. Visible dirty/saved state in the character header.
3. Visible `Última actualización` freshness information using the existing durable timestamp.
4. Character status editing in PC Settings (`Activo`, `Inactivo`, `Retirado`, `Muerto`).
5. Search in Equipo.
6. Search in Conjuros.
7. Prepared-only spell filter.
8. Collapsible spell-level blocks.
9. Collapsible Equipo sections.
10. Search/filter Rasgos by name/type.
11. Duplicate action for list-like character records where it is useful (combat entries, inventory/special items, traits, spells, titled notes, and compatible module entries).
12. Consistent list-section headers with title/count/sort/filter/add controls.
13. Counts on useful list domains.
14. Tablet-aware character list grid.
15. Tablet-aware PC Settings layout.
16. Explicit tablet responsive pass for Trasfondo/Rasgos/Conjuros/Notas/Equipo/Combate.
17. Consistent useful empty states.
18. Consistent destructive confirmation.
19. Consistent vector icon vocabulary and accessibility semantics.
20. Component-specific responsive density rather than one global tablet column count.
21. Structured proficiencies/languages/tools/armor/weapons section, because the durable sheet must be a complete backup/reference and this core character information is not represented structurally today.
22. Manual Weapon Mastery records (weapon + mastery/property + source/notes), without legality enforcement.
23. Heroic Inspiration / Inspiration durable field.
24. Optional death-save success/failure durable state for digital-active-sheet use.
25. Pin/favorite support for frequently used combat entries, traits, and spells, exposed as a compact `Acceso rápido` area on General.
26. Generic quick-resource dashboard built from explicitly pinned/quick resource records, rather than hard-coding every class resource into General.

## 4. Class/subclass identity becomes first-class data

The current `CharacterClassLevel` free-text class name is insufficient for the closure target.

Each class entry must preserve stable identity and remain permissive:

- display name;
- level;
- hit die / remaining hit dice;
- rules generation/source family (`D&D 5e`, `D&D 5.5e`, `Personalizado` or equivalent non-restrictive provenance);
- optional source-book label;
- optional built-in catalog key;
- optional subclass name;
- optional subclass source-book label;
- optional subclass catalog key;
- optional subclass rules-generation/provenance;
- manual/custom class and subclass names must always remain possible.

Do not enforce subclass level legality. Do not reject old/new mixed content. Multiclass characters may have an independent subclass per class entry.

Known official catalog entries are conveniences, not rules-enforcement gates. Source/provenance must be visible enough to distinguish variants with the same or revised names.

## 5. Official class baseline to support

Current core D&D 5.5e classes:

- Bárbaro
- Bardo
- Clérigo
- Druida
- Guerrero
- Monje
- Paladín
- Explorador
- Pícaro
- Hechicero
- Brujo
- Mago

Artífice is also a first-class supported class using the 2025 `Eberron: Forge of the Artificer` revision while retaining representation of the earlier 5e/Tasha-era Artificer.

The catalog must also represent published Wizards of the Coast 5e subclasses from earlier books because the product deliberately permits mixing generations, plus newer official 5.5e sourcebook options. Partnered third-party content is not built in by default, but custom/manual subclass entry remains available.

Official material announced/available in early digital access but not yet generally released may be represented with an explicit `Próximo/Acceso anticipado` provenance rather than pretending it is generally released.

## 6. Class-by-class dedicated-surface audit

### Artífice — dedicated `Artífice` surface: YES

Reason: the class has a large, persistent invention/loadout domain independent of ordinary Rasgos/Conjuros/Equipo.

Core surface:
- known magic-item plans / infusions-equivalent according to selected rules generation;
- replicated/currently created items;
- active/attuned marker where useful, linked to ordinary inventory when desired but never forced;
- Tinker's Magic quick resource/notes;
- Flash of Genius/resource can be exposed through generic quick resources;
- Spell-Storing Item record: item, stored spell, uses/notes;
- manual source/provenance for every record.

Subclass panels inside Artífice:
- Alchemist: Experimental Elixir records;
- Armorer: active armor model/configuration and notes;
- Artillerist: device/cannon summary with optional link to `Compañeros`;
- Battle Smith: Steel Defender link to `Compañeros`;
- Cartographer: Adventurer's Atlas / mapping configuration and notes;
- Reanimator: Reanimated Companion link to `Compañeros`.

The data shape remains permissive enough for future/custom Artificer specializations.

### Bárbaro — dedicated class tab: NO

Rage, subclass choices, transformations and per-rest uses fit `Recursos rápidos`, `Rasgos` and `Combate`. Giant/Wild Heart/Beast-specific choices may be stored as Rasgos without adding a mostly-empty permanent surface.

### Bardo — dedicated class tab: NO

Bardic Inspiration uses belong in quick resources; subclass features remain Rasgos. College of Spirits' mutable/tabled narrative effects can be represented by Rasgos/notes without a dedicated tab.

### Clérigo — dedicated class tab: NO

Channel Divinity and domain features fit quick resources/Rasgos/Conjuros. Domain identity becomes structured subclass data.

### Druida — conditional `Formas` tab: YES

Wild Shape is central enough, has known/prepared forms in 5.5e, and is repeatedly consulted at the table.

`Formas` records are manual and source-aware rather than a rules-enforced bestiary:
- name;
- source/reference;
- CR or equivalent optional text;
- AC/HP/temporary-HP notes where useful;
- movement/senses summary;
- compact attack/action summary;
- notes;
- favorite/pin;
- manual order + optional A–Z display.

Later bestiary integration may link rather than duplicate, but is not required for this Phase 4 build.

Circle of Wildfire may additionally enable `Compañeros` for the Wildfire Spirit.

### Guerrero — conditional `Técnicas` tab for maneuver/shot-style builds: YES when applicable

Battle Master strongly justifies this surface. Arcane Archer and any future/manual technique-based option may reuse it.

Fields:
- name;
- category/source;
- resource/cost text;
- save/DC text if relevant;
- effect summary;
- notes;
- favorite/pin;
- manual/A–Z display;
- optional shared resource pool reference (e.g. Superiority Dice) represented as a quick resource.

Champion/Eldritch Knight/Psi Warrior/etc. do not force the tab unless the user explicitly enables a compatible technique module.

### Monje — dedicated class tab: NO by default

Focus Points belong in quick resources; most subclass abilities fit Rasgos/Combate. A technique-heavy subclass such as Warrior of the Mystic Arts may opt into the generic `Técnicas` surface rather than creating a Monk-only tab.

### Paladín — dedicated class tab: NO

Lay on Hands and Channel Divinity belong in quick resources; oath features/tenets fit Rasgos/Trasfondo. Subclass identity is structured.

### Explorador — dedicated class tab: NO by default

General ranger state fits existing domains. Beast Master and Drakewarden enable `Compañeros`.

### Pícaro — dedicated class tab: NO

Sneak Attack/Cunning Strike/psionic or token resources fit Combate/Rasgos/quick resources. No subclass requires a universal Rogue-specific page.

### Hechicero — conditional `Metamagia` tab: YES

Sorcery Points + selected Metamagic are central, frequently consulted and large enough to justify a dedicated surface.

Fields:
- current/max Sorcery Points or a linked quick-resource pool;
- selected Metamagic options;
- cost/activation summary;
- effect/notes/source;
- manual/A–Z display;
- favorite/pin;
- subclass state cards when needed (e.g. Lunar Sorcery current phase) without baking every subclass rule into the schema.

### Brujo — conditional `Pactos` tab: YES

Invocations are a large persistent choice list and Pact Magic/Mystic Arcanum differs enough from ordinary spell preparation to benefit from a dedicated quick surface while still sharing authoritative spell records with `Conjuros`.

Fields:
- patron/subclass summary from structured class identity;
- Pact Magic quick slot summary linked to authoritative spellcasting state rather than duplicated;
- invocations list (name, prerequisite/source/effect summary/notes/favorite);
- pact-boon style choices represented as invocation/feature records as appropriate;
- Mystic Arcanum spell references linked to `Conjuros`;
- manual/A–Z display.

Vestige Patron may enable `Compañeros`.

### Mago — dedicated class tab: NO

`Conjuros` already acts as the natural spellbook surface. School/subclass features fit Rasgos. New Necromancer familiars/companions may enable `Compañeros`.

## 7. Generic conditional `Compañeros` tab — YES

This is a reusable PC-owned stat/reference domain and a direct bridge to future DM quick views.

Enable when the character has at least one companion record or when a relevant class/subclass module suggests creating one. Do not hard-enforce availability.

Candidate official uses include:
- Ranger Beast Master;
- Ranger Drakewarden;
- Druid Circle of Wildfire;
- Artificer Battle Smith;
- Artificer Artillerist devices when treated as a tracked entity;
- Artificer Reanimator;
- Warlock Vestige Patron;
- Wizard Necromancer familiars where relevant;
- Pact/familiar or homebrew companions entered manually.

Companion record minimum:
- stable ID;
- name;
- kind/type;
- source/provenance;
- owner class/subclass association optional;
- AC;
- max/current/temp HP;
- speed/movement summary;
- ability scores optional;
- senses/proficiencies text;
- traits/actions as ordered compact records;
- notes;
- active/available state;
- manual ordering + A–Z display.

Do not implement full monster/NPC rules enforcement here. Future DM entity/bestiary linkage can be additive.

## 8. Built-in official subclass catalog boundary

Built-in names/source metadata should cover Wizards of the Coast-published fifth-edition subclasses from:
- Player's Handbook (2014 and 2024/5.5e revisions);
- Dungeon Master's Guide (2014 legacy villainous options, clearly marked);
- Sword Coast Adventurer's Guide;
- Xanathar's Guide to Everything;
- Guildmasters' Guide to Ravnica;
- Explorer's Guide to Wildemount;
- Mythic Odysseys of Theros;
- Tasha's Cauldron of Everything;
- Van Richten's Guide to Ravenloft;
- Fizban's Treasury of Dragons;
- Dragonlance: Shadow of the Dragon Queen;
- Bigby Presents: Glory of the Giants;
- Eberron: Forge of the Artificer;
- Forgotten Realms: Heroes of Faerûn;
- Ravenloft: The Horrors Within;
- Arcana Unleashed entries known as of 2026-09-03, marked as official early-access/upcoming where appropriate until general release.

Do not build in Partnered Content (Critical Role partner books, third-party marketplace classes/subclasses, etc.) as `official WotC` catalog entries. `Explorer's Guide to Wildemount` is included because it is a Wizards-published D&D source; later partner-only Tal'Dorei material is not.

Manual `Personalizado` entry remains available for any campaign content and for the owner's later hand-maintained reference corpus.

## 9. Catalog summary by class (unique subclass concepts; source/version variants remain distinguishable)

### Artífice
Alchemist; Armorer; Artillerist; Battle Smith; Cartographer; Reanimator.

### Bárbaro
Berserker; Totem Warrior/Wild Heart (versioned); World Tree; Zealot; Battlerager; Ancestral Guardian; Storm Herald; Beast; Wild Magic; Giant.

### Bardo
Dance; Glamour; Lore; Valor; Creation; Eloquence; Swords; Whispers; Spirits; Moon.

### Clérigo
Life; Light; Trickery; War; Knowledge; Nature; Tempest; Arcana; Forge; Grave; Order; Peace; Twilight; Death (legacy DMG). Revised/current source variants are kept distinguishable.

### Druida
Land; Moon; Sea; Stars; Dreams; Shepherd; Spores; Wildfire.

### Guerrero
Battle Master; Champion; Eldritch Knight; Psi Warrior; Arcane Archer; Cavalier; Samurai; Rune Knight; Echo Knight; Purple Dragon Knight/Banneret (versioned).

### Monje
Mercy; Shadow; Elements/Four Elements (versioned); Open Hand; Drunken Master; Kensei; Long Death; Sun Soul; Astral Self; Ascendant Dragon; Warrior of the Mystic Arts (official 2026 early-access/upcoming as applicable).

### Paladín
Devotion; Glory; Ancients; Vengeance; Conquest; Redemption; Crown; Watchers; Oathbreaker (legacy DMG); Noble Genies.

### Explorador
Beast Master; Fey Wanderer; Gloom Stalker; Hunter; Horizon Walker; Monster Slayer; Swarmkeeper; Drakewarden; Winter Walker; Hollow Warden.

### Pícaro
Arcane Trickster; Assassin; Soulknife; Thief; Inquisitive; Mastermind; Scout; Swashbuckler; Phantom; Scion of the Three.

### Hechicero
Aberrant Mind/Aberrant Sorcery (versioned); Clockwork Soul/Clockwork Sorcery (versioned); Draconic Bloodline/Draconic Sorcery (versioned); Wild Magic/Wild Magic Sorcery (versioned); Divine Soul; Shadow Magic/Shadow Sorcery (versioned); Storm Sorcery; Lunar Sorcery; Spellfire Sorcery.

### Brujo
Archfey; Celestial; Fiend; Great Old One; Fathomless; Genie; Hexblade; Undead; Undying; Vestige Patron (official 2026 early-access/upcoming as applicable).

### Mago
Abjuration/Abjurer; Divination/Diviner; Evocation/Evoker; Illusion/Illusionist; Conjuration/Conjurer; Enchantment/Enchanter; Necromancy/Necromancer; Transmutation/Transmuter; Bladesinging/Bladesinger; War Magic; Order of Scribes; Chronurgy; Graviturgy. Revised/current source variants remain distinguishable.

## 10. Architecture guardrails

- Names/catalog metadata are convenience/reference data, not a legality engine.
- No copyrighted sourcebook rules text is copied into a built-in rules database in this work. User-entered summary/notes remain user data.
- All class/subclass/module data is local-first and durable.
- Stable UUID identity for list records.
- Source/provenance retained.
- No automatic inference that would prevent homebrew/mixed-generation play.
- Live encounter/combat state remains separate from durable character state.
- New companion/form/module records must be suitable for later read-only DM quick projections without requiring a schema rewrite.
- Do not activate auth/cloud/realtime/PDF/SRD-AI subsystems merely because this character build grows.

## 11. Closure acceptance target

The next APK is the **Phase 4 Character Foundation Closure Candidate**.

Acceptance focuses on phone + tablet:
- migration/data preservation;
- class/subclass/source editing and multiclass persistence;
- dynamic conditional tabs/modules;
- app-wide IME safety;
- consistent actions;
- drag/reorder + Manual/A–Z preservation;
- compact Equipo/Monedas;
- responsive portrait/landscape layouts;
- search/filter/collapse flows;
- companion/form/module persistence;
- unsaved-change protection and freshness;
- save/reopen/rotation/recreation resilience.

Once this passes without blocking defects, remaining minor cosmetic items should not keep Phase 4 open indefinitely; prepare merge and move to the DM combat tracker.
