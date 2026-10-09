# DnD Custom Aid — Plan técnico de implementación V11

**Fecha:** 2026-10-09 · **Estado:** plan V11 publicado en rama de trabajo; integración Kotlin NO implementada, NO fusionado.  
**Repo:** `MrSimkin/dnd_custom_aid`; `main` verificado al preparar el plan: `27bd199633f1068ca481c387973d38f35cbb0c48` (se debe verificar nuevamente al ejecutar).  
**Referencia de diseño:** `dnd_custom_aid_V11_Carta_revision.zip`, maqueta visual A/B aprobada en conversación por el propietario. **No usar el ReportLab/Python de V11 como renderer final.**

## A. Resultado que se persigue y frontera

Implementar en la aplicación una **opción de exportación independiente, opt-in, de Hoja de PJ V11**, tamaño **US Letter (612×792 pt)**, con variantes A/B seleccionables en la exportación; preservar sin modificaciones visuales las cuatro familias existentes (Fantasy, Custom v1, Custom v2 por atributo y por habilidad). Conservar los modos de exportación permanente/current snapshot, opción de Libro de Conjuros y manejo de retrato ya existente, según las capacidades verificadas de la App. Mantener un único contrato semántico y algoritmo de composición para Desktop y Android; usar adaptadores de dibujo mínimos cuando la plataforma lo exija. No realizar llamadas a nube para generar el PDF.

**En alcance:** captura/mapeo desde datos reales, paginador y compositor adaptativo V11, recursos tipográficos/logo/símbolos, UI de selección A/B y nuevo estilo, exportación local Android/Desktop, pruebas, QA documental y binario de prueba con procedencia exacta.

**Fuera de alcance:** rediseñar otras familias, migraciones de datos, cambiar sincronización/servidor/autenticación, implementar una API pública, nuevos anexos, reabrir estética aprobada, actualizar fuentes v8, adoptar Python/ReportLab en las Apps, publicarlo en `main` o desplegar servicios. No habilitar costes.

## B. Diagnóstico previo contrastado

1. El prototipo V11 es un generador independiente Python/ReportLab con datos sintéticos; no existe evidencia de equivalencia con el dominio real. Contiene funciones redefinidas sucesivamente (`spell_plan`, `spells_page`, etc.), fuente de pruebas en rutas absolutas `/usr/share/fonts/...` y logo textual provisional. **No portarlo línea por línea.**
2. `book_parts` detiene fichas más altas que una columna (`RuntimeError`), y algunos recorridos de Conjuros detienen niveles que superen una columna (`ValueError`). Implementar paginación fragmentable explícita con continuación y prioridad de texto íntegro.
3. `fit()` acepta un tamaño mínimo sin demostrar que el texto realmente cabe; la validación debe detectar el excedente, envolver o paginar, nunca comprimir sin límite ni cortar silenciosamente.
4. Los 1.104 checks declarados por V11 miden principalmente datos y geometría sintéticos. Son útiles como fixtures heredados, pero no prueban integración ni binario instalado.
5. La auditoría histórica de Mara registró fracaso real en una versión previamente validada por CI (Fantasy 45p, Custom v1 FAIL, Custom v2 28p/27p). Se exige auditoría del **artefacto candidato exacto** y generación sobre Mara en las cuatro familias anteriores además de V11.
6. El repo contiene `assets/fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf` y su `GUIDE.md`; los otros roles tipográficos están documentados. Verificar archivo, licencia y empaquetado real en ambas Apps. Logo: usar la pieza original de la hoja del propietario, conforme al uso personal indicado.
7. La documentación principal todavía señala la fase documental sin implementación. El primer commit documental debe **registrar la autorización y el diseño V11 aprobado**, y los ajustes que sustituyen las secciones históricas pertinentes; la nueva ruta se publica con `LATEST.md` + checkpoint coherentes, sin reescribir la historia.

## C. Contrato visual congelado que implementa V11

- Carta, marcos exteriores C2 dobles, interiores C2c, paleta blanca/azul niebla, **Barlow Condensed** para rótulos; Fira Sans valores como elección técnica inicial; **Symbols v8** para estados y glifos de juego (Font Awesome Free solo complementario); logo original D&D del asset de la hoja. Embed de fuentes permitido según recursos/licencias verificadas, sin entregar archivos de fuentes por separado al usuario.
- Cabecera idéntica en todas las páginas: logo + nombre del PJ en el mismo renglón; no duplicar nombre en P1. Dos variantes A/B exportables. Atributos (6 básicos y personalizados) con puntaje en rectángulo y modificador en óvalo solapado; salvación un poco más **arriba y a la derecha** respecto de V11. Una habilidad que aparece bajo su atributo no añade `(XXX)`; fuera de ese contexto muestra abreviatura de 3 letras mayúsculas.
- P1: identidad/retrato, combate, Ataques en tres columnas (bonificador estrecho), Rasgos en dos columnas cuando corresponda, bloque de Lanzamiento obligatorio aun sin magia (niveles 1–9; 10–12 solo por reglas épicas efectivamente presentes), munición hasta 20 marcas en grupos de 5 y `____ / X` cuando exceda; sin registros dejar reserva manuscrita útil.
- P2: Equipo/ Tesoro con sus dos columnas internas de objeto-cantidad/denominación-cantidad; Rasgos mecánicos en dos columnas de alto utilizable; Equipo Especial **ancho completo**, ubicación/nombre/descripción y estado ACTIVO leído del origen real, nunca inferido. Sin datos conservar reservas manuscritas.
- P3: **solo Trasfondo, Perfil e Historia/narrativa**; sin Rasgos mecánicos. Espacio manuscrito suficiente; párrafos digitales consecutivos y alineados con renglones (también cuando están vacíos).
- Lista rápida de Conjuros **solo con conjuros reales**: tres columnas, agrupada por Trucos 0 + niveles 1–9, todos visibles si existe la página; niveles en proporción a superficie; preparación no equivale a gasto; marca también en renglones vacíos; fuentes/slots solo reales, sin valores fingidos; repetición identificada y continuaciones si nivel/entrada crece.
- C2: composición dos columnas o mixta (NO vertical), agrupación adaptativa por roles, referencias físicas de ida/vuelta, Equipo Especial ancho completo; ningún registro perdido o repetido. **Relleno manuscrito NOTAS** con pequeño encabezado y cuadrícula completa únicamente en huecos disponibles. Estas Notas de relleno no son las Notas digitales C-N3.
- C-N3 solo para notas digitales realmente existentes, en páginas exclusivas. Libro opcional después del contenido y notas digitales: fichas de dos columnas con nombre, nivel, escuela y descripción completa; ficha íntegra si cabe en la siguiente columna, fragmentación rotulada si excede una columna completa.
- Última página física siempre solo **Notas** manuscritas: único encabezado, arriba renglones en dos columnas y abajo cuadrícula sin filas/columnas recortadas; no contiene texto digital, ni se usa para continuaciones.
- Nunca perder información real para ahorrar páginas. Prioridad: integridad → legibilidad y uso manuscrito → localización → cantidad de páginas. No inventar atributos, habilidades, monedas, fuentes mágicas, casillas, estados, reglas épicas ni valores por defecto de semántica desconocida.

## D. Diseño técnico propuesto

**1. Snapshot estable e inmutable** desde los modelos reales existentes de personaje/modo de exportación. Preservar IDs y relaciones (atributo↔habilidad, conjuro↔fuente/estado, Equipo Especial↔ubicación/ACTIVO, notas generales/tituladas, imagen original/seleccionada, etc.). Resolver hechos ausentes con ausencia explícita, no con valores ficticios. Sin acceso de red obligatorio.

**2. Adaptador semántico V11** que convierte el snapshot real en registros de presentación tipados con `sourceId`, tipo, origen y estado comprobables. Reutilizar el `PcSheetPdfRenderPlan` o su contrato actual tras inspección; ampliar cuidadosamente sin romper otras familias. Conservar opciones A/B por exportación. Registrar un manifiesto de IDs exportados y número de apariciones.

**3. Un compositor/layout determinista en Kotlin**: reglas tipográficas reales, medición del texto usando fuentes efectivas, reserva por módulos, asignación de columnas, paginación y continuación de entradas largas. Flujo: medir → decidir fragmentos → planificar páginas → asignar número físico → renderizar. Un único cálculo de saltos para las plataformas. Nada de resolver posiciones alterando manualmente offsets de cada fixture.

**4. Dibujo por plataforma**: preferir y extender el renderer PDF ya integrado; el proyecto documenta PDFBox 3.0.8 y `DesktopPcSheetWholeDraftRenderer`, pero el Worker debe identificar la ruta Android real antes de decidir compartición de backend. Si Android no puede reutilizar PDFBox, compartir al menos el plan de página íntegro y comprobar equivalencia estructural/visual entre adaptadores. Mantener la producción de familias previas inalterada.

**5. Recursos**: cargar por recursos empaquetados (sin ruta absoluta de sistema), obtener glifos v8 por codepoint del `GUIDE.md`, incrustar fuentes válidas en cada PDF, extraer logo de la hoja fuente sin rasterizar el PDF entero y verificar acentos/ñ/ceros/casillas y nombres largos.

**6. Publicación segura**: rama corta creada desde `main` vigente; commits incrementales trazables, PR no fusionada. Cambios documentales de checkpoint/routing junto con los funcionales. No modificar backend, secretos, servicios ni `main`.

## E. Fases y gates: una única ejecución acotada

| Gate | Trabajo | Para declarar PASS | En STOP |
|---|---|---|---|
| G0 — preflight | Verificar HEAD/CI, ruta canónica, adjunto V11, fuentes/assets, exportadores y escenarios históricos; fijar matriz de observaciones V11 e histórico Mara | Inventario de ruta real y matriz fuente→código→prueba; sin contradicciones materiales no resueltas | Falta ZIP, repo no accesible, cambio de HEAD incompatible, incapacidad de extraer recursos o conflicto de producto material |
| G1 — contrato y datos | Snapshot/mapper desde datos de verdad (Permanente/Actual), variantes A/B, spells, notas, equipo, narrativas | Tests unitarios de semántica, IDs completos, ausencias diferenciadas | Cualquier estado requerido no existe o es ambiguo; documentar una sola consulta material agrupada |
| G2 — paginación/layout | Contrato geométrico V11 y continuaciones exactas, relleno C2, Libro largo, encabezados, referencias físicas | Pruebas deterministas por fixtures heredados y nuevos límites; 0 pérdidas/duplicados | Truncado, bucle o crecimiento descontrolado |
| G3 — render/UI | Fuentes genuinas, logo, selección de estilo y variante, Android+Desktop | Compilación y generación local en ambas rutas aplicables; PDFs con fuentes incrustadas y Carta | El backend no soporta los recursos o la semántica es diferente entre plataformas |
| G4 — regresión | Casos sintéticos + Mara real × 4 familias antiguas + V11 A/B + vacío + largos | 100 % pruebas bloqueantes, PDFs inspeccionados, sin regresión de exportaciones congeladas | Fallo de cualquier familia o dato faltante; no se compensa con menos páginas |
| G5 — candidato reproducible | APK QA con versión/versionCode nuevos, binario/artifact SHA-256, commit, CI, archivos PDF realmente generados por ese candidato, Desktop equivalente | Matrix por observación FIXED/OPEN/CHANGED; comparación artefacto ↔ runtime verificable | Identidad del APK dudosa, diferencia material entre PDF del Worker y App instalada o ítems OPEN bloqueantes |
| G6 — cierre técnico | Entrega de PR + informe de auditoría externa/independiente, informe de riesgos residuales | 0 bloqueos y owner acepta promoción por separado | Dejar PR sin fusionar y entregar paquete de fallos mínimo |

**Matriz mínima de escenarios:** casi vacío/no lanzador; 6+2 y 6+3 A/B; custom extra 4+ con habilidades relacionadas; 386 IDs sintéticos extremo y contenido asimétrico; una lista de conjuros con un nivel que excede columna; un conjuro del Libro más largo que columna; notas generales y tituladas largas; moneda valor `0` vs ausente; objeto especial ACTIVO no derivable; retrato ausente/2 opciones Crop/Fit; nombre/etiquetas con tildes/ñ y cadenas largas; personaje Mara real en las 4 familias legadas y nueva V11 cuando el mismo snapshot sea accesible.

**Aceptación automatizada (bloqueante):** 0 IDs perdidos/duplicados; ninguna exportación fallida dentro del dominio válido; Carta en todas las hojas V11; última hoja verdaderamente final e intacta; no texto fuera de límites ni glifos invisibles ni marcas superpuestas; no valores o slots inventados; texto completo en Libro e Historia; todos los niveles de conjuros apropiados; todas las cuadrículas completas; referencias físicas válidas ida y vuelta; cero cambios de apariencia de las familias legadas fuera de tolerancias pactadas; paridad entre rutas Desktop/Android de información y paginado dentro de expectativas técnicas verificadas. Testear geometría **real** del PDF más allá de extraer texto (ej. rectángulos, bounding boxes, renderizadas comparadas, PDF fonts).

## F. Protocolo anti-loop estricto

1. **Una sola rama y una sola PR** para este objetivo; no crear ramas alternativas especulativas ni emitir una secuencia de prototipos V12/V13. V11 es golden visual, no plantilla de iteraciones estéticas.
2. **Tres pasadas por gate como máximo:** primera implementación + **máximo dos ciclos de corrección**, cada uno con diagnóstico causal distinto, prueba que reproduzca fallo y evidencia final. Repetir la misma acción sin hipótesis/evidencia está prohibido.
3. Si un gate falla luego de la segunda corrección, **STOP**: sin más código ni nuevos tests especulativos. Entregar diagnóstico, archivos/líneas, comando fallido, evidencia, riesgo y propuesta mínima de salida, en un solo informe. Retomar solo por nueva instrucción del usuario.
4. **No convertir recomendaciones en requisitos sobre la marcha.** Si un asunto es opcional, anotarlo como deuda futura fuera de alcance; no consume un ciclo. Si es bloqueante (pérdida, cuelgue, generación fallida, regresión), debe resolverse dentro del gate o STOP.
5. **No reabrir aprobaciones visuales de V11**; solo corregir desvíos mensurables. La nueva tipografía/logo puede obligar a ajustar la medida del texto, pero no a rediseñar secciones.
6. **No pedir al propietario respuestas ya dadas**: proyecto 100 % personal, logo original, tamaño Carta, A/B, último Notas, diseño V11 aprobado, dos ajustes menores, cero huecos injustificados.
7. **No afirmar PASS por CI solamente** ni por tests derivados del mismo modelo que generan los PDFs. Inspección independiente de PDFs finales y binario exacto. Un PASS parcial no reemplaza al gate restante.
8. **No hacer merge, despliegue, cambios de backend o publicación** como parte de esta ejecución. Finalizar entregando PR lista para revisión o paquete STOP.

## G. Entregables obligatorios del Worker para revisión independiente

- URL/branch/HEAD de PR no fusionada; diff enumerado con módulos tocados; ruta de código exportadora identificada en ambas Apps.
- Registro de decisiones V11 consolidado y corrección de rutas canónicas (`LATEST.md` + checkpoint), sin conflicto oculto con el brief antiguo.
- Matriz de trazabilidad `observación → componente → prueba → PDF candidato/página → FIXED/OPEN/CHANGED`.
- Mapa de campos fuente: propiedad real → semántica → destino PDF → respaldo de test; especial foco spell slots, conjuro, ACTIVO, moneda `0`, notas, retrato.
- PDFs reales y sus SHA-256; manifest de fuentes incrustadas, tamaño Carta, número de páginas por caso, IDs y continuaciones; composición comparativa A/B.
- CI/test logs y cobertura por familias; nota de ejecución manual/Android necesaria con pasos exactos. APK con `versionName/versionCode` ÚNICOS, SHA-256 y commit origen; Desktop artefacto equivalente.
- Informe independiente de limitaciones y peligros de implementación; no se admite autodeclaración de cierre sin artefactos.

## H. Lectura de cierre

Hasta que los gates G0–G6 hayan sido acreditados **no existe implementación V11 aprobada técnicamente**. Este documento es la revisión de arquitectura/preparación y evita equivaler un prototipo aprobado a una aplicación terminada. El propietario **ya aprobó la estética**, no debe participar en cada ajuste de ingeniería. Su única intervención final normal es aceptar o rechazar la promoción tras ver el paquete de evidencia.