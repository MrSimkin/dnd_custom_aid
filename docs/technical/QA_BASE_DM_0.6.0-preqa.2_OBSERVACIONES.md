# Observaciones del propietario · QA «Base para DM» 0.6.0-preqa.2

Este registro es el libro de aceptación de la próxima corrección (AGENTS §6.3). Ninguna observación se cierra por documentación, por una prueba verde ni por un dato de prueba cambiado. Se cierra solo con evidencia del candidato instalado.

## Datos del QA

- **Candidato probado:**
  - APK `0.6.0-preqa.2` (build 60200), SHA-256 `8aade822…`;
  - App de Windows `0.6.2`, SHA-256 `9d01b038…`.
- **Fuente:** commit `b333fcd1` en `feature/dm-ux-redesign`.
- **Ventana de QA:** https://claude.ai/artifact/KJaAuzENdus3wmHuxGL6Bq, colección `resultados`, respuestas del 2026-11-13 entre 15:02 y 15:45 UTC.
- **Resumen:** 7 bien · 5 falla · 33 no aplica.

## Observaciones originales, al pie de la letra

| Id | Marca | Texto del propietario | Estado |
| --- | --- | --- | --- |
| P1 | Bien | — | — |
| P2 | Bien | — | — |
| P3 | Bien | «El nombre gestores es poco claro y la forma del menu es poco clara, no queda claro que es cada funcion. No tuve que loguearme, asique no se que usuario estoy operando» | ABIERTA |
| P4 | Bien | — | — |
| P5 | Falla | «quiero que uses el termino dungeon, nunca mazmorra, no me gusta masmorra / no puedo crear un dungeon, solo lugares zonas / Necesito que releas el documento o pienses en un mejor UX, porque no entiendo como crear varias zonas y conectarlas,» | ABIERTA |
| P6 | Falla | «Necesito que » (cortado en la ventana). Completado en el chat el 2026-11-13: «"necesito que sea mas claro", lo siento era un comentario menor» | ABIERTA · lo cubre A3 (Preparar, con nombres y propósito claros) |
| P7 | Bien | «quiero que el explorador se parezca al de windows, o por lo menos que me permita guardar o setear carpetas tipo, es una joda navegar hasta la carpeta de una campaña, no puedo mover las marcas» | ABIERTA |
| P8 | Falla | «use gmail / si siquiera veo el botón que me dices» | ABIERTA |
| E1 | Falla | «Solo pude entrar como jugador, no hay un menu claro y no pude sincronizar nada» | ABIERTA |
| E2–E7 | No aplica | — (bloqueadas por E1) | BLOQUEADAS |
| R1 | No aplica | «pero puede verlo en el PC» | BLOQUEADA |
| R2–R3, S1–S7, C1–C5, D1–D9 | No aplica | — (bloqueadas por E1) | BLOQUEADAS |
| N1 | Bien | «lo puede hacer en PC, pero no puedo probrlo en tablet y no encuentro las notas» | ABIERTA |
| N2 | No aplica | — | BLOQUEADA |
| W1 | Bien | — | — |
| W2–W3 | No aplica | — | BLOQUEADAS |
| W4 | Falla | «no encuentro eso» | ABIERTA |

## Mensaje del propietario en el chat (2026-11-13), al pie de la letra

- «Me falta la opcion, funciona a medias en la amrca de QA»
- «y observaciones relacionadas pero no de la prueba especifica y observaciones adicional al final del QA, tuve varios problemas»
- «probablemente tengas dudas»
- «La UX me confunde mucho, no entiendo nada»
- «Si te envio un DOCX con una ciudad entera, aventuras y dungeons, puedes crear una versión parcial (con solo una parte del contenido) para hacer pruebas?»

## Primera lectura técnica

Esta lectura **no** es un diagnóstico cerrado. Solo explica qué falta comprender antes de corregir.

1. **E1, P8 y la mayoría de los «No aplica».**
   - El rol en la tablet sale de la membresía alojada de la cuenta (`campaignRoleFor`).
   - Con Gmail, la cuenta es Jugador o no tiene membresía, así que la tablet solo ofrece «Abrir» de jugador.
   - En Windows, Administración de campaña muestra «Sincronizar contenido del DM» solo con una membresía DM activa.
   - La ventana pedía Outlook, pero ni la App ni la ventana muestran con qué cuenta y con qué rol estás.
   - Es un fallo de experiencia, aunque el permiso funcione como se diseñó.
2. **P3 y «no sé qué usuario estoy operando».**
   - Windows trabaja en local sin pedir sesión, y la sesión vive dentro de Administración de campaña.
   - «Gestores» es un nombre técnico.
3. **W4.**
   - «Responder con IA» aparece solo con una sesión iniciada (`rulesAiAvailable`).
   - Sin sesión, el botón no se muestra y nada explica por qué.
4. **P5.**
   - No existe un objeto «dungeon» que se pueda crear: el dungeon es un texto repetido en cada zona.
   - Conectar zonas exige abrir cada una por separado.
   - Además, el término debe ser «dungeon» en toda la App.
5. **P7.** Selector de archivos propio sin carpetas favoritas; las marcas del mapa no se pueden arrastrar.
6. **N1.** En Windows la nota se puede crear; en la tablet quedó bloqueada por E1. Falta aclarar dónde buscó las notas y no las encontró.
7. **Ventana de QA.** Faltaban «A medias», una observación por prueba fuera de la prueba y un cuadro final. Se agregaron en la versión 3 de la ventana, con las mismas pruebas.

## Decisiones del propietario

| # | Fecha | Pregunta | Respuesta, al pie de la letra | Consecuencia |
| --- | --- | --- | --- | --- |
| D1 | 2026-11-13 | ¿En la tablet quieres poder ser DM también sin iniciar sesión? (se recomendó que sí) | «No, solo con cuenta» | El Escritorio DM de la tablet exige una sesión con rol DM en la campaña. La corrección no agrega un DM local sin cuenta; debe hacer visible la cuenta y el rol, y explicar por qué no aparece el Escritorio DM. |
| D2 | 2026-11-13 | ¿La App de Windows debe pedir iniciar sesión al abrirse, igual que la tablet? | «si, al abrirse, y con "recordar" automatico, si quiero cambiar despues, necesito "cerrar sesion" o "cambiar sesion", aunque deberia ser usuario no sesion, tu me entiendes» | Windows pide la cuenta al abrirse y la recuerda sola. Para cambiarla hay «Cerrar sesión» y «Cambiar usuario» (se dice usuario, no sesión). |
| D3 | 2026-11-13 | (comentario del propietario, sin pregunta) | «entiendo que ahora la sincronizacion está en sebug, pero tiene que estar clarita en menu normal tambien, para probar, hemos avanzado de eso» | Sincronizar sale de Administración de campaña y queda visible en el menú normal de Windows. |
| D4 | 2026-11-13 | (aclaración del propietario, sin pregunta) | «Gmail es mi cuenta de DM/jugador (mientras hagamos las pruebas) / y Outlook es mi cuenta de jugador» | Reemplaza la identidad canónica de AGENTS §9. En el servidor DEV (lectura del 2026-11-13), en «QA - PC Sheet PDF Runtime» Gmail es PLAYER/ACTIVE y Outlook DM/ACTIVE; «Hosted Batch Test» solo tiene a Outlook como DM. Ninguna campaña tiene contenido DM sincronizado (0 elementos). Esto explica E1 y P8. Cambiar los roles en el servidor necesita el sí del propietario. |
| D5 | 2026-11-13 | ¿Así quieres crear un dungeon y conectar sus zonas en Windows? (maqueta: lista de dungeons, plano con «＋ Zona», arrastrar para unir zonas, pasaje secreto punteado, panel de zona) | «lo de nuevo dungeon es cuando estoy administrando, el resto en los escritorioa, creo yo / no se si entendi bien la pregunta, la respuesta correcta es si, quizas con ajustes» | Se aprueba la maqueta, con posibles ajustes. Crear y armar un dungeon va en la parte de preparar (lo que hoy se llama Gestores); jugarlo (turnos, marcas, mover el grupo) va en la Mesa de Dungeon de los escritorios. Los ajustes se verán en el próximo QA. |
| D6 | 2026-11-13 | ¿Cambio en el servidor de pruebas a Gmail como DM y a Outlook como jugador? | «Sí, cámbialo» | Aplicado en Neon DEV (ver evidencia). |

### Evidencia D6 (Neon DEV, 2026-11-13)

- Campaña `7a000000-0000-4000-8000-000000000001` («QA - PC Sheet PDF Runtime»).
- **Antes:** Gmail `4ba0f476…` PLAYER/ACTIVE; Outlook `f34bc5f0…` DM/ACTIVE; revisión 0.
- **Después:** Gmail DM/ACTIVE; Outlook PLAYER/ACTIVE; revisión 1.
- Se hizo en una sola transacción, que se deshacía si no cambiaba exactamente una fila de cada cuenta. La propiedad y el control de los 3 PJ de prueba siguen en Gmail.
- La conexión pasó de `neonctl` al controlador sin mostrarse ni guardarse.
- «Hosted Batch Test» no se tocó: Outlook sigue como único DM.

## Comentarios generales del propietario (2026-11-13), al pie de la letra

| Id | Texto | Estado |
| --- | --- | --- |
| G1 | «la UX es muy confura, los nombres no son ideales, si tuviera mi tablet en la mesa no sabria como rayos operarla de forma rápida» | ABIERTA |
| G2 | «no tengo como enviar handouts a mis jugadores» | ABIERTA |
| G3 | «la organizacion de campañas es muy "mutante" y lo hace dificil» | ABIERTA |
| G4 | «no puedo leer el SRD, ni me demostraste que ahora puedo crear PJ con el SRD y se suponia que aunque no esté desarrollado deberia estar el ver el SRD y yo poder agregar cosas homebrew como objetos, clases, dotes, conjuros, etc, si bien no esta desarrollado no está "la maqueta" de aqui va.....» | ABIERTA |
| G5 | «en el QA me dalta un "A Medias", o "En parte" o "bien, pero no era lo que yo esperaba que se hiciera", y una seccion de "mis comentarios adicionales" sobre la prueba y sobre el total.» | CORREGIDA en la ventana de QA, versión 5: «Bien, pero no lo esperado», «En parte», «Mis comentarios adicionales» por prueba y «sobre todo el QA». |

Lectura del estado actual para G4 (sin cerrar):

- El SRD está en la App solo como texto para buscar reglas en el Escritorio DM (`SrdCorpus`).
- No hay un lector del SRD para todos.
- El editor de PJ no usa listas del SRD: razas, clases, trasfondos, dotes, conjuros y objetos estructurados siguen pendientes en `ROADMAP.md`.
- No hay pantallas de homebrew para esas familias, ni siquiera vacías.

## D7 · Rediseño delegado (2026-11-13), al pie de la letra

> «no soy diseñador ni programador, asique tendré que confiar en ti en mejoras o rediseños, y asi lo haré, quizas mi diseño era poco optimo, pero sabes que necesito y para que, ademas para eso tienes antecedentes en el repo.
> Por el momento cederé toda esa parte de rediseño a ti, y lo quiero todo para evaluar en el QA»

**Consecuencias:**

- El agente decide la experiencia, los nombres y la organización. No consulta el estilo ni los detalles de interfaz.
- Se apoya en los antecedentes del repositorio: `PRODUCT.md`, decisiones, `DM_UX_APROBADA.md` y los diseños aprobados P1 a P37.
- Todo el rediseño llega junto a un QA del propietario, que lo evalúa allí.
- Siguen siendo del propietario:
  - el alcance del MVP cuando agregue una función grande nueva;
  - la privacidad y la visibilidad hacia los jugadores;
  - las acciones destructivas;
  - los costos.
- Las decisiones D1 a D6 de arriba se mantienen.

## D8 · Crear PJ desde el SRD (2026-11-13)

- **Pregunta:** «¿Crear PJ eligiendo raza, clase y lo demás desde el SRD entra en este QA?»
- **Respuesta, al pie de la letra:** «Sí, entra».
- **Consecuencia:** se adelanta al QA del rediseño el trabajo de razas, clases, trasfondos, dotes, conjuros y objetos estructurados, que `ROADMAP.md` dejaba para después. La hoja V11 y su PDF no cambian de diseño; solo reciben los datos que se eligen.

## D9 · Orden de selección en todo el editor (2026-11-13)

- **Respuesta, al pie de la letra:** «no solo entra, pero varias cosas como agregar objetos, ataques, armas y similares, ahora debe por defecto permitir seleccionar desde el SRD, y luego desde homebrew, y luego desde custom».
- **Consecuencia:** cada vez que se agrega algo a un PJ (objeto, arma, armadura, ataque, conjuro, dote, rasgo y similares), el selector ofrece primero el SRD, después el homebrew de la campaña o personal y al final la entrada libre (custom).

## Material de prueba del propietario (2026-11-13)

Se entregó para armar un set de prueba realista. **No entra al repositorio público.** Se procesa solo en la PC del propietario y en su servidor DEV.

- `Campus_Celeste - Escenario de Campaña v2.docx`, con:
  - ciudad en 7 distritos;
  - unas 60 tiendas y servicios con inventario;
  - 31 PNJ;
  - 9 aventuras con escenas;
  - dungeons por zonas (Sitio 021, La Torre, Depósitos Retenidos, Conductos, Capilla del Eco Sellado, Cuarto de Verificación);
  - 6 diagramas.
- `Banos_Campus_Celeste_v2_1_Maquetado.docx` y `v2_2_Compacto_A4.docx`, la minisesión de los baños; los dos textos son casi iguales.
- `Campus_Celeste_10_ganchos_medallon.docx`.
- 11 imágenes: mapas de la ciudad, la región con D1 a D5 y el distrito Pórtico Bajo.

## D10 y D11 · Qué entra en el set de prueba (2026-11-13)

- **D10, aventuras.**
  - Pregunta: «¿qué aventuras de Campus Celeste meto?»
  - Respuesta: «La Forja (Sitio 021), La Versión No Oficial».
  - Además, al pie de la letra: «quiero la ciudad como lugares y tambien quiero todo lo de los baños».
- **D11, PJ.**
  - Pregunta: «¿Cómo los cargamos?»
  - Respuesta, al pie de la letra: «Crealos tu, como "placeholder", pero hyo te voy a decir uno por uno, quienes son y como son».
  - Los PJ Nerissa, Silas Ren, Eilixis y Arruk empiezan como fichas provisionales. Se completan con lo que el propietario describa, uno por uno.
- **Set resultante.** Una campaña de prueba, con Gmail como DM, que lleva:
  - la ciudad como lugares (7 distritos, lugares clave, tiendas y servicios con su inventario y sus precios);
  - los 31 PNJ del escenario y todo el material de los baños (3 establecimientos, personal, variantes, tablas, rumores, invitaciones, carteles e hilos personales);
  - los 10 ganchos;
  - La Forja (escenas, Sitio 021 con 12 zonas y 5 combates);
  - La Versión No Oficial (escenas, La Torre con 13 zonas, 6 encuentros y 4 puzzles);
  - los mapas e imágenes como handouts.
- **Dónde queda.** Los archivos del set viven en `D:\DnD_Aid\Set de prueba Campus Celeste`, fuera del repositorio.

## D12 · PJ del set de prueba (2026-11-13)

- Los 4 PJ son de nivel 7. El propietario dio especie, clase, armas de leyenda, trasfondo homebrew y backstory de cada uno, más sus manuales homebrew: Manual Simkin 1.1 y 1.4, Tomo de Magia 1.0 y Fe en el Mundo Fragmentado v2.
- El detalle, con los nombres reales de los jugadores, queda fuera del repositorio, en `D:\DnD_Aid\Set de prueba Campus Celeste\PJ_del_grupo.md`.
- El homebrew se extrae de esos manuales para la biblioteca de prueba (A8, A11 y A12). Los conjuros oficiales del Tomo que no están en el SRD solo se usan en datos privados, nunca en el repositorio.

## Alcance del rediseño (libro de aceptación)

| # | Qué se hará | Observaciones que cubre |
| --- | --- | --- |
| A1 | Entrada y cuenta: Windows pide la cuenta al abrir y la recuerda; la cuenta y el rol se ven siempre; «Cerrar sesión» y «Cambiar usuario»; si una cuenta no es DM, se explica por qué no aparece el Escritorio DM. | P3, P8, E1, W4, D1, D2, D4 |
| A2 | Campañas: una sola lista estable de campañas con su rol, en la tablet y en Windows, sin pantallas que cambian según el estado. | G3, E1 |
| A3 | Windows, preparar: «Gestores» se reemplaza por una sección «Preparar» con nombres claros y una línea que dice para qué sirve cada cosa. Sincronizar queda en el menú normal. | P3, P8, D3 |
| A4 | Dungeons: objeto propio y editor con plano (maqueta D5); en toda la App se dice «dungeon». | P5, D5 |
| A5 | Archivos y mapas: carpetas favoritas y recientes, la carpeta de la campaña recordada; marcas del mapa que se arrastran. | P7 |
| A6 | Tablet en la mesa: Escritorio pensado para uso rápido, con acciones grandes y directas y nombres claros. | G1, E1 |
| A7 | Handouts a los jugadores: el DM elige qué mostrar y el jugador lo ve en su App. | G2 |
| A8 | SRD: lector del SRD para todos, y pantallas de homebrew por familia (objetos, clases, dotes, conjuros…), con su lugar reservado aunque aún estén vacías. | G4 |
| A9 | Notas: un lugar fijo donde encontrarlas en la tablet y en Windows. | N1 |
| A10 | P6: las escenas en Preparar tienen que ser más claras (comentario menor); lo cubre A3. | P6 |
| A12 | Selector SRD → homebrew → custom al agregar objetos, armas, ataques, conjuros, dotes y similares a un PJ. | G4, D9 |
| A11 | Crear PJ eligiendo desde el SRD (especie o raza, clase, subclase, trasfondo, dotes, conjuros y equipo) en el editor de PJ, 5e y 5.5e, y cada lista con su homebrew de A8. | G4, D8 |

## Próximo paso

1. Recibir el DOCX de contenido para armar una versión parcial de prueba. Ese contenido no entra al repositorio público.
2. Implementar A1 a A9 y A11, y preparar un QA que los cubra todos.
