# Resolución de conflictos de sincronización de PJ — diseño y decisiones

**Origen:** pedido del propietario V11-QA-004, 2026-11-10. Textual: «agregar a la App, resolución de conflictos y poder importar desde servidor viendo diferencias y todo lo involucrado». **Estado:** diseño acordado de a una pregunta; todavía sin código.

## Punto de partida (código actual)

- `HostedPcSnapshotPullService` detecta el conflicto `LOCAL_AND_HOSTED_CHANGED` comparando la copia local con la **línea base**: la última versión que el aparato y el servidor tenían en común (`HostedPcSyncBaselineRepository`). También detecta `LOCAL_REVISION_AHEAD`, `LOCAL_TOMBSTONE`, `SYNC_BASELINE_MISSING`, `SYNC_BASELINE_REVISION_MISMATCH` y `HOSTED_CHANGED_WITHOUT_REVISION`.
- Hoy solo existe «conservar local» (`AndroidHostedPcConflictResolver.keepLocal`), y solo desde la pantalla DEV de los builds de depuración. Antes de actuar vuelve a pedir el servidor y sube con revisión esperada, para que el servidor rechace la subida si cambió entre medio.
- No se pueden ver las diferencias ni traer la versión del servidor.

## Diseño mostrado al propietario (2026-11-10)

1. **Aviso visible:** la lista de Personajes y Campañas marcan el PJ en conflicto con «Revisar diferencias». Ya no depende de la pantalla DEV.
2. **Pantalla de diferencias:** solo los datos que difieren, en dos columnas, «Este aparato» y «Servidor». Con la línea base cada dato se clasifica en tres grupos: cambió solo aquí (azul), solo en el servidor (verde) o en los dos (rojo).
3. **Salidas:** «Juntar los cambios», «Usar todo lo de este aparato» y «Usar todo lo del servidor» (importar desde el servidor).
4. **Seguridad:** antes de aplicar se vuelve a pedir el servidor. Si cambió, se avisa y no se pisa nada; las subidas siguen protegidas por la revisión esperada.

## Decisiones del propietario

| # | Pregunta | Respuesta literal | Regla |
|---|---|---|---|
| D1 | Cuando un dato cambió en los dos lados (rojo), ¿cómo se decide? | «Elijo un lado entero» | «Juntar los cambios» toma los azules del aparato y los verdes del servidor. Si hay datos en rojo, pide elegir **aparato o servidor para todos los rojos juntos**; no se elige dato por dato. |
| D2 | ¿Guardamos una copia de la versión que se descarta? | «Sí, guardar copia» | Al resolver, la versión descartada (entera o sus datos en rojo) se guarda como copia de respaldo local, visible en Ajustes del PJ → Copias anteriores, con Ver, Restaurar y Exportar JSON. Restaurar crea una versión nueva que se sincroniza como un cambio más; no borra nada. |
