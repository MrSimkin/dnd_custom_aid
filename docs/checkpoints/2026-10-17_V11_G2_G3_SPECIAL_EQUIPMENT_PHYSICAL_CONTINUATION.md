# V11 G2/G3 — Equipo Especial C2: identidad de un objeto al continuar a otra página

**Fecha Chile:** 2026-10-17. **Autoridad:** [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT/OPEN, rama `docs/pc-sheet-v11-approved-implementation-handoff`, sin merge; `main` remoto observado `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Alcance funcional y hallazgo

El contrato V11 aprobado exige Equipo Especial en cuatro columnas físicas (UBICACIÓN/NOMBRE/DESCRIPCIÓN/ACTIVO), íntegro y legible al continuar a C2. La salida anterior del servicio Desktop [Mara B / Equipo Especial p7](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195856/artifacts/12122340801) fue inspeccionada: la tabla y su NOTAS inferior ya tienen la gramática autorizada. El código de las extensiones `specialC2Rows` reimprimía sus cuatro encabezados al pasar de página, pero **la segunda parte de una fila cuyo campo descripción ocupa más de una página no vuelve a mostrar su NOMBRE/UBICACIÓN** (solo conserva los fragmentos restantes de la descripción). Esto podía dejar la continuación sin identidad de objeto en una página nueva. Es un hallazgo técnico independiente, no nueva observación del propietario.

**Otros dos microajustes ya presentes, NO repetidos:** P1 SALVACIÓN se desplaza +2.3pt horizontal y +2.1pt vertical respecto de la versión anterior, tal como consta en ambos renderizadores y en el PDF real Mara B; C2 ya imprime cartelas/grillas completas NOTAS en zonas útiles de páginas mixtas, tablas pareadas y especiales, con control de área libre. **Estas comprobaciones son puntuales, no aprobación global P105/C207.**

## Cambio acotado

- [Commit funcional `a7d34560265709fc0d10fa6993083abfef0462bb`](https://github.com/MrSimkin/dnd_custom_aid/commit/a7d34560265709fc0d10fa6993083abfef0462bb): renderizadores V11 **Android y Desktop** agregan en la banda **ya libre** a la derecha de `EQUIPO ESPECIAL` una referencia real `CONTINÚA EQUIPO: nombre` cuando el primer fragmento de la página viene de una fila que comenzó antes (`startLine > 0`). Si el nombre no cabe a 7pt con la fuente real, ofrecen `CONTINÚA ID: <UUID fuente>`; sin inventar identificadores, valores o estado activo. Falla de forma explícita si ninguna alternativa cabe, sin truncar.
- **No** añade páginas, no duplica ubicación/nombre/estado ni consume celdas descriptivas. Mantiene el diseño de cuatro columnas, alturas, cuadrícula NOTAS debajo de contenido, páginas de otros módulos y fuentes. Los cuatro exportadores legacy, Golden, `main`, persistencia y UI no se modificaron.
- Test `g3OversizedSpecialEquipmentKeepsSourceIdentityOnEveryC2PageAB` toma el respaldo real Mara y alarga **solo en memoria de prueba** la descripción de un objeto especial auténtico, sin guardar cambios al usuario. Genera PDF nativo de App Desktop en A/B, comprueba cuatro encabezados por página, marcadores inicial/final únicos, continuidad identificada en páginas posteriores, páginas Carta y NOTAS final.

## Evidencia de validación

- [Scaffold checks #39090496826](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39090496826): **SUCCESS** del commit funcional exacto, incluyendo Kotlin Shared/Desktop y compilación Android, backend/DB. [QA traceability #39090496818](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39090496818) comprobó la trazabilidad de QA (no aprobación owner).
- [Artefacto ZIP de 46 PDF nativos Desktop #12122961164](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39090496826/artifacts/12122961164), SHA-256 `4c24e7a376c71c8a16e077223d664653c8f76130fa59f8208ccaca457b93f2e7`, CRC íntegro. Frente a los [44 PDF anteriores #12122340801](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39089195856/artifacts/12122340801): **44/44** sin diferencias en cantidad de páginas, palabras y posiciones PDF redondeadas a 0.01pt, recuentos de trazados/imágenes por página. No se afirma igualdad de bytes ni comparación exhaustiva de cada comando PDF.
- `V11-INTERNAL-PROOF-C2-long-special-item-A.pdf`: SHA `8e0d63b0e56d85e2b7b5b17dd7e71f4ab63e88b4ce7b5baea974c59813c915b9`; variante B: SHA `a42f45f52adbf2f0432b6e4044eb766cb69f937aafeabf6501f00c2dca7da004`. **13 páginas Carta cada uno**, Equipo Especial C2 en páginas físicas 7–11, nombre fuente inicial en p7, contexto `CONTINÚA EQUIPO: QAC2ANCLA` en p8, p9, p10 y p11. Inicio/final de la descripción presente una vez cada uno; última página NOTAS manuscritas.
- Revisión independiente PyMuPDF del texto/posiciones y raster de bandas altas C2 p8 B y p11 A: cartel de continuación separado físicamente del título y del cuadro de cuatro columnas, sin corte ni superposición visible en áreas examinadas. **PASS G2/G3 LOCAL de identidad en continuación de Equipo Especial cuando el nombre cabe**.
- No demuestra visual/paridad Android instalado, rama alternativa ID con nombre demasiado largo, lectura óptica de todas las páginas, ni aceptación owner.

## Estado y siguiente paso

Se conservan **105 reglas globales NOT_VERIFIED**, **12 stage-checks PASS históricos**, matriz/registro QA originals sin reclasificar, `active_candidate=null`. G0 QA original de Mara instalada incompleta; G4 BLOCKED, G5/G6 pendientes. Contrato V11 visual aprobado sigue siendo autoridad para el resultado esperado, **no** aprobación de renderer ni nuevo permiso técnico. RCR-1 no activado. PR #173 permanece sin fusionar.

**Próximo bloque:** seleccionar el siguiente requisito material de G2/G3 aún NO demostrado contra fuente+PDF real, idealmente un borde de renderizado previsto por la matriz (incluyendo fallback por ID en un nombre genuinamente largo, cuando corresponda), y comenzar consolidación G4 de D-0076 §§3.1–3.48 y ocho golden. No repetir P105/C207 ni los casos C2 ya verificados. No abrir G5 ni pedir aprobación owner sin pasar G4.
