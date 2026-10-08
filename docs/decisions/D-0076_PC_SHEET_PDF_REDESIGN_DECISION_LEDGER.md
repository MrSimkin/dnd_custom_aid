# D-0076 — Rediseño del contrato de exportación PDF de hojas de PJ (registro incremental)

**Fecha:** 2026-10-08 (Chile)  
**Responsable de decisiones:** propietario del proyecto  
**Estado:** REDISEÑO EN DISCUSIÓN; decisiones parciales aprobadas, contrato integral NO cerrado  
**Autorización de implementación:** NO. Solo discusión de producto y consolidación documental. No generar PDFs, modificar plantillas, cambiar código ni elegir bibliotecas.

## 1. Motivo y relación con el historial

El propietario reabre el diseño del contrato y de la arquitectura de exportación de hojas de personaje porque el sistema anterior no satisfizo adecuadamente el resultado esperado. El rediseño es prospectivo y no cambia retroactivamente los hechos de D-0074, sus baselines visuales, las aprobaciones anteriores o los informes de QA/repair.

D-0074 y los contratos de familias/continuaciones existentes siguen siendo evidencia histórica y autoridad de la implementación existente. Las decisiones expresamente aprobadas **en este registro** prevalecen para el diseño nuevo en sus puntos de conflicto; no deben interpretarse como funcionalidades ya implementadas ni como autorización para continuar la antigua fase de reparación. Los demás contratos no se consideran derogados por implicación. La relación definitiva entre las antiguas familias de exportación y el nuevo diseño sigue pendiente de decisión.

## 2. Concepto general y estado de las opciones

Se evaluaron tres familias de estrategias: (a) composición HTML/CSS con render a PDF; (b) composición SVG/PDF vectorial, con posible reutilización visual; (c) núcleo estable y anexos dinámicos.

**APROBADO — dirección de producto:** modelo híbrido, orientado a una hoja equilibrada, funcional, legible y completa. La hoja se concibe como un conjunto de **piezas o módulos** cuya disposición administra un sistema de composición.

**EN DISCUSIÓN — hipótesis técnica/estructural:** núcleo semirrígido, zonas modulares y anexos dinámicos. No hay elección definitiva de motor, formato intermedio, biblioteca, algoritmo de distribución, composición por página ni reutilización específica de templates antiguos.

No asumir reglas globales de tamaño fijo, síntesis o desbordamiento. Cada sección debe discutirse caso por caso y solo entonces adquirir requisitos aprobados.

**ACLARACIÓN DEL PROPIETARIO — distribución por páginas:** la primera página es relativamente compacta y menos modular/flexible que las secciones posteriores. No convertir su zona superior en una gran banda de cabecera independiente ni exigir que todos sus módulos comiencen a la misma altura. Los detalles de estructura y qué módulos coexistirán todavía están EN DISCUSIÓN. La mayor flexibilidad/composición modular corresponde a las secciones posteriores y se determinará caso a caso. No inferir un algoritmo global ni posiciones concretas.

## 3. Cabecera / identidad del PJ — decisiones APROBADAS

### 3.1 Alcance por página

- La **cabecera completa** de identificación del PJ aparece **solo en la primera página**.
- **Todas** las páginas, incluidos anexos, exhiben **el logo de D&D en la esquina superior izquierda Y el nombre del PJ**.
- En la primera página, el nombre del PJ forma parte de la cabecera completa; en las páginas posteriores se muestra el nombre del PJ sin repetir el resto de los campos de la cabecera.
- Ninguna página posterior repite la cabecera completa.
- La posición, tamaño y estilo del nombre del PJ en páginas posteriores quedan **PENDIENTES de diseño**; no inferir que acompañará al logo en la misma línea.

### 3.2 Elementos obligatorios de la primera página

La cabecera contiene:
1. retrato del PJ;
2. nombre del PJ;
3. nombre del jugador;
4. alineamiento;
5. clase / nivel;
6. raza (y subraza cuando corresponda).

### 3.3 Retrato

- Marco **cuadrado**, integrado en la cabecera de la primera página.
- Dos modos de visualización ofrecidos al exportar:
  - **Recortar para llenar** el cuadrado conservando proporciones;
  - **Ajustar imagen completa**, conservando proporciones y admitiendo márgenes.
- La selección afecta únicamente la salida PDF; no altera el archivo fuente del retrato.
- **Sin imagen:** presentar una **silueta genérica** dentro del recuadro cuadrado. Esta decisión sustituye **para el diseño nuevo** la regla histórica de D-0074 §7 que dejaba el área en blanco.
- Sin decidir aún: dimensiones, peso visual, proporciones relativas, estilo de la silueta, tratamiento predeterminado de imagen y diferencias entre ausencia real de retrato y archivo temporalmente inaccesible/offline.

### 3.4 Jerarquía tipográfica y campos

- **Nombre del PJ:** elemento visualmente protagónico; tipografía destacada y de mayor tamaño que el nombre del jugador.
- **Nombre del jugador:** visualmente secundario respecto del nombre del PJ.
- **Clase / Nivel:** formato compacto, p. ej. `Guerrero 8` o `Guerrero 5 / Mago 3`. No añadir el nivel total como dato separado. Pendiente el tratamiento de cadenas excepcionalmente largas.
  - **Distribución espacial APROBADA (Sección 01):** Clase / Nivel ocupa el ancho libre disponible **a la derecha del retrato cuadrado**, con prioridad de anchura frente a los campos secundarios. No debe extenderse por debajo del retrato para tomar una franja horizontal de página completa. La medida exacta, el tamaño de letra y el tratamiento de multiclases excepcionalmente largas continúan pendientes. Esta aprobación parcial **no** aprueba el boceto C completo ni la ubicación definitiva del nombre del jugador. La disposición de Raza/Alineamiento se aprobó por separado (ver debajo).
- **Raza:** incluir subraza cuando exista, p. ej. `Elfo — Alto elfo`; sin subraza, `Humano`.
- **Alineamiento:** nombre completo, p. ej. `Legal Bueno` o `Caótico Neutral`; nunca abreviaturas.
  - **Distribución espacial APROBADA (Sección 01):** **Raza y Alineamiento en paralelo**, en una misma fila **debajo de Clase / Nivel y a la derecha del retrato**; Raza a la izquierda y Alineamiento a la derecha. La decisión es solo sobre la distribución relativa; **APROBADO: Raza 60 % / Alineamiento 40 % del espacio asignado a su fila**. Quedan pendientes el ancho total de esa fila, las proporciones generales, márgenes, tipografías y el tratamiento de valores excepcionalmente largos. No se aprueba la ubicación definitiva del nombre del jugador ni el boceto C completo.

### 3.5 Sección izquierda de la primera página — dos variantes de exportación APROBADAS

El propietario aprobó que **ambas** organizaciones de atributos, tiradas de salvación y habilidades se ofrezcan **como opciones seleccionables al exportar**, sobre los mismos datos canónicos del PJ:

- **A — Agrupada por atributo:** cada atributo junto a su tirada de salvación y habilidades asociadas.
- **B — Listas independientes:** bloque de seis atributos, lista separada de tiradas de salvación y lista separada de habilidades.

Estas opciones son **variantes de presentación** de la sección izquierda, no personajes distintos ni páginas que deban emitirse simultáneamente. No se ha aprobado copiar las geometrías del PDF de inspiración ni mantener intactas las familias de exportación anteriores. Faltan medidas y visuales definitivos, así como el comportamiento ante estadísticas extra.

### 3.6 Expansibilidad de atributos y habilidades — requisitos APROBADOS / validación pendiente

**APROBADO por el propietario:**

- Las **dos** opciones de organización izquierda (A por atributo y B por listas independientes) deben **adaptar la primera página** para alojar datos personalizados, y no expulsarlos automáticamente a anexos.
- Objetivo declarado de capacidad de la primera página: además de los seis atributos normales, permitir acomodar **aproximadamente dos o tres atributos personalizados**, **con sus habilidades asociadas**. El propietario expresó «2 o 3»; **no** convertirlo todavía en una garantía cuantitativa exacta ni prometer que cualquier número arbitrario de habilidades por atributo cabe físicamente. **Diseñar y validar con escenario de tres**.
- Deben integrarse igualmente **habilidades personalizadas vinculadas a atributos normales**, no solo las vinculadas a atributos personalizados.
- Las habilidades personalizadas siguen la **misma semántica funcional** que las habituales: atributo rector indicado explícitamente en los datos (p. ej. `Conocimiento arcano (INT)`), bonificador, estado de competencia/pericia cuando corresponda, y el tratamiento que requiera la variante exportada. No inventar una regla de cálculo nueva por tratarse de un campo personalizado.
- **Cuando ya no quepa el contenido sin perder legibilidad, crear página(s) de extensión** y continuar allí toda la información restante. No omitir, truncar ni reemplazar por un resumen.
- Esta expansibilidad se evalúa para **ambas** distribuciones A y B; su redistribución física no tiene por qué ser idéntica.
- **Distribución vertical dinámica y ajustes deliberados APROBADOS (corrección expresa del propietario):** en la sección izquierda de la primera página, el contenido se distribuye verticalmente según la cantidad de atributos/habilidades, **pero los elementos de atributos SÍ pueden estirarse o comprimirse artificialmente** para aprovechar y acomodar el espacio (p. ej., alturas de bloques y separaciones internas), siempre protegiendo la legibilidad y la integridad de los datos. Se **tolera el espacio libre** si queda disponible; ello **no prohíbe** redistribuir o ampliar elementos deliberadamente. Esta decisión **reemplaza expresamente** la frase previa «No forzar estiramiento de grupos». No invadir horizontalmente la sección derecha solo para ganar capacidad. Al agotarse la altura utilizable sin poder ajustarla legiblemente, continuar en página(s) de extensión. El ancho exacto de la columna sigue pendiente.
- **Prioridad de los seis atributos originales APROBADA:** Fuerza, Destreza, Constitución, Inteligencia, Sabiduría y Carisma tienen **preferencia de colocación en la primera página** frente a atributos personalizados cuando el espacio sea limitado. No sacrificar ni desplazar innecesariamente atributos estándar para hacer caber extras. Las habilidades custom ligadas a atributos existentes mantienen su asociación y no se omiten por esa prioridad; si exceden capacidad, usan la continuidad correspondiente.
- **Cohesión de grupos APROBADA:** en la variante A (agrupada por atributo), mantener juntos en una misma página el atributo y sus habilidades **siempre que resulte viable**. Si el grupo es demasiado grande, permitir una continuación claramente vinculada en la extensión, preservando toda la información. En la variante B (listas independientes), respetar su organización propia; **no exigir artificialmente** juntar una habilidad de la lista con la tarjeta de su atributo. Los detalles de señalética y corte están pendientes.
- **Hipótesis contextual aún no cerrada:** probablemente no habrá módulos distintos de atributos/salvaciones/habilidades debajo de esa sección izquierda; la distribución del resto de módulos se discutirá en la sección derecha. Esta posibilidad no equivale todavía a aprobar todo el mapa de la primera página.

**EN DISCUSIÓN / PENDIENTE DE VALIDACIÓN TÉCNICA:** demostrar la capacidad de los escenarios 6+2 y 6+3 atributos (incluidas habilidades añadidas tanto a atributos normales como nuevos) con tamaños legibles y sin inutilizar los demás módulos de la primera página; distinguir capacidad base de cargas extremas; definir regla de colocación/continuación para grupos y listas, orden de lectura, indicadores de continuación y composición de la página extendida. El formato exacto del sufijo `(INT)` en cada variante todavía se diseñará: la asociación semántica es obligatoria, pero no se impone una etiqueta redundante dentro de cada grupo A.

**Antecedente histórico:** D-0074 §4 ofrecía modos *Extended Page*, *App Modified Sheet* y *Modified Sheet + Complete Extended Page*. La nueva prioridad explícita es **adaptar la página principal hasta donde permita la legibilidad y después extenderla**, sin aprobación automática de las tres modalidades históricas ni del comportamiento de duplicación.

## 4. Límites del acuerdo

**No están aprobados todavía:** tamaño de página o márgenes nuevos, altura/ancho de cabecera, colocación exacta de campos, columna del retrato, relación exacta logo/retrato, proporciones, tratamiento de nombres largos, detalles visuales del ícono de silueta, ubicación/salto de módulos distintos de cabecera, motor de PDF y criterios universales de overflow.

Los bocetos previos fueron **exploratorios**, no un diseño final confirmado. Las propuestas A–D de cabecera independiente no han sido aprobadas. La nueva aclaración de compactación obliga a explorar la convivencia de identificación y contenido funcional en la parte superior sin copiar la geometría del PDF aportado por el propietario. Ese PDF es **inspiración de resolución espacial**, no plantilla ni regla a reproducir. No trasladar decisiones de un módulo a otro sin consulta expresa.

## 5. Método de toma de decisiones — APROBADO

- Diseñar **sección por sección** y **una decisión concreta por vez**.
- Ofrecer alternativas con ventajas, desventajas y riesgos reales; no asumir conformidad por una sugerencia del asistente.
- Identificar claramente **APROBADO**, **EN DISCUSIÓN** y **PENDIENTE DE VALIDACIÓN TÉCNICA**.
- Priorizar funcionalidad, legibilidad e integridad de la información; no resumir, comprimir u omitir por una política genérica anticipada.
- Conservar los contratos y pruebas históricas sin atribuirles aceptación del rediseño.

## 6. Punto de continuación exacto

**SECCIÓN 01 — SECCIÓN IZQUIERDA: expansibilidad y continuidad de atributos/habilidades.**

La primera página conserva cabecera compacta y estructura relativamente restringida. En la sección izquierda ya están aprobadas **dos variantes de exportación** (A agrupada por atributo, B con listas independientes) y su **adaptación para 2–3 atributos personalizados y habilidades vinculadas tanto a estos como a los atributos normales**, con **extensión cuando no quepan**. **Aprobado:** crecimiento vertical dinámico con posibilidad explícita de **estirar/comprimir elementos de atributos** para utilizar el espacio, también aceptando vacíos residuales; prioridad para los **6 atributos originales**, grupos juntos cuando sea posible y continuación en extensión si falta capacidad. **Pendiente:** mecanismos concretos de corte/continuación por variante A/B y validación de capacidad. No inventar medidas exactas ni modificar el renderer.

**Decisiones más recientes:** silueta genérica sin retrato; logo + nombre del PJ en todas las páginas; primera página compacta; Clase / Nivel a la derecha del retrato; Raza/Alineamiento 60/40; variantes A/B seleccionables; adaptación de la primera página para unos 2–3 atributos personalizados con habilidades y habilidades custom ligadas a atributos normales; páginas extendidas para lo que no quepa. **Punto activo: mecánica de expansión/continuación y validación de capacidad**.

**Implementación:** BLOQUEADA hasta aprobación explícita de un contrato suficientemente completo y de las validaciones técnicas necesarias.
