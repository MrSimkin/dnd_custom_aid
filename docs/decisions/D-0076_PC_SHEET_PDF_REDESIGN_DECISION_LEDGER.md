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

### 3.7 Sección derecha / Bloque 01 — Combate y supervivencia (APROBADO)

**Contenido y organización general APROBADOS:** el bloque superior derecho de la primera página se compone de **tres filas compactas** y estos **ocho** campos:

1. Fila 1: **Clase de Armadura**, **Iniciativa**, **Velocidad**.
2. Fila 2: **Bono por Competencia**, **Inspiración**, **Dados de Golpe**.
3. Fila 3: **Puntos de Vida actuales** (visualmente destacados) y **Puntos de Vida máximos**.

**Dados de Golpe:** únicamente una expresión textual compacta, p. ej. `2d6` o `5d10 / 3d6`. **No** añadir desgloses, controles de dados usados/disponibles, casillas ni otras estadísticas en este campo.

**Expresamente FUERA de este primer bloque:** **PV temporales** y **salvaciones contra la muerte**. No reintegrarlos en esta zona por deducción del asistente.

**Pendiente de validación visual:** dimensiones físicas, proporciones, espaciado, tipografías, decoración, estado de Inspiración y presentación precisa de valores. El boceto de tres filas es autoridad **estructural**, no maqueta final de píxeles. Los valores mostrados en bocetos son ficticios.

### 3.8 Sección derecha / Bloque 02 — Ataques (estructura y semántica APROBADAS)

**APROBADO:** tabla **unificada** de resumen de ataques, armas y conjuros, con **tres columnas**, en este orden:
1. **ARMA / ATAQUE / CONJURO**;
2. **BONIFICADOR**;
3. **DAÑO / TIPO DE DAÑO**.

El propietario aclara que este bloque es **la lista resumen de ataques ya concebida en la App**. El exportador debe presentar el resumen correspondiente al PJ, sin reinterpretarlo como inventario completo, ficha detallada del arma/conjuro ni agregar columnas de alcance, propiedades, descripciones extensas o efectos por iniciativa propia. La tabla puede mezclar armas, ataques y conjuros en una sola lista. El contenido factual debe provenir del modelo/resumen existente de la App, no de valores inventados.

**Pendiente:** número de filas que caben en la primera página, alturas/ancho de columnas, tipografía, saltos de línea, orden de entradas según reglas de la App, datos personalizados y tratamiento del excedente. No aprobar cantidad fija de filas, cortes ni extensión específica de este módulo sin discutirlo. Los valores de las maquetas son ficticios.

### 3.9 Sección derecha / Bloque 03 — Rasgos y Atributos (semántica APROBADA)

**APROBADO por el propietario:** mostrar los rasgos del personaje en una **lista de entradas concisas sin descripciones**. Cada entrada identifica su **origen/categoría** y su **nombre**, por ejemplo:
- `Rasgo de Clase - Nombre 1`
- `Rasgo de Raza - Nombre 2`

La denominación del módulo es **«Rasgos y Atributos»** (como en la hoja de inspiración). El patrón de una entrada es **origen - nombre**, tomando las categorías reales y nombres del personaje registrados por la App. Los ejemplos de clase y raza son ilustrativos: **no** implican excluir otros orígenes válidos que el modelo de la App pueda contemplar.

**Regla negativa explícita:** **no imprimir descripciones de los rasgos en la hoja de personaje**, **ni siquiera en páginas de extensión**. No convertir este módulo en fichas explicativas, ni añadir efectos, reglas desarrolladas u otro texto descriptivo. Su función es identificar/listar, no documentar.

**Continuidad y presentación aún por definir:** cantidad de elementos visibles en la primera página; ubicación y altura respecto a Ataques y otros módulos; cuándo mover excedentes a extensiones; manejo de nombres excepcionalmente largos; tratamiento visual para otros orígenes y datos custom. La lista se preservará íntegra sin omisiones; «una línea por rasgo» describe el formato de entrada, no una autorización para cortar texto largo o fijar cantidad de renglones.

### 3.10 Ataques + Rasgos y Atributos — reparto vertical OPCIÓN C (APROBADO)

**APROBADO por el propietario:** composición **dinámica con un mínimo útil para escritura manual** entre los módulos independientes de **Ataques** y **Rasgos y Atributos**, dispuestos de forma vertical en la sección derecha debajo del bloque 01 de Combate.

- Ambos módulos conservan una **reserva razonable de espacio escribible**, incluso con pocas entradas, sin imponer una tabla completamente fija.
- Su altura puede **adaptarse a la cantidad de contenido** real de cada módulo. El espacio que no utilice uno puede aprovecharlo el otro, sin fabricar paneles vacíos de gran tamaño.
- **Ninguno debe desaparecer ni quedar completamente expulsado de la primera página por el crecimiento del otro**. Cuando la información no quepa legiblemente, trasladar el excedente a una o más páginas de extensión, conservando las entradas sin duplicaciones, omisiones ni descripciones de rasgos.
- En extensiones, no repetir sin motivo grandes andamiajes o zonas vacías para módulos ya agotados.
- La reserva manual es una **regla de producto y uso en papel**, no autoriza filas vacías ilimitadas, descartar contenido, ni sacrificar otros elementos esenciales de la primera página.

**EXPRESAMENTE SIN FIJAR:** número de filas de cada módulo, mínimos de filas, medidas físicas, proporciones de altura, umbrales de salto, orden detallado de los excedentes y algoritmo de reparto. Los casos numéricos en wireframes son exclusivamente ilustrativos. La composición debe reconsiderarse cuando se definan los demás módulos de la primera página y validarse con casos reales antes de su implementación.

### 3.11 Lanzamiento de Conjuros — presencia en página 1 OBLIGATORIA (APROBADO)

**APROBADO por el propietario:** el módulo **«Lanzamiento de Conjuros» debe estar presente en la primera página de la hoja de PJ aun cuando el personaje no tenga capacidad de lanzar conjuros**. **No es condicional a ser lanzador** y no puede omitirse, ocultarse o colapsarse solo porque los campos carecen de valores. En esos casos se presenta el módulo con campos sin rellenar, preservando la utilidad de la hoja en papel y sin inventar estadísticas de lanzamiento.

**NUEVA APROBACIÓN EXPRESA:** el módulo de Lanzamiento de Conjuros de la primera página debe **ofrecer siempre los NUEVE niveles de conjuros, del 1 al 9**, con **cuadros/casillas para marcar** por nivel, también en PJ que no lanzan conjuros. No suprimir los niveles altos al adaptar la hoja a un personaje de nivel bajo ni eliminar las casillas cuando aún no tiene espacios. Conservar el significado visual de la hoja de referencia: identificación de nivel, espacios y seguimiento de espacios gastados, con espacio escribible. El PDF del propietario muestra círculos para gastos; la nueva exigencia expresa es **cuadros marcables**, no reproducción literal de los círculos.

**Composición compacta APROBADA (concepto, aún sin medidas):**
- **Una sola fila con tres campos independientes**, en este orden conceptual: **Aptitud Mágica**, **CD de Salvación de Conjuro** y **Modificador de Ataque Mágico**. Son tres campos uno al lado del otro, no una columna vertical de estadísticas. El orden exacto de rotulación / microestilo es perfeccionable en la maqueta.
- **Matriz de espacios de conjuro de TRES columnas** y lectura natural **por filas**: primera fila niveles **1, 2, 3**; segunda **4, 5, 6**; tercera **7, 8, 9**. Cada celda conserva nivel, identificación/espacio disponible y **cuadros marcables** de seguimiento. El objetivo es altura compacta, sin comprometer legibilidad al imprimir.
- **Ampliación épica CONDICIONAL:** si las reglas de niveles mágicos épicos están **activadas** en el contexto del personaje, agregar **solo una cuarta fila** con **niveles 10, 11, 12**, de izquierda a derecha. Si NO están activadas, los niveles 10–12 **no** aparecen; los niveles 1–9 **sí** aparecen siempre, incluso en no lanzadores. No inventar niveles 13+.
- La aprobación es de **disposición y comportamiento esperado**; la existencia/forma concreta de la opción de reglas épicas en la App, y sus datos aplicables al PJ, **deben verificarse técnicamente**. No asumir una bandera ya implementada a partir de esta decisión.

**No aprobados aún:** tamaños físicos y ocupación de columnas, número real de cuadros por nivel y manejo de cifras mayores, etiquetas compactas, ubicación del módulo respecto de otros, detalles de impresión, reglas de paginación. Los esquemas que dibujan cuatro cuadros por nivel son **ilustrativos** y NO autorizan fijar ese número ni asumir un máximo de espacios. Preservar marcas usables a mano, los espacios registrados y los gastados; no inventar datos por el simple hecho de que un nivel sea visible.

**Fuente visual de inspiración:** la hoja aportada por el propietario contiene niveles/espacios de conjuro, espacios gastados, CD de salvación de conjuro, modificador de ataque mágico y aptitud mágica. La **presencia del módulo, niveles 1–9, cuadros marcables, una fila de tres estadísticas y matriz horizontal de tres columnas con fila épica 10–12 condicional** están aprobados como estructura. Quedan **EN DISCUSIÓN/VALIDACIÓN** medidas, número de casillas, comportamiento ante cantidades altas, posición física respecto de otros módulos y extensión. El wireframe no es plantilla final.

No interpretar esta aprobación como obligación de imprimir descripciones de conjuros o datos ficticios; se refiere a la presencia del módulo en la hoja base.

### 3.12 Sistema visual transversal — filas, tonos y casillas (reglas del propietario APROBADAS)

**Reglas generales de diseño declaradas por el propietario (2026-10-08):**

1. **Renglones/líneas de uso general: 7 mm de altura nominal al imprimir**. Aplicar a módulos de entradas repetidas que utilicen renglones (p. ej., tablas de ataque y listas de rasgos). La medida es un objetivo explícito de diseño físico, no los píxeles de los bocetos de pantalla. Si un registro necesita más de una línea para no recortarse, medir y adaptar su altura preservando lectura y contenido.
2. **Habilidades pueden usar 5 mm por renglón**, como variante compacta admitida por el propietario, sujeta a legibilidad y uso en papel. No generalizar 5 mm a todas las secciones, ni afirmar que todo nombre/bono cabe en una sola línea de 5 mm.
3. **Bandas alternadas por fila con distintos tonos (*hues*)**, no solamente alternancia de gris más oscuro/claro. Cada renglón consecutivo debe distinguirse visualmente de su vecino con fondos discretos y agradables; los colores concretos aún no se aprueban. El boceto explora tonos azul niebla y marfil cálido **solo como muestra**, cuidando contraste y bajo consumo de tinta y que la lectura también funcione impresa en escala de grises.
4. **Casillas de marcado estéticamente cuidadas y realmente utilizables**: cuadradas, claramente delimitadas, sin tamaño tan pequeño que dificulte marcar con lápiz; considerar esquinas suavizadas y trazo limpio. No aprobar todavía dimensiones de casilla, radios, grosor, colores ni cantidad de casillas por nivel; probar en impresión física.
5. **Criterio transversal:** la hoja debe ser **práctica Y estéticamente atractiva**. Ni la compacidad justifica una tipografía ilegible ni la decoración puede consumir el espacio de escritura o entorpecer la consulta rápida.

**Alcance y pruebas pendientes:** la pauta de 7/5 mm se refiere a altura de líneas/renglones de listas/tablas, NO a que cada caja de estadísticas, atributo o celda de la cuadrícula mágica deba medir exactamente 7 mm. Las alturas de componentes multilineales pueden diferir. Validar a escala de impresión real, contraste y tintas, continuidad entre páginas, nombres largos, y adaptación de 5 mm en habilidades. No convertir una muestra de estilo en plantilla ni layout integral aprobado.

### 3.13 Línea estética base A — Azul niebla clásico (SELECCIONADA)

**Aprobado por el propietario como DIRECCIÓN ESTÉTICA BASE PROVISIONAL:** de las tres alternativas visuales exploradas (A Azul niebla clásico; B Azul niebla elegante; C Azul niebla arcano), se selecciona **A — Azul niebla clásico** como base de los siguientes wireframes y pruebas. No confundir esta **opción A estética** con la **variante A de agrupación de atributos**: las dos variantes funcionales de atributos A/B **siguen vigentes**.

El lenguaje visual de referencia A usa encabezados claramente diferenciados, marcos contenidos y tonos suaves asociados al azul niebla. El propietario quiere comparar varias propuestas más adelante; **NO** se aprueban aún hexadecimales específicos, identidad visual definitiva, textura/decoración, fuentes, grosores, radios ni geometría de recuadros. Conservan autoridad las reglas ya aprobadas de filas 7 mm / habilidades posiblemente 5 mm, bandas alternadas en diferentes matices y casillas prácticas y estéticamente cuidadas.

### 3.14 Módulos inferiores potenciales de página 1 — evaluación abierta

**Exploración solicitada por el propietario:** revisar si **Tesoro**, **Otros** y **Municiones** realmente necesitan vivir en la primera página. El propietario **duda expresamente** de los tres, en especial de Tesoro/Otros; por ahora **NO** están aprobados como módulos obligatorios de página 1 ni como eliminados de la exportación.

- **Municiones — CANDIDATO VISUAL PRIORITARIO EN PÁGINA 1:** si se incorpora, el propietario indica que su ubicación tentativa inicial es **aquí**, en la primera página, y quiere verlo dibujado. Estudiar módulo de seguimiento compacto con casillas de marcado legibles, nombre/tipo de munición si existe en datos, reserva escribible compatible con las reglas de producto. No aprobar aún presencia obligatoria, cantidad de filas/casillas, controles por tipo o paginación.
- **Tesoro — PENDIENTE:** comparar integración compacta en página 1 con traslado a página posterior de pertenencias/inventario; no inventar contenido monetario ni decidir omisión.
- **Otros — PENDIENTE:** cuestionar un módulo genérico sin función clara frente a áreas de anotaciones/notas ya existentes; no eliminar información asociada ni asumir un destino definido.

**Riesgo de capacidad identificado para estudio:** la columna derecha ya considera Combate, Ataques, Rasgos y Atributos y Lanzamiento de Conjuros obligatorio (niveles 1–9, posible 10–12 épico). Encajar además Municiones/Tesoro/Otros exige validar altura y ancho físicos, protección de espacios para escritura manual y continuidad, especialmente porque el módulo de conjuros tiene una matriz horizontal de tres columnas. El boceto inicial con Municiones es exploratorio; NO demuestra cabida a escala real ni aprueba el mapa completo de página.

### 3.15 Ubicación y formato de Municiones / Tesoro / Otros — APROBADO (sustituye la hipótesis de §3.14)

**Decisión expresa del propietario:** la **primera página sí incorpora el módulo «Municiones»**. **Tesoro y Otros NO se incluyen en la primera página** y se trasladan a **una o más páginas posteriores de la hoja**. Esta es una decisión de **ubicación**, no una orden de omitir datos, descartar módulos o fijarlos a la página 2. La hipótesis previa del §3.14 sobre Municiones como mero candidato y Tesoro/Otros en discusión queda **resuelta en cuanto a ubicación** por esta sección.

**Formato de Municiones APROBADO:**
- Sección **compacta**, apropiada para la estética de trabajo **A — Azul niebla clásico**, sin aprobar tonos exactos, bordes ni geometría final.
- Cada fila representa un **tipo/nombre de munición** con **cuadros marcables** para seguimiento en papel.
- **Cantidad de filas adaptable a los diferentes tipos de municiones** realmente asociados al PJ, sin cupo fijo aprobado; conservar el contenido real de la App y no generar municiones ficticias a partir de ejemplos de wireframes.
- **REGLA VIGENTE APROBADA — hasta 20 casillas por REGISTRO lógico de munición (aclaración del propietario):** mantener **una sola línea/entrada lógica por tipo de munición**, con hasta **20 cuadros marcables en total**, separados visualmente en **grupos de cinco**. **Se permiten una o DOS SUBFILAS físicas de cuadros dentro de ESA MISMA ENTRADA**, para no exigir 20 cuadros en una única hilera horizontal. Propuesta exploratoria coherente: 2 subfilas de 10 cuadros cada una, cada subfila con dos grupos de cinco; el usuario no ha fijado 10+10 como geometría obligatoria. Esta regla **sustituye explícitamente** la prohibición anterior de repartir cuadros en dos hileras. El número efectivo de cuadros hasta el límite sigue los valores reales del PJ; no se presuponen 20 en todos los casos.
- **Ritmo vertical:** la línea lógica de Municiones con dos subfilas **puede superar la altura nominal de 7 mm** necesaria para mantener casillas realmente utilizables a mano; los 7 mm siguen siendo pauta general de renglones de otras tablas/listas, no un techo rígido para esta entrada especial. Validar la capacidad real y no reducir las casillas a tamaño impracticable.
- **Si la cantidad supera 20:** sustituir esa secuencia de cuadros por **una presentación compacta de contador manuscrito** con forma literal `____ / X`. **X** es el valor de referencia correspondiente al registro; la semántica exacta del numerador escrito a mano (gastado/restante), y si hay otros valores que mostrar, deberán respetar los datos y se confirmarán al concretar el módulo, **sin inferirlos**. No generar 21+ casillas ni filas de casillas ilimitadas.
- **Condición física PENDIENTE:** comprobar que nombre/tipo y hasta 20 casillas, con separación por grupos, **caben horizontalmente sin hacerlas impracticables para marcar** en el espacio real de la primera página. Si no caben con legibilidad, reestudiar el ancho/composición del módulo antes de degradar dimensiones; el acuerdo de 20 en una sola línea no es una prueba de cabida. También siguen abiertos estado sin municiones, diseño exacto de cuadros, continuidad de demasiados tipos y relación con los demás módulos prioritarios.

**Tesoro y Otros:** quedan **fuera de página 1** y su **maquetación, contenidos y posición exacta en páginas posteriores** siguen abiertos. Deben preservarse los datos pertinentes, sin inventar una estructura genérica para «Otros» ni eliminar Tesoro.

**Paso siguiente de diseño:** contrastar el conjunto estructural de la primera página (cabecera, dos variantes de atributos/habilidades, combate, Ataques, Rasgos y Atributos, Lanzamiento de Conjuros 1–9 y 10–12 condicional, Municiones) contra sus restricciones físicas/visuales; después diseñar los módulos posteriores, entre ellos Tesoro y Otros. Ningún boceto ha demostrado todavía cabida real ni constituye autorización para programar el renderer.

### 3.16 Selección de marcos C2 y política de tipografías/glifos (2026-10-08)

**Marco C2 — SELECCIONADO COMO BASE VISUAL DE TRABAJO:** el propietario prefiere **C2, «Marco doble discreto y casilla suavizada»**. Se combina con la línea estética **A — Azul niebla clásico** ya aprobada. Marco exterior fino, segundo trazo interior ligero, encabezados diferenciados y casillas de esquinas suavizadas son la dirección inicial. **No** se aprueban tamaños concretos, doble contorno en cada mini casilla, grosores, radios o iconos ornamentales exactos. Los símbolos propios no se sustituyen automáticamente por un icono decorativo.

**Tipografías — REQUISITOS/ESTUDIO, SIN SELECCIÓN DE FAMILIA DE TEXTO:**
- El propietario requiere fuentes **sans-serif y estrechas/condensadas** para el texto, priorizando la legibilidad impresa. Una fuente delgada de ancho **no** implica usar pesos extraligeros ilegibles. No aprobar Cinzel/Alegreya ni ninguna de las opciones T1–T3 antes de **ver muestras de las tipografías reales**; el propietario no pudo elegirlas con wireframes de sustitución.
- Incluir la tipografía del propietario **Para Hoja de PJ**, **última versión canónica verificable**. La versión actualmente identificada en los activos versionados es **«Para Hoja de PJ Symbols v8»**, owner-approved/frozen, en `assets/fonts/owner/para-hoja-de-pj/v8/`. Comprobar su mapa de glifos en `GUIDE.md`; no usarla como si fuera una familia común de caracteres alfabéticos.
- Incluir **Font Awesome** para glifos/iconos apropiados, diferenciando su papel del de Para Hoja de PJ: privilegiar los símbolos específicos de competencia/pericia/seguimiento de juego de la fuente del propietario y estudiar Font Awesome para iconografía general. Verificar licencia aplicable (preferir Free sin costes), mapeo de glifos, incrustación y compatibilidad con PDF/impresión; no inventar una dependencia ya integrada.
- **Candidatas a muestra tipográfica REAL, no aprobadas:** el repositorio ya conserva Fira Sans Regular/SemiBold y Barlow Condensed Bold como recursos de QA; para una muestra condensada también se podrán evaluar variantes legítimas como Fira Sans Condensed, Barlow Condensed en otros pesos, IBM Plex Sans Condensed y Roboto Condensed, si su archivo original y licencia se verifican. No ofrecer falsos ejemplos renderizados con fuentes sustitutivas.
- Próxima validación estética: una **misma composición de texto real** (español con diacríticos, nombres largos, cifras, habilidades, marcadores de juego) con **archivos tipográficos auténticos**, jerarquías de texto y C2, medida/impresión comparables, sin renderizador de producto ni plantilla definitiva hasta nueva autorización.

**No se ha aprobado** una familia Sans específica, pareja tipográfica ni tipografía final de título o texto. Las fuentes históricas son referencias y recursos de contraste, no aprobación automática del rediseño.

### 3.17 Tipografía de estructura, jerarquía de glifos y ornamentación interior (2026-10-08)

**Tipografía estructural APROBADA:** utilizar la familia **Barlow Condensed** para **encabezados, subencabezados y etiquetas/rotulación de campos** de la hoja rediseñada. Esta aprobación es un **rol funcional de familia**, NO significa usar Barlow Condensed para todos los valores de PJ, texto manuscrito simulado, respuestas o campos rellenados. **El tipo de letra de los valores/escrito/rellenado sigue PENDIENTE**, como pidió el propietario. La selección previa C2 y Azul niebla clásico A siguen como orientación exterior.

**Versalitas SOLO EN ESTUDIO:** el propietario contempla títulos en **versalitas** y desea evaluar la variante real, comparándola con títulos Barlow Condensed de capitalización normal. Comprobar si existen versalitas OpenType reales o un archivo que las soporte; no denominar versalitas a simples mayúsculas, ni aprobar el efecto simulado sin muestras reales. Asegurar acentos, eñes, contraste y legibilidad a escala.

**Jerarquía de glifos APROBADA:** usar preferentemente **«Para Hoja de PJ» (última versión aprobada verificable: «Para Hoja de PJ Symbols v8» en el repo)** para iconos/símbolos y estados del juego que su vocabulario soporte; utilizar **Font Awesome Free** como **fuente secundaria/complementaria**. No sustituir arbitrariamente glifos de la fuente del propietario por Font Awesome. La aplicación/embebido de Font Awesome y su versión concreta deben verificarse técnicamente, sin presumir que el paquete ya está presente.

**Marcos interiores — EN EXPLORACIÓN, C2 NO CERRADO INTERNAMENTE:** el propietario considera demasiado sobrios los **cuadros interiores de C2** y quiere que resulten **levemente más ornamentales**, sin sacrificar utilidad y compacidad. Se mantiene C2 como referencia de **marco exterior doble discreto**, pero **no** está aprobada una geometría interior definitiva. Alternativas de wireframe para debatir: **C2a filete interior**, **C2b pequeños remates en esquinas**, **C2c cartela con esquinas recortadas**; son opciones ilustrativas, no aceptación implícita de C2b/C2c ni de detalles de iconos.

**Limitación física importante:** los cuadros **de datos/valores** pueden tener ornamentación en esquinas o remates si la prueba impresa lo permite; las **casillas pequeñas marcables a lápiz** deben mantenerse inequívocas, sencillas y de tamaño cómodo. No decorar todas las minicasillas con marcos dobles, ni consumir espacio de escritura con filetes, ni convertir un glifo decorativo en un indicador de estado de juego.

**Siguiente acción de diseño:** comparar muestras de **Barlow Condensed auténtica**, versalitas frente a capitalización habitual, y aplicar los tratamientos interiores C2a/b/c sobre el mismo componente de prueba, siempre separando etiquetas de los valores cuya fuente aún no se selecciona. La maqueta física integral queda postergada hasta terminar esta discusión.

### 3.18 C2c — cartelas interiores de esquinas recortadas (APROBADO, 2026-10-08)

**Decisión explícita del propietario:** seleccionar **C2c — Cartelas con esquinas recortadas**, en lugar de las propuestas C2a (filete interior) y C2b (esquinas ornamentadas), como **dirección de diseño para los cuadros interiores de valores y estadísticas** del rediseño.

**Conservar la combinación estructural ya elegida:** estética **A — Azul niebla clásico** + **C2 exterior (marco doble discreto)** + **C2c interior (cartelas con esquinas recortadas)**. El dibujo exploratorio C2c mostraba esquinas achaflanadas, un filete interior tenue y un pequeño acento ornamental; son rasgos de referencia para diseñar, **no medidas ni adorno obligatorio en cada cuadro**.

**Regla funcional:** nunca sacrificar espacio para escribir, legibilidad impresa ni compacidad por la ornamentación. Los **cuadros pequeños que se marcan a mano** (p. ej. Municiones y Lanzamiento de Conjuros) **NO heredan automáticamente cartela ornamental**: deben continuar simples, legibles y cómodos para marcarlos con lápiz. El estilo de las casillas pequeñas sigue sujeto a prueba física.

**Pendiente de validación visual/técnica:** grosor y número exacto de filetes, ángulos de esquinas, radios, proporciones, acentos decorativos por clase de campo, tamaños, tipografía del texto rellenado e interacción con filas de 7/5 mm. Se mantiene **Barlow Condensed** para encabezados/subencabezados/etiquetas, **versalitas como posibilidad sin validar**, y prioridad de glifos **Para Hoja de PJ Symbols v8 > Font Awesome Free**.

**PIN EXPLÍCITO — DIFERIDO POR EL PROPIETARIO (2026-10-08):** queda **pospuesto deliberadamente** el trabajo de pruebas ornamentales de C2c, variantes físicas de marcos y casillas, pruebas tipográficas finales (versalitas y fuente de valores rellenados), colores definitivos y afinación visual. La **condición para retomarlo** es disponer de un **modelo completo de las páginas/secciones del rediseño**: primero revisar, definir y ordenar el contenido y la estructura de las **páginas posteriores a la 1**, incluyendo Tesoro, la exclusión del bloque genérico Otros y las extensiones pertinentes. **Las pruebas de ornamentación deben aplicarse al modelo completo**, no a una tarjeta aislada que podría llevar a decisiones erróneas de densidad. No confundir POSPUESTO con DESCARTADO ni deshacer las decisiones ya aprobadas: Azul niebla clásico A, marco externo C2, cartelas interiores C2c como dirección, Barlow Condensed para títulos/etiquetas, Para Hoja de PJ v8 primero y Font Awesome Free segundo. La primera página mantiene lo ya aprobado, pendiente de validación física. **No se autoriza implementar código, PDFs de producto ni plantillas**.

### 3.19 Página 2 — distribución A + Tesoro; omitir módulo genérico Otros (APROBADO, 2026-10-08)

**Decisión expresa del propietario:** se selecciona la **propuesta A de estructura de página 2**, incorporando **Tesoro** y **omitiendo «Otros» como módulo/sección genérica de la hoja de PJ exportable**. Esta decisión **sustituye** la parte del §3.15 que proponía enviar Tesoro y Otros conjuntamente a páginas posteriores. Tesoro sí se incluye, ahora en **página 2**; «Otros» **NO tendrá recuadro propio en ninguna página de esta exportación**, salvo que el propietario reabra la decisión. **No implica borrar datos de la App, ni suprimir entradas reales con origen/categoría «Otros» dentro de Rasgos y Atributos, ni omitir contenido legítimamente cubierto por otros módulos.**

**Estructura APROBADA para página 2:**
- **Zona superior en DOS columnas:** **Equipo** a la izquierda y **Rasgos y Atributos** a la derecha.
- **Equipo Especial** debajo de ambas, **ocupando todo el ancho disponible** de la zona de contenido, con campos de **Ubicación / Nombre / Descripción** (incluidas ubicaciones personalizadas). No confundir las descripciones de equipo especial con las descripciones de rasgos, estas últimas expresamente prohibidas.
- **Tesoro es parte de página 2. La ubicación interna quedó APROBADA posteriormente en §3.20:** **debajo de Equipo dentro de la columna izquierda**; no utilizar una franja horizontal de ancho completo ni desplazar Equipo Especial. La estructura interna de monedas, filas y dimensiones sigue abierta salvo los requisitos explícitos añadidos en §3.20.
- **Equipo ordinario:** listado de objetos y cantidades, sin inventar peso, «Consumible» ni descripciones largas en ese módulo.
- **Rasgos y Atributos:** lista de **origen/categoría - nombre**, SIN descripciones. Su función en página 2 es la **continuación, no repetición**, de entradas que no cupieron en página 1; ofrecer espacio manual razonable cuando corresponda y evitar grandes andamios vacíos.

**Pendiente:** distribución fina y capacidad entre Equipo/Rasgos/Tesoro; política de desbordamiento hacia extensiones para cargas atípicas; tratamiento de ausencia de inventario/rasgos; cabida de ubicaciones especiales, unidades de tesoro y nombres largos; alturas, dimensiones, proporciones y texto de valores. El layout se valida en la maqueta completa, NO se implementa aún.

**PIN VISUAL VIGENTE:** no iniciar pruebas finales de ornamentación, tipografías de valores, versalitas, esquinas o grosores hasta completar el diseño estructural de todas las páginas. Las decisiones visuales aprobadas (Azul niebla A / exterior C2 / interior C2c / Barlow Condensed para rótulos / fuente del propietario v8 primero y Font Awesome Free segundo) continúan intactas.

### 3.20 Página 2 — Tesoro debajo de Equipo, monedas custom y espacio escribible (APROBADO, 2026-10-08)

**Decisión expresa del propietario:** en la estructura A de página 2, **Tesoro va inmediatamente debajo del módulo Equipo, dentro de la columna izquierda**. El panel **Rasgos y Atributos** continúa en la columna derecha, y **Equipo Especial** permanece debajo con ancho completo. Esta decisión cierra la posición general de Tesoro debatida en §3.19; **no** aprueba dimensiones ni proporciones finales.

**Contenido mínimo de Tesoro APROBADO:**
- Admitir **monedas personalizadas / custom** del personaje, además de las monedas habituales que correspondan; no limitar el modelo visual a un catálogo fijo de denominaciones.
- Mantener **espacios en blanco realmente escribibles a mano** para denominaciones y cantidades que el jugador desee anotar. Los renglones en blanco son intencionales aun si no existen monedas custom cargadas. No confundirlos con celdas vacías accidentales ni eliminarlos todos por compactación automática.
- Si el personaje tiene monedas custom registradas, mostrarlas respetando sus **nombres y cantidades reales**, sin inventar datos ni reemplazarlos por guiones vacíos. La reserva de renglones manuales complementa los datos.
- El PDF de inspiración incluye **Platino, Oro, Plata y Cobre**; su representación figura como referencia del diseño anterior, **sin cerrar aún** si se fijan exclusivamente estas cuatro, si existen otras monedas admitidas por el modelo de la App, su orden, ni las dimensiones de los campos.

**ACLARACIÓN POSTERIOR APROBADA — Tesoro con SUBCOLUMNAS internas y flujo por filas:** dentro del módulo Tesoro (que permanece BAJO Equipo en la columna izquierda de página 2), distribuir las entradas de monedas en **subcolumnas/celdas compactas de denominación + cantidad**. **Cuando no quepa otra moneda en la misma fila, continuar en la FILA SIGUIENTE dentro de Tesoro.** Esto evita una única hilera horizontal interminable y aprovecha su ancho, sin crear columnas nuevas a escala de página ni invadir la columna derecha de Rasgos. Monedas custom con sus valores reales y celdas de nombre/cantidad **en blanco para escritura manual** participan en el mismo patrón de distribución. La figura de **dos subcolumnas** es una explicación ilustrativa, **NO fija definitivamente el número de subcolumnas**; conservar anchos suficientes para denominaciones personalizadas y cantidades, y verificar capacidad real en el modelo integral.

**PENDIENTE:** número físico de subcolumnas y filas, cantidad mínima de espacios en blanco, nombres largos de monedas custom, límites y continuación cuando el bloque crece, tratamiento de moneda inusual o sin cantidad y ajuste de alturas/anchos compatible con Equipo a la izquierda, Rasgos a la derecha y Equipo Especial a todo el ancho.

**PIN VISUAL VIGENTE:** terminar antes la estructura de las demás páginas. Las pruebas decorativas/ornamentales y la tipografía de valores siguen pospuestas hasta un modelo completo. Sin modificación de renderizador ni PDFs.

### 3.21 Página 2 — Equipo Especial: presentación híbrida adaptable C (APROBADO, 2026-10-08)

**Decisión expresa del propietario:** elegir la **opción C «híbrida adaptable»** para la **presentación de filas de Equipo Especial**, dentro de la estructura de **página 2 propuesta A** ya aprobada. Estas letras describen decisiones diferentes; **opción C de Equipo Especial** NO cambia la distribución general A de página 2, ni el acabado interior **C2c** ni la variante A/B de atributos.

**Reglas funcionales APROBADAS de la opción C:**
- Mostrar primero las **entradas reales de Equipo Especial vinculadas a ubicaciones ocupadas**, conservando sus campos **Ubicación / Nombre / Descripción**, sin inventar datos. Si el modelo de la App permite múltiples objetos en una misma ubicación, mostrar todas las entradas reales en vez de imponer una sola por ubicación.
- Reservar **un número razonable y adaptable de filas sin datos prellenados** para anotación manual; algunas podrán corresponder a ubicaciones habituales vacías y otras ser de **ubicación personalizable/libre**. No reservar sistemáticamente una fila por cada ubicación canónica vacía, como haría la opción A «todas las ubicaciones visibles», ni eliminar toda referencia para escritura manual como haría la opción B «solo ocupadas».
- Admitir **ubicaciones personalizadas** como entradas legítimas; no restringir la tabla a las diez ubicaciones de la hoja de inspiración (Cabeza, Rostro, Cuello, Mano Izquierda, Mano Derecha, Brazo Izquierdo, Brazo Derecho, Pecho, Piernas, Pies). La aparición concreta de nombres canónicos vacíos y el número de filas libres se determinarán según capacidad y utilidad.
- **La sección Equipo Especial mantiene su posición inferior a TODO EL ANCHO** de la página 2, con las tres columnas ya definidas. Seguir el ritmo general de filas de 7 mm cuando la entrada quepa, y admitir mayor altura cuando el nombre/descripción requiera varias líneas. No truncar nombres o descripciones válidas ni rellenar toda la página de cuadros vacíos.
- **Casilla de marcado — SIGNIFICADO APROBADO posteriormente en §3.22:** sirve para indicar que un objeto de Equipo Especial está **ACTIVO**. Su tratamiento inicial en la exportación y su geometría quedan por comprobar; no asumir que el objeto está activo sin datos.

**No aprobados aún:** número mínimo/exacto de filas libres; selección de ubicaciones canónicas vacías; política de orden/grupo con múltiples entradas por ubicación; representación e inicialización del cuadro marcable; desbordamiento, cortes/continuaciones, ancho de columnas, altura física final y comportamiento sin registros. Se debe proteger contenido real, escritura manual y espacio de Equipo/Tesoro/Rasgos de la misma página.

**Casilla de Equipo Especial:** el significado ACTIVO se fija en §3.22. El siguiente paso vuelve al diseño estructural de páginas restantes, sin reabrir todavía las pruebas ornamentales. El **PIN VISUAL** del §3.18 sigue activo: ornamentación/tipografías finales solo se validan sobre un modelo estructural completo. Nada de código o PDFs implementados.

### 3.22 Equipo Especial — casilla «ACTIVO» (APROBADO, 2026-10-08)

**Decisión expresa del propietario, al escoger la SEGUNDA de tres alternativas («equipado», «activo», «libre para marcar»):** la casilla que acompaña el nombre del objeto en **Equipo Especial** representa que el **objeto está ACTIVO**. **No** representa solo que está equipado, **no** es una casilla sin significado y **no** se interpreta por defecto como «sintonizado».

**Comportamiento de papel:** mantener una casilla marcable asociada a cada entrada de Equipo Especial, identificable por su posición/rotulación, para seguimiento manual del estado **activo**. Su significado no crea por sí solo un campo booleano nuevo de la App ni autoriza inventar estados activos.

**Datos iniciales / PENDIENTE:** si hay un estado «activo» respaldado realmente por la App, decidir más adelante cómo se imprime su valor; **si no hay evidencia de un estado almacenado, no pre-marcar la casilla ni afirmar actividad automáticamente**. El mecanismo exacto de relleno, símbolo, impresión, espacio y accesibilidad queda sujeto a la maqueta integral. No confundir las casillas de Equipo Especial con los contadores de Municiones o Conjuros.

**PIN visual:** dimensiones, bordes y iconografía para la casilla se comprueban con el modelo completo y no ahora. Regla C híbrida de Equipo Especial y estructura general A de página 2 permanecen vigentes; ninguna modificación del código o PDF autorizada.

### 3.23 Página 3 — Propuesta A: Trasfondo e Historia + expansión de Rasgos (APROBADO, 2026-10-08)

**Elección expresa del propietario:** distribución A de **dos columnas** para la página 3.

- **Izquierda:** Trasfondo (nombre, Religión/Fe, resumen), Perfil narrativo (Rasgos de personalidad, Ideales, Vínculos y Defectos) e Historia del PJ; datos y campos de la App, sin omitir párrafos largos.
- **Derecha:** «Rasgos y Atributos» como **continuación**, solamente con entradas todavía no impresas en páginas 1 o 2, expresadas como `origen/categoría - nombre` **sin descripciones ni duplicados**.
- Los «Rasgos de personalidad» narrativos no son las entradas mecánicas de «Rasgos y Atributos».
- La App contempla imágenes principal/secundaria del trasfondo, pero su inclusión y ubicación en esta exportación **NO están aprobadas**.

**Por resolver:** anchos, alturas, renglones escritos a mano, caso de cero rasgos remanentes, extensión de Historia, imágenes y medición integral. No es aprobación de una composición a escala ni de ornamentación.

### 3.24 Página 4 — Propuesta B: lista de conjuros en tres columnas dinámicas (APROBADO, 2026-10-08)

**Elección expresa del propietario:** aprobar **B** para la página 4, una **lista rápida de conjuros con TRES columnas DINÁMICAS**, agrupada por **niveles**, con **casillas de preparación** junto a las entradas apropiadas. Esto no es una página de descripciones extensas.

- Conjuros reales que estén asociados al PJ, incluidos **Trucos (nivel 0)** y **niveles 1–9** como marco normal; mantener agrupación por nivel, sin exigir igual número de filas por cada uno ni asumir un reparto fijo de niveles por columna. Conservar nombres largos y espacio útil para escritura manual. Si el personaje tiene más entradas de las que caben, continuarlas sin omisiones.
- **Las casillas representan preparación** cuando aplique al conjuro y a la **fuente de lanzamiento** correspondiente. La App permite un mismo conjuro asociado a varias fuentes con estados de preparación diferentes: no amalgamar esos estados en un único indicador engañoso. El detalle de visualización de varias fuentes/casillas y de conjuros no preparables **queda pendiente**, igual que cómo inicializar marcas a partir de datos verificados. No fingir que todo conjuro es preparable.
- La primera página ya contiene **Aptitud Mágica, CD, modificador de ataque y control de espacios de conjuro gastados**. **Esta prohibición inicial de duplicar controles es SUSTITUIDA por §3.25:** el propietario quiere que el encabezado de cada nivel de la página 4 incluya también sus espacios y el seguimiento de los gastados; la posible repetición de Aptitud Mágica/CD/Ataque en una franja superior está en discusión. Página 4 sigue siendo lista rápida, no libro de descripciones.
- El **Libro de Conjuros opcional**, ya existente en el proyecto, cumple el papel de referencia detallada con descripciones, componentes y otra información; no convertir esta lista rápida en una copia del libro. Componentes ritual/concentración como símbolos compactos son **propuestas no aprobadas todavía**.
- Los detalles de **niveles épicos 10–12**, si están habilitados para el PJ, se deben resolver consistentemente con la regla condicional acordada en página 1, pero **NO asumir hoy que el modelo actual admite conjuros de esos niveles**. La pantalla de la App revisada gestiona niveles 0–9.

**Pendientes:** orden de lectura y flujo por las tres columnas, reserva manual por nivel, apariencia y significado exacto de casillas según fuente/preparación, no lanzadores y ausencia de conjuros (mostrar u omitir página adicional), desbordamiento, medidas físicas, iconos y leyenda de ritual/concentración, niveles épicos, y representación de fuentes. La estructura B queda aprobada, **no la geometría definitiva**.

**PIN VISUAL VIGENTE:** posponer muestras finales de Barlow Condensed, tipografía para valores y ornamentación C2c hasta contar con el modelo integral de todas las páginas. No se autorizan cambios de código, plantillas ni generador PDF.

### 3.25 Página 4 — Espacios en encabezados por nivel y referencia mágica superior (2026-10-08)

**Cambio explícito del propietario respecto de §3.24: APROBADO recuperar el contador de espacios en cada encabezado de nivel**, aunque esa información esté repetida desde página 1. Cada módulo de conjuros por nivel tendrá en su propia cabecera un identificador de **NIVEL**, dato de **ESPACIOS** correspondiente y **casillas marcables de ESPACIOS GASTADOS** si proceden. Se recupera la idea de la hoja fuente «NIVEL / ESPACIOS / ESPACIOS GASTADOS» sin copiar obligatoriamente su geometría antigua. **La repetición en papel es intencional** para poder usar la página 4 de forma autónoma.

- **Regla posterior APROBADA en §3.26:** cada encabezado de nivel debe mantener **Nivel + Espacios + Gastados/notaciones y casillas en UNA SOLA LÍNEA horizontal estricta**, sin partirla en dos sublíneas. Ajustar la composición y las abreviaturas sin hacer casillas ilegibles; la cabida física sigue por validar. No inventar números de casillas ni marcas iniciales.
- **Nivel 0 — Trucos:** incluye bloque de lista, pero no se inventa para él un contador ordinario de espacios de conjuro.
- Las casillas de **preparación junto a los nombres** se distinguen inequívocamente de las casillas para **gasto de espacios en encabezados**. No confluir preparación con gasto.
- Si el PJ tiene varias fuentes/orígenes mágicos y sus espacios no corresponden todos al mismo recurso, **no fusionar números o contadores incompatibles**: debe respetarse el contrato real de datos. Los detalles de representación por fuente se diseñarán después.
- **Decisión posterior APROBADA en §3.26:** repetir en una **franja superior obligatoria de página 4** los **tres campos de referencia de página 1**: Aptitud Mágica, CD de salvación y Modificador de Ataque Mágico. Distinguir las fuentes de lanzamiento cuando sus valores difieren y evitar una CD universal ficticia. La duplicación es intencional.
- Continúa vigente la diferenciación entre esta **lista rápida** y el **Libro de Conjuros opcional** de descripciones detalladas.

**Pendiente para el modelo integral:** resolver representación exacta de múltiples fuentes en la referencia superior, **validar cabida de cada cabecera estrictamente en una línea**, cantidad real de casillas y disposición en casos extremos sin saltos internos, marcas iniciales, niveles épicos si aplicaran y política de página para no lanzadores.

**PIN VISUAL ACTIVO:** no seleccionar todavía dimensiones ornamentales, tipografía de valores ni esquinas definitivas; las pruebas serán sobre el modelo de todas las páginas. Sin cambiar código/renderer/PDF.

### 3.26 Página 4 — Referencia mágica superior + encabezado por nivel EN UNA LÍNEA (APROBADO, 2026-10-08)

**Confirmación explícita del propietario:** **SÍ** repetir sobre la lista de conjuros de página 4 la **franja superior** de referencia con **Aptitud Mágica / CD de salvación / Modificador de Ataque Mágico**. Es deliberadamente redundante respecto de página 1 para que la página 4 se use sin cambiar de hoja. Si hay varios orígenes/fuentes mágicos con estadísticas diferentes, conservar su asociación correcta en lugar de declarar valores universales; la geometría de cada fuente sigue por diseñar.

**RESTRICCIÓN FÍSICA ESTRICTA:** la **CABECERA DE CADA NIVEL** de conjuro debe integrar **NIVEL + ESPACIOS + notaciones/casillas de ESPACIOS GASTADOS en exactamente UNA ÚNICA LÍNEA horizontal**. **PROHIBIDO partir el encabezado de nivel en dos sublíneas**, aun dentro de los módulos dinámicos de las tres columnas B. Esta regla sustituye expresamente la propuesta de cabeceras de dos sublíneas de §3.25. Usar abreviaturas inequívocas y ajustar asignación de anchos sin miniaturizar controles marcables o recortar información.

**Separaciones semánticas:** las casillas de gasto del encabezado de nivel NO son casillas de preparación junto al nombre del conjuro. Nivel 0/Trucos no recibe espacios ordinarios inventados. Los valores impresos y marcas iniciales deben venir de datos reales; no asumir número fijo de casillas ni fusionar fuentes o reservas incompatibles.

**Tensión de cabida identificada:** tres columnas de listas dinámicas y cabeceras de una sola línea pueden entrar en conflicto cuando haya muchos espacios, fuentes particulares o anotaciones. Esta decisión **NO autoriza desbordar el encabezado a una segunda línea**, ocultar datos ni reducir cuadros a tamaños inutilizables. El caso deberá validarse en el modelo físico completo y someterse a nueva decisión de composición si es imposible, manteniendo la preferencia explícita por una línea.

**Pendientes sin cambio:** tamaño real, texto de abreviaturas, indicadores ritual/concentración, orden y paginación, estado múltiple de preparación por origen, niveles épicos condicionales, no lanzadores y casos extremos; casillas y cartelas definitivas se prueban al acabar todas las páginas bajo el PIN visual.

**No autorización:** el acuerdo es documental y estructural. No se autoriza modificar código, renderer, fuentes ni generar PDF de producto.

### 3.27 Página de Notas — opción C híbrida adaptable y página final en blanco (APROBADO, 2026-10-08)

**Elección expresa del propietario:** la sección **Notas** se diseñará con **opción C, híbrida adaptable**, mezclando **Notas generales** y **Notas con título** registradas en la App con un espacio manual útil **rayado y cuadriculado**. Los datos de la App se imprimen sin truncarlos ni perder el título/contenido real; si no caben en una página, se utilizarán páginas de continuación. No se debe convertir la página en un andamio fijo de casillas repetidas independientemente de los datos.

**Condición obligatoria adicional del propietario:** **el exportable debe TERMINAR CON UNA PÁGINA COMPLETA EN BLANCO para anotaciones**, visualmente inspirada en la **última hoja del PDF original**: **mitad/zona superior con renglones divididos en DOS columnas** y **zona inferior con papel cuadriculado** para anotaciones, esquemas y croquis. «En blanco» significa **sin contenido de notas digitales prellenado**: no usar esta última hoja como continuación automática de las notas de la App ni reducirla a un pequeño espacio libre; la hoja final sigue estando disponible para escritura manual. Conservar la regla aprobada de **logo D&D y nombre del PJ en todas las páginas**, incluida la última. La proporción física exacta de renglones/cuadrícula queda por probar; los dibujos no representan escala definitiva.

**Flujo obligatorio:** tras las páginas estructurales y cualquier número de páginas de Notas con contenido, **añadir una página manual en blanco al FINAL de la exportación de hoja del PJ**. Si se incluye el **Libro de Conjuros opcional u otros anexos**, la ordenación de esos anexos debe resolver de forma explícita la exigencia «terminar con la página en blanco»; no dar por implementado un orden incompatible, no eliminar la hoja en blanco ni dejar datos digitales impresos sobre ella. Este punto de integración se comprobará en la fase del modelo completo.

**TRES DIAGRAMAS EXPLORATORIOS para la sección híbrida (NO APROBADOS INDIVIDUALMENTE):**
- **C-N1 «equilibrada»:** notas con título en tarjetas arriba, notas generales en franja ancha central, espacio manual mixto rayado/cuadriculado abajo. **Es una recomendación del asistente, NO una selección del propietario.**
- **C-N2 «dos columnas funcionales»:** notas tituladas en una columna y notas generales en la otra, rematando con banda inferior manual.
- **C-N3 «prioridad a notas digitales»:** notas digitales ocupan el espacio principal y continúa su contenido sin recortes; reserva manual interior menor, apoyándose en la página final siempre en blanco.

La **opción C general y la página final en blanco SÍ están aprobadas**. **ACTUALIZACIÓN POSTERIOR: la variante C-N3 quedó seleccionada en §3.28**, en reemplazo de esta frase histórica sobre variantes pendientes. No están aprobados porcentajes, cantidad de tarjetas, columnas internas exactas, medidas ni estilo ornamental.

**Datos de la App revisados:** `CharacterNotesTabV4.kt` distingue **Notas generales** (texto `generalNotes`) y **Notas con título** (cada tarjeta con `title` y `content`). El original `Hoja de PJ v2 - 5.0 - Simkin.pdf` presenta una última hoja de Notas en blanco con renglones a dos columnas superiores y cuadrícula inferior. La nueva exportación debe proteger ambos tipos de contenido sin duplicarlo.

**PIN VISUAL VIGENTE:** pruebas finales de la ornamentación C2c, tipografías de valores y muestras reales solo cuando esté completo el modelo estructural de páginas. Solo se documentan decisiones; no se autoriza implementar código, templates ni PDF.

### 3.28 Página de Notas — variante C-N3, prioridad al contenido digital (APROBADO, 2026-10-08)

**Decisión explícita del propietario:** al comparar C-N1/C-N2/C-N3, selecciona **C-N3 — «Prioridad a las notas digitales»**. Esta es la **variante interna aprobada para la opción C híbrida adaptable** de §3.27, no un cambio al estilo de página 4 ni a otra opción C del proyecto.

- **Distribución conceptual:** **Notas con título** de la App ocupan el área principal, con sus títulos y contenidos íntegros y una jerarquía clara; **Notas generales** aparecen en un módulo amplio a continuación. La estructura debe priorizar el contenido escrito real y expandirse o continuar en páginas adicionales cuando sea necesario, sin trunca­miento ni pérdida de entradas.
- **Reserva manual secundaria:** permitir un espacio menor de anotaciones libres dentro de la página de notas digitales cuando la capacidad lo permita, sin que esa zona comprima el contenido preexistente. El diseño no establece un porcentaje fijo de área libre, un número fijo de tarjetas ni obliga a crear vacíos grandes.
- **Última página obligatoria en blanco INTACTA:** después de las páginas con notas digitales y cualquier continuación, la exportación de hoja debe conservar al FINAL una **página completa en blanco** según §3.27: renglones en dos columnas arriba y cuadrícula abajo, más logo y nombre del PJ conforme a la regla de cabeceras. Esta página no se usa para el desbordamiento de notas ni se llena automáticamente con datos digitales.
- **Con anexos opcionales:** sigue pendiente resolver el orden físico de Libro de Conjuros y extensiones para cumplir siempre la condición de última hoja en blanco. La elección C-N3 no resuelve por sí sola ese orden.

**NO APROBADO todavía:** medidas/proporciones exactas de C-N3, número de tarjetas por hoja, tipografía de texto rellenado, tratamiento de páginas sin notas, corte de notas muy extensas y reglas físicas de desbordamiento; C-N1 y C-N2 quedan como opciones examinadas pero no elegidas. **PIN visual vigente:** las pruebas de ornamentación y tipografías se harán sobre el modelo completo. Ninguna autorización para editar código, plantillas ni generador PDF.

### 3.29 Política general de extensiones: C2 compartidas entre módulos distintos (APROBADO, 2026-10-08)

**Elección expresa del propietario:** tras analizar que una hoja de extensión separada por cada tipo de contenido puede generar páginas casi vacías, elige **C2: «extensiones compartidas entre contenidos relacionados o diferentes cuando quepan bien sin sacrificar legibilidad»**. Esta «C2» es la alternativa de **composición dinámica de páginas adicionales**, distinta de los marcos exteriores C2 ya elegidos en el lenguaje visual, de C2c (cartelas interiores) y de otras opciones A/B/C del proyecto.

**Reglas de producto aprobadas a nivel de principio:**
- Cuando queden registros válidos que no caben en las páginas principales, **permitir que excedentes de varios módulos compartan una misma página extra**, incluso si proceden de páginas base distintas (p. ej., equipo, monedas custom, Equipo Especial, Rasgos). **No crear obligatoriamente una hoja por módulo/entidad**.
- **Criterio de combinación:** usar el espacio disponible cuando el resultado sea legible, fácil de localizar durante la partida y práctico para anotar a mano. **No reducir fuentes, casillas o columnas a tamaños inutilizables, no inventar datos, no truncar entradas ni omitir información solo para ahorrar papel**.
- **Respetar el destino y jerarquía ya fijados:** antes de promover «Rasgos y Atributos» a una extensión extra, utilizar la continuación prevista en páginas 2 y 3; las páginas extendidas no reemplazan sin autorización los módulos obligatorios de las páginas 1–5.
- En una hoja extra compartida, identificar de forma inequívoca cada módulo y su condición de **CONTINUACIÓN**. Mantener los contratos por módulo: **Atributos, Tiradas de Salvación y Habilidades** (primera página, variantes A/B seleccionables y campos personalizados relacionados con su atributo rector), **Ataques** (lista-resumen unificada de tres columnas, NO inventario), Equipo (objetos/cantidad), Tesoro (monedas/cantidad en subcolumnas con salto a filas), Equipo Especial (Ubicación/Nombre/Descripción y casilla ACTIVO), **Rasgos y Atributos** (origen/categoría - nombre sin descripciones, distinto de «Atributos» de característica), Historia (párrafos), Conjuros (por nivel y cabeceras de UNA línea; casillas de preparación y gasto diferenciadas), **Notas (contenido completo, pero EXCLUIDAS de páginas C2 compartidas con cualquier otra sección según §3.31)**. Esta enumeración explícita repara la omisión de los módulos de la primera página en los ejemplos introductorios de C2; la excepción posterior de Notas prevalece sobre la posibilidad genérica de mezclar módulos.
- **No mezclar de manera forzada bloques físicamente incompatibles**, como párrafos extensos con tablas densas, si ello degrada lectura/uso. Una página extra adicional es aceptable cuando protege esa calidad; C2 **NO significa maximizar ahorro de papel a cualquier coste**.
- La cabecera de **logo D&D + nombre del PJ** permanece en cualquier hoja extra, y la **última hoja de Notas en blanco** continúa siendo estrictamente la última, no utilizada para desbordamiento.
- La posibilidad de agrupar residuos **entre páginas base distintas** puede requerir referencias de continuación/identificadores de página para localizarlos. Deben diseñarse sin numeraciones inventadas; los números físicos se asignarán tras ordenar/paginar.

**Aspectos aún NO aprobados (para seguir afinando C2):**
- **DECIDIDO en §3.30:** las extensiones compartidas se intercalan **cerca de los temas de origen, después de la última página base involucrada** (ubicación A); no agrupar todas automáticamente al final. Sigue pendiente el algoritmo de ordenación y referencias/identificadores de continuación, en especial cuando módulos de páginas base distintas comparten hoja.
- **Cómo escoger plantillas dinámicas y repartir el espacio** de varias secciones: filas/columnas/anchos, prioridades entre módulos, volumen mínimo de reserva manuscrita, número máximo de módulos por página, orden de aparición, cortes de texto largo y paginación física.
- **Política para anexos/libro de conjuros opcional**, páginas sin contenido (p. ej. no lanzadores), nivel de repetición de encabezados cuando una lista continúa, estado y ubicación de imágenes del trasfondo.
- Tratamiento concreto de combinaciones atípicas o muchas fuentes mágicas. Los ejemplos de una sola extensión para 4 módulos son **didácticos, no garantía de cabida ni contrato de número de páginas**.

**PIN VISUAL ACTIVO:** todavía no probar ornamentación C2c ni elegir fuente de valores; esas pruebas se harán sobre el modelo completo. Esta decisión es **documental/de diseño**, no autoriza modificar código, plantillas, renderer, ni generar PDFs.

### 3.30 Ubicación A de extensiones compartidas e inclusión explícita de Ataques, Atributos y Habilidades (APROBADO, 2026-10-08)

**Respuesta expresa del propietario:** «A, pero no has contemplado ataques, atributos ni habilidades en esto». En consecuencia, se elige **ubicación A** para las extensiones C2: **intercalar cada página compartida cerca de los temas que continúan, concretamente después de la última página base a la que pertenecen los módulos incluidos**, en lugar de reunir todas las extensiones tras las páginas principales. Por ejemplo, una página compartida que continúa Equipo (base 2) y Rasgos (base 3) va tras la base 3; una que continúa exclusivamente datos de primera página va tras la base 1. Las referencias «continúa en pág. N», cuando se apruebe su presentación, deberán calcularse con números físicos reales después de paginar, no inventarse.

**Corrección de cobertura necesaria:** la política C2 **incluye también los tres módulos omitidos en los ejemplos iniciales**:
- **Atributos de característica:** los **seis originales** tienen prioridad en la primera página frente a atributos personalizados. La primera página procura adaptar dinámicamente aproximadamente **2–3 atributos personalizados** con habilidades asociadas, sin garantía física universal. Cuando no quepan, trasladar datos restantes a continuación sin omitirlos. Los Atributos son **distintos** de «Rasgos y Atributos», cuyos nombres no llevan descripciones.
- **Habilidades y Tiradas de Salvación:** seguir la variante de exportación escogida **A (agrupada con su atributo rector)** o **B (listas separadas)**; mantener asociación con los atributos tradicionales o personalizados, bonificadores y estados de competencia/pericia. En la A, mantener grupos juntos cuando sea viable y señalar su continuación si un grupo se divide; la B no obliga a reformar la lista en grupos. Mantener referencia a atributos para habilidades custom. Altura nominal de habilidades hasta 5 mm solo si legible.
- **Ataques (resumen):** conservar exactamente la estructura unificada aprobada de **tres columnas**: `ARMA / ATAQUE / CONJURO`, `BONIFICADOR`, `DAÑO / TIPO DE DAÑO`. La continuación puede compartir página con Atributos/Habilidades u otros contenidos compatibles. No confundir Ataques con inventario/Equipo ni convertirlo en registros de armas detallados.
- **Balance de la primera página:** el área dinámica de Ataques y «Rasgos y Atributos» conserva el **mínimo útil de escritura manual para ambos**; el crecimiento de uno no elimina el otro. Las páginas extra reciben solo los excedentes, no copian completos los módulos agotados.
- **C2 compartida y no forzada:** los excedentes de Ataques, Atributos, Habilidades y Tiradas de Salvación pueden combinarse entre sí o con otros módulos si caben BIEN; no reservar una página por módulo ni garantizar forzosamente todo en una sola hoja. Conservar toda la información original con tamaños de letra, marcas y registros útiles.

**No cerrado:** algoritmo de composición, prioridades para módulos de distinta página, cuándo conviene separar dos conjuntos a pesar de ser combinables, ubicación/navegación cuando un módulo usa varias páginas, tamaños reales, casos de atributos extensos y línea de tablas, continuaciones de distintos esquemas de atributos. Toda representación de filas/cantidades de un wireframe es ilustrativa, no un límite fijo ni diseño definitivo.

**PIN VISUAL VIGENTE.** El paso sigue siendo solo de documentación: no cambiar código, plantillas, renderer o PDF.

### 3.31 Política dinámica C2 — prioridades aprobadas y excepción ABSOLUTA de Notas (2026-10-08)

**Confirmación expresa del propietario después de estudiar siete escenarios conceptuales con distintos volúmenes y tipos de excedentes:** se acepta que **una extensión compartida C2 pueda contener módulos procedentes incluso de páginas originales lejanas**, pero **no es prioritario conseguir esa mezcla solo para reducir la cantidad de hojas**. El propietario valida el criterio de calidad del asistente, con el siguiente orden de prioridades de diseño:

1. **Integridad completa de los datos**: nada se omite, duplica, trunca o transforma indebidamente.
2. **Legibilidad y utilidad física en papel**: lectura clara, casillas marcables, escritura manual razonable y formato adecuado al módulo.
3. **Facilidad para encontrar la información**: preferir continuidad cercana y referencias precisas. **La distancia entre origen y extensión puede aceptarse** cuando la mezcla realmente mejora la composición sin perjudicar sustancialmente la consulta; no es una prohibición absoluta de combinación entre páginas lejanas.
4. **Ahorro de páginas** después de satisfacer los criterios anteriores. No perseguir máximos ficticios de ocupación a costa de la calidad. Si hacen falta hojas extra para conservar la legibilidad, se agregan.

**EXCEPCIÓN DURA Y SIN AMBIGÜEDAD: «NOTAS SIEMPRE VAN APARTE DE TODO, NOTAS NO SE MEZCLA CON NADA».**

- **Todas las páginas de Notas**, empezando por el módulo principal C-N3 y siguiendo por cualquier continuación, serán **exclusivas de Notas**. Solo pueden contener **Notas con título**, **Notas generales** y el espacio manual de su propia composición ya aprobada. **Queda prohibido insertar en esas páginas** Ataques, Atributos, Salvaciones, Habilidades, Municiones, Equipo, Tesoro, Equipo Especial, Rasgos y Atributos, Trasfondo/Historia, Conjuros o cualquier otro módulo ajeno a Notas.
- **Prohibición simétrica:** ningún excedente de Notas se introduce en páginas C2 compartidas con otros tipos de contenido, **aunque pueda caber y permitiría ahorrar papel**. Cuando desborden, Notas generan página(s) de continuación **solo de Notas**, próximas a la base de Notas conforme a la ubicación A.
- Se mantiene **intacta la última hoja totalmente en blanco** de renglones en dos columnas arriba y cuadrícula abajo; es una página exclusivamente manuscrita, **no se utiliza para notas digitales excedentes ni para otro módulo** y debe conservar su lugar **al final de toda la exportación**. El orden definitivo del Libro de Conjuros opcional respecto a esa hoja permanece pendiente de integrar, sin derogar esta exigencia.
- La combinación de **Notas generales + Notas con título** dentro de páginas de Notas **sí está permitida**, porque ambas pertenecen a la misma sección Notas C-N3. La independencia es frente a **otras secciones**, no entre componentes de Notas.

**Qué NO queda aprobado ahora:** un algoritmo físico particular, umbrales numéricos para aceptar combinaciones lejanas, pesos de optimización, porcentajes de ocupación, reparto de módulos o mediciones de cabida. Los casos T1–T7 del estudio conceptual exploratorio siguen siendo **hipótesis, no pruebas físicas ni decisiones del propietario**. **Actualización de §3.32: está APROBADO que el generador pruebe automáticamente distribuciones verticales, de dos columnas y mixtas y escoja la mejor sin intervención del usuario**; no se aprueba aún el algoritmo técnico ni una garantía de cabida. La revisión global de todos los excedentes es una propuesta de planificación coherente con este mecanismo, pero el contrato detallado del algoritmo queda pendiente. No reinterpretar prioridades como tolerancia a recortes.

**PIN VISUAL VIGENTE:** el trabajo permanece documental; no cambiar código, plantillas ni generador PDF, ni adelantar QA ornamental/tipográfica hasta tener el modelo integral.

### 3.32 Compositor C2 — selección AUTOMÁTICA de disposiciones candidatas (APROBADO, 2026-10-08)

**Confirmación expresa del propietario («sí»):** al exportar, el generador **probará automáticamente varias familias de distribución** para las páginas extendidas compartidas C2 y **elegirá la que mejor cumpla las reglas del diseño**, **SIN preguntar al jugador qué distribución usar cada vez**.

**Familias mínimas aprobadas para comparar, sin dimensiones todavía:** 
- **Vertical**: módulos apilados y, cuando convenga, bloques anchos para textos largos como Historia.
- **Dos columnas**: módulos de listas/tablas compatibles lado a lado, respetando los anchos mínimos legibles.
- **Mixta**: combinación de zonas de ancho completo y zonas divididas en columnas para adaptarse a módulos de formas distintas.

**Condiciones de decisión ya vinculantes de §3.31:** proteger primero integridad del contenido y sus contratos, luego legibilidad/uso manuscrito, luego facilidad de consulta y proximidad (ubicación A), por último ahorro de páginas. Ninguna familia se impone si falla una de las restricciones duras. Puede mezclar residuos de distintas páginas originales cuando convenga; no debe separar datos semánticamente inseparables ni generar bloques o controles ilegibles. Este acuerdo **no obliga** a escoger siempre una composición distinta ni a ofrecer manualmente una selección de formato.

**NOTAS EXCLUIDAS:** **ninguna página de Notas C-N3 ni continuación admite contenido ajeno a Notas, ni sus excedentes participan en composiciones C2 mixtas**. Mantener notas generales + tituladas en sus propias páginas, con su última hoja manuscrita en blanco de dos columnas rayadas arriba y cuadrícula abajo al final del documento. El diseño interno aprobado de Notas C-N3 permanece, sin convertirlo en un módulo mezclable C2.

**Desglose importante de alcance:** queda aprobado **el comportamiento funcional de probar y seleccionar automáticamente**, pero **no** un algoritmo de optimización concreto, ecuaciones de puntuación, umbrales, proporciones, número mínimo/máximo de módulos, geometrías ni promesa de capacidad. La inspección del conjunto de excedentes antes de ordenar páginas es un enfoque candidato coherente con la selección automática; definir exactamente sus pasos, el manejo de textos largos, referencias cruzadas, orden final y casos extremos requiere estudio/validación posterior.

**Próxima decisión estructural:** cómo hacer legibles las **referencias de continuación** cuando una misma página C2 recibe, por ejemplo, Ataques originarios de P1, Equipo de P2 y Rasgos de P3. Las referencias finales deben usar números reales después de paginar; no inventar referencias prematuras. También permanecen pendientes anexos opcionales y páginas sin contenido.

**PIN VISUAL VIGENTE:** decisión documental, sin autorización de código, PDF, fuentes o maquetas ornamentales finales.

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

**EXTENSIONES C2: composición AUTOMÁTICA APROBADA (§3.32): comparar disposición VERTICAL, DOS COLUMNAS y MIXTA, elegir la mejor sin intervención en cada exportación; se aplican prioridades de §3.31. Ubicación A mantiene continuaciones cerca de fuentes, con mezcla remota posible. NOTAS NUNCA SE MEZCLAN. ÚLTIMA HOJA DE NOTAS EN BLANCO INTACTA. SIGUIENTE DECISIÓN: señalética y referencias cruzadas de continuación; después anexos, no lanzadores y cabida real. PIN VISUAL ACTIVO.**

La primera página conserva cabecera compacta y estructura relativamente restringida. En la sección izquierda ya están aprobadas **dos variantes de exportación** (A agrupada por atributo, B con listas independientes) y su **adaptación para 2–3 atributos personalizados y habilidades vinculadas tanto a estos como a los atributos normales**, con **extensión cuando no quepan**. **Aprobado:** crecimiento vertical dinámico con posibilidad explícita de **estirar/comprimir elementos de atributos** para utilizar el espacio, también aceptando vacíos residuales; prioridad para los **6 atributos originales**, grupos juntos cuando sea posible y continuación en extensión si falta capacidad. **Pendiente:** mecanismos concretos de corte/continuación por variante A/B y validación de capacidad. No inventar medidas exactas ni modificar el renderer.

**Decisiones más recientes:** silueta genérica sin retrato; logo + nombre del PJ en todas las páginas; primera página compacta; Clase / Nivel a la derecha del retrato; Raza/Alineamiento 60/40; variantes A/B seleccionables; adaptación de la primera página para unos 2–3 atributos personalizados con habilidades y habilidades custom ligadas a atributos normales; páginas extendidas para lo que no quepa. **Bloque 01 derecho APROBADO:** tres filas y ocho campos; dados de golpe solo texto; sin PV temporales ni salvaciones contra la muerte. **Bloque 02 Ataques APROBADO:** tabla resumen unificada con tres columnas (ARMA / ATAQUE / CONJURO; BONIFICADOR; DAÑO / TIPO DE DAÑO). **Bloque 03 Rasgos y Atributos APROBADO:** lista origen - nombre de cada rasgo, sin descripciones en ninguna página. **Reparto Ataques/Rasgos aprobado: opción C**, alturas dinámicas, reservas para escritura manual, ninguno desplazado completamente, sin fijar filas/medidas. **Lanzamiento de Conjuros APROBADO como módulo obligatorio incluso para PJ no lanzadores**; su estructura interior y convivencia con otros módulos siguen pendientes. **Municiones APROBADA en página 1** como filas por tipo/nombre con cuadros marcables, compactas y dinámicas. **Página 2 APROBADA: propuesta A, Equipo a la izquierda, Rasgos y Atributos a la derecha y Equipo Especial de ancho completo en la zona inferior; Tesoro también va en página 2 con ubicación interna pendiente. «Otros» se excluye como módulo genérico del exportable (NO se suprimen categorías «Otros» válidas dentro de otros módulos).** Continuidades y validación física pendientes.

**Implementación:** BLOQUEADA hasta aprobación explícita de un contrato suficientemente completo y de las validaciones técnicas necesarias.
