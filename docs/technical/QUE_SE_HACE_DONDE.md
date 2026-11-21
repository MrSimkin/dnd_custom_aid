# Qué se hace en cada aparato

Punto 5 de la revisión crítica («lo de un aparato no siempre está en el otro»). Esta tabla es la regla, sin excepciones: si algo aparece en un aparato donde la tabla dice que no va, es un defecto; si falta donde la tabla dice que sí va, también.

La App la muestra tal cual en **Configuración → Qué se hace en cada aparato** (Windows y tablet), para que no haya dos versiones.

| Tarea | Windows | Tablet del DM | Teléfono del jugador |
| --- | --- | --- | --- |
| Preparar lugares, PNJ, escenas, dungeons, encuentros y criaturas | **Sí, aquí** | No | No |
| Papelera de lo preparado | **Sí, aquí** | No | No |
| Cargar un paquete de contenido | **Sí, todo** | Solo los PJ del paquete | No |
| Ver qué cambiaría el paquete antes de cargarlo | **Sí, aquí** | No | No |
| Copia de seguridad de la campaña | **Sí, aquí** | No | No |
| Mesas en vivo: combate, dungeon y escena | Sí | Sí | No |
| Pantalla del DM (grupo y referencia rápida) | Sí | Sí | No |
| Crear un personaje paso a paso | Sí | Sí | Sí |
| Editar la hoja a fondo | No | Sí | Sí |
| Ver la hoja y guardarla en PDF | Sí | Sí | Sí |
| Handouts y mapas: preparar y enviar | **Sí, aquí** | Enviar lo ya preparado | Recibir |
| Reglas del SRD y tu homebrew | Sí | Sí | Sí |
| Notas de la mesa | Ver todas | Escribir en la mesa | No |
| Jugadores e invitaciones | **Sí, aquí** | No | Unirse con el código |
| Sincronizar | Sí | Sí | Sí |

## Por qué está repartido así

- **Preparar es de Windows** (decisión D5): son formularios largos, con teclado y pantalla grande. La tablet es para la mesa.
- **La mesa es de los dos**: el DM juega con la tablet en la mano o con el PC al lado, y el escritorio de combate es el mismo código (`dmUi`) en ambos.
- **La hoja de PJ se edita donde está el jugador**: en su teléfono o en la tablet. Windows la muestra y la imprime, pero no la edita, porque el PJ es del jugador, no del DM.
- **Cargar contenido completo es de Windows** porque es donde está el material del DM; a la tablet solo llegan los PJ de un paquete, y el resto llega al sincronizar.

## Cómo se mantiene

Cuando una tarea nueva llegue a un aparato, se agrega aquí y, si corresponde a los dos, se construye en `dmUi` para que no se desvíen. El asistente «Nuevo personaje» (18-11-2026) es el ejemplo: vivía solo en la tablet y se movió a `dmUi` para cumplir esta tabla.
