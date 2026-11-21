# QA del selector de encuentros: 0.7.0-preqa.5

Esta candidata responde a la observación **E1** del propietario sobre 0.7.0-preqa.4, registrada textualmente en [QA_REPARACIONES_DM_0.7.0-preqa.4_OBSERVACIONES.md](QA_REPARACIONES_DM_0.7.0-preqa.4_OBSERVACIONES.md):

> «La seleccion de encuentros preparados no puede ser una ventana poptup, debe haber una ventana intermedia selector, para poder improvisar y modificar una, o crear un encuentro nuevo»

Todo lo de 0.7.0-preqa.4 sigue adentro; esta pasada agrega la pantalla «Elegir encuentro» y lo que apareció al recorrerla en la tablet real.

## Candidata

| Pieza | Identidad | SHA-256 |
| --- | --- | --- |
| APK | `dnd-custom-aid-0.7.0-preqa.5-build-70500.apk` (versionName `0.7.0-preqa.5`, versionCode 70500) | `253ff9fe47edfe4d0f50d3bb5b04c4ccfca5cc0fb8fd0c9ffc5861bfcaec1197` |
| Windows | `dnd-custom-aid-windows-0.7.4-preqa.5.zip` (packageVersion 0.7.4) | `87959149d4ccd46f31534880c2dbec1ae492a1ac2cebb509cffb9a52b0d04940` |
| Fuente | rama `feature/dm-ux-redesign` | — |
| Worker | `dnd-custom-aid-api`, sin cambios | — |

- Carpeta local del propietario: `D:\DnD_Aid\APKs\QA 0.7.0-preqa.5 Elegir encuentro`.
- La evidencia está en `verificacion\tablet` de esa carpeta. Muestra el set privado, así que no entra al repositorio.

## Qué cambió

### 1. «Elegir encuentro» es una pantalla, no una ventana emergente

- Se abre sola al crear un combate nuevo que todavía no tiene enemigos.
- Se abre también desde «Agregar → Elegir o armar encuentro», tanto en el paso 1 (Preparar) como en el combate en vivo.
- A la izquierda están los encuentros de la campaña, con buscador, «＋ Encuentro nuevo» y «Sin encuentro: improvisar ▸» (o «← Volver al combate» si el combate ya empezó).
- A la derecha está el encuentro elegido, y se puede cambiar antes de usarlo:
  - subir y bajar cantidades;
  - mandar una criatura a la Reserva o traerla de vuelta;
  - quitar criaturas;
  - agregar del bestiario de la campaña o del SRD;
  - agregar una criatura inventada con su nombre, CA, PV y cantidad.
- Tres salidas, separadas a propósito:
  - **«Usar en este combate ▸»** mete lo que se ve en el combate y **no toca el encuentro guardado**;
  - **«Guardar cambios en el encuentro»** guarda los cambios en el encuentro de la campaña;
  - **«Guardar como encuentro nuevo»** / **«Guardar encuentro»** crea uno nuevo (conserva la guía del que sirvió de base).
- Una criatura inventada con CA o PV queda también en el bestiario de la campaña cuando se guarda el encuentro, para que el encuentro conserve esos números.
- En pantalla ancha se ven las dos partes lado a lado; en pantalla angosta se ve una u otra, con «← Encuentros» para volver.

### 2. Lo que apareció al recorrer la tablet real (AGENTS §6.4)

- **La casilla «Cantidad» venía con «1» y lo escrito se pegaba detrás:** al escribir 3 quedaba ×13. Ahora tocarla la deja vacía, y si se deja vacía vuelve a valer 1. Afecta también a «Improvisado» y al bestiario del combate.
- **En la tablet los nombres se dibujaban una letra por línea.** El escritorio de combate mostraba tres columnas, demasiado angostas en una tablet. Ahora, bajo 820 dp, son dos columnas: la lista a la izquierda y, a la derecha, «Agregar» con lo que se está ajustando debajo. Lo mismo en el paso 1 y en el combate en vivo.
- **El título del paso se partía igual** junto al mensaje del encabezado; ahora cada uno respeta su espacio.

### 3. Dato de la tablet, no es un defecto de esta candidata

Los encuentros que ya estaban en la tablet aparecen «Sin criaturas»: se importaron con una versión anterior al 14-11-2026, que todavía no guardaba los participantes del encuentro. La pantalla lo dice y deja agregarlas. Al volver a cargar el set, o al sincronizar desde Windows, llegan con sus criaturas.

## Cómo se verificó

**Tablet (emulador Lenovo Tab P11, `-read-only -no-snapshot-save`)** — recorrido completo, con captura por paso:

| # | Prueba | Resultado |
| --- | --- | --- |
| T1 | Un combate nuevo abre «Elegir encuentro» | VISTO FUNCIONANDO |
| T2 | Elegir un encuentro muestra sus criaturas y su guía | VISTO FUNCIONANDO |
| T3 | Criatura inventada: nombre, CA, PV y cantidad en su casilla | VISTO FUNCIONANDO (tras la reparación) |
| T5 | «Guardar encuentro» lo deja en la campaña | VISTO FUNCIONANDO |
| T6 | «Usar en este combate» lo mete en el combate con su guía | VISTO FUNCIONANDO |
| T7 | El paso 1 se lee en dos columnas | VISTO FUNCIONANDO |
| T8 | El combate en vivo se lee en dos columnas | VISTO FUNCIONANDO |
| T9 | «Agregar → Elegir o armar encuentro» en el combate en vivo | VISTO FUNCIONANDO |
| T10 | «Guardar cambios en el encuentro» | VISTO FUNCIONANDO |
| T11-T12 | Agregar del bestiario de la campaña y del SRD | VISTO FUNCIONANDO |

**Windows** — el recorrido quedó a medias a pedido del propietario, que estaba usando el equipo. Lo visto antes de parar: el combate nuevo abre la pantalla, elegir un encuentro, subir una cantidad y mandar una criatura a la Reserva funcionan. **Falta recorrer en Windows:** criatura inventada, «Usar en este combate», los dos guardados y «Sin encuentro: improvisar».

**Pruebas automáticas** (`:shared:desktopTest`, `:desktopApp:test`, guardas de CI): todas pasan. Las nuevas cuidan el borrador del encuentro, la pantalla entera de punta a punta, que cada casilla guarde lo suyo, la cantidad que se escribe y que en pantalla angosta el nombre siga legible.
