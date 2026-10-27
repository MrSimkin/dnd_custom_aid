# C234 — P3 Historia con el ritmo del golden (opción B del propietario)

Fecha 2026-10-26 (Chile). PR #173 DRAFT/NO MERGE. **Estado: PASS LOCAL FÍSICO (no G4).** Código `815a8bfd`: Scaffold [#40428255698](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40428255698) y QA [#40428255742](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/40428255742) SUCCESS (incluye el nuevo control de paridad). Los PDF de la CI son idénticos a los locales (96 PDF / 828 páginas). Contra la CI de `2d806db8` no se pierde ninguna palabra; solo 4 PDF de historias extremas tienen una página más.

Anterior: [C233](2026-10-26_V11_C233_P2_SPECIAL_EQUIPMENT_TALL_ROWS.md) (Equipo Especial P2, cierre C211 y comparación medida con el golden).

## Decisión del propietario

Tras ver tres vistas previas (`docs/pc-sheet-v11/qa/evidence/golden_gap_2026-10-26/P3_historia_opciones_A_B_C.png`), el propietario respondió: «Opción B: usa 7 pt sobre renglones de 17 pt como el diseño aprobado en Historia. Sigue con C2.» Quedó registrada en el contrato («Aclaración del propietario — Historia de P3 con el ritmo del golden»).

## Cambio (Desktop + Android, idénticos)

- `storyFont`: Fira 7 pt cuyo interlineado medido es exactamente 17 pt. La línea base queda 4 pt sobre la regla al pie de cada renglón, igual que en el golden (en `v11_extremo_variante_A.pdf` p3, los renglones son de 17,0 pt y el texto queda justo sobre la regla).
- P3: la historia se mide y se dibuja con `storyFont`, y las bandas azules alternas pasan a 17 pt.
- Continuación: la historia restante es un flujo propio (`p3StoryOverflow`) con el mismo ritmo. Las continuaciones de los campos de perfil siguen en 9 pt (`p3FieldOverflow`). La numeración de página continúa entre ambos flujos (`firstPageNumber`).
- Los campos de perfil de P3 no cambian.

## Pruebas

- Nueva prueba PDF `g3P3StoryUsesOwnerApprovedGoldenRhythmSevenPointOnSeventeenPointRowsAB` (Mara A/B). Mide en el PDF real:
  - cada línea de historia en 7 pt;
  - líneas base separadas exactamente 17 pt;
  - cada línea base 4 pt sobre la regla de su renglón;
  - los campos de perfil siguen en 9 pt.
- Nuevo control de paridad Android/Desktop `scripts/test_v11_p3_story_rhythm_contract.py`, registrado en el workflow de QA.
- `audit_v11_p3_c2_manuscript.py`: la franja de historia de la primera página exige 17 pt, y la continuación admite 12,42 pt (campos) o 17 pt (historia).

## Verificación local

- Pasan las pruebas Shared, Desktop (94 en la clase PDF, incluida la nueva), las Python y Android `assembleDebug`.
- Los 11 auditores pasan sobre 96 PDF / 828 páginas. P3/C2: 20.591 palabras alineadas a su regla y 816 franjas alternas.
- Comparación con los PDF de la CI de `2d806db8`: ninguna palabra perdida. Solo 4 PDF con historias extremas (P3-long-story-heading A/B y P3-three-paragraphs A/B) ganan una página de continuación.

## Pendiente

- Pendiente menor: las celdas de continuación de P3 empiezan en el borde de la banda (x=42), sin el pequeño margen interior del golden. Viene de antes y se corregirá junto con los márgenes de P3.
- **C2 «Habilidades, acciones y recursos» — PROPUESTA ESPERANDO DECISIÓN DEL PROPIETARIO.**
  - Hallazgo: la mayor parte de esta C2 en Mara son **acciones** (tipos ACTION / BONUS_ACTION / REACTION) y **recursos personalizados**. Ninguno de los dos existe en el golden, así que su disposición es una decisión de diseño y no un error medido.
  - Vista previa con datos reales de Mara y fuentes reales: `docs/pc-sheet-v11/qa/evidence/golden_gap_2026-10-26/C2_propuesta_acciones_recursos_R1_R2.png` (script en `tools/c2_preview.py`).
  - Propuesta: tabla ACCIONES con la gramática de la tabla de Ataques de P1 (acción | tipo | alcance/efecto, notas en una línea de 7 pt bajo la fila) y RECURSOS con casillas agrupadas de cinco, reutilizando la gramática aprobada de Municiones.
  - Pregunta al propietario: R1 (todas las casillas vacías y el texto «Quedan x de y») o R2 (las casillas gastadas marcadas con X según el valor guardado).
  - Los ataques que no caben en P1 ya existen en el golden (tabla ATAQUES) y se pasarán a esa tabla junto con este cambio.
