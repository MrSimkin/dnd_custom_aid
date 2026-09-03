# Character class/subclass module audit

**Audit date:** 2026-09-03  
**Status:** Approved design input under D-0047  
**Purpose:** determine which official class/subclass mechanics justify reusable conditional PC-sheet modules without turning the application into a legality/rules engine.

## 1. Audit rules

This audit follows the project's existing product rules:

- D&D 5e (2014-era) and D&D 5.5e (2024-era) may coexist;
- official supplemental material may be represented even when it is outside the SRD corpus;
- the owner may later add official source material manually to the project's local/custom rules corpus;
- source/provenance must remain identifiable;
- official catalog entries are entry conveniences, not legality constraints;
- any class/subclass may be entered manually through the custom/open path;
- module suggestions may be automatic, but PC Settings must allow manual module overrides;
- hiding a module never deletes its data;
- multiclass characters see the union of relevant modules, never duplicate module tabs.

The audit asks one practical question: **does this class/subclass create enough persistent, frequently consulted state that a dedicated reusable surface is materially better than ordinary Traits/Combat/Spells/Management?**

## 2. Current official-source verification

Verified against current official D&D Beyond source material available on 2026-09-03:

- **Player's Handbook (2024):** 12 base classes, each with four subclasses.
- **Eberron: Forge of the Artificer (2025):** revised Artificer plus Alchemist, Armorer, Artillerist, Battle Smith and Cartographer.
- **Forgotten Realms: Heroes of Faerûn (2025):** College of the Moon, Knowledge Domain, Banneret, Oath of the Noble Genies, Winter Walker, Scion of the Three, Spellfire Sorcery and Bladesinger.
- **Ravenloft: The Horrors Within (2026):** Reanimator, College of Spirits, Grave Domain, Hollow Warden, Phantom, Shadow Sorcery and Undead Patron.
- **Arcana Unleashed:** official D&D Beyond source content is already present as of 2026-09-01 even though earlier promotional material advertised September 15. The current source includes Arcana Domain, Arcane Archer, Warrior of the Mystic Arts, Vestige Patron, Conjurer, Enchanter, Necromancer and Transmuter.

Official playtest Unearthed Arcana is **not** treated as released official catalog content. Partnered third-party classes/subclasses sold on D&D Beyond are also **not** treated as Wizards official content. Both remain representable through the manual/custom path.

## 3. Reusable conditional modules

### 3.1 Artifice (`Artífice` working user-facing label)

**Primary trigger:** any Artificer class entry.

Purpose:

- plans/replicated magic-item choices or equivalent version-specific invention records;
- created/active artificer items where persistent tracking adds value;
- spell-storing or similar artificer-specific persistent state;
- subclass section(s) without creating one new tab for each specialist.

Subclass interaction:

- Alchemist: Artifice + Resources/Traits/Spells; no separate Alchemist tab.
- Armorer: Artifice + Equipment/Combat; armor-model state can live as an Artifice section.
- Artillerist: Artifice; structured cannon can also use Companions when represented as an owned deployable entity.
- Battle Smith: Artifice + Companions for Steel Defender.
- Cartographer: Artifice; map/tool/plan-specific state remains an Artifice section.
- Reanimator: Artifice + Companions for Reanimated Companion.

### 3.2 Forms (`Formas`)

Purpose: persistent alternate-form/transformation records that are more useful as a library/state surface than as prose Traits.

Core trigger:

- Druid, because Wild Shape is a class-defining reusable form workflow.

Subclass/feature triggers or suggested uses include:

- Circle of the Moon and other Wild Shape-intensive Druids;
- Circle of Spores/Stars and similar persistent transformation states when useful;
- Hollow Warden Ranger's form state;
- Winter Walker Ranger if transformation/form state benefits from structured reference;
- Undead Patron Warlock's form when structured quick reference adds value;
- Astral Self / similar Monk form-like state when useful;
- any custom/homebrew class/subclass manually enabled by the user.

Forms must not require every alternate-state feature to become a full creature-stat engine. It stores the human-useful reference/state needed by the character.

### 3.3 Techniques (`Técnicas`)

Purpose: chosen maneuver/shot/rune/flourish/technique libraries with repeated quick use.

Strong triggers:

- Battle Master Fighter — maneuvers;
- Arcane Archer Fighter — Arcane Shot options;
- Rune Knight Fighter — known/active runes where the structured option library is useful.

Possible shared use where it improves UX:

- College of Swords Bard flourishes;
- Warrior of the Elements / Warrior of the Mystic Arts Monk option sets;
- other official or custom selectable combat-technique libraries.

A Fighter does **not** get a dedicated Fighter tab merely for being a Fighter. Techniques appears only when the character has meaningful technique-library state or the user manually enables it.

### 3.4 Metamagic (`Metamagia`)

**Primary trigger:** Sorcerer.

Purpose:

- known Metamagic options;
- option cost/reference;
- quick favorite/access behavior;
- integration with F06 generic Sorcery Point resource rather than a duplicate point system.

Sorcerer subclasses normally do not receive their own tabs. Aberrant, Clockwork, Draconic, Wild Magic, Divine Soul, Shadow, Storm, Lunar, Spellfire and other subclasses use Metamagic + Traits/Spells/Management and optionally Forms/Companions where their mechanics justify it.

### 3.5 Pacts (`Pactos`)

**Primary trigger:** Warlock.

Purpose:

- Pact-related choices;
- Eldritch Invocations;
- Mystic Arcanum-facing choices/reference where applicable;
- patron-linked persistent selections that are awkward as one undifferentiated Traits list.

Pacts must integrate with Spells/Resources rather than duplicate spell records or slots.

Subclass-specific notes:

- Undead Patron may additionally use Forms;
- Vestige Patron may additionally use Companions for the Vestige Companion;
- other patrons normally remain Pacts + Traits/Spells/Management rather than separate patron tabs.

### 3.6 Companions (`Compañeros`)

Purpose: reusable structured owned companion/construct/spirit/familiar-like entities whose recurring statistics/actions/state deserve more than a prose Trait.

Strong official triggers/examples:

- Beast Master Ranger;
- Drakewarden Ranger;
- Circle of Wildfire Druid's Wildfire Spirit;
- Battle Smith Artificer's Steel Defender;
- Artillerist's Eldritch Cannon when represented as a deployed tracked entity;
- Reanimator Artificer's Reanimated Companion;
- College of Creation Bard's Dancing Item when persistent tracking is useful;
- Vestige Patron's Vestige Companion;
- Necromancer Wizard familiars/companions where the official option creates persistent owned stat state;
- custom/homebrew companions manually enabled by the user.

Companions is intentionally reusable by the later DM quick-view/combat work. Character-sheet companions remain durable character data; adding one to live combat later must still create/use appropriate live combat state rather than making combat directly mutate the durable sheet.

## 4. Class-by-class result

| Class | Dedicated class-domain module? | Other conditional modules | Audit conclusion |
|---|---|---|---|
| Barbarian | No | Management, Resources; Forms only for a truly form-like subclass/feature | Rage and subclass counters fit generic resources/traits better than a permanent Barbarian tab. |
| Bard | No | Resources; Techniques for Swords-like option libraries; Companions for Creation-like persistent entities | Bardic Inspiration belongs in generic resources; subclass-specific libraries can reuse shared modules. |
| Cleric | No | Resources/Management/Spells | Channel Divinity and subclass resources do not justify a permanent Cleric tab. |
| Druid | **Forms** | Resources, Companions where applicable | Wild Shape creates enough reusable structured state to justify Forms. |
| Fighter | No | **Techniques** when Battle Master/Arcane Archer/Rune-like state applies | Avoid an empty Fighter tab for Champion/etc. |
| Monk | No | Resources; Techniques/Forms only when subclass option/state benefits | Focus/Ki is generic Resource state. |
| Paladin | No | Resources/Spells/Management | Lay on Hands/Channel Divinity-like state fits Resources/Traits; no dedicated Paladin tab required. |
| Ranger | No | Companions for Beast Master/Drakewarden; Forms for Hollow Warden or similar; Spells | Subclass determines extra surface. |
| Rogue | No | Resources/Management; Companions only if a future/manual option genuinely needs it | Sneak Attack and ordinary subclass features remain Combat/Traits. |
| Sorcerer | **Metamagic** | Resources; Forms/Companions only when justified | Persistent Metamagic choices are substantial and class-wide. |
| Warlock | **Pacts** | Resources; Forms/Companions as subclass requires | Invocations/pact choices are persistent enough for their own reusable domain. |
| Wizard | No | Spells; Companions for Necromancer-like persistent owned entities; Techniques only if an option library genuinely warrants it | Spellbook/Spells is already the class's main working surface; do not duplicate it in a Wizard tab. |
| Artificer | **Artifice** | Companions for Battle Smith/Reanimator/etc.; Equipment/Spells/Resources integration | Artificer has enough unique persistent creation/plan state to deserve a class-domain module. |

## 5. Official subclass inventory considered

This inventory is for **representation/module auditing**, not for hard-coded rules enforcement. Names may have revised equivalents; source provenance distinguishes versions.

### Barbarian

2014-era official families/options considered: Berserker, Totem Warrior, Battlerager, Ancestral Guardian, Storm Herald, Zealot, Beast, Wild Magic, Giant.  
2024 PHB: Berserker, Wild Heart, World Tree, Zealot.

**Module result:** no subclass-specific tab. Resources/Traits/Management cover most state; manually enable Forms if a homebrew/revised option genuinely needs it.

### Bard

2014-era: Lore, Valor, Glamour, Swords, Whispers, Eloquence, Creation, Spirits.  
2024 PHB: Dance, Glamour, Lore, Valor.  
Heroes of Faerûn: College of the Moon.  
Ravenloft: College of Spirits.

**Module result:** Resources for Bardic Inspiration; Techniques may serve Swords-like option sets; Companions may serve Creation's persistent Dancing Item.

### Cleric

2014-era: Knowledge, Life, Light, Nature, Tempest, Trickery, War, Death, Arcana, Forge, Grave, Order, Peace, Twilight.  
2024 PHB: Life, Light, Trickery, War.  
Heroes of Faerûn: Knowledge.  
Ravenloft: Grave.  
Arcana Unleashed: Arcana.

**Module result:** no dedicated Cleric/subclass tab. Resources + Traits + Spells cover the persistent state cleanly.

### Druid

2014-era: Land, Moon, Dreams, Shepherd, Spores, Stars, Wildfire.  
2024 PHB: Land, Moon, Sea, Stars.

**Module result:** Forms is class-relevant; Companions additionally covers Wildfire Spirit and similar persistent entities.

### Fighter

2014-era: Champion, Battle Master, Eldritch Knight, Banneret/Purple Dragon Knight, Arcane Archer, Cavalier, Samurai, Echo Knight, Psi Warrior, Rune Knight.  
2024 PHB: Battle Master, Champion, Eldritch Knight, Psi Warrior.  
Heroes of Faerûn: Banneret.  
Arcana Unleashed: Arcane Archer.

**Module result:** Techniques for Battle Master/Arcane Archer/Rune-like choice libraries; no universal Fighter tab. Echo-like owned entities can use Companions only when the chosen representation benefits from persistent tracked state.

### Monk

2014-era: Open Hand, Shadow, Four Elements, Long Death, Sun Soul, Drunken Master, Kensei, Mercy, Astral Self, Ascendant Dragon.  
2024 PHB: Mercy, Shadow, Elements, Open Hand.  
Arcana Unleashed: Warrior of the Mystic Arts.

**Module result:** Focus/Ki through Resources; Techniques for option libraries when useful; Forms for substantial transformation state such as Astral Self if structured reference is useful.

### Paladin

2014-era: Devotion, Ancients, Vengeance, Oathbreaker, Crown, Conquest, Redemption, Glory, Watchers.  
2024 PHB: Devotion, Glory, Ancients, Vengeance.  
Heroes of Faerûn: Oath of the Noble Genies.

**Module result:** no universal Paladin/subclass tab; Resources/Traits/Spells/Management are sufficient.

### Ranger

2014-era: Hunter, Beast Master, Gloom Stalker, Horizon Walker, Monster Slayer, Fey Wanderer, Swarmkeeper, Drakewarden.  
2024 PHB: Beast Master, Fey Wanderer, Gloom Stalker, Hunter.  
Heroes of Faerûn: Winter Walker.  
Ravenloft: Hollow Warden.

**Module result:** Companions for Beast Master/Drakewarden; Forms for Hollow Warden and other meaningful form-state subclasses; otherwise ordinary Traits/Spells/Resources.

### Rogue

2014-era: Thief, Assassin, Arcane Trickster, Mastermind, Swashbuckler, Inquisitive, Scout, Phantom, Soulknife.  
2024 PHB: Arcane Trickster, Assassin, Soulknife, Thief.  
Heroes of Faerûn: Scion of the Three.  
Ravenloft: Phantom.

**Module result:** no universal Rogue/subclass tab. Soulknife/Phantom-like counters use Resources/Management; spellcasting uses Spells.

### Sorcerer

2014-era: Draconic Bloodline, Wild Magic, Storm Sorcery, Divine Soul, Shadow Magic/Sorcery, Aberrant Mind, Clockwork Soul, Lunar Sorcery.  
2024 PHB: Aberrant Sorcery, Clockwork Sorcery, Draconic Sorcery, Wild Magic Sorcery.  
Heroes of Faerûn: Spellfire Sorcery.  
Ravenloft: Shadow Sorcery.

**Module result:** Metamagic is class-wide; Sorcery Points use generic Resources. Subclasses add ordinary Traits/Spells/Management and Forms/Companions only when needed.

### Warlock

2014-era: Archfey, Fiend, Great Old One, Undying, Celestial, Hexblade, Fathomless, Genie, Undead.  
2024 PHB: Archfey, Celestial, Fiend, Great Old One.  
Ravenloft: Undead Patron.  
Arcana Unleashed: Vestige Patron.

**Module result:** Pacts is class-wide. Undead may also use Forms; Vestige Patron uses Companions for its Vestige Companion.

### Wizard

2014-era: Abjuration, Conjuration, Divination, Enchantment, Evocation, Illusion, Necromancy, Transmutation, Bladesinging, War Magic, Chronurgy, Graviturgy, Order of Scribes.  
2024 PHB: Abjurer, Diviner, Evoker, Illusionist.  
Heroes of Faerûn: Bladesinger.  
Arcana Unleashed: Conjurer, Enchanter, Necromancer, Transmuter.

**Module result:** no dedicated Wizard tab because Spells already represents the main persistent class library. Necromancer may use Companions for persistent familiars/owned entities; other school state remains Traits/Resources/Management unless a future concrete need proves otherwise.

### Artificer

Legacy official subclasses: Alchemist, Armorer, Artillerist, Battle Smith.  
Forge of the Artificer (2025): Alchemist, Armorer, Artillerist, Battle Smith, Cartographer.  
Ravenloft (2026): Reanimator.

**Module result:** Artifice is class-wide. Battle Smith and Reanimator strongly activate Companions; Artillerist may use Companions for cannon tracking. Other subclass state remains sections within Artifice plus existing generic domains.

## 6. Base-tab/surface structure after this audit

General character surfaces remain broadly reusable rather than class-specific:

- General;
- Habilidades;
- Combate;
- Equipo;
- Trasfondo;
- Rasgos;
- Conjuros when enabled/relevant;
- Notas;
- **Gestión** (working label) for live character upkeep approved under D-0047.

Conditional modules may add:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

On phone these may participate in the character navigation system. On wide/tablet surfaces D-0047 allows a navigation rail and master-detail arrangements rather than requiring every module to appear as a horizontally scrolling phone-style tab.

## 7. Supercompact relationship

The experimental Supercompact view is **not another independent character-data model**.

It should project existing character data and F15 Favorites/Quick Access into a high-density operational view. Candidate information includes:

- identity/class/subclass/level;
- AC, HP/temp HP, Initiative, Speed, proficiency bonus;
- abilities and important saves/passives;
- Inspiration/death saves when relevant;
- conditions/Exhaustion/concentration;
- favorite attacks, traits, spells, resources, forms and companions;
- spell slots/resources where live interaction is appropriate.

Tablet portrait/landscape should use substantially more useful columns than phone where the available width supports them.

## 8. DM-stage reuse

The following Phase 4 data is intentionally useful to the next DM stage without coupling the two states:

- portrait/token;
- class/subclass/level/provenance;
- AC/HP/saves/abilities/passives;
- defenses;
- senses/movement;
- conditions/concentration reference;
- Favorite/Quick Access projection;
- companions where appropriate.

The future DM quick view may consume these durable character projections. Live combat remains a separate working state under D-0025/D-0033/D-0044.

## 9. Acceptance rule

This audit defines **which reusable modules the new closure implementation must be capable of representing**. It does not require automatic implementation of every official feature's rule text or every subclass calculation.

A future official/custom subclass not present in this audit must still be representable through manual subclass identity plus generic Traits/Resources/Spells/Management and manual module enablement. Therefore new source books should usually extend catalog metadata and module suggestions rather than force a schema redesign.
