# Revisión crítica de la App (2026-11-14)

A pedido del propietario («quiero que hagas un análisis sobre toda la App… sin límites ni reservas»), el agente listó 15 problemas y 4 prioridades. Este documento registra las respuestas del propietario, textuales, y lo que se propone o decide para cada punto. Es la fuente de verdad para la etapa que sigue: el refactor va primero.

## Respuestas del propietario y propuesta para cada punto

| # | Problema | Respuesta textual | Propuesta o acción | Estado |
| --- | --- | --- | --- | --- |
| 1 | El contenido propio no tiene una forma real de entrar a la App. | «quiero que haya un importador que lea un formato dado, cosa que pueda darle mi material a una IA y lo convierta al formato que pueda importar en la App, pero aun no llegamos a eso.» | Un formato de importación documentado, pensado para que una IA lo produzca a partir de tus DOCX. Se diseña más adelante. | Para después |
| 2 | El agente entrega candidatas sin haberlas usado en la App real. | «entonces corrigelo y registralo» | Regla nueva en AGENTS §6.4: antes de cada QA, recorrer la App real (Windows y emulador) con datos aparte, con capturas por prueba, y entregar solo lo que se vio funcionar. Windows tendrá una carpeta de datos aparte para esas pruebas. | Registrado; se aplica desde ya |
| 3 | La sincronización es frágil y opaca, y no se sabe cómo consolidar diferencias. | «tienes razon y tampoco se como consolidar si hay diferencias» · «pero hay varias sincronizaciones, necesitas aclararme como hacerlo, que propones» | Ver «Propuesta de sincronización». | Espera decisión |
| 4 | «Personal» contra «campaña» confunde. | «solicionalo» | Lo preparado vive en la campaña. Desaparecen los botones «Crear Personal», «Crear en campaña» y «Copiar a campaña». Queda una sola biblioteca del DM, solo para homebrew de reglas, siempre visible con su cuenta. Para reutilizar algo se usa «Copiar a otra campaña». El modelo de datos no cambia, solo lo que se ve. | Va en el refactor |
| 5 | Lo de un aparato no siempre está en el otro. | «que propones?» | Una tabla pública de qué se hace dónde, aplicada sin excepciones. Windows: preparar todo, las mesas, crear PJ (se lleva el asistente) y cargar contenido. Tablet: las mesas, editar rápido, crear PJ y cargar contenido. Cargar contenido sale de «Diagnóstico» y pasa a «Campañas» en los dos. Preparar sigue en Windows (decisión D5). | Propuesto |
| 6 | El rediseño quedó como fachada: los editores siguen con lenguaje técnico. | «entonces que propones?» | Cada objeto de Preparar se abre como la ficha que verás en la mesa, con «Editar» por bloque. Se van revisión, IDs, copias y tecnicismos. Personajes de Windows deja las líneas de auditoría («Tombstone», «outbox»), que pasan a Diagnóstico. | Aprobado (prioridad 4: «si») |
| 7 | La ayuda tapa pantallas que no se explican solas. | «que propones» | Cada pantalla tiene una acción principal clara, y las pantallas vacías enseñan qué hacer («Aún no hay dungeons → ＋ Nuevo dungeon»). La ayuda queda como apoyo. Regla: si una pantalla necesita más de tres pasos de ayuda, se rediseña. | Propuesto |
| 8 | Dos estilos visuales en Windows. | «entonces corrige» | Un solo estilo (Material 3) y un juego de componentes compartido por Windows y la tablet. | Va en el refactor |
| 9 | No se ve cómo entra un jugador a la campaña. | «que propones» | En Jugadores de la campaña, «Invitar»: un código de 6 letras que vale 7 días. El jugador lo escribe en «Tus campañas → Unirme con código». Después el DM le asigna su PJ. | Propuesto; la privacidad la decide el propietario |
| 10 | Los formularios son demasiado largos para la mesa. | «que pro`pones» | Dos vistas por objeto. «Ficha de mesa»: lo esencial en 5 o 6 líneas (qué es, qué quiere, secreto, combate breve), que es lo que muestran las mesas. «Preparación completa»: todo, con «Ver todo». | Propuesto |
| 11 | Los datos extraídos automáticamente pueden traer errores sin aviso. | «que propones?» | Revisiones automáticas en cada construcción (todo monstruo con CA, PG y acciones; toda clase con 20 niveles…) y un informe de lo incompleto. En la App, «Ver en el SRD, p. X» para comparar con el original. Los conjuros del Tomo se completan cuando exista el importador del punto 1. | Propuesto |
| 12 | Cargar el paquete otra vez no actualiza lo editado, y no se ve qué quedó viejo. | «entonces que propones» | Una pantalla «Cambios del paquete» lista lo que difiere de lo tuyo. Para cada objeto: «mantener lo mío», «usar lo del paquete» o «ver diferencias». Usa la misma pantalla de diferencias que la sincronización. | Propuesto |
| 13 | Mucho código viejo y archivos enormes. | «entoncces llego la hora de refactorizar, ahora esto es prioridad antes de continuar» | Ver «Plan de refactor». | Prioridad 1 |
| 14 | Peso de la APK y del SRD. | «eso no es un problema» | Sin cambios. | Cerrado |
| 15 | Los topes del servidor gratuito. | «podemos hacer un sistema que dependa de una cuenta gratuita de drive o dropbox? pensemos en planes para cambiar.» | Ver «Planes para el servidor». | Espera decisión |

Respuestas a las prioridades:
1. Cambiar la forma de entregar: «ok».
2. Cargar el contenido propio: «tendrémos que discutir como va a funcionar eso».
3. Sincronización: «pero hay varias sincronizaciones, necesitas aclararme como hacerlo, que propones».
4. Rehacer los editores de Preparar y llevar el asistente de PJ a Windows: «si».

## Propuesta de sincronización

**Hoy hay cinco sincronizaciones separadas:**
1. Cuenta, campañas y roles.
2. PJ (hojas).
3. Contenido del DM (lugares, PNJ, escenas, dungeons, reglas…).
4. Imágenes.
5. Lo enviado a los jugadores.

Cada una tiene su cola y sus reglas, y hay tres botones distintos que no corren todo.

**Propuesta:**
1. **Un solo «Sincronizar»** que corre las cinco en orden. Además corre solo al abrir la App y cada pocos minutos si hay conexión. El botón queda para forzarla.
2. **Envío en lotes** de hasta cien objetos por petición. La primera carga de Campus Celeste pasa de unos 15 minutos a segundos, y se gastan cien veces menos peticiones del plan gratis (punto 15).
3. **Una pantalla «Estado de sincronización»**, en palabras simples:
   - «todo al día»;
   - «3 cambios esperando conexión»;
   - «1 diferencia que decidir»;
   - «2 imágenes que faltan».
4. **Regla para las diferencias**, que necesita la decisión del propietario:
   - Si un objeto cambió en un solo aparato, gana ese cambio, sin preguntar.
   - Si cambió en los dos, nada se pisa solo: la pantalla muestra las dos versiones lado a lado, con «quedarme con la de este aparato», «quedarme con la otra» o «guardar las dos».
   - Lo borrado en un aparato y editado en otro se pregunta igual.
5. **Los PJ siguen la misma regla.** El jugador de cada PJ pasa a viajar con el PJ.

## Planes para el servidor (punto 15)

Hoy el servidor es Cloudflare Worker + base Neon + KV para imágenes + Descope para entrar, todo en plan gratis. Los límites se revisan con los términos vigentes antes de decidir (AGENTS §10).

| Plan | Qué es | A favor | En contra |
| --- | --- | --- | --- |
| A, recomendado | Seguir igual, con envío en lotes. | Ya funciona, con roles y permisos; las peticiones bajan unas cien veces. | El límite diario de escrituras de imágenes en KV sigue. |
| B | A, pero las imágenes en tu Google Drive o Dropbox (una carpeta de la App, gratis), y el texto en el servidor. | Imágenes casi sin límite, en tu propia cuenta. | Una entrada más con Google o Dropbox; compartir imágenes con jugadores requiere enlaces. |
| C | Todo en Drive o Dropbox, sin servidor propio. | Sin servidor que mantener. | Se pierden roles y permisos propios (invariante de AGENTS §3), los conflictos empeoran y cada jugador necesita acceso a tus carpetas. No recomendado. |

## Plan de refactor (prioridad antes de continuar)

**Regla:** sin cambios de comportamiento. Cada fase se verifica con las pruebas y recorriendo la App real (AGENTS §6.4).

| Fase | Qué |
| --- | --- |
| F1 | Sacar código muerto: las hojas antiguas (Fantasy, Custom v1/v2) y sus pruebas, las pruebas de diseño viejas, nombres de etapas pasadas en lo nuevo. La hoja V11 no se toca. |
| F2 | Un solo estilo: la App de Windows pasa a Material 3 y comparte componentes con la tablet (títulos, campos, tarjetas, ayuda, avisos). |
| F3 | Dividir los archivos de más de mil líneas por responsabilidad: mesas, editores, sincronización. |
| F4 | Lo de «Personal / campaña» (punto 4) y Personajes sin auditoría técnica (punto 6). |
| F5 | Infraestructura de verificación: Windows con carpeta de datos aparte, recorrido guiado con capturas en Windows y en el emulador. |

Después del refactor siguen: la sincronización (cuando se decida la regla), los editores como fichas (punto 6, aprobado), el asistente de PJ en Windows y el resto de los puntos propuestos.

## Efecto sobre la candidata 0.7.0-preqa.3

Se recomienda al propietario no hacer todavía el QA de preqa.3. El refactor produce una nueva candidata que el agente recorre primero en la App real. Las pruebas de la ventana «QA Reparaciones DM» se reutilizan.

## Avance del refactor

### F1: sacar el código muerto (2026-11-17)

- **Hojas antiguas fuera de las dos Apps.** Se quitaron Fantasy Sheet, Custom v1 y Custom v2 (Classic, Hybrid, Extended, SharedBase, TemplateProof, VisualCompletion y WholeDraft), junto con sus renderizadores de libro de conjuros, retrato y primitivas, y su puente en Android. Son 11 archivos de Windows, 12 de Android y 28 pruebas de esas familias: unas 53.500 líneas menos.
- **Exportación.** Los servicios de Windows y Android exportan solo la hoja V11 A/B; las opciones de exportación ya no tienen «familia» ni «estadísticas personalizadas».
- **Script y CI.** Se quitó `scripts/generate_android_pc_sheet_renderer.py`, que copiaba las hojas antiguas de Windows a Android, junto con su paso en CI. La guarda `check_android_pc_sheet_pdf_delivery.py` ahora exige la ruta V11 y falla si vuelve una familia antigua.
- **Textos y nombres.** Se quitaron los textos de «PRUEBA INTERNA» de las pantallas y el nombre «PREQA» del PDF de la tablet. Ahora se llama «<PJ> - Hoja de PJ V11 A.pdf», igual que en Windows.
- **Sin cambios.** Los archivos del renderizador V11, su medidor de fuentes, el cargador de recursos y el planificador compartido no se tocaron, así que el PDF V11 no cambia. Las plantillas y fuentes del propietario en `assets/` se conservan.
- **Pendiente del proceso V11.** El PDF V11 todavía imprime «PRUEBA INTERNA V11 - NO APROBADA» en el pie de página. Es parte de la hoja contractual y se decide con su QA, no en el refactor.

### F2: un solo estilo (2026-11-17)

- **Tema único.** La App de PC usa solo Material 3 (antes, la mesa de trabajo usaba Material 2 y las mesas, Material 3). Los nueve temas de Configuración (Sistema, Claro, Oscuro, Gris, Púrpura, Cian, Azul noche, Verde bosque, Pergamino) conservan sus mismos colores, ahora como paletas propias (`DesktopPalette`) que alimentan un único `ColorScheme`. La fuente elegida se aplica a todos los estilos de texto.
- **Pantallas convertidas.** Las 21 pantallas de Windows pasaron a Material 3:
  - texto, botones, tarjetas, campos, menús y diálogos;
  - divisores como `HorizontalDivider`;
  - estilos de texto según la tabla de equivalencias (h4 → headlineMedium, caption → bodySmall…);
  - elevaciones de tarjeta y superficie con su forma nueva.
- **Sin Material 2.** Se quitó la dependencia de Material 2 de la App de PC, para que no vuelva a entrar.
- **Un solo tema en las mesas.** Las mesas ya no aplican un segundo tema propio: usan el de la App.
- **Pieza compartida.** `DmScreenHeader` (en `dmUi`) es la cabecera de pantalla con título, línea de propósito, «← Atrás» y «? ¿Cómo se usa?». La usan la mesa de trabajo de Windows (`WorkbenchHeading`) y la pantalla Reglas, en Windows y en la tablet.
- **Recorrido nuevo con capturas.** `DesktopWorkbenchTourTest` abre Inicio, Preparar, Lugares y tiendas, Dungeons, Personajes, Reglas, Handouts y mapas, Campañas y Configuración con contenido, y guarda una captura de cada una en `desktopApp/build/workbench-tour/`. Falla si alguna pantalla no abre.
- **Visto en las capturas, para F4 y el punto 6:**
  - quedan textos técnicos («Gestor de Escenarios», «Stage», «Personal», «auditarlo», «PC locales», «workbench»);
  - Configuración tiene su propia cabecera;
  - en el plano de dungeon, dos zonas sin posición quedan pegadas y tapan el pasaje.

### F3: dividir por responsabilidad (2026-11-17)

- **Una sola fuente para la hoja V11.** El renderizador V11 y su medidor de fuentes de Windows son la fuente. La copia de la tablet se genera con `scripts/sync_android_v11_renderer.py`, que cambia solo el paquete, la librería de PDF y la lectura del logo y las fuentes. La copia generada es idéntica byte a byte a la que había, así que el PDF no cambia. El CI (`scaffold-check`) exige que sigan iguales.
- **Archivos divididos.** Se movieron funciones completas, sin reescribirlas; solo cambiaron de `private` a `internal` las que se usan desde el archivo nuevo:

  | Archivo original | Antes | Después | Archivos nuevos |
  | --- | --- | --- | --- |
  | `CharacterEditorV4.kt` | 3.703 | 1.594 | `CharacterOverviewTabV4`, `CharacterSkillsTabV4`, `CharacterEditorFieldsV4`, `CharacterEditorDraftV4` |
  | `CombatDesk.kt` | 1.304 | ~320 | `CombatPrepareStep`, `CombatLiveStep`, `CombatCreatureEditing` |
  | `DesktopPcManager.kt` | 1.147 | ~710 | `DesktopPcInspection` (la inspección técnica que F4 saca de tu pantalla), `DesktopPcSheetExportPanel` |
  | `UiPreferences.kt` | 1.321 | ~700 | `AppSettingsScreen` (la pantalla Configuración de la tablet) |
  | `CharacterManagementSuccessorV4.kt` | 1.420 | ~980 | `CharacterManagementDialogsV4` |
  | `CharacterEquipmentClosureV4.kt` | 1.338 | ~720 | `CharacterEquipmentEditorV4` |

- **Pendiente.**
  - `CharacterEditorV4.kt` sigue en 1.594 líneas porque casi todo es una sola función de pantalla. Partirla exige reescribirla, y se hará cuando se toque ese editor.
  - Cinco archivos de pestañas de la tablet rondan las 1.000 a 1.170 líneas y se dividen cuando se trabaje en ellos.
  - Las copias del renderizador V11 no se dividen: son la hoja contractual.

### F4: un solo lugar para el contenido y sin palabras técnicas (2026-11-17)

Puntos 4 y 6. El modelo de datos no cambia; cambia lo que se ve.

- **Preparar en Windows (lugares, PNJ, criaturas, encuentros, escenas, zonas, reglas de la casa):**
  - se quitaron el título «Gestor de…» con su descripción técnica (Preparar ya muestra la cabecera), el botón «Crear Personal» y el aviso «Biblioteca Personal no disponible»;
  - «Crear en campaña» pasa a «＋ Crear», y el contador dice «N en esta campaña.»;
  - se quitaron del editor la línea «Personal/Campaña • revisión N», la «Copia de 1a2b…», la «Proveniencia» y la «Revisión» de los PNJ, y la revisión en «Cambios guardados»;
  - lo que quedó de la antigua biblioteca personal se marca «De tu biblioteca anterior» y se trae con «Traer a esta campaña»;
  - Lugares: «Organización Stage» pasa a «Ordenar y filtrar», se quitó el filtro «Ámbito: Personal / Campaña», y «Stage • Lugares / Tiendas» pasa a «Lugares y tiendas» (también en Escenas);
  - los separadores verticales vuelven a ser verticales (F2 los había dejado horizontales).
- **La biblioteca del DM** queda solo en Reglas → homebrew, como dice el punto 4.
- **Personajes en Windows:**
  - el título es «Personajes», y sin personaje elegido dice «Elige un personaje de la lista para verlo.» y «Personajes en esta campaña: N»;
  - las secciones se llaman «La hoja: lo principal», «La hoja: equipo, conjuros y rasgos», «Dueño y quién lo juega» y «Corregir como DM»;
  - sincronización, historial de reconciliación y estado interno quedan bajo «▸ Datos técnicos (para diagnóstico)», cerrado por defecto.
- **Cargar paquete** sale de Diagnóstico y pasa a Campañas, como en la tablet (punto 5). Se muestra para la campaña abierta cuando eres su DM o está solo en este equipo.
- **Configuración en Windows** usa la cabecera común («Configuración», con su ayuda) y textos sin «workbench» ni «fuentes Android».
- **Pendiente para los editores como fichas (punto 6, después del refactor):** «Copiar a otra campaña», la vista de ficha con «Editar» por bloque, y las zonas sin posición que se tapan en el plano del dungeon.
