# Base para DM · Mesa de Dungeon (paquete 7)

La Mesa de Dungeon está en la tablet y en Windows: es `dmUi/DungeonDesk.kt`, las mismas fuentes compiladas en las dos Apps. Sigue el diseño aprobado (`DM_UX_APROBADA.md` §5; P16 a P18 y P20 a P23) y las notas de la regla beta de turnos de mazmorra (`docs/discovery/2026-09-10_DUNGEON_TURNS_BETA_SOURCE_NOTES.md`).

## Modelo (`shared/dm/Dungeon.kt`)

- **Actividades:** `DungeonRules.betaActivities` reúne las 15 actividades de la regla beta, más «Exploración normal *» y «Ocultarse *» (P17).
  - Una regla casera de la campaña puede traer su propia lista (`ContentLinks.activities`), que se edita en la App de PC; si existe, la mesa usa esa.
  - «Ayudar» toma el costo de la actividad ayudada.
  - «Buscar» pide un foco y, si es a fondo, no deja avanzar.
- **Avance del grupo:** lo marca la actividad más lenta y se muestra solo como dato. La App no calcula zonas ni mueve a nadie.
- **Exploración en curso:** `DungeonDocument` (tabla local `dm_dungeon`, igual que un combate) guarda:
  - el turno y las marcas de cada zona (Explorada, Saqueada, Despejada, Peligro activo, Bloqueada, Secreto hallado);
  - los grupos (separar y juntar) y los marcadores de criaturas, que se ubican a mano;
  - la actividad de cada PJ y la del turno anterior, que se ve solo de referencia;
  - el registro y los relojes.
- **«Cerrar turno»:**
  - escribe el registro, pasa las actividades a «anterior» y suma +1 a los relojes por turno;
  - si un reloj se llenó, avisa una vez y no aplica consecuencias;
  - si hubo «Emboscar», lo informa para preguntar por la ronda sorpresa (P22).
- **«Interrumpido por combate»:** registra quién alcanzó a completar su actividad.
- **Diagrama:** sale de los vínculos de cada zona: el nombre de la mazmorra, la posición (0 a 1) y los pasajes, incluidos los secretos (P23). Las zonas sin posición se ordenan en filas.

## Combate desde la mazmorra (P22)

- **Ronda sorpresa:** `CombatDocument.surpriseActorIds` y la ronda 0. En ella actúan solo los emboscadores; después, la ronda 1 con todos. Siempre se puede empezar sin sorpresa.
- **Dónde se ve:** la ventana de iniciativa muestra quiénes actúan y el DM puede cambiarlo.
- **Qué lleva el combate:** cuando lo abre la emboscada, una interrupción o «Preparar ▸ ⚔» en la ficha de una zona, el combate trae los PJ y los encuentros vinculados a esa zona.

## Notas y relojes (P20, P21)

- **Notas:** son un tipo nuevo de contenido del DM, `NOTE`.
  - Se guardan al escribir, con su contexto (zona, lugar, escena, PNJ, mazmorra o campaña), y se sincronizan con la App de PC.
  - El panel 📝 de la barra pega la nota a lo que esté abierto y permite cambiarla de un toque o mandarla a la campaña; además reúne todas las notas con su contexto.
- **Relojes:** los de la mazmorra viven en su exploración. El panel ⏱ suma relojes de un lugar o de la campaña, guardados en el aparato.

## App de PC

- **Zonas:** la sección «Vínculos» de cada zona tiene:
  - el nombre de la mazmorra;
  - la ubicación en el diagrama, con un clic sobre el plano, donde se ven las demás zonas;
  - los pasajes, marcables como secretos;
  - el encuentro de la zona.
- **Reglas caseras:** tienen el editor de «Actividades del turno de mazmorra», que empieza desde la lista beta.

## Pruebas

- **Compartidas:** `DungeonTest` (actividades, avance, registro, relojes, emboscada, grupos, diagrama y lista propia), la ronda sorpresa en `CombatTest` y una nota sincronizada en `HostedDmContentSyncTest`.
- **Windows:** `DesktopDmWorkspaceTest` dibuja la mesa en `desktopApp/build/dm-desk-proofs/` (w9 a w12).
