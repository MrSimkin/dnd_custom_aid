# Observaciones del propietario sobre 0.7.0-preqa.4

Registro textual (AGENTS §6.3). Cada observación queda abierta hasta que haya evidencia en la candidata que la repare.

## E1 · Selector de encuentros (2026-11-18, en el chat)

> «La seleccion de encuentros preparados no puede ser una ventana poptup, debe haber una ventana intermedia selector, para poder improvisar y modificar una, o crear un encuentro nuevo»

**Qué está mal, en términos concretos**

- En el combate, «Agregar → Encuentro preparado» abre una ventana emergente que solo lista los encuentros.
- Tocar uno lo mete entero en el combate, sin verlo antes y sin poder cambiarlo.
- «Nuevo combate» entra directo al combate con los PJ. No hay un paso para elegir, improvisar ni crear el encuentro.

**Qué debe verse para darla por reparada**

1. Una pantalla intermedia «Elegir encuentro» dentro de la mesa de combate, en lugar de una ventana emergente.
   - Se abre al crear un combate nuevo y desde «Agregar → Encuentro preparado».
2. En esa pantalla:
   - elegir un encuentro de la campaña y ver sus criaturas, cantidades y guía;
   - modificarlo antes de usarlo: cantidad, quitar, agregar del bestiario, del SRD o improvisado;
   - usarlo así en el combate sin cambiar el encuentro guardado;
   - guardar los cambios en el encuentro;
   - guardarlo como encuentro nuevo;
   - crear un encuentro nuevo desde cero;
   - seguir sin encuentro (improvisar).
3. Lo mismo en la tablet y en Windows: la mesa de combate es compartida (`dmUi`).

**Superficie responsable:** `dmUi` (`CombatDesk`, `CombatPrepareStep`, `CombatCreatureEditing`). El borrador vive en `shared` (`dm`), con pruebas.

**Estado:** REPARADA en 0.7.0-preqa.5 (APK build 70500), a falta del recorrido en Windows.

| Criterio | Estado | Evidencia |
| --- | --- | --- |
| Pantalla intermedia en vez de ventana emergente | REPARADO | `verificacion\tablet\E1_T1_combate_nuevo_abre_elegir.png`; prueba `DesktopEncounterSelectorTest` |
| Se abre al crear un combate nuevo | REPARADO | `E1_T1_...png` |
| Se abre desde «Agregar» en Preparar y en vivo | REPARADO | `E1_T9_desde_combate_vivo.png` |
| Ver criaturas, cantidades y guía | REPARADO | `E1_T2_encuentro_elegido.png` |
| Modificarlo: cantidad, quitar, reserva, bestiario, SRD, improvisado | REPARADO | `E1_T3_cantidad_correcta.png`, `E1_T11_bestiario.png`, `E1_T12_srd_agregado.png` |
| Usarlo sin cambiar el encuentro guardado | REPARADO | `E1_T6_usado_en_combate.png`; prueba `EncounterDraftTest` |
| Guardar los cambios en el encuentro | REPARADO | `E1_T10_cambios_guardados.png` |
| Guardarlo como encuentro nuevo / crear uno desde cero | REPARADO | `E1_T5_guardado.png` |
| Seguir sin encuentro (improvisar) | REPARADO en código y prueba; falta verlo en la App real | `DesktopDmWorkspaceTest` |
| Igual en la tablet y en Windows | REPARADO en la tablet; en Windows falta terminar el recorrido | `E1_T7_preparar_dos_columnas.png` |

**Hallazgos nuevos al recorrer la tablet** (reparados en la misma candidata, ver [QA_REPARACIONES_DM_0.7.0-preqa.5.md](QA_REPARACIONES_DM_0.7.0-preqa.5.md)):

- la casilla «Cantidad» venía con «1» y lo escrito se pegaba detrás (escribir 3 daba ×13);
- en la tablet las tres columnas del escritorio de combate dejaban los nombres en una letra por línea;
- el título del paso se partía igual junto al mensaje del encabezado.

**Pendiente antes de cerrar la observación:** terminar el recorrido en Windows (criatura inventada, «Usar en este combate», los dos guardados y «Sin encuentro: improvisar»). El propietario pidió el 18-11-2026 no usar su pantalla mientras trabajaba.
