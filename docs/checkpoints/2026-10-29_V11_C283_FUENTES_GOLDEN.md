# C283–C285 — Fuentes del golden, campos de P3 y nombre en Garamond (2026-10-29)

PR #173 DRAFT/NO MERGE. Anterior: [C272–C282](2026-10-29_V11_C272_MATRIZ_DIEZ_BLOQUES.md).

**Instrucción del propietario:** «se agregan las fuentes, continua con 10 bloques».

| Bloque | Qué | Base |
|---|---|---|
| C283 | Fuentes exactas del golden en el PDF V11 | Noto Sans Display Condensed Bold (rótulos), Noto Sans Regular/Bold (texto), EB Garamond 12 Bold (nombres). Anchos idénticos a los del golden (error 0,0 pt). Origen: paquetes oficiales de Debian (el golden se generó con esas fuentes del sistema) y el repositorio de EB Garamond. OFL 1.1, SHA-256 en `assets/fonts/pdf/text/README.md`. |
| C284 | Campos de P3 como el golden | Seis campos de cuatro franjas de 17 pt (rótulo + tres renglones), columnas de 263,48 pt; rótulos 7 pt #1d384f, valores 7,3 pt #24394b. Corrige el desborde de P3 que causaba la caja más alta de Noto y el corrimiento de páginas de C2. |
| C285 | Nombre del encabezado en Garamond | EB Garamond 12 Bold 13,4 pt #1d384f en todas las páginas. Las letras que esa fuente no tiene (acentos, eñe, rayas) salen de la EB Garamond Bold actual del mismo proyecto. |

## Hallazgos

- **Defecto del golden:** su EB Garamond 12 Bold no tiene letras acentuadas; el golden imprimió «[hueco]gico» y «Proyectil m». La app no pierde letras (prueba `g3HeaderNameUsesGoldenGaramondAndNeverDropsAccentedLettersAB`).
- **Noto es más alta que Fira** (caja 1,36 em contra 1,2 em): los ritmos medidos del golden se mantienen; un renglón del golden más estrecho que la caja (equipo especial, 7/9 pt) escala la caja al renglón. Las habilidades de B mantienen 9 por columna con paso 10,5 pt.
- **Auditorías:** C207 y la de manuscrito detectaban NOTAS por la altura de la caja de Barlow; ahora usan el rótulo de 12 pt y su línea base (+7 pt hasta la cuadrícula). La de P3 verifica la geometría del golden.

## Pendiente (siguiente)

- Encabezado de página: posición del nombre (golden x161,6, línea base 735,3), rombo decorativo y rótulo del módulo arriba a la derecha (golden 7,3 pt).
- Nombres de las fichas del Libro en Garamond 11,3 pt (golden), con su franja de encabezado más alta.
- Títulos de módulo (app 12 pt bajo la franja; golden 9,1 pt).
- Matriz: registrar evidencia de G3 de estos bloques con la CI exacta.
