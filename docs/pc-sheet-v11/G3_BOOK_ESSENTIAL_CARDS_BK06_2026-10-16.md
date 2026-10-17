# V11 G3 — BK06: Libro de Conjuros con ficha esencial realmente aprobada

**Fecha:** 2026-10-16, Chile. **Plan rector inalterado:** `docs/pc-sheet-v11/PLAN_TECNICO_IMPLEMENTACION_V11.md` desde 2026-10-09, gates G0→G6 y obligación añadida 2026-10-16 de validar **cada regla negociada** antes del QA propietario. Única [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173), rama `docs/pc-sheet-v11-approved-implementation-handoff`, DRAFT/UNMERGED; `main`, cuatro familias legacy y ZIP golden no cambiados.

## 1. Defecto contractual confirmado en PDF real

La aprobación explícita del propietario en **D-0076 §3.47 (alternativa A)** y el contrato `CONTRATO_VISUAL_APROBADO.md` exigen en cada ficha de Libro **NOMBRE, NIVEL, ESCUELA si el origen real la respalda y DESCRIPCIÓN COMPLETA**. **BK06 prohíbe** trasladar metadatos de Tiempo/Alcance/Componentes/Duración, etc., de las otras alternativas. Se comprobó que el PDF *verdadero de la App* para Ilyra anterior aún imprimía una ficha extendida con seis grupos de metadatos: `LANZAMIENTO ... | ALCANCE ...`, `COMPONENTES ...`, `DURACIÓN ...`, `FUENTE ... | PREPARADO ...`, otros campos y `NOTAS ...`. Esa salida incumple el acuerdo y difiere de las fichas esenciales observadas en los ocho PDFs golden aprobados (por ejemplo `v11_ordinario_6mas2_variante_B.pdf` y `v11_extremo_variante_B.pdf`).

**Dato de fuente verificado:** el modelo real `CharacterSpell` carece de campo `school/escuela`. Aunque el golden sintético incluya escuelas, **NO** se pueden adivinar por nombre, clase, notas, fuentes de magia ni descripción. Esta limitación es independiente y pendiente de trazabilidad/solución semántica **BK03/G1**, no autoriza inventar escuelas ni bloquea eliminar los campos extra vedados por BK06.

## 2. Código y pruebas de PDF real

[`7cf0194266e1e579912e945efd550a5dd47f33e8`](https://github.com/MrSimkin/dnd_custom_aid/commit/7cf0194266e1e579912e945efd550a5dd47f33e8): los **DOS renderizadores PDFBox V11, Android y Desktop**, producen una ficha del Libro que contiene solo **`spell.name`**, `NIVEL <spell.level>`, `DESCRIPCIÓN:` y el `spell.description` completo. Se eliminan **solo de la presentación opcional del Libro** las líneas automáticas técnicas y las notas de conjuro que no corresponden a la ficha esencial. Los datos almacenados originales, la página rápida P4, el índice y sus IDs quedan intactos. La composición sigue usando el medidor real, `keepTogetherIfFits`, fragmentación física de textos extensos y referencias del índice recalculadas a partir de las páginas definitivas. Las variantes A/B y la última hoja manuscrita son las originales.

Pruebas nuevas: `g3Bk06EssentialSpellbookCardsNeverPrintUnapprovedTechnicalMetadata` parte del respaldo auténtico de Ilyra; inyecta cadenas centinela en los **campos técnicos persistidos** y reemplaza una descripción por varios párrafos reales de prueba, verificando A/B, ausencia de los centinelas técnicos, presencia del comienzo y final de la descripción, fuente de nombre/nivel, P4 conservado y hoja final NOTAS. El guard `scripts/test_v11_spellbook_essential_card_contract.py` compara ambas implementaciones y bloquea la reintroducción de cualquiera de las líneas no acordadas. Pruebas originales del índice, Libro largo y no lanzador siguen habilitadas.

**Scaffold CI [#38990283697](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38990283697) SUCCESS** para exactamente el SHA de código `7cf01942...` (Kotlin/Desktop PDFBox, Android DEBUG, backend y DB); **QA traceability [#38990283731](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38990283731) SUCCESS**. No usar un build de otro SHA para aprobar.

## 3. Examen físico y regresión independiente

[Artefacto nativo de App **12 PDFs** #12085520133](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38990283697/artifacts/12085520133), ZIP SHA256 `892db1ac658835ccf4af80988c4208283a273e0ffb0634fdb94bdc87babd0e41`, CRC PASS. Solo PDFs genuinos del servicio Desktop; no ReportLab ni maqueta sintética como sustituto.

- Ilyra B: **13→12 páginas**, **25/25** fichas y **25/25** referencias del índice comprobadas por PyMuPDF hacia páginas físicas correctas. `DESCRIPCIÓN:` sigue apareciendo 25 veces. Las páginas de datos antes del Libro, coordenadas de palabras y vectores se mantienen invariables (solo cambian contadores físicos en pies cuando procede).
- Mara B: **14→13 páginas**, **24/24** fichas y **24/24** referencias del índice correctas; `DESCRIPCIÓN:` permanece 24 veces. Pre-Libro no alterado, última NOTAS intacta.
- Ocho PDFs sin Libro: mismo número de páginas que antes y páginas pre-Libro idénticas en palabras/ubicaciones y dibujo vectorial.
- Nuevos PDFs `V11-INTERNAL-PROOF-BK06-essential-spellbook-A/B.pdf`, **12 páginas cada uno**; descripción larga de Fire Bolt con inicio `BK06_DESCRIPCION_REAL_INTEGRA_V11` y final `BK06_DESCRIPCION_FIN`, nunca truncada, todos los centinelas de campos vedados ausentes de las páginas Libro, todas las páginas Carta 612×792 pt. Index **25/25** correcto en A y B. SHA256 A `d118403059d11d9dc03d36502885a4542df26200a60fd862ddc4256bfce22f93`; B `637b090479bd2036fa2c7615eb94727c37b6bc7e5c2956a7a866b114dec207b4`.
- Raster real del Libro A/B inspeccionado: texto legible dentro de los márgenes y de las dos columnas, sin cabecera técnica prohibida. La reducción de páginas solo elimina metadatos no contratados; el test independiente **no afirma similitud de toda la maquetación con golden**, que sigue pendiente G4.

## 4. Estado del plan y siguiente avance

`qa/MATRIZ_CONTRATO_V11_PRE_QA.json` conserva **105 IDs**; ahora `BK06.stage_checks.G3=PASS` **limitado** y con evidencia vinculada a prueba, run, SHA y PDF exactos, `BK06.stage_checks.G2=NOT_VERIFIED`, `BK06.status=NOT_VERIFIED`; `BK03` sigue sin escuela respaldada. El gate global sigue `G4=BLOCKED` y `active_candidate=null`. Evidencia histórica Mara de cuatro familias, 48 secciones D-0076, ocho PDFs golden auditados integralmente, paridad Android instalado y candidato exacto continúan pendientes; G5/G6/QA propietario **NO autorizados**. RCR-1 0/2; no merge ni cambio de diseño.

**Siguiente tarea de desarrollo:** seguir por los módulos reales donde exista defecto contractual demostrable; especialmente BK03 escuela sin dato, separación visual de fichas y corte de descripciones extensas, sin agregar campos no aprobados ni abrir un rediseño. Conservar pruebas por etapa y continuar el G3 con PDFs de la App, sin rehacer trabajo válido.
