# Checkpoint — rediseño del contrato PDF de hojas de PJ

**Fecha:** 2026-10-08 (Chile)  
**Estado:** DISEÑO ACTIVO / DECISIONES PARCIALES / SIN IMPLEMENTACIÓN  
**Rama de implementación activa:** ninguna.

## Cambio de ruta autorizado por el propietario

El propietario confirmó que el sistema de exportación PDF anterior no obtuvo un resultado satisfactorio y reabrió el contrato y la arquitectura **en fase de discusión**. Se autoriza únicamente consolidación documental de las decisiones aprobadas. No se autoriza nuevo renderer, modificación de templates, generación de PDF ni inicio de la antigua Phase 3 de reparación como sustituto de este rediseño.

**Registro canónico de las nuevas decisiones:** `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md`.

## Estado previo que se preserva

La auditoría Phase 2 de Mara permanece **COMPLETE/PUBLISHED**, con su propio detalle bajo `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`. D-0074, los baselines y defect/acceptance ledgers anteriores permanecen disponibles y no se reescriben. La Phase 3 anterior estaba planificada, no iniciada; **no se debe inferir autorización actual para implementarla**.

El punto explícitamente cambiado frente a D-0074 es el retrato faltante: **silueta genérica** en el nuevo diseño en lugar de marco vacío. No declarar obsoleto el resto del contrato anterior sin decisión concreta.

## Continuación actual

**SECCIÓN 01 — DIMENSIONES Y DISTRIBUCIÓN VISUAL DE LA CABECERA**: discutir alternativas compactas de estructura relativa que contengan retrato cuadrado, nombre PJ, nombre del jugador, alineamiento, clase/nivel y raza en la primera página; logo D&D en la esquina superior izquierda **y nombre del PJ en TODAS las páginas, incluidos anexos**; cabecera completa solo en la primera. El diseño actual aportado por el propietario es inspiración sobre ahorro de espacio, **no** una plantilla a reproducir.

**Aclaración del propietario:** la primera página debe ser relativamente compacta y es menos flexible/modular que las secciones posteriores. No reservar por defecto una cabecera gigante ni imponer una rejilla completamente dinámica a la primera página. Las secciones posteriores admiten más flexibilidad, pero sus reglas particulares se discutirán sección por sección.

Los bocetos anteriores son exploratorios. No hay medidas exactas, ubicación del nombre en páginas posteriores, composición final, biblioteca, algoritmo universal de packing o regla transversal de desbordamiento aprobados.

**Actualización de continuidad (2026-10-08):** la sección izquierda admite dos variantes de exportación (A: habilidades agrupadas por atributo; B: atributos y listas independientes), adaptabilidad vertical con estiramiento/compresión de bloques de atributos y prioridad para los seis atributos originales. Objetivo sujeto a validación: alojar 2–3 atributos extra y habilidades custom sin perder legibilidad; excedentes a páginas extendidas. La fila de Raza/Alineamiento usa 60/40.

**Bloque 01 derecho APROBADO:** tres filas — CA/Iniciativa/Velocidad; Bono por Competencia/Inspiración/Dados de Golpe; PV actuales/PV máximos. Dados de golpe en forma de cadena simple (`5d10 / 3d6`), sin desglose o contadores. PV temporales y salvaciones contra la muerte quedan fuera de este bloque. Sin medidas ni maqueta final aprobadas.

**Bloque 02 — Ataques APROBADO como resumen:** tabla unificada que recoge la lista resumen ya prevista en la App, con columnas `ARMA / ATAQUE / CONJURO`, `BONIFICADOR` y `DAÑO / TIPO DE DAÑO`. No agregar columnas de detalle ni convertir el resumen en descripciones completas. Sin dimensiones definitivas ni política específica de desbordamiento todavía.

**Bloque 03 derecho — Rasgos y Atributos APROBADO:** cada rasgo aparece como entrada de lista únicamente con categoría/origen y nombre (p. ej. `Rasgo de Clase - Nombre 1` o `Rasgo de Raza - Nombre 2`). Nunca incluir descripciones de rasgos en la hoja, tampoco en extensiones. Clase/raza son ejemplos, no prohibición de otros orígenes reales de la App. Faltan criterios de altura, cortes y nombres largos.

**Opción C APROBADA para el reparto vertical Ataques/Rasgos:** ambos son módulos independientes con altura adaptable al contenido, espacio mínimo útil para anotar manualmente, aprovechamiento mutuo del espacio libre y continuación en extensiones cuando no cabe. Ninguno puede crecer hasta eliminar completamente al otro de la primera página. El diseño **no** fija números de filas, medidas, proporciones ni algoritmo; la reserva de escritura manual no obliga a crear áreas vacías enormes. Las páginas extendidas no deben duplicar grandes módulos ya agotados.

**Lanzamiento de Conjuros — contenido y composición conceptual APROBADOS en primera página:** módulo siempre visible; **niveles 1 a 9** con cuadros marcables por nivel, incluso para PJ no lanzadores (valores vacíos/escribibles). Tres estadísticas (Aptitud Mágica / CD Salvación / Mod. Ataque Mágico) **en una sola fila de tres campos**. Niveles en **tres columnas por filas**: 1/2/3, 4/5/6, 7/8/9. **Si se activan reglas de niveles mágicos épicos**, sumar **una sola cuarta fila** 10/11/12, sin sumarla cuando no están activadas y sin inventar niveles superiores. El estado/soporte real de esa configuración en la App debe ser verificado, no presumido. La hoja original representa espacios y gastos. Cuatro casillas por nivel en ejemplos son ilustrativas, **no** límite ni progresión. Siguen pendientes dimensiones, cantidad de cuadros, cambios con cargas extremas, posición dentro de la página y viabilidad impresa.

**Reglas de lenguaje visual transversales APROBADAS (2026-10-08):** renglones normales de **7 mm** de altura física nominal; **5 mm permitidos para habilidades** como variante compacta si se valida legibilidad; bandas de filas consecutivas **alternadas entre dos *hues*/tonos distintos** (no solo valor de gris); casillas marcables **atractivas, claras y prácticas**; la hoja impresa debe ser a la vez útil y estéticamente atractiva. Tonos azul niebla y marfil cálido, bordes y esquinas suavemente redondeadas son solo **exploraciones de estilo**, no colores/dimensiones finales aprobados. Las celdas de conjuros y cajas de estadísticas no quedan automáticamente sujetas a 7 mm; validar proporciones y tamaño escrito/impreso.

**Selección estética de trabajo APROBADA:** **A — Azul niebla clásico** como base visual. No confundir «A» estética con «A» de agrupación de atributos: las variantes funcionales A/B continúan aprobadas. Los colores precisos, tipografías, radios, marcos, adornos y composición exacta quedan abiertos, y más adelante el propietario quiere ver varias propuestas.

**Módulos inferiores — decisión posterior APROBADA (reemplaza la exploración anterior):** **Municiones se incluye en página 1** como módulo compacto con nombre/tipo de munición y casillas marcables; crece en número de filas según los tipos reales de la App. **Aclaración posterior de casillas APROBADA:** hasta **20 cuadros por UNA sola entrada lógica de Municiones**, agrupados **de cinco en cinco**. **Dentro de esa misma entrada se permiten dos SUBFILAS físicas de casillas**; no exigir 20 en una única hilera horizontal. 2×10 es una posible distribución, no una dimensión final obligatoria. La altura de esa entrada especial puede superar los 7 mm nominales para conservar casillas legibles/marcables. Si la cantidad excede 20, usar el contador compacto **`____ / X`** en vez de más casillas. No inventar el significado del numerador ni un valor específico de X. El ajuste horizontal físico con nombre del tipo de munición, tamaño utilizable de las casillas, estados sin munición, exceso de tipos, desbordamiento y dimensiones quedan abiertos. **Tesoro y Otros se trasladan a página(s) posteriores**; no se excluyen de la exportación ni se fija una página exacta. Azul niebla clásico es la dirección estética seleccionada, sin fijar valores cromáticos o geometría.

**Marco C2 elegido como base visual de trabajo:** «Marco doble discreto y casilla suavizada», compatible con **A — Azul niebla clásico**. No fijar radios, líneas ni colores definitivos.

**Decisiones tipográficas y de glifos APROBADAS posteriormente:** **Barlow Condensed** para **encabezados, subencabezados y etiquetas/rotulación**; la fuente de los **valores o contenido escrito/rellenado queda deliberadamente PENDIENTE**. **Versalitas** para títulos son posible estilo por evaluar con la tipografía verdadera; no confundirlas con mayúsculas. Símbolos: **Para Hoja de PJ Symbols v8 (última aprobada en repo) PRIMERO**, **Font Awesome Free SEGUNDO** como fallback/complemento. Verificar soporte real, mapas, licencia e incrustación de iconos. No sustituir una muestra real por una simulación de sistema ni aprobar T1/T2/T3 por asociación.

**Marcos interiores — C2c APROBADO posteriormente:** el propietario seleccionó **C2c — cartelas con esquinas recortadas** como referencia de **los cuadros interiores de valores/estadísticas**. Conservar **marco exterior C2 doble discreto** y estilo **A — Azul niebla clásico**. El filete delicado y pequeños remates del boceto son ideas a validar, no dimensiones ni adorno requerido en cada recuadro. **Casillas marcables pequeñas (Municiones, Conjuros) siguen simples, no C2c automáticamente**, priorizando lápiz y legibilidad. C2a/C2b quedan descartadas como base.

**PIN ACTIVO / PRIORIDAD CAMBIADA POR EL PROPIETARIO (2026-10-08):** POSPONER expresamente las pruebas ornamentales C2c, detalles de cuadros y casillas, muestras tipográficas finales de Barlow/versalitas y elección de tipografía para valores rellenados. No descartar ni revertir la dirección seleccionada (Azul niebla A + C2 exterior + C2c interior; Barlow para encabezados/etiquetas; fuente del propietario v8 antes de Font Awesome Free). **Condición para desanclar el PIN:** haber diseñado primero el **modelo estructural completo**, incluidas las **páginas posteriores**, con Tesoro, Otros, continuaciones/extensiones y sin omitir información. Recién entonces preparar pruebas de ornamentación **sobre una maqueta integral**, no tarjetas aisladas. **CONTINUACIÓN INMEDIATA:** estudiar la estructura y los módulos de la **página 2 en adelante**, una decisión por vez y sin fijar prematuramente páginas/medidas; después validar y maqueter la hoja completa. No autorizar código, plantillas ni PDF de producto.

Consultar una sola decisión cada vez, registrar su estado y detenerse antes de implementar. El propietario resuelve las decisiones de comportamiento y UX.

## Restricciones

- Trabajo documental solamente; no código, plantillas, PDF ni nuevas pruebas de renderer.
- Priorizar integridad de datos, legibilidad y utilidad en papel.
- Conservar antecedentes históricos y distinguir la exportación ya existente del rediseño futuro.
