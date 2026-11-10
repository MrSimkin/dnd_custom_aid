# Checkpoint canónico de main — «SIGUE» Hoja de PJ V11

**Fecha:** 2026-10-09 (Chile).  
**Alcance exclusivo de este checkpoint:** navegación/reanudación del trabajo **sin fusionar ni modificar el renderer ni el APK**.  
**Implementación activa:** rama `docs/pc-sheet-v11-approved-implementation-handoff`, PR [#173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) **DRAFT / SIN FUSIONAR**.  
**Estado:** V11 DISEÑO VISUAL APROBADO; G0 PARCIAL, G1–G6 NO EJECUTADOS. Proyecto local/personal sin servicios nuevos. El diseño V11 tiene dos detalles técnicos conocidos pendientes (rótulo «Salvación» levemente arriba/derecha; relleno de huecos C2 mediante Notas manuscritas).

## Instrucción «sigue» (sin pérdida de contexto)

Con este repositorio identificado, recibir **«sigue»** o **«continúa»** **autoriza retomar automáticamente el próximo trabajo técnico seguro** de la implementación V11. No significa volver a discutir diseño, ni autoriza fusionar PR #173, hacer despliegues o saltarse QA.

1. Verificar `main` actual, leer `AGENTS.md → RESUME.md → docs/checkpoints/LATEST.md → este checkpoint`.
2. Usar **SOLO** la rama `docs/pc-sheet-v11-approved-implementation-handoff` / PR #173 como autoridad de trabajo V11. **Resolver su HEAD remoto vigente en ese momento**, sin fijar un SHA histórico ni crear una segunda rama de implementación. Si la PR ya fue integrada en el futuro, seguir el nuevo puntero de main, nunca revivir ramas antiguas.
3. En esa rama, leer **solo** lo necesario: `docs/pc-sheet-v11/COMANDO_SIGUE_Y_G1_HANDOFF.md` → `CONTRATO_VISUAL_APROBADO.md` → `PREFLIGHT_TECNICO_Y_RIESGOS.md` → `G0_MAPA_SEMANTICO_PRELIMINAR.md` → `PROCEDIMIENTO_QA_SIN_PERDIDA.md` / `qa/REGISTRO_QA_V11.json` → `PROTOCOLO_ROMPER_CICLO_Y_REPLANIFICAR.md` → plan G0–G6. **No reconstruir historia** ni sustituir textos originales del propietario.
4. Comprobar evidencia de **último gate PASS** y de los riesgos OPEN. **G0 no está aprobado por escribir este documento**. Quedan trabajo de modelo/UI real, inspección original de QA Mara y prueba técnica real; el ZIP está custodiado en la rama (Actions run **38009729554**, SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`).
5. Ejecutar directamente el **siguiente paso seguro**, no preguntar si se desea continuar. Registrar cambios en commits sobre PR #173 y actualizar su checkpoint/registro QA. Si una dependencia bloquea un paso, resolver otros subpasos seguros, documentar `BLOCKED` preciso y la condición de desbloqueo; **no gastar intentos ficticios ni prometer trabajo en segundo plano**.
6. Antes de editar Kotlin, cumplir gate de comprensión QA `AGENTS.md §6.3`; antes de afirmar corregido, demostrar **diff productivo + candidato APK/desktop identificado + PDF real de ese candidato + cotejo de todas las observaciones de QA**. Aplicar RCR-1 si se repite el mismo fallo; nunca recrear V12/V13 por cansancio.

## Resultado esperado de la siguiente sesión

**Primero terminar G0**: trazar cierre de datos reales y UI de exportación, reabrir QA Mara original con la evidencia material existente, conservar las 32 observaciones históricas y las dos V11, verificar los CI y decidir G0 PASS/BLOCKED con evidencia. **Si G0 PASS, arrancar G1 automáticamente** en la misma PR: snapshot/mapeo Kotlin compartido de V11 A/B y pruebas de compatibilidad para las cuatro familias anteriores. G2 es compositor/paginación; G3 integración Android+Desktop; G4 regresiones reales; G5 APK/PDF candidatos; G6 revisión técnica sin merge automático.

## Límites y compromisos

- **Una sola PR para implementar V11:** #173; esta actualización documental de ruta en `main` es separada y **no constituye merge de V11**.
- Git es memoria. No reiniciar observaciones, contadores, QA, decisiones de diseño o gate al cambiar de chat.
- Uso **100% personal**, logo original D&D, Carta, Barlow Condensed, Symbols-v8, variantes A/B, última hoja Notas; decisiones cerradas.
- `main` conserva sus cuatro familias actuales; la rama #173 tiene documentos y QA pero **no contiene implementación Kotlin V11** a fecha de este checkpoint.
- El CI general del último HEAD anterior a este checkpoint pasó (Scaffold checks, golden y QA). **No equivale a PDF/App V11 probado**.

## Comando corto para un chat nuevo

`Sigue el proyecto MrSimkin/dnd_custom_aid según main/AGENTS.md → RESUME.md → LATEST.md. Ejecuta el siguiente paso seguro de V11 en PR #173, conserva QA/observaciones y RCR-1, actualiza GitHub. No vuelvas al diseño ni fusiones la PR de implementación.`

La palabra «sigue» **sola en un chat sin contexto** puede no identificar qué proyecto se pretende; el usuario solo necesita nombrar el repositorio una vez. Este checkpoint permite que cualquier agente, después de identificarlo, continúe sin memorias de ChatGPT.
