# V11 G1/G2/G3 — Rasgos, checks por fase y título C2c (2026-10-16)

**Única rama:** `docs/pc-sheet-v11-approved-implementation-handoff` · **PR #173:** OPEN/DRAFT, sin merge. **Resultado delimitado:** Rasgos corregidos en PDF real Desktop y ambos renderers Kotlin compilados, título azul C2c; el gate **G4 SIGUE BLOCKED** y el QA del propietario **NO ESTÁ AUTORIZADO**. Preservar los 4 exportadores anteriores, golden original, main y registro QA owner.

## Corrección del contrato previamente omitido

Regla aprobada `D-0076 §3.9` y aclaración expresa del propietario 2026-10-16: **solo identidad de Rasgos, jamás descripción ni notas en P1, P2 o C2**. La gramática es `Nombre - Tipo/Categoría: Fuente` (ejemplo `Pericia - Rasgo de Clase: Pícaro`), sin inventar origen cuando no existe. La ficha **digital** continúa conservando `description`, `notes` y otros metadatos. Rasgos mecánicos no ocupan P3, distinta de los «rasgos de personalidad» narrativos. **Equipo Especial SÍ conserva su descripción.**

Antes: P1 imprimía solo nombre; P2/C2 imprimían `name (source): description / notes` y algunos IDs de P1 se reimprimían. Correcciones **productivas**:
- `shared/.../PcSheetV11TraitSummary.kt`: texto común tipado, seis categorías reales, fuente en blanco ≠ inventar fuente.
- `shared/.../PcSheetV11FlowSourceMapper.kt`: ahora proyecta lista de identificaciones concisas en vez de descripciones extensas (conserva nombre histórico de propiedad para compatibilidad).
- Ambos PDFBox V11 aislados Desktop/Android: P1 ajusta cada nombre completo a su ancho real con Barlow/Fira medidos, usa primeras entradas por orden fuente en 12 líneas escribibles; P2 excluye IDs ya impresos en P1, y C2 imprime solamente sufijos restantes de P2; 0 descripción/notas.
- Ambos PDFBox V11: P1 `RASGOS Y ATRIBUTOS` tiene **una cartela azul niebla de ocho vértices C2c** x312..570, PDF y368..382, dentro de la banda original, título auténtico **Barlow Condensed 9pt** y datos Fira Sans 7pt; sin mover marco/filas ni paginar por estética.

## Controles LOCALES durante G1, G2, G3 (además del G4 final)

La matriz `qa/MATRIZ_CONTRATO_V11_PRE_QA.json` tiene inicialmente **104 reglas**; ahora todas tienen `stage_checks` con nombre de fase, método/objetivo y ruta de prueba. **NO se convierten en PASS automáticamente**. Test `PcSheetV11TraitSummaryTest.kt` cubre gramática exacta, todas las categorías, fuente ausente y fuente íntegra en G1; prueba Desktop PDFBox `g2G3ApprovedRasgosAreIdentityOnlyOnceAcrossP1P2AndC2` crea 48 Rasgos fuente con identificadores independientes en A y B, comprueba ocurrencia única, ausencia de descripción/notas, destino correcto P1/P2/C2 y P3/Notas intactas. Test real `g3RealP1RasgosUsesClippedBlueCartelaInsideOriginalHeaderStrip` inspecciona operadores PDF y región. `scripts/test_v11_rasgos_render_contract.py` corre en CI verificando la paridad exacta entre el código de ambos renderizadores; no pretende sustituir Android instalado.

Comandos: `scripts/check_v11_pre_qa_gate.py --mode inventory` garantiza que existe cada obligación/ruta de check y no hay PASS falso; `--mode stage --stage G1|G2|G3` BLOQUEA cualquier gate local incompleto; `--mode release` exige **todos** los controles previos PASS con prueba exacta, todos los 104 globales, revisión 48 §§ D-0076, comparación de los ocho PDF golden y QA independiente. El checker de owner `check_v11_qa_registry.py --gate owner_qa` exige este último, sin excepciones tácitas. Incluso los PASS locales caducan en la liberación si no corresponden al commit exacto del futuro candidato.

## Evidencia primaria REAL, no solamente CI

**Scaffold #38964145115** SUCCESS (Kotlin completo, Android DEBUG, backend y DB) para el Rasgos solo-identificación, artefacto `v11-internal-real-app-pdf-proofs` **12073892786**. **Scaffold #38965077553** SUCCESS con primera cartela azul, artifact **12074970704**. **Scaffold #38965638462** SUCCESS para el título Barlow auténtico, artifact **12074454208**, generado por el **servicio Desktop de la App** y extraído/inspeccionado con PyMuPDF. **Scaffold #38965958407** SUCCESS para el guard reforzado que también comprueba pruebas de otras fases; QA ledger **#38965958429** SUCCESS. **Estos PASS no equivalen a QA del propietario ni G4.**

PDF final exacto para fuente Barlow en build `15b2b685bcdd2c84d9158b828c5396231a2e2b0f`:

| PDF App verdadero | Páginas Carta | SHA-256 individual calculado |
|---|---:|---|
| Aldren A | 7 | `8f0ca7eb9e15743c38d1e2c59e3b885592b645245da19455e6d4eb0888d2a36a` |
| Ilyra B | 13 | `9d7cef9de89b0053ff8f02e754c23cb69fe2df1150d744ec10e9d53456cb9517` |
| Mara A | 9 | `d35f17e07ab244e0e986e09e5957c321d0c24c6d4b1bdcbb10c6fda49f1729bb` |
| Mara B | 14 | `1e48129f7489bc6b3ddb340fdecdd386d5372ba654de027e449ddfd1248fea05` |

**Comparación independiente física** de esos cuatro PDFs con artifact original Rasgos ya corregido **12073892786** (antes de cartela): **misma cantidad física de páginas; mismo texto fuente y coordenadas de TODAS las palabras salvo el único título estilizado deliberadamente en P1; exactamente una figura vectorial añadida en P1 de cada PDF; cero nuevos vectores en el resto de las páginas**. Última hoja NOTAS manuscrita permanece. En ambos Mara, se cotejaron los **26 nombres de rasgos originales de fixture real contra PDF**, cada uno aparece **exactamente UNA vez**; las cadenas de las descripciones y notas de rasgo originales **no aparecen**. El ZIP final del artifact Actions 12074454208 tiene CRC PASS. Se abrieron también recortes físicos del título Rasgos de Ilyra y Mara: legibilidad y separación visual comprobadas sin nueva colisión en el bloque.

**NO afirmar:** que todo el golden ya se reprodujo, que Android instalado exportó este mismo PDF, que todos los 104 requisitos están verificados, que los antiguos 32 defectos de Mara se resolvieron, G4/G5/G6 PASS, ni aceptación del propietario. Dejar `qa/REGISTRO_QA_V11.json` original intacto, observaciones owner V11-QA-001/002 OPEN, RCR-1 0/2.

## Progreso y siguiente fase

Los defectos concretos de **descripciones de Rasgos** y **P1/P2 duplicados** ya tienen reparación productiva, pruebas Desktop, contraste externo de cuatro PDFs, nuevos checks locales por fase y P1 cartela Barlow. La matriz G4 debe seguir **BLOCKED** hasta que se concilien completamente las 48 decisiones y ocho golden y se verifiquen TODAS las reglas sobre Android+Desktop candidate. Proseguir por componentes específicos con evidencia, no ejecutar QA manual del propietario hasta gate G4 PASS; siguiente bloque visual a decidir por diferencia física medible en el golden (no por preferencia subjetiva).
