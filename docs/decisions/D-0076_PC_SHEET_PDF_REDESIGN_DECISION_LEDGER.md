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

**DISEÑO VISUAL PREVIO A LA MAQUETA INTEGRAL: comparativas de tipografías sans condensadas REALES, símbolo v8 + Font Awesome, marco C2 y comprobación de Municiones con dos subfilas posibles.**

La primera página conserva cabecera compacta y estructura relativamente restringida. En la sección izquierda ya están aprobadas **dos variantes de exportación** (A agrupada por atributo, B con listas independientes) y su **adaptación para 2–3 atributos personalizados y habilidades vinculadas tanto a estos como a los atributos normales**, con **extensión cuando no quepan**. **Aprobado:** crecimiento vertical dinámico con posibilidad explícita de **estirar/comprimir elementos de atributos** para utilizar el espacio, también aceptando vacíos residuales; prioridad para los **6 atributos originales**, grupos juntos cuando sea posible y continuación en extensión si falta capacidad. **Pendiente:** mecanismos concretos de corte/continuación por variante A/B y validación de capacidad. No inventar medidas exactas ni modificar el renderer.

**Decisiones más recientes:** silueta genérica sin retrato; logo + nombre del PJ en todas las páginas; primera página compacta; Clase / Nivel a la derecha del retrato; Raza/Alineamiento 60/40; variantes A/B seleccionables; adaptación de la primera página para unos 2–3 atributos personalizados con habilidades y habilidades custom ligadas a atributos normales; páginas extendidas para lo que no quepa. **Bloque 01 derecho APROBADO:** tres filas y ocho campos; dados de golpe solo texto; sin PV temporales ni salvaciones contra la muerte. **Bloque 02 Ataques APROBADO:** tabla resumen unificada con tres columnas (ARMA / ATAQUE / CONJURO; BONIFICADOR; DAÑO / TIPO DE DAÑO). **Bloque 03 Rasgos y Atributos APROBADO:** lista origen - nombre de cada rasgo, sin descripciones en ninguna página. **Reparto Ataques/Rasgos aprobado: opción C**, alturas dinámicas, reservas para escritura manual, ninguno desplazado completamente, sin fijar filas/medidas. **Lanzamiento de Conjuros APROBADO como módulo obligatorio incluso para PJ no lanzadores**; su estructura interior y convivencia con otros módulos siguen pendientes. **Municiones APROBADA en página 1** como filas por tipo/nombre con cuadros marcables, compactas y dinámicas. **Tesoro y Otros van a páginas posteriores**, todavía sin composición ni número de página. Continuidades y validación física pendientes.

**Implementación:** BLOQUEADA hasta aprobación explícita de un contrato suficientemente completo y de las validaciones técnicas necesarias.
