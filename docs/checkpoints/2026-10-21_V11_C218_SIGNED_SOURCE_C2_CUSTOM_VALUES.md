# C218 — signo numérico real de habilidades, modificadores y salvaciones C2

Fecha: 2026-10-21. Rama única `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT/NO MERGE. HEAD anterior `da77567e15ffdd385c35f69b14345767aa9faf04`; `main` remoto `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Scaffold del HEAD anterior #39696913225 en progreso al principio de esta sesión, QA #39696913281 SUCCESS; producto C217 validado en #39695802858 y #39695802842 SUCCESS.

## Brecha física auténtica frente a V11 aprobado

En el golden original `V11_Carta/pdfs/v11_extremo_variante_A.pdf` p4, ZIP SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`, los **totales de habilidad, modificadores de atributo y salvaciones con valor no negativo llevan signo +**, incluso +0; los valores negativos usan - y los ausentes no reciben números fabricados. El código C217 sí alineaba 490 totales a derecha, pero el PDF real `V11-INTERNAL-PROOF-Mara-A.pdf` p4 seguía imprimiendo `mod 1 / salvación 0` y valores `9` y `8` sin prefijo. En P1 el renderer YA emitía correctamente `+0`, `+1`, `+9` según la fuente, por lo que **C2 no respetaba la misma gramática aprobada**.

## Corrección de producto coherente C218 (Android y Desktop)

Ambos renderers crean una función local `signedC2(value: Int): String` que imprime `+N` si es N>=0 y `-N` si es negativo, sin alterar `PcSheetV11ExportData`, IDs, valores persistidos ni cálculos. Se aplica **en la única proyección original de texto C2** a tres familias del mismo módulo: `attribute:` (modificador y salvación real si existe, NO puntaje sin signo); `attribute-skill:` (totales reales ligados); y `skill:` genéricas separadas en C2 (total real). Estados ausentes `sin resolver` y `sin valor` siguen visibles literalmente. El validador físico fuente/offset C214 reconstruye exactamente el nuevo texto con la misma función; C215 coloca las figuras sobre los glifos reales ya medidos, C216 reserva la casilla vacía y C217 alinea el **número real con signo** a la derecha. Se mantiene la misma paginación compartida, sin crear ni duplicar ninguna entrada.

Guard de paridad `scripts/test_v11_c2_source_navigation_contract.py` ampliado y test Kotlin de PDF App real para Mara A: `mod +1 / salvación +0` (Cordura), `mod +4 / salvación +8` (Éter), `salvación sin valor` (valor ausente), `+9` y `+8` (habilidades ligadas); los casos de negativos y null también están definidos por la función y requieren inspección adicional del corpus donde existan.

**Estado al commit:** IMPLEMENTADO, CI exacto y contraste de 76 PDF Desktop anteriores vs nuevos **pendientes**. El desplazamiento natural de anchuras por el signo puede modificar wrapping o páginas C2: exigir fuente completa, misma identidad sin doble impresión, contornos y casillas no colisionando, layout G2 y no regresión P1/P2/P3/P4/CN3/NOTAS. No declarar PASS antes de comprobar. El alcance **no es equivalencia visual integral C204/G4**.

**Gates:** G0 parcial histórico; G1 PASS semántico previo; G2/G3 parciales; **G4 BLOCKED 105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 pendientes. C211 BLOCKED 3/3 normal y 0/2 RCR-1 estructural; C212–C217 PASS local acotado ya demostrado. Android instalado y aprobación técnica/visual del propietario pendientes; PR sin merge.

**Siguiente trabajo tras evidencia:** continuar conciliación por módulos con los ocho golden y D-0076, con cambios funcionales demostrados y conservación explícita de las fuentes; no reiniciar C211 ni promover G4 desde pruebas locales.
