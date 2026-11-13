# Rediseño «Base para DM» · plan de trabajo

**Origen:** QA del propietario sobre 0.6.0-preqa.2 y las decisiones D1 a D13 del 2026-11-13. El propietario delegó el rediseño al agente (D7).

**Libro de aceptación:** `QA_BASE_DM_0.6.0-preqa.2_OBSERVACIONES.md`, puntos A1 a A13.

**Entrega:** todo junto, en un solo candidato y un solo QA, con el formato de ventana de QA nuevo:
- marcas «Bien, pero no lo esperado» y «En parte»;
- «Mis comentarios adicionales» por prueba y para todo el QA.

## Principios del rediseño

1. **Una pregunta por pantalla.** Cada pantalla dice arriba qué es y para qué sirve, en una línea. No hay nombres técnicos («Gestores», «Wave», «payload»).
2. **Siempre se sabe quién eres.** La cuenta y el rol están arriba en todo momento, en la tablet y en Windows.
3. **Nada desaparece sin explicación.** Si algo no está disponible (rol, sesión, conexión), el lugar sigue ahí y dice por qué y qué hacer.
4. **Lo mismo en la tablet y en Windows.** Mismas palabras, mismo orden y misma paleta (la de Configuración). Windows suma lo propio de PC: «← Atrás», Esc, Alt+←, barras, cursor de mano y menú lateral fijo.
5. **En la mesa, rápido.** La tablet del DM prioriza acciones grandes de un toque y deja la edición para «Preparar».
6. **Tres orígenes, en este orden.** SRD → Homebrew → Custom. Son orígenes de datos separados.
   - El SRD trae solo el SRD (5.1 y 5.2.1, CC BY 4.0).
   - El homebrew es la biblioteca del DM.
   - Custom es lo que se escribe a mano.

## Paquetes y orden

| Paquete | Alcance | Puntos |
| --- | --- | --- |
| **R0 · Guía del rediseño** | Antes de construir: guía visual para el propietario con las pantallas nuevas y cómo se usan (D14). Dentro de la App: ayuda corta «¿Cómo se usa?» en cada pantalla. | A14 |
| **R1 · Entrada, cuenta y campañas** | Windows pide la cuenta al abrir y la recuerda; barra superior con cuenta y rol; «Cerrar sesión» y «Cambiar usuario»; lista estable de campañas con el rol de cada una (tablet y Windows); explicación cuando la cuenta no es DM; Sincronizar en el menú normal; jugador asignable a cada PJ aunque no tenga cuenta. | A1, A2, A3 (sincronizar), A13 |
| **R2 · Preparar (Windows)** | «Gestores» pasa a «Preparar», agrupado por lo que el DM prepara: Lugares y tiendas, PNJ, Aventuras y escenas, Dungeons, Encuentros y criaturas, Handouts y mapas, Reglas de la casa. Cada sección tiene una línea de propósito, búsqueda y crear arriba. | A3, P6 |
| **R3 · Dungeons** | Dungeon como objeto propio; editor con plano (maqueta D5): «＋ Zona», arrastrar para ubicar, arrastrar de zona a zona para crear pasajes, pasaje secreto punteado, panel de zona y plano de fondo opcional. La Mesa de Dungeon lee ese dungeon. En toda la App se dice «dungeon». | A4 |
| **R4 · Reglas: SRD y homebrew** | Lector del SRD para todos, con índice y búsqueda. Biblioteca homebrew por familia: especies (con semirraza), clases y subclases, trasfondos, dotes, conjuros, objetos, armas de leyenda y fe/dioses. Cada familia tiene su pantalla, aunque esté vacía, con «Aquí va…». | A8 |
| **R5 · PJ desde el SRD** | Catálogos estructurados del SRD (especies, clases y subclases, trasfondos, dotes, conjuros, equipo) en 5e y 5.5e. Creación guiada de PJ. En cada «Agregar» del editor (objetos, armas, ataques, conjuros, dotes, rasgos), un selector en orden SRD → Homebrew → Custom. La hoja V11 no cambia de diseño. | A11, A12 |
| **R6 · Mesa rápida** | Escritorio DM de la tablet para usar en la mesa: acciones grandes, nombres claros, notas en un lugar fijo, handouts que se envían a los jugadores y que el jugador ve en su App, explorador de archivos con carpetas favoritas y recientes, marcas de mapa que se arrastran. | A5, A6, A7, A9 |
| **R7 · Set de prueba y QA** | Se carga el set privado «Campus Celeste» (D10 a D13) en la campaña de prueba con Gmail como DM; candidato con identidad única; ventana de QA nueva que cubre A1 a A13. | — |

## Lo que no cambia

- La hoja de PJ V11 y su PDF (diseño contractual).
- Las reglas de autoridad: el DM de la campaña es distinto del dueño del PJ, y el dueño del PJ es distinto de quien lo controla.
- Las revisiones, los tombstones y la idempotencia.
- El costo USD $0.

## Contenido privado

El set «Campus Celeste», los manuales del propietario y los nombres de sus jugadores viven en `D:\DnD_Aid\Set de prueba Campus Celeste`, fuera del repositorio público. El repositorio solo guarda código, el SRD (CC BY 4.0) y pruebas con datos inventados.

## Estado (2026-11-13)

| Paquete | Estado | Evidencia |
| --- | --- | --- |
| R0 | Guía visual publicada para el propietario; falta la ayuda «¿Cómo se usa?» en cada pantalla (el editor de dungeons ya la tiene). | Guía: artefacto «Guía del rediseño DM». |
| R1 | **Windows hecho:** pide la cuenta al abrir y la recuerda con DPAPI; barra superior con campaña, rol, «⟳ Sincronizar» y menú «Cambiar usuario / Cerrar sesión»; lista de campañas con el rol; «Nueva campaña» creada en el servidor como DM; aviso cuando la cuenta es Jugador; «Jugadores de la campaña». **Tablet:** correo visible, «Cambiar usuario» y rol explicado en cada campaña. Falta A13 (jugador asignable a cada PJ). | `DesktopRememberedAccountTest` (5 pruebas). |
| R2 | Portada «Preparar» con 8 tarjetas y propósito en una línea; «Gestores» desaparece. | Compila; se cubre en el QA. |
| R3 | Dungeon como objeto propio (familia `DUNGEON`, migración 33), editor con plano en Windows («＋ Zona», arrastrar, pasajes con ●, secreto punteado, encuentro, ficha completa, plano de fondo). Los nombres antiguos se convierten en dungeons con el mismo id en todos los equipos. «Mazmorra» pasa a «dungeon» en toda la App. | `DungeonContentTest` (5), sincronización PC→tablet (`HostedDmContentSyncTest`) y `DesktopDungeonEditorTest`, con su imagen de prueba. |
| R4 | Modelo de homebrew de reglas (familia `RULES_ENTRY`, migración 34) con 14 tipos. Faltan el lector del SRD y las pantallas. | Compila. |
| R5 | Catálogo del SRD estructurado (`scripts/build_srd_catalog.py`): 5e con 316 conjuros, 37 armas, 13 armaduras, 9 especies y 4 subespecies, 12 clases y 12 subclases, 1 trasfondo y 1 dote; 5.5e con 336 conjuros, 38 armas, 13 armaduras, 9 especies, 12 clases y 12 subclases, 4 trasfondos y 17 dotes. Faltan el asistente para crear PJ y los selectores. | `--check` reproduce los archivos. |
| R6, R7 | Pendientes. | — |

En total pasan 803 pruebas (compartidas, Windows y tablet) y la APK compila.
