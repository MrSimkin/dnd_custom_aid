# Rediseño de la Hoja de PJ — síntesis integral para revisión visual

**Fecha:** 2026-10-08 (Chile)  
**Estado:** BRIEF DE REVISIÓN, **NO maqueta aceptada, NO diseño físico verificado, NO autorización de implementación**.  
**Autoridad de detalle:** `D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md`, §§3.1–3.48. Si esta síntesis simplifica u omite un matiz, **prevalece D-0076**.  
**Ruta operativa:** `docs/checkpoints/2026-10-08_PC_SHEET_PDF_REDESIGN_DISCUSSION.md`.

## 1. Objetivo de la próxima revisión

Presentar al propietario la **hoja completa como un sistema**, no otras decisiones aisladas. Comprobar: (1) jerarquía y consulta en partida; (2) uso real de lápiz y casillas; (3) integridad de campos/textos; (4) crecimiento dinámico, extensiones y navegación; (5) estética coherente con tamaño de impresión. La revisión de este brief **no significa** que ya existan capturas, maquetas, pruebas físicas ni PDFs. En la fase documental no producir código, templates, PDFs o tests del renderer.

**Alcance cerrado para evitar crecimiento indefinido:** mantener íntegramente las **48 secciones de decisión §§3.1–3.48** sin reabrirlas por dudas menores. **No incorporar anexos nuevos**: solo **Libro de Conjuros opcional**. Las extensiones C2, las páginas exclusivas C-N3 con Notas digitales y la hoja final manuscrita **son partes del contrato de Hoja**, **no nuevos anexos**. Consultar al propietario únicamente un conflicto material de producto, semántica de juego, integridad, organización de información o cambio de alcance que los contratos no resuelvan. El equipo técnico decide algoritmos, mediciones, empaquetado, fuentes de datos verificables y cortes de lectura **sin alterar conductas aprobadas**.

## 2. Secuencia aprobada (no numeración física fija)

1. **Página base 1**, identidad + características y bloques de juego.
2. **Página base 2**, Equipo / Tesoro / Rasgos / Equipo Especial.
3. **Página base 3**, Trasfondo, narrativa/Historia y Rasgos pendientes, **siempre presente**.
4. **Página base de lista rápida de Conjuros (conceptual P4)** **solo si hay conjuros reales**; nunca omitir el bloque de Lanzamiento en P1.
5. **Extensiones C2**, según contenido sobrante, **intercaladas cerca del último origen base del que participan** (no necesariamente todas después de P4); pueden compartir módulos distintos, excepto Notas.
6. **Notas digitales C-N3**, **solo si hay Notas generales o tituladas reales**, cada página exclusiva de Notas; ninguna nota en C2 ni viceversa.
7. **Libro de Conjuros**, únicamente si se solicitó el **anexo opcional**; tras el contenido digital y Notas C-N3 cuando existan.
8. **Última página COMPLETAMENTE manuscrita**, **siempre y literalmente la última página física**, también si hay Libro. Arriba dos columnas rayadas; abajo cuadrícula.

**Aclaración:** los puntos 1–4 nombran bases conceptuales; una extensión C2 puede intercalarse entre ellas, por lo que los números físicos y referencias se fijan **solo tras paginar**.

## 3. Matriz para revisar las páginas

| Vista | Contenido/estructura aprobados | Comportamiento exigido y riesgo que revisar |
| --- | --- | --- |
| **P1 cabecera** | Logo D&D superior izquierdo; **retrato cuadrado**, nombre PJ protagonista, jugador, Clase/Nivel a derecha del retrato, Raza/Alineamiento 60/40 y demás identificación | Cabecera **compacta**, no banda gigante. **Dos opciones de imagen son candidatos del retrato**, NO ilustraciones de Trasfondo; encuadre elegible **recortar** o **ajustar imagen completa**; sin retrato **silueta genérica**. Tipografía/ubicación física pendientes |
| **P1 izquierda** | Variante **A**: atributos con salvaciones/habilidades asociadas; variante **B**: listas separadas. Seis atributos normales más valores y habilidades personalizados | Mantener los **seis** y admitir 2–3 atributos adicionales/habilidades vinculadas, con altura ajustable y C2 si excede; no separar grupos arbitrariamente |
| **P1 derecha** | Combate/supervivencia aprobado, **Ataques** de tres columnas (`ARMA / ATAQUE / CONJURO`, `BONIFICADOR`, `DAÑO / TIPO DE DAÑO`), **Rasgos y Atributos** (`origen - nombre`, sin descripción), Lanzamiento de Conjuros, Municiones | Ataques y Rasgos se reparten verticalmente de manera dinámica; reservas escribibles **amplias** para Ataques sin registros y útiles para Rasgos. Lanzamiento siempre presente, incluso no lanzadores, niveles **1–9** en cuadrícula 3×3, **10–12 solo si regla épica real activada**. Municiones por tipo: hasta 20 casillas en grupos de cinco y 1–2 subfilas; cuando cantidad >20, `____ / X`. Sin Municiones: **una entrada lógica manual**, sin número fijo de casillas |
| **P2 izquierda** | **Equipo ordinario** arriba; **Tesoro** inmediatamente debajo, pares compactos `denominación + cantidad` en subcolumnas internas con salto de fila | Equipo sin registros: **reserva manuscrita amplia**. Tesoro sin moneda real: mínimo útil de celdas y expansión solo si queda espacio. Monedas reales/custom y cantidades **sin invenciones**; valor registrado cero no equivale a ausencia |
| **P2 derecha** | Continuación **Rasgos y Atributos** mecánicos sin duplicados ni descripciones | Equilibrar con izquierda sin truncar ni borrar reserva manual útil |
| **P2 abajo** | **Equipo Especial a todo el ancho**; `Ubicación / Nombre / Descripción` + casilla **ACTIVO** | Con datos, fichas reales y reserva adaptable; sin objetos, algunas ubicaciones habituales preimpresas y otras libres, según nombres reales validados de la App. **ACTIVO** no se marca por inferencia ni equivale automáticamente a equipado/sintonizado |
| **P3 izquierda** | **Trasfondo, Religión/Fe, descripción/resumen, Rasgos de personalidad, Ideales, Vínculos, Defectos, Historia** | **Siempre se imprime**, incluso vacía; conservar espacio de escritura. Historia larga completa y continuaciones legibles. **Sin imágenes de Trasfondo** |
| **P3 derecha** | Rasgos y Atributos mecánicos que no cupieron previamente | Cuando no queden Rasgos pendientes: reserva manuscrita **compacta** de Rasgos y superficie restante aprovechable para Historia manuscrita, sin inventar continuación digital |
| **Lista rápida de Conjuros** | **Tres columnas dinámicas**, agrupación por nivel, referencia mágica superior, cabecera por nivel **en una línea**, marcas de preparación/fuentes y de gasto diferenciadas | **Omitir página si no hay conjuros reales** (de cualquier fuente); conservar fuentes/preparaciones distintas sin mezclarlas en un estado falso; casos épicos/ritual/concentración y casillas a validar |
| **Notas C-N3** | Notas generales y tituladas, con **prioridad íntegra del texto digital** y reserva manuscrita secundaria cuando quepa | Páginas **exclusivas**. Con cero Notas digitales, **omitir C-N3**, manteniendo siempre hoja final manuscrita. Textos largos continúan íntegros |
| **Libro opcional** | **DOS columnas por página**, fichas de altura variable; `nombre / nivel / escuela / descripción completa` | Mantener una ficha **unida** si cabe entera en siguiente columna. Si supera columna completa, cortar legiblemente con identificación/continuación y sin pérdida. No añadir cabeceras de tiempo/alcance/componentes/duración; conservar orden como anexo |
| **Última hoja** | Solo escritura, dos columnas rayadas arriba y cuadrícula abajo; logo D&D + nombre del PJ | **Siempre al final**, totalmente libre de texto digital, overflow, fichas y demás módulos |

## 4. Sistema visual transversal ya elegido

- **Dirección estética A — Azul niebla clásico**, tonos suaves y bandas alternadas por matices, legible también en grises, tinta moderada.
- **Marco exterior C2** (doble discreto), cartelas interiores **C2c** de esquinas recortadas como dirección aprobada; no aplicar ornamentación complicada a casillas pequeñas de lápiz.
- **Barlow Condensed** para rótulos, encabezados y subencabezados. **Fuentes para datos/valores todavía NO elegidas**. Contrastar tipografías reales, español con tildes/ñ y números, sin fingir muestras de una familia con otra.
- Glifos preferentes **Para Hoja de PJ Symbols v8** y complementarios **Font Awesome Free**, con verificación de recursos/licencias.
- **Renglones nominales de 7 mm** en tablas/listas y posibilidad de **5 mm** en Habilidades, con crecimiento cuando haya registros multilínea. Casillas suficientemente grandes para usar lápiz; tamaños/colores exactos por validar.
- Todas las páginas —bases, C2, C-N3, Libro y última hoja— contienen **logo D&D + nombre del PJ**, sin repetir cabecera completa fuera de P1.

## 5. Navegación, datos y crecimiento dinámico

- Prioridad de composición **integridad de datos → legibilidad/escritura manual → facilidad para hallar la información → ahorro de páginas**. Las reservas manuales aprobadas **no** autorizan ocultar datos.
- C2 puede compartir Ataques, características, salvaciones, habilidades, Equipo, Tesoro, Equipo Especial, Rasgos, narrativa y Conjuros conforme a sus formatos. El compositor compara automáticamente **vertical / dos columnas / mixto**; referencias de continuación de **ida y vuelta por módulo**, con números físicos finales.
- **Notas nunca se mezclan** con otros módulos; Libro tampoco invade Notas ni última hoja. Texto digital extenso puede exigir más páginas.
- La exportación **se regenera desde los datos actuales**. La hoja impresa anterior no cambia automáticamente. No hace falta una decisión de usuario para cada combinación de módulos vacíos ya regulada por §§3.36–3.44.
- Verificar las fuentes y estados reales de App antes de interpretar monedas `0`, ranuras mágicas, reglas épicas, conjuros de múltiples fuentes, Equipo Especial `ACTIVO`, Notas con campos vacíos, elecciones de retrato y metadatos de conjuros. **Nunca inventar campos o valores**.

## 6. Casos que deben cubrir la próxima revisión integral

Estos son **escenarios de inspección/documentación, no fixtures nuevos ni PDFs creados**:

1. **PJ recién creado o casi vacío:** P1 Lanzamiento obligatorio, Ataques y Municiones manuales; P2 amplio inventario vacío/Tesoro/Equipo Especial, P3 manual, sin P4 ni C-N3; hoja manuscrita final.
2. **No lanzador con inventario extenso:** P4 ausente pero Lanzamiento de P1 presente, P2 y C2 con inventario/monedas custom, Equipo Especial y referencias.
3. **PJ con muchos atributos/habilidades custom:** ambas variantes A/B de P1, 6+2 y 6+3 atributos, habilidades por atributo y C2 si necesario, sin desordenar Combate.
4. **Lanzador con fuentes/preparaciones múltiples:** P1 slots, P4 tres columnas, valores por fuente sin confundir preparación y gasto; Libro opcional a dos columnas de fichas esenciales.
5. **Historia/Notas largas:** P3 íntegra y posibles C2; Notas digitales solo en C-N3, sin mezclarse; final siempre sin contenido.
6. **Libro con fichas cortas y enormes:** ficha que pasa entera a columna siguiente y ficha que requiere varios tramos; orden de lectura inequívoco, última hoja final intacta.
7. **Retrato en dos opciones de fuente:** encuadre recortar vs ajustar, sin retrato con silueta; comprobar diferencia entre dato realmente ausente e imagen inaccesible.

## 7. Pendientes clasificados por responsable — NO nuevas decisiones automáticas

| Clase | Responsable | Criterio |
| --- | --- | --- |
| Tamaño de papel, márgenes, anchos, alturas, intercolumna, mínimos visuales, cantidad de casillas/renglones y cortes | **Técnico / revisión física** | Proponer mediciones verificables; comprobar legibilidad sin cambiar comportamiento. **No hay resultados medidos aún** |
| Fuente tipográfica de valores, contraste, cabeceras, visualización de continuaciones | **Preparar opciones visuales para una revisión integrada del propietario** | No autoaprobar gusto estético; evitar microvotaciones; contrastar bajo mismo contenido/escala |
| Lectura de datos reales y estados, campos disponibles, fuentes de magia, imagen vs recurso inaccesible, nombres válidos de ubicación | **Verificación técnica** | Escalar al propietario si hay ambigüedad funcional o semántica auténtica; no inventar defaults |
| Muchas fuentes de conjuro o narrativas extremas y datos custom | **Verificación técnica y composición** | Mantener íntegro el contenido; cualquier pérdida/incompatibilidad es bloqueante |
| **Un nuevo anexo distinto del Libro** | **FUERA DE ALCANCE** | No sugerir ni implementar sin nueva solicitud explícita del propietario |
| Cambio de módulos, datos visibles, reglas de juego, accesibilidad/uso o estructura aprobada | **Propietario** | Una pregunta agrupada **solo ante conflicto concreto demostrado**, con alternativas y consecuencias |

## 8. Puertas de salida de esta fase

**Estado presente:** cierre conceptual **agrupado** aprobado, decisiones §§3.1–3.48 vigentes, **maqueta integral no evaluada**, PIN VISUAL aún activo y **sin autorización de código/PDF**.

**Siguiente entrega legítima de diseño:** paquete de **propuestas visuales integrales** que muestre el documento completo, variantes requeridas de P1, familias C2/C-N3/Libro y hoja final, con riesgos, sin atribuir aprobación a una muestra aislada. Antes de generar PDFs o modificar el renderer, solicitar autorización expresa de la fase/artefactos; el plan agrupado no constituye esa autorización.

**Criterios para declarar listo el contrato integral:** aprobación visual del propietario sobre la composición a escala real; revisión de fuentes/datos auténticos; ejemplos extremos sin pérdida; navegación; uso manuscrito; casos especiales; y resolución material de conflictos. **Ninguno de esos gates se declara PASS por el solo hecho de aprobar este brief o de tener CI verde en PR documental**.

**Regla de anti-bucle:** no reabrir §§3.1–3.48 ni generar una «decisión 49» por una mera variación técnica ya regida. Si se detecta una incompatibilidad real, registrar evidencia mínima, opciones y efecto en normas, pedir al propietario una sola decisión agrupada y luego reanudar el camino a la maqueta.
