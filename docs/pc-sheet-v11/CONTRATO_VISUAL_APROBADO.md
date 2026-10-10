# Contrato visual V11 — APROBADO por el propietario (2026-10-09)

**Estado:** APROBACIÓN VISUAL EXPLÍCITA; implementación y QA runtime **NO** aprobadas ni ejecutadas.
**Autoridad:** revisión iterativa V8→V9→V10→V11 por el propietario, última respuesta "la aprobación visual de la maqueta está dada ahora". Este contrato especifica el resultado aprobado, no declara que Kotlin lo genere.
**Referencia:** ZIP `dnd_custom_aid_V11_Carta_revision.zip` SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`. ZIP **incorporado a esta rama** en `docs/pc-sheet-v11/` y verificado desde GitHub Actions (run 38009729554; SHA-256, 20 entradas y 8 PDFs). Consultar `ARTEFACTOS_Y_PROCEDENCIA.md`.
**Jerarquía:** los acuerdos V11 posteriores del propietario prevalecen únicamente en los puntos incompatibles con el antiguo D-0076 y su brief. D-0076 conserva íntegra su historia; no reescribirla. Esta V11 representa una **opción nueva de exportación** sin alterar familias legacy.

## Decisiones globales congeladas

- **Carta** 612 × 792 pt; apariencia de hoja física de fantasía, blanco/azul niebla, no beige; marco doble exterior C2 y cartelas interiores C2c; márgenes imprimibles; ningún elemento cortado.
- Títulos/etiquetas: Barlow Condensed auténtica. Valores: Fira Sans como **elección técnica provisional**; medir y registrar el resultado sin inventar una aprobación estética no dada a la fuente de valores. Estados: Para Hoja de PJ Symbols v8 como prioridad, Font Awesome Free secundaria cuando corresponda. No distribuir archivos de fuentes fuera del programa.
- Logo D&D recuperado de la hoja propia del propietario; proyecto **100 % personal**. Cabecera **una misma línea con logo + nombre PJ** en toda página. No repetir nombre en P1.
- **Dos variantes seleccionables A/B** al exportar. El usuario no debe elegir una nueva disposición cada vez que hay overflow; la composición es automática y determinista.
- Deben existir renglones de escritura manual bajo texto impreso y en filas vacías; el texto se alinea al renglón, no lo pisa. Evitar huecos arbitrarios. Nunca descartar datos para ahorrar páginas.
- Ajustes menores finales pendientes de aplicar al V11 aprobado: etiqueta **SALVACIÓN ligeramente más arriba y a la derecha**; rellenar todos los huecos C2 utilizables con sección manuscrita **NOTAS** y cuadrícula completa, sin forzar páginas extras.

## P1 — Personaje/combatientes

- Cabecera compacta, retrato con alternativas Crop/Fit y silueta si no existe, identidad, clase/nivel, raza/alineamiento.
- Seis atributos estándares + personalizables; nota rectangular y modificador ovalado solapado, tamaño V11 aprobado. Salvaciones visualmente ancladas. En variante A habilidades bajo su atributo **sin** sufijo; habilidad separada de su atributo lleva **(XXX)** abreviatura de tres mayúsculas.
- Ataques en tres columnas; bonificador deliberadamente angosto; rasgos mecánicos resumidos, sin repetir descripciones.
- Lanzamiento de Conjuros P1 siempre presente aun en no lanzadores: 1–9 y 10–12 **solo** si reglas épicas reales. No inventar casillas numéricas. Municiones hasta 20 marcas agrupadas de cinco; para cantidad mayor formato `____ / X`. Sin datos, reserva útil.

## P2 — Inventario y rasgos

- Equipo ordinario con dos subcolumnas internas objeto/cantidad; Tesoro justo debajo en parejas denominación/cantidad, valores cero reales no se borran.
- Rasgos mecánicos en **dos columnas**, aprovechan verticalmente P2; siguen a C2 si no caben. **Nunca continúan a P3**.
- Equipo Especial ancho físico completo: ubicación/nombre/descripción/ACTIVO; estados **reales**, sin inferencias automáticas. Misma gramática con y sin datos, filas utilizables.

## P3 — Solo Trasfondo/Perfil/Historia

- Página narrativa completa, sin módulos mecánicos de Rasgos.
- Trasfondo deja superficie manuscrita suficiente. Historia y otras narrativas mantienen párrafos consecutivos sin intercalar renglones innecesarios; todo impreso alineado con la gramática rayada y con continuación cuando el texto crece.

## Lista rápida de Conjuros

- Página presente **únicamente** si existen conjuros reales. **Tres columnas** a la anchura completa disponible y repartición dinámica proporcional al contenido y la zona libre, no grandes bloques blancos.
- Siempre mostrar Trucos 0 + niveles 1–9 si hay página de conjuros (incluso niveles sin conjuros). Fuentes/preparación/gasto separados conforme a estados reales; no literalidades CL/PREP inventadas.
- Todas las filas manuales vacías incluyen casilla de preparación. No inventar slots o progresiones de ejemplo. Continuaciones por nivel si desborda.

## C2 — Extensiones compartidas

- Dos columnas preferidas, mixto cuando se requiera; **nunca una columna vertical gigante por defecto**.
- Atributos personalizados y sus habilidades conservan relación visual; sufijo (XXX) solo cuando no existe atributo junto a la habilidad.
- Equipo ordinario conserva subcolumnas interiores; Equipo Especial ancho completo incluso en C2.
- Agrupar módulos diferentes de manera compacta según contenido real; referencias bidireccionales a **páginas físicas finales**.
- Todo hueco físicamente apto sin módulo útil se rellena con una **pequeña cuadrícula completa titulada NOTAS** (anotaciones manuscritas incidentales). No generar una página C2 adicional únicamente para rellenar ni insertar renglones sin sentido.
- Estas cuadrículas de relleno **no son datos digitales Notas C-N3**: los datos digitales van en páginas exclusivas.

## Notas, Libro opcional y última página

- Páginas C-N3 exclusivas solo si existen Notas digitales generales o tituladas; nunca mezclarlas con C2.
- Libro de Conjuros es **único anexo opcional**, en dos columnas, fichas de altura variable nombre/nivel/escuela/descripción íntegra. Ficha completa pasa a siguiente columna cuando allí cabe; ficha mayor que una columna se parte legiblemente con continuidad (el Python V11 *NO resuelve* este caso).
- **Última página física obligatoria y enteramente manuscrita**: un solo título NOTAS, renglones en dos columnas arriba y cuadrícula entera abajo, sin última fila recortada; nunca usada para contenido digital.

## Criterios para no repetir la revisión visual

El propietario aprobó P1/P3/P4, P2, C2, final y variante B tras revisión V11. Los dos microajustes mencionados son obligaciones técnicas, no convocatoria a V12. Un error medido frente al golden podrá corregirse dentro del gate y límites fijados, pero nunca reabrir el diseño por preferencias del Worker.

**STOP obligado:** si la nueva fuente o datos verdaderos obligan a cambiar semántica o estructura y no hay equivalencia con el contrato; elevar **una sola** decisión material respaldada por evidencia, no interrogatorios en cadena.
