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
- **Raza:** incluir subraza cuando exista, p. ej. `Elfo — Alto elfo`; sin subraza, `Humano`.
- **Alineamiento:** nombre completo, p. ej. `Legal Bueno` o `Caótico Neutral`; nunca abreviaturas.

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

**SECCIÓN 01 — DIMENSIONES Y DISTRIBUCIÓN VISUAL DE LA CABECERA.**

Comparar en pasos pequeños alternativas **compactas** para distribuir los seis elementos obligatorios, el retrato cuadrado y el logo D&D en la zona superior de la primera página, permitiendo, si se aprueba, convivencia vertical con los módulos funcionales. Además, todas las páginas repiten el **nombre del PJ y el logo** (logo arriba a la izquierda); aún no se ha decidido cómo presentar el nombre en las páginas siguientes. Primero discutir **estructura relativa**, sin aprobar medidas exactas, tecnologías o arquitectura definitiva.

**Decisiones más recientes:** ausencia de imagen → **silueta genérica**; identificación mínima de **todas las páginas: logo D&D + nombre del PJ**. Primera página compacta y comparativamente menos modular; distribución exacta aún en discusión.

**Implementación:** BLOQUEADA hasta aprobación explícita de un contrato suficientemente completo y de las validaciones técnicas necesarias.
