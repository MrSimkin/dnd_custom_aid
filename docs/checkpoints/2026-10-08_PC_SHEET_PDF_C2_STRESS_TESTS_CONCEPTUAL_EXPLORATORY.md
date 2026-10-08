# Estudio conceptual — extensiones C2 compartidas (NO APROBACIÓN DE NUEVAS REGLAS)

**Fecha:** 2026-10-08. **Estado:** EXPLORATORIO / PENDIENTE DE VALIDACIÓN DEL PROPIETARIO. **Base verificada:** D-0076 §3.29–3.30 en main `829f13498709f0eff55f486275c764bec7fccfd4`.

## Alcance

Simular cómo el rediseño de Hoja de PJ podría acomodar *excedentes ficticios* de diversos tamaños y tipos. La política C2 (compartir páginas entre módulos compatibles) y ubicación A (después de la última base de origen) **ya están aprobadas**; este estudio **no aprueba** ningún algoritmo, cupo, proporción, número de páginas ni política extra. No modificar código, PDF ni plantillas. PIN visual/ornamental sigue activo.

Se prueban excedentes **DESPUÉS de agotar los espacios aprobados en las páginas principales**, incluida la continuación de Rasgos por las páginas 1→2→3. Por tanto, los conteos de abajo **no** son el total de elementos del personaje, sino solo los que todavía no se pudieron colocar.

## Modelo de presión ficticio, no de cabida real

Para comparar casos se asignan **100 unidades abstractas U** de espacio útil a una hipotética página extra. No equivalen a mm, líneas, columnas ni al tamaño de papel (aún no cerrado). Los siguientes costos son **arbitrarios y NO están medidos**; solo sirven para ejercer presión conceptual y hacer aritmética reproducible:

| Tipo | U por dato |
|---|---:|
| Atributo de característica | 14 |
| Habilidad | 3 |
| Tirada de salvación | 4 |
| Entrada de Ataques | 7 |
| Tipo de Municiones | 8 |
| Objeto de Equipo | 6 |
| Moneda | 5 |
| Equipo Especial | 12 |
| Rasgo y Atributo (origen + nombre, NO descripción) | 4 |
| Párrafo narrativo corto | 18 |
| Párrafo narrativo largo | 40 |
| Conjuro listado | 4 |
| Encabezado de nivel de conjuro | 8 |
| Nota extensa | 35 |
| Encabezado de módulo diferente | 3 |

**Mínimo virtual** = techo((suma de costos + 3 U por módulo, exceptuando cabeceras de nivel)/100). Este mínimo NO es una promesa de que esa cantidad de hojas sea físicamente posible: no modela geometría, ancho de columnas, control de espacio manuscrito, fragmentación, filas multilineales, repetición de cabeceras al pasar de página, fuentes mágicas múltiples ni márgenes. Un layout auténtico puede necesitar **más páginas, nunca menos que el mínimo calculado bajo el modelo elegido**.

Reglas reales ya aprobadas que no se tocan: 7 mm nominal por fila general, posible 5 mm por habilidad legible, conjuros por niveles y **cabecera NIVEL/ESPACIOS/GASTADOS de una sola línea**, preparaciones por fuente, Ataques exactamente tres columnas, A/B de Atributos/Habilidades, recursos del PJ, Equipo Especial «ACTIVO», última página de notas en blanco y cabecera D&D/nombre del PJ en todas las hojas.

## Casos ensayados

| Caso | Excedentes ficticios mezclados | Total U | Mínimo virtual | Lectura |
|---|---|---:|---:|---|
| **T1 — goteo de 3 bases** | P1: 2 Ataques, 3 Habilidades; P2: 4 Equipo, 2 monedas; P3: 4 Rasgos | 88 | 1 | **Una hoja compartida podría bastar** después de base 3; anotar en base 1 y 2 las referencias a esa página. Coste: localización más lejana para algunos contenidos |
| **T2 — saturación base 1** | P1: 3 Atributos custom, 14 Habilidades, 3 Salvaciones, 11 Ataques, 3 Municiones | 212 | 3 | **Al menos 3** en proxy; ubicar tras base 1. Componer atributo/habilidades con variante A o B, y resumen de Ataques sin convertir en Equipo. No garantizar 2 por recortar |
| **T3 — mezcla densa de 3 bases** | P1: 6 Ataques, 2 Habilidades; P2: 12 Equipo, 4 Equipo Especial, 5 monedas; P3: 8 Rasgos, 2 párrafos cortos | 282 | 3 | **Al menos 3** teóricas; probablemente 4+ al respetar anchuras. Posibilidad de dos extras tras base 2 y una tras base 3; Equipo/Especial/Tesoro y Rasgos/Historia podrán ocupar estructuras diferentes |
| **T4 — historia larga junto a listas cortas** | P2: 3 Equipo; P3: 3 Rasgos, 4 párrafos largos; P4: 2 conjuros de dos niveles | 226 | 3 | Mínimo virtual 3; **no forzar** texto narrativo largo en microcolumnas junto a tablas. Podrían ser 4+ físicas por incompatibilidad. Continuación narrativa ancha antes de base 4, conjuro después de base 4 |
| **T5 — lanzador con muchos conjuros y notas** | P1: 3 Ataques, 4 Habilidades; P3: 4 Rasgos; P4: 36 Conjuros de cuatro niveles (incluye los encabezados); P5: 2 notas extensas | 310 | 4 | Mínimo virtual 4. Posible 1 extra cercana a P3 (residuos previos), 2 de Conjuros tras P4 y 1 de Notas tras P5. Si conjuros tienen múltiples fuentes, la celda base no modela ese espacio: podrían necesitar más |
| **T6 — residuo pequeño remoto y notas largas** | P2: 1 Equipo Especial; P3: 2 Rasgos; P5: 8 notas extensas | 309 | 4 | Combinar 1+2 registros en una extensión tras P3, y dejar 3 o más páginas propias de Notas tras P5. **No arrastrar por defecto** bloques remotos hasta las Notas para ahorrar sin beneficio legible |
| **T7 — sobrecarga extrema** | P1: 4 Atributos, 24 Habilidades, 5 Salvaciones, 25 Ataques, 5 Municiones; P2: 30 Equipo, 12 monedas, 10 Especial; P3: 25 Rasgos, 6 párrafos largos; P4: 90 conjuros de siete niveles; P5: 12 notas extensas | 1935 | 20 | Mínimo virtual 20; **NO perseguir reducción imposible**: paginar, preservar todo el contenido, asegurar avance de cursor en textos/renglones, no truncar, respetar hoja final en blanco. Casos patológicos multifuente podrían crecer más |

### Composiciones conceptuales representativas

- **T1**: una única página extra con módulos `ATAQUES + HABILIDADES | EQUIPO + MONEDAS + RASGOS`, después de la base 3; esquema sujeto a ancho físico real. Las bases 1 y 2 indicarán el número físico de continuación cuando se decida el sistema de referencias.
- **T2**: E1 `ATRIBUTOS + SALVACIONES + parte de HABILIDADES`; E2 `otras HABILIDADES + parte de ATAQUES`; E3 `resto de ATAQUES + MUNICIONES`. Todas tras base 1, preservando variante A/B de habilidades.
- **T3**: un reparto **por área solamente** que llega a 3 hojas estaría cerca del límite; p. ej. E1 tras base 2 `6 ATAQUES + 2 HABILIDADES + 6 EQUIPO` = 93 U incluyendo cabeceras; E2 tras base 2 `6 EQUIPO + 4 ESPECIAL + 1 MONEDA` = 98 U; E3 tras base 3 `4 MONEDAS + 8 RASGOS + 2 PÁRRAFOS CORTOS` = 97 U. Esto **no demuestra cabida**: son densidades muy elevadas y varios encabezados/anchos reales podrían llevar a **4 o más**. El contraejemplo sirve precisamente para evitar algoritmos que declaren PASS al 98% de ocupación abstracta.
- **T4**: no emplear tres columnas pequeñas para Historia. Permitir una página de texto completa o de ancho adecuado aunque la distribución total use una hoja más que el mínimo abstracto.
- **T5**: evitar que la preparación por fuente y las casillas de espacios se confundan. Los encabezados por nivel siguen obligatoriamente en una única línea, con continuidad correcta entre columnas/páginas. La última hoja en blanco debe quedar después de posibles anexos.
- **T6**: eficiencia = mínima combinación útil, no agrupación forzada de notas con Equipo Especial y Rasgos de páginas alejadas.
- **T7**: el caso debe terminar siempre; ninguna entrada desaparece, se duplica ni causa bucles al desbordar.

## Hallazgos conceptuales (NO garantías físicas)

1. **C2 puede ahorrar hojas** en excedentes pequeños de diferentes módulos (**T1**), pero puede alejar del origen una continuación de P1 situada tras P3. Las referencias cruzadas son esenciales.
2. **Cuando la suma de contenido es grande**, combinar módulos no elimina mágicamente superficie requerida (**T2**, **T3**, **T7**); la mejora está en reducir fragmentación y encabezados/páginas desperdiciadas.
3. **Una página con ocupación abstracta cercana al 100% no es evidencia de cabida**: el ancho y las condiciones de impresión cuentan (**T3**).
4. **Textos largos y tablas estrechas no deben mezclarse por obligación** (**T4**). A veces una página adicional legible es mejor que un ahorro forzado.
5. **Conjuros multifuente** y la regla estricta de encabezado de nivel a una línea pueden aumentar las necesidades físicas frente al proxy (**T5**).
6. **Notas largas** y la **última hoja manual en blanco** no son reserva gratuita para otros módulos. Evitar mezclar residuos remotos si no genera ventaja real (**T6**).
7. Las dos variantes de Atributos/Habilidades A/B deben mantenerse también en las continuaciones. Diferenciar **Atributos de característica** y **Rasgos y Atributos**, y **Ataques** de **Equipo**.

## Hipótesis de algoritmo candidato, aún NO aprobado

1. Construir primero las páginas base, sin romper su protección manuscrita y sin repetir rasgos ya impresos.
2. Hacer una cola de excedentes con `baseOrigen`, `modulo`, `orden`, `fuente`, alto/ancho medido y restricciones de fragmentación.
3. Probar composiciones **predefinidas** (ancho completo; dos columnas; mixtas) contra **condiciones duras**: legibilidad, integridad, tamaño de casillas, mínimo manuscrito, encabezados de conjuros de una línea, párrafos separados de tablas cuando sea mejor.
4. Entre alternativas válidas, preferir el menor número **razonable** de páginas **sin empeorar excesivamente su localización**; colocar la página tras la última base representada conforme a ubicación A. No dar prioridad absoluta a ahorrar una hoja cuando empuja un excedente lejísimos de su base.
5. Paginar definitivamente, generar rótulos «CONTINUACIÓN» y referencias de páginas reales; disponer el Libro de Conjuros opcional de modo que la **última página de la exportación permanezca en blanco**.
6. Verificar invariantes al finalizar: entradas impresas exactamente una vez, flujo completo de textos, fuente/casillas correctas, módulos obligatorios presentes, y al menos un dato colocado por iteración cuando exista excedente (sin ciclo infinito).

### Decisión que falta

¿Cuánto penalizar llevar información de una página inicial a una extensión situada varias páginas más tarde para ahorrar **una hoja**? Una política mixta localización/eficiencia parece conveniente, pero **no tiene umbral ni aprobación del propietario**. También falta decidir si el compositor debe evaluar **globalmente** todos los excedentes de una exportación antes de fijar las extensiones.

**No modificar el ledger canónico con estas hipótesis como si fueran decisiones aprobadas.** La presente nota de estudio puede mantenerse en rama hasta revisión del propietario.
