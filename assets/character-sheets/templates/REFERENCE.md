# Character Sheet PDF Visual and Terminology Reference

This document is a durable text companion to the owner's character-sheet PDF templates in this directory.

It exists so future work can use the terminology, page structure, and visual intent of the PDFs without requiring the owner to re-upload the binary files in every chat or tool session.

## Source templates

- `Hoja de PJ - 5.0 - Simkin.pdf` - 5 pages.
- `Hoja de PJ v2 - 5.0 - Simkin.pdf` - 5 pages.

Both source PDFs remain the authoritative visual references. This Markdown file is an index/interpretation aid, not a replacement for them.

## Usage rule

Use these PDFs as reference for Spanish character-sheet terminology and for the owner's preferred information grouping/layout.

Do not silently replace a PDF label with an English product term merely because the implementation currently uses one. When the app and PDF terminology disagree, record the mismatch and reconcile it with the owner.

Likewise, do not infer that every field visible in a PDF must exist in every app screen or data model. Product/data decisions remain governed by the repository's approved decisions and conventions.

Do not silently correct spelling, rule-system assumptions, or possible inconsistencies found in the source PDFs. Preserve the source wording in this reference and flag any proposed correction separately for owner approval.

## Terminology observed in the PDFs

### Core character/combat

Observed labels include:

- `Clase y Nivel`
- `Raza`
- `Alineamiento`
- `Puntos de Experiencia`
- `Siguiente Nivel`
- `Clase de Armadura`
- `Mod. Destreza`
- `Armadura`
- `Escudo`
- `Modificador`
- `Iniciativa`
- `Velocidad`
- `Dados de Golpe`
- `Puntos de Vida`
- `Puntos de Vida Máximos`
- `Bono por Competencia`
- `Inspiración`
- `Ataques` / `Ataque`
- `Arma / Conjuro`
- `Bonificador`
- `Daño` / `Daño / Tipo de Daño`

### Attributes, saves, and skills

The PDFs use Spanish attribute names:

- `Fuerza`
- `Destreza`
- `Constitución`
- `Inteligencia`
- `Sabiduría`
- `Carisma`

Saving throws appear as `Tirada de Salvación` / `Tiradas de Salvación`.

Skills observed include:

- `Atletismo`
- `Acrobacias`
- `Juego de Manos`
- `Sigilo`
- `Conoc. Arcano`
- `Historia`
- `Investigación`
- `Naturaleza`
- `Religión`
- `Medicina`
- `Percepción`
- `Perspicacia`
- `Supervivencia`
- `Trato con Animales`
- `Engaño`
- `Interpretación`
- `Intimidación`
- `Persuación`

`Hoja de PJ v2 - 5.0 - Simkin.pdf` contains two alternative first-page layouts: one groups skills beside their associated attributes, while the other uses dedicated `TIRADAS DE SALVACIÓN` and `HABILIDADES` lists.

Note: spellings/attribute abbreviations above reproduce the PDF source as observed; proposed corrections belong in a separate owner-approved decision.

### Spellcasting terminology

The PDFs use **`Lanzamiento de Conjuros`** as the character-sheet section label.

Associated labels include:

- `CD de Salvación de Conjuro` / abbreviated `CD Salv. de Conjuro`
- `Modificador de Ataque Mágico` / abbreviated `Mod. de Ataque Mágico`
- `Aptitud Mágica`
- `Nivel`
- `Espacios`
- `Espacios Gastados` / `Esp. Gastados`
- `Trucos` for level 0 spells/cantrips

The dedicated spell page in both PDFs provides sections for level 0 (`Trucos`) and spell levels 1 through 9, with spell-slot and spent-slot information presented at the level headers.

### Traits/background/biography

Observed terminology includes:

- `Rasgos y Atributos`
- `Otros Rasgos y Atributos`
- `Trasfondo`
- `Rasgos de Personalidad`
- `Ideales`
- `Vínculos`
- `Defectos`
- `Historia del Personaje` / `Historia`
- `Notas`

### Equipment and wealth

Observed terminology includes:

- `Equipo`
- `Monedas`
- `Piezas de Platino`
- `Piezas de Oro`
- `Piezas de Plata`
- `Piezas de Cobre`
- `Piezas de Eléctrum` (present in the original PDF)
- `Gemas / Joyas / Arte`
- `Objeto`
- `Valor P.O.`
- `Tesoro`
- `Municiones`
- `Otros`
- `Equipo Especial`
- `Ubicación`
- `Nombre`
- `Descripción`

Special-equipment body locations observed include `Cabeza`, `Rostro`, `Cuello`, `Mano Izquierda`, `Mano Derecha`, `Brazo Izquierdo`, `Brazo Derecho`, `Pecho`, `Piernas`, and `Pies`, plus blank/custom rows.

## Visual/page structure

### `Hoja de PJ - 5.0 - Simkin.pdf`

1. Main character sheet: identity/progression, armor/combat values, six attributes with saves/skills, compact spellcasting summary, attacks, and `Rasgos y Atributos`.
2. Equipment sheet: `Equipo`, `Monedas`, `Gemas / Joyas / Arte`, and `Equipo Especial`.
3. Narrative sheet: `Trasfondo`, `Rasgos de Personalidad`, `Ideales`, `Vínculos`, `Defectos`, `Otros Rasgos y Atributos`, `Historia del Personaje`, and `Notas`.
4. Dedicated spell sheet: `Trucos` plus spell levels 1-9.
5. Notes sheet: large `Notas` area plus a large square grid.

### `Hoja de PJ v2 - 5.0 - Simkin.pdf`

1. Compact main character sheet with skills grouped beside attributes, combat/attacks, `Rasgos y Atributos`, compact `Lanzamiento de Conjuros`, `Tesoro`, `Municiones`, and `Otros`.
2. Alternative main character sheet using dedicated `TIRADAS DE SALVACIÓN` and `HABILIDADES` lists while retaining the same overall combat/spell/wealth blocks.
3. Equipment/narrative sheet: `Equipo`, `Trasfondo`, `Vínculos`, `Ideales`, `Historia`, and `Equipo Especial`.
4. Dedicated spell sheet: `Trucos` plus spell levels 1-9.
5. Notes sheet: large `NOTAS` area plus a large square grid.

## Current QA terminology finding

### T-01 - `Quick Magic`

During Phase 4 owner phone QA, the installed app displays a section/settings terminology using `Quick Magic`.

The owner explicitly identified this as an inadequate Spanish-equivalent label and instructed that the PDFs be used as terminology reference.

The PDF source terminology for the corresponding spellcasting summary is `Lanzamiento de Conjuros`, with related labels such as `CD de Salvación de Conjuro`, `Modificador de Ataque Mágico`, `Aptitud Mágica`, `Espacios`, and `Espacios Gastados`.

Status: **open terminology reconciliation finding**. Do not silently rename during QA; record it for the post-QA correction/consolidation pass unless the owner explicitly asks to interrupt QA and fix it sooner.

## Future-agent continuity

When working on character-sheet UI or generated-sheet output:

1. Read this file and the directory `README.md` first.
2. Use the two PDFs themselves when binary/rendered access is available and visual fidelity matters.
3. Use this reference for terminology and page/grouping context when binary PDF access is unavailable.
4. Record any newly discovered PDF/app mismatch here or in the appropriate QA/decision document.
5. Do not ask the owner to re-upload these PDFs merely to recover terminology already captured here.
