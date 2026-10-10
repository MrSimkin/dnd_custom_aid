# ENCARGO AL WORKER — IMPLEMENTAR HOJA DE PJ V11 SIN BUCLES

**Fuentes requeridas para la ejecución:** (1) ZIP V11 con SHA-256 documentado en `ARTEFACTOS_Y_PROCEDENCIA.md`; (2) `PLAN_TECNICO_IMPLEMENTACION_V11.md` en este mismo directorio. El ZIP **YA ESTÁ VERSIONADO Y VERIFICADO** dentro de `docs/pc-sheet-v11/` en la rama (Actions run #38009729554). Usar esa fuente, y comprobar su SHA antes de ejecutar; no volver a pedirlo al propietario. Tu autoridad operativa es el repositorio actual `MrSimkin/dnd_custom_aid` más el **diseño V11 aprobado expresamente por el propietario** y las dos correcciones pendientes. No presupongas que `main` conserva el HEAD aquí anotado. **No fusionar ni publicar en producción.**

## Mandato

Implementa la hoja de personaje V11 como **nueva opción de exportación opt-in**, tamaño Carta, variantes A y B seleccionables, integrada con el modelo real del personaje y las acciones de exportación disponibles en Android y Desktop. **No reemplaces ni remodeles las cuatro familias existentes.** Reutiliza la arquitectura de snapshot/render-plan/renderers productivos y comparte el algoritmo de paginación; no copies el generador ReportLab del ZIP como fuente del producto.

La revisión visual integral V11 **ESTÁ APROBADA**; evita rondas estéticas. Ajustes exactos pendientes: 1) `Salvación` un poco arriba y a la derecha; 2) C2 sin huecos injustificados, rellenar espacio aprovechable con cartela `Notas` y cuadrícula completa. Estos bloques de relleno son manuscritos y **no equivalen a Notas digitales C-N3**.

## Preflight bloqueante — antes del primer cambio de código

1. Confirma remoto `main`, identidad, estado de CI y ruta `AGENTS.md → RESUME.md → docs/checkpoints/LATEST.md → checkpoint activo → D-0076/brief`, leyendo exactamente los artefactos visuales V11 adjuntos y el informe del ZIP.
2. Reabre la evidencia histórica **real** de defectos PDF y QA de Mara según `AGENTS.md §6.3` (checkpoint 2026-09-26, QA runtime FAIL 2026-09-28, matriz 50800 y auditoría 2026-09-30). **No bases un diagnóstico solo en resúmenes o tests verdes.**
3. Identifica los entry points reales Android/Desktop, render plan y fuentes de datos; localiza Barlow Condensed auténtica, Fira Sans, `assets/fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf` y su `GUIDE.md` y el logo recuperable de la hoja del propietario. No inventes una compatibilidad PDFBox/Android no verificada.
4. Crea una **única matriz de aceptación y trazabilidad**: cada requisito visual aprobado, cada riesgo técnico real, fuente de dato/estado, lugar del código, prueba concreta y evidencia necesaria. Registra dónde el brief viejo fue superado por la aprobación V11, sin revocar silenciosamente decisiones previas.
5. STOP solo si una dependencia real falta, la topología cambió materialmente, hay ambigüedad semántica de producto que alteraría los datos exportados, o el código no es implementable de forma segura. No preguntes detalles estéticos cerrados ni trabajo administrativo al propietario.

## QA con trazabilidad obligatoria (desde el primer ciclo)

Aplicar `PROCEDIMIENTO_QA_SIN_PERDIDA.md` en cada vuelta: registrar literalmente cada nota del propietario con ID durable en `qa/REGISTRO_QA_V11.json`; diagnosticar y publicar una matriz completa de reparación ANTES de editar; realizar diff **productivo** y pruebas rojas→verdes; generar **nuevo** APK/PDF de la App y cotejar exactamente cada observación; conservar las notas antiguas y los resultados sin reescribirlos. **No declarar FIXED por plan, cambios de docs, tests o PDFs sintéticos**. Ejecutar `python3 scripts/check_v11_qa_registry.py` y su CI; `OWNER_ACCEPTED` requiere aprobación explícita del propietario para el binario exacto. La evidencia vieja caduca al cambiar materialmente el candidato. El presupuesto de correcciones y recuperación RCR-1 sigue aplicable.

## Reglas de implementación

- Contrato inmutable de exportación desde el modelo real: atributos normales/custom y habilidades, ataques, municiones, inventario/monedas custom incl. valor 0, Equipo Especial y ACTIVO real, Rasgos, Trasfondo/Historia, conjuros por niveles/fuentes/preparación/gasto, notas y Libro opcional, retrato, Permanente vs Instantánea Actual.
- Layout Kotlin determinista: medir con fuentes efectivas → asignar módulos → fragmentar contenido largo → paginar → numerar físicamente → renderizar. Una fuente de verdad de saltos y asociaciones para Android/Desktop. Nunca cortar silenciosamente, inventar slots o datos, ni reducir fuentes hasta ilegibilidad.
- 3 páginas base siempre; P4 de Conjuros solo con registros reales; C2 adaptativo, dos columnas/mixto sin vertical, Equipo Especial todo ancho; C-N3 solo con notas digitales y exclusivas; Libro opcional dos columnas con fichas largas fragmentables; **Notas manuscritas completamente finales**.
- Logo original y fuentes **auténticas** incrustadas; Symbols v8 conforme a su mapa real de codepoints; no rutas absolutas del sistema, no sustituir por placeholders.
- Mantener el proyecto 100 % personal y local-first; **sin servicios, costes, backend, migraciones o cambios de seguridad**.
- Implementar captura UI de variante A/B en el momento de exportar; si existen opciones de fuente de retrato/encuadre o Libro, conservarlas. Sin reescribir el sistema de UI entero.

## Pruebas/Gates obligatorios

Sigue las fases **G0–G6** y criterios del PLAN adjunto. Como mínimo:

- Fixtures V11: casi vacío, 6+2 A/B, 6+3 A/B, extremos con 386 IDs, C2 mixto, todas las páginas y niveles 0–9, cero registros omitidos/duplicados, ninguna cuadrícula cortada, renglones con casillas, abreviaturas solo según contexto y última página siempre intacta.
- Casos nuevos de frontera: conjuro que excede columna, Libro con descripción más larga que columna, Historia/Notas largas, estados ausentes vs false/0, no lanzador, retrato ausente y dos opciones, cadenas largas y español/acentos/ñ.
- **Regresión con Mara real y las cuatro familias anteriores**, además de V11; las pruebas sintéticas son necesarias pero nunca suficientes. Si Mara no está disponible de forma reproducible, entregar el bloqueo y la evidencia concreta sin simularla ni declarar PASS.
- Analizar PDFs del candidato real (visual/raster, texto/IDs, geometría, fonts, páginas, referencias); contrastar los PDFs con artefacto APK/compilación y commit exactos. Tests de Desktop/Android proporcionales a ambas rutas.
- Antes de entrega al propietario: `versionName/versionCode` nuevos, SHA-256 del APK/artefactos, commit, CI/run, matriz FIXED/OPEN/CHANGED frente a la salida exacta del candidato. **CI verde por sí solo NO cierra ninguna observación.**

## Límites duros contra loops

**Una rama / una PR / un paquete coherente.** Primera implementación + **máximo DOS ciclos correctivos normales por gate**; cada corrección requiere causa y prueba roja→verde. Tras el tercer fallo, aplica obligatoriamente `PROTOCOLO_ROMPER_CICLO_Y_REPLANIFICAR.md` (RCR-1): pausa, evidencia y, **solo con alternativa técnicamente distinta y checkpoint PASS**, retroceso seguro de máximo dos gates, una implementación alternativa y una corrección. Máximo una recuperación por gate/causa y dos globales; luego STOP sin más pruebas especulativas. Bloqueos de evidencia/capacidad = BLOCKED, no quemar intentos. Mantener contadores, invalidaciones y SHAs en Git, sin reset --hard ni force push. No pedir reaprobación visual V11. No refactors oportunistas ni arreglos históricos ajenos; no fusionar ni desplegar.

## Entrega final obligatoria

(1) PR/branch/HEAD limpios y sin fusionar; (2) mapa de arquitectura y archivos afectados; (3) matriz de requisito→código→test→página y estado; (4) PDFs reales, manifiesto de fuentes incrustadas, hashes y tabla de IDs/páginas; (5) logs CI + smoke de las cinco familias visuales (cuatro históricas más V11); (6) identidad exacta del candidato Android y equivalente Desktop, o bloqueo claramente indicado; (7) informe de riesgos residuales; (8) dictamen propio `READY FOR INDEPENDENT REVIEW` o `STOP`, nunca `APPROVED BY OWNER`. Actualiza el checkpoint vigente y `LATEST.md` coherentemente. **Termina en una sola entrega consolidada, sin preguntarme decisiones que ya tomé.**