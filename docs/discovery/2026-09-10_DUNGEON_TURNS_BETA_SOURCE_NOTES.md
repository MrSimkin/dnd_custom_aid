# Dungeon Turns beta — source continuity notes

**Date captured:** 2026-09-10  
**Source:** owner-supplied DOCX, `turno de dungeon.docx`  
**Status:** BETA house-rule source material; requires playtesting  
**Product/application decisions:** see `docs/decisions/D-0068_DM_LIVE_WORKSPACE_DESKS_AND_DUNGEON_DIRECTION.md`

## Purpose of this file

The owner supplied a five-page working draft of the optional house rule **Turnos de dungeon** during DM/Dungeon Desk product discovery.

The original DOCX is not itself a repository artifact. This file preserves the rule structure and operational content needed to continue product design from Git without relying on the chat attachment. It is a continuity extraction, not a declaration that the beta rule is final.

Do not silently rewrite the beta for rules purity. In particular, `Recuperar aliento` and `Tratar heridas` are intentionally experimental and must remain available for later playtesting unless the owner changes them.

## Core rule

- The rule is optional and applies to dungeons, ruins, caves, fortresses, crypts, sewers, abandoned temples, underground complexes and equivalent dangerous exploration environments.
- One Dungeon Turn represents **10 minutes**.
- Each character chooses one principal **Actividad** for that turn.
- An Activity represents the character's main focus during that interval: searching, moving quietly, guarding, manipulating a lock/mechanism, casting, looting/searching bodies, preparing defenses, catching breath or similar tasks.
- Dungeon Turns do not replace combat. When combat starts, normal combat/initiative takes over. After combat, the DM decides whether the interrupted Dungeon Turn completed, was interrupted or should resolve partially.

## Zones

Movement is expressed in **zonas**.

A zone is a logical exploration unit defined by the DM: room, chamber, corridor section, stairs, crypt, crossing, cave section, gallery, sewer section, courtyard or another area that makes sense as one exploration unit.

A zone has no fixed physical size. The DM decides its practical boundaries according to map/context, visibility, obstacles, doors, noise, light and exploration rhythm.

For Dungeon Desk design, D-0068 clarifies that the application does **not** adjudicate or calculate these zones automatically; the DM keeps that prerogative.

## Group advancement

If the group stays together, its movement is determined by the slowest Activity performed by any character in the group.

Four advancement categories exist in the beta:

- **Avance rápido:** up to 3 zones. Requires `Avanzar`; characters give up other demanding exploration tasks.
- **Avance normal:** up to 2 zones. Ordinary movement while doing Activities compatible with movement without special caution.
- **Avance reducido:** up to 1 zone. Used when at least one character performs an Activity requiring caution, attention, silence or vigilance.
- **Sin avance:** no movement to another zone when an Activity requires remaining in place, unless the party separates.

If an Activity costs `avance reducido`, the group may move 1 zone while doing it. If an Activity costs `sin avance`, the group remains unless the relevant character is left behind or the party divides.

### Known beta gap / proposal

The current draft defines `Avance normal` but does not contain an explicit Activity whose primary purpose is ordinary two-zone exploration. D-0068 retains `(*) Exploración normal` as a **Proposed** beta addition, not an approved rule change.

## Separating the party

The group may split during a Dungeon Turn.

Each subgroup resolves its advancement and Activities separately. A character performing a `sin avance` Activity may remain behind while another subgroup moves; a character using `Moverse en silencio` may advance silently without the rest of the group doing so.

The DM decides distance, communication, visibility, sounds, encounters and dangers between separated subgroups.

## Declaring Activities

At the start of every Dungeon Turn, each player declares the character's Activity. The group agrees the practical order of resolution.

The DM may secretly determine environmental changes, creature movement, consequences of noise, enemy actions, dungeon alterations and other events occurring during the ten-minute interval.

Each Activity is expressed through:

- **Requisito** — what must be true to attempt it;
- **Costo** — movement limitation, test, tool, resource, spell slot, light, noise, materials or other cost/consequence;
- **Beneficio** — what completion/success provides.

D-0068 records the owner clarification that **re-declaration every turn is intentional**, but the app should remember/show the previous Activity (e.g. `Anterior: Buscar — secretos`) as reference instead of silently carrying it forward.

## Conditional declarations

A player may add one simple condition with a clear trigger and concrete response, e.g.:

- search for physical dangers; if a trap is found, the rogue attempts to disarm it;
- if no trap is found, move silently to the next zone;
- if footsteps are heard, prepare an ambush;
- if an inscription shows supernatural signs, cast an appropriate ritual;
- if the door is locked, manipulate the lock; otherwise guard.

A condition does **not** grant two complete Activities in one Dungeon Turn. It determines which Activity is actually carried out if the situation changes during the turn.

## Simultaneous abstraction

Although Activities are resolved in an agreed order for table speed, the Dungeon Turn represents ten minutes of continuous activity.

Results are understood as belonging to the same interval. Small narrative inconsistencies are resolved with reasonable abstraction: assume each Activity occurred at a sensible moment within the turn unless two Activities are physically incompatible.

This means, for example, that one PC may search a trapped door while another conditionally manipulates the trap if it is found, without treating the whole turn as a strict second-by-second initiative sequence.

## Checks, failure and partial success

The DM asks for an ability check when the result is uncertain, risky, opposed, precise, pressured or failure has meaningful consequences.

A failed Activity does not obtain its Benefit and may create an appropriate consequence other than damage, including:

- lost time;
- noise;
- resource consumption;
- damaged tools;
- revealed position;
- activated trap;
- blocked mechanism;
- incomplete information;
- lost opportunity;
- party separation;
- arriving creatures;
- another reasonable change.

The DM may allow partial success when the failure is narrow or the Activity had several components; partial success gives part of the Benefit plus a complication.

## Help and group checks

`Ayudar` may grant advantage on another creature's associated check when the help is reasonable.

If several characters perform the same Activity, the DM may use a group check. The beta uses the normal threshold: the group succeeds if at least half succeed.

The DM decides whether a task supports help, a group check or both.

## Interruptions

A Dungeon Turn may be interrupted when:

- combat begins;
- a creature takes damage;
- a danger needs immediate resolution;
- a directly hostile creature appears;
- a trap substantially changes the situation;
- another event prevents declared Activities from continuing.

After interruption, the DM decides which Activities actually completed. Unresolved Activities normally gain no Benefit, though the DM may grant a partial result when enough time was invested or the result was already evident.

D-0068 proposes a small Dungeon Turn log that can preserve this completed/partial/interrupted state without becoming a giant action history.

## Activity summary from the beta draft

| Actividad | Costo resumido | Beneficio resumido |
|---|---|---|
| Avanzar | Sin otra Actividad exigente | Avanza hasta 3 zonas. |
| Ayudar | Mismo costo que la Actividad ayudada | Da ventaja a una prueba de otra criatura. |
| Buscar | Avance reducido o sin avance + prueba | Encuentra indicios según el foco elegido. |
| Descansar | Sin avance | Acumula turnos para descanso corto/largo. |
| Emboscar | Sin avance + prueba | Ventaja en iniciativa o empezar oculto, según resultado/circunstancias. |
| Examinar objeto | Sin avance + prueba | Obtiene información sobre objeto/indicio. |
| Interactuar | Sin avance si exige tiempo/cuidado | Manipula un objeto o entorno. |
| Lanzar un conjuro | Sin avance + gasto normal | El conjuro produce sus efectos. |
| Manipular mecanismo | Sin avance + prueba | Abre/bloquea/altera/desarma mecanismo o trampa. |
| Montar guardia | Avance reducido + prueba | Alerta temprana o evitar ser sorprendido. |
| Moverse en silencio | Avance reducido + prueba | Evita detección. |
| Preparar defensa | Sin avance + materiales | Monta defensa o trampa improvisada. |
| Recuperar aliento | Sin avance + 1 Dado de Puntos de Golpe | Recupera PG sin hacer descanso corto. |
| Registrar | Sin avance | Recoge objetos evidentes/botín visible. |
| Tratar heridas | Sin avance + prueba si corresponde | Estabiliza/diagnostica o ayuda a Recuperar aliento. |

## Important Activity details

### Avanzar

No prerequisite. Characters advancing this way give up searching, quiet movement, guarding, looting, mechanism work, spellcasting, preparing defenses, ambushing and other demanding Activities during the turn. Intended for known/open areas or movement with speed instead of careful exploration.

### Ayudar

The helper must contribute reasonably. Specialized work may require appropriate tools, language, knowledge, spell, trait, training or capability. The helper uses the turn Activity and inherits the movement cost of the helped Activity. Help improves the chance of success; it does not duplicate the Benefit.

### Buscar

The player declares a concrete target/area and a **search focus**. Cost is reduced movement while searching on the move/entering a new zone, or no movement for a thorough search of a room/object/complex feature. The DM chooses an appropriate check, commonly Perception, Investigation, Survival, Arcane Knowledge, Religion or another suitable check.

Current search focuses:

- **Peligros físicos** — mechanical traps, pressure plates, wires, pits, darts, springs, collapse signs, obvious poison, unstable structures, weakened floors, dangerous mechanisms, burn marks, physical ambush signs/material threats.
- **Señales sobrenaturales** — runes, ritual circles, sacred/profane symbols, magical residue, summoning marks, seals, glyphs, impossible alterations, curses/strange phenomena and other supernatural traces; detection does not automatically reveal exact function.
- **Secretos** — secret doors, hidden compartments/passages, concealed hinges, false walls, disguised mechanisms/symbols/accesses and similar deliberately hidden structures.
- **Rastros** — tracks, blood, disturbed dust, odors, food remains, ash, mud, hair/scales, claw marks, drag marks, recent activity and creature passage.
- **Objetos ocultos** — hidden treasure, keys, documents, relics, supplies, weapons, jewelry and objects deliberately concealed in debris/corpses/furniture/containers.
- The player may propose another focus; the DM decides whether it fits an existing focus or another Activity.

The owner confirmed that multiple separate search-focus turns are intentional. Thoroughness consumes time; **the pressure is the point**.

### Descansar

Requires a location in which resting/non-strenuous activity is possible. No advancement. The beta uses **6 consecutive Dungeon Turns for a short rest** and **48 consecutive Dungeon Turns for a long rest**. Environmental changes may continue while resting.

### Emboscar

Requires reasonable knowledge/suspicion of an approaching potentially hostile creature plus a suitable tactical position. Normally uses Stealth, but terrain-focused preparation may justify Investigation, Survival, tools or another check; group checks are possible.

The draft's written Benefit is advantage on initiative against that creature **or** beginning hidden when circumstances allow. During 2026-09-10 discovery the owner additionally clarified the intended house-rule bridge as opening a **surprise round** when appropriate. Exact later Combat Desk semantics remain to be designed rather than silently rewriting this beta source note.

### Examinar objeto

Requires direct access to the object/material/inscription/etc. No advancement and normally an appropriate knowledge/investigation/medicine/tool check. Success yields useful information such as apparent function, age, origin, manufacture, symbols, approximate value, materials, condition, evident danger, likely use, associated tradition or general magical nature. It does not automatically reveal every magic-item property.

### Interactuar

Requires access to the relevant object/environment. Interactions needing care, force, time, tools, coordination or repeated attempts cost no advancement; trivial/riskless interactions may occur without consuming the Activity. The DM may call for a check when uncertain.

### Lanzar un conjuro

Requires normal ability/components. Normally no advancement and consumes the spell's normal resource. Ritual casting occupies the turn without a slot. Casting times over ten minutes consume as many Dungeon Turns as needed. The DM decides perceptibility through sounds, light, movement/components/effects.

### Manipular mecanismo

Covers locks, mechanisms and physical traps; tools may be required. Normally no advancement plus an appropriate check such as thieves' tools, Investigation or Athletics. Failure may trigger the device, create noise, jam it, damage tools or consume materials. Purely magical traps require a suitable method at DM discretion.

### Montar guardia

The player declares what direction/access/zone/threat is watched. Reduced movement plus Perception or an appropriate opposed/target check. Current draft offers `Alerta temprana` (initiative advantage against a threat appearing from the watched direction before the end of the next Dungeon Turn) or `Advertencia` (cannot be surprised by that threat and can warn allies who can perceive the warning).

### Moverse en silencio

Requires a reasonable way to avoid observation/detection. Reduced movement plus Stealth. If a group advances together silently, everyone benefiting normally chooses the Activity. Success avoids detection against appropriate passive/active Perception; an encounter may begin with enemies unaware of the character's initial position or with the character hidden when circumstances allow.

### Preparar defensa

Requires suitable place/materials. No advancement; may consume materials and complex preparation may take several turns/help. Can create a defensive position or an improvised trap. Improvised trap purposes include alarm, obstacle/delay, tactical advantage or minor improvised damage; they are not intended to replace complex dungeon traps.

### Recuperar aliento — explicitly beta

Requires at least 1 HP and an opportunity to stop/breathe/drink/bandage/etc.; unavailable in combat. Costs no advancement and **1 Hit Die**. The Activity may be used once, refreshing after a short or long rest. Roll the spent Hit Die and recover HP as though spent during a short rest, but this does not count as a short rest and does not recharge short-rest features.

This mechanic is deliberately retained for playtesting rather than removed for theoretical balance concerns.

### Registrar

Requires physically accessible contents such as corpses/open containers/shelves/bags/furniture/camps/debris/visible treasure. No advancement; large/disordered areas may take additional turns or limit what can be processed. It collects evident/visible material. Deliberately hidden/camouflaged material requires `Buscar` with an appropriate focus.

### Tratar heridas — explicitly beta

Requires being adjacent to the target and reasonable treatment means; tools/supplies may be required. No advancement and Medicine when the result is uncertain/serious/poisoned/diseased/pressured. It can stabilize, clean/bandage, diagnose, identify obvious poison/disease, splint, stop minor bleeding or assist `Recuperar aliento`.

When assisting `Recuperar aliento` in the same turn, the beta allows the creature to reroll the Hit Die but requires using the new result.

This mechanic is deliberately retained for playtesting.

## Proposed additions retained outside the beta source

D-0068 records but does not silently insert into the owner's beta rules:

- `(*) Exploración normal` — candidate Activity to make the existing ordinary 2-zone movement category explicit;
- `(*) Ocultarse` — candidate stationary hiding Activity distinct from `Moverse en silencio` and `Emboscar`;
- possible sound/activity focus under `Buscar` instead of a separate `Escuchar` Activity.

These remain **Proposed** until the owner explicitly accepts/revises them.

## Application-design consequences already decided

See D-0068 for the controlling application interpretation. In brief:

- players re-declare every Dungeon Turn;
- the app remembers/displays the previous Activity as reference;
- the app does not adjudicate zone geometry/movement;
- the Dungeon Turn controller should help track declared Activities and meaningful consequences with minimal DM input;
- a bounded Dungeon Turn log is useful and should mostly be produced automatically from operation;
- clocks are a separate reusable concept: some may be linked to Dungeon Turns, others independent;
- interruption into combat should eventually hand relevant opening state to Combat Desk;
- uncertain beta mechanics must remain testable rather than being designed out before playtest.
