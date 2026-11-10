# Resolución de conflictos de sincronización de PJ — diseño y decisiones

**Origen:** pedido del propietario V11-QA-004, 2026-11-10. Textual: «agregar a la App, resolución de conflictos y poder importar desde servidor viendo diferencias y todo lo involucrado». **Estado:** diseño acordado (D1–D4, 2026-11-10); implementación en `feature/pc-sync-conflict-resolution`.

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
| D3 | Si un PJ se borró en el servidor pero aquí tiene cambios sin subir, ¿qué ofrece la App? | «Las dos opciones» | Se ofrecen A «Conservarlo como PJ nuevo» (nueva identidad, se sube; el borrado no se deshace, por la regla de no resurrección) y B «Aceptar el borrado» (sale del aparato; los cambios quedan en Copias anteriores). Decide quien está frente al aparato. |
| D4 | ¿Agregamos «Comparar con el servidor» en Ajustes del PJ, aunque no haya conflicto? | «Solo con conflicto» | La pantalla de diferencias y «traer lo del servidor» existen solo cuando hay un conflicto. No se agrega una entrada permanente en Ajustes del PJ. |

## Diseño final acordado

- **Cuándo aparece:** solo con un conflicto de sincronización. La lista de Personajes y Campañas marcan el PJ con «Revisar diferencias».
- **Versiones distintas** (`LOCAL_AND_HOSTED_CHANGED`): la pantalla de diferencias muestra solo los datos que difieren, coloreados según la línea base. Las salidas son:
  - **«Juntar los cambios»:** azules del aparato, verdes del servidor. Si hay rojos, se elige un lado para todos (D1).
  - **«Usar todo lo de este aparato».**
  - **«Usar todo lo del servidor».**
- **Sin línea base utilizable** (`SYNC_BASELINE_MISSING` o `SYNC_BASELINE_REVISION_MISMATCH` con contenido distinto): no se puede saber quién cambió cada dato. Se muestran las diferencias sin colores de origen y solo se elige un lado entero.
- **Borrado en el servidor con cambios aquí:** se ofrecen las dos opciones de D3.
- **Copia de respaldo** de lo descartado, en Ajustes del PJ → Copias anteriores (D2).
- **Seguridad:** antes de aplicar se vuelve a pedir el servidor. Toda subida usa la revisión esperada, y el servidor sigue decidiendo quién puede escribir el PJ: si no lo permite, la App lo explica y no sube nada.

## Plan técnico

1. **Shared:**
   - diferencia por campo entre local, servidor y línea base, sobre el documento normalizado del PJ (`normalizePcSyncSnapshot`);
   - combinación para «Juntar»;
   - repositorio de copias de respaldo (tabla SQLDelight nueva con migración);
   - servicio de resolución con las tres salidas, las dos opciones de borrado y la subida con revisión esperada;
   - pruebas en `desktopTest`.
2. **Android:**
   - aviso en Personajes y Campañas;
   - pantalla de diferencias;
   - Copias anteriores en Ajustes del PJ;
   - reemplazo del botón DEV «conservar local» por la pantalla nueva.
3. **QA:** caso real con el Aldren del propietario (Cobre 12) y guía de QA con un APK nuevo.
