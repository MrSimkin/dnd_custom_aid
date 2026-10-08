# Checkpoint — rediseño del contrato PDF de hojas de PJ

**Fecha:** 2026-10-08 (Chile)  
**Estado:** DISEÑO ACTIVO / DECISIONES PARCIALES / SIN IMPLEMENTACIÓN  
**Rama de implementación activa:** ninguna.

## Cambio de ruta autorizado por el propietario

El propietario confirmó que el sistema de exportación PDF anterior no obtuvo un resultado satisfactorio y reabrió el contrato y la arquitectura **en fase de discusión**. Se autoriza únicamente consolidación documental de las decisiones aprobadas. No se autoriza nuevo renderer, modificación de templates, generación de PDF ni inicio de la antigua Phase 3 de reparación como sustituto de este rediseño.

**Registro canónico de las nuevas decisiones:** `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md`.

## Estado previo que se preserva

La auditoría Phase 2 de Mara permanece **COMPLETE/PUBLISHED**, con su propio detalle bajo `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`. D-0074, los baselines y defect/acceptance ledgers anteriores permanecen disponibles y no se reescriben. La Phase 3 anterior estaba planificada, no iniciada; **no se debe inferir autorización actual para implementarla**.

El punto explícitamente cambiado frente a D-0074 es el retrato faltante: **silueta genérica** en el nuevo diseño en lugar de marco vacío. No declarar obsoleto el resto del contrato anterior sin decisión concreta.

## Continuación actual

**SECCIÓN 01 — DIMENSIONES Y DISTRIBUCIÓN VISUAL DE LA CABECERA**: discutir alternativas compactas de estructura relativa que contengan retrato cuadrado, nombre PJ, nombre del jugador, alineamiento, clase/nivel y raza en la primera página; logo D&D en la esquina superior izquierda **y nombre del PJ en TODAS las páginas, incluidos anexos**; cabecera completa solo en la primera. El diseño actual aportado por el propietario es inspiración sobre ahorro de espacio, **no** una plantilla a reproducir.

**Aclaración del propietario:** la primera página debe ser relativamente compacta y es menos flexible/modular que las secciones posteriores. No reservar por defecto una cabecera gigante ni imponer una rejilla completamente dinámica a la primera página. Las secciones posteriores admiten más flexibilidad, pero sus reglas particulares se discutirán sección por sección.

Los bocetos anteriores son exploratorios. No hay medidas exactas, ubicación del nombre en páginas posteriores, composición final, biblioteca, algoritmo universal de packing o regla transversal de desbordamiento aprobados.

**Actualización de continuidad (2026-10-08):** la sección izquierda admite dos variantes de exportación (A: habilidades agrupadas por atributo; B: atributos y listas independientes), adaptabilidad vertical con estiramiento/compresión de bloques de atributos y prioridad para los seis atributos originales. Objetivo sujeto a validación: alojar 2–3 atributos extra y habilidades custom sin perder legibilidad; excedentes a páginas extendidas. La fila de Raza/Alineamiento usa 60/40.

**Bloque 01 derecho APROBADO:** tres filas — CA/Iniciativa/Velocidad; Bono por Competencia/Inspiración/Dados de Golpe; PV actuales/PV máximos. Dados de golpe en forma de cadena simple (`5d10 / 3d6`), sin desglose o contadores. PV temporales y salvaciones contra la muerte quedan fuera de este bloque. Sin medidas ni maqueta final aprobadas.

**Bloque 02 — Ataques APROBADO como resumen:** tabla unificada que recoge la lista resumen ya prevista en la App, con columnas `ARMA / ATAQUE / CONJURO`, `BONIFICADOR` y `DAÑO / TIPO DE DAÑO`. No agregar columnas de detalle ni convertir el resumen en descripciones completas. Sin dimensiones definitivas ni política específica de desbordamiento todavía.

**Bloque 03 derecho — Rasgos y Atributos APROBADO:** cada rasgo aparece como entrada de lista únicamente con categoría/origen y nombre (p. ej. `Rasgo de Clase - Nombre 1` o `Rasgo de Raza - Nombre 2`). Nunca incluir descripciones de rasgos en la hoja, tampoco en extensiones. Clase/raza son ejemplos, no prohibición de otros orígenes reales de la App. Faltan criterios de altura, cortes y nombres largos.

**Opción C APROBADA para el reparto vertical Ataques/Rasgos:** ambos son módulos independientes con altura adaptable al contenido, espacio mínimo útil para anotar manualmente, aprovechamiento mutuo del espacio libre y continuación en extensiones cuando no cabe. Ninguno puede crecer hasta eliminar completamente al otro de la primera página. El diseño **no** fija números de filas, medidas, proporciones ni algoritmo; la reserva de escritura manual no obliga a crear áreas vacías enormes. Las páginas extendidas no deben duplicar grandes módulos ya agotados.

**Continuación inmediata:** examinar los restantes módulos de la primera página, empezando por **Lanzamiento de Conjuros** (en el PDF del propietario incluye espacios por nivel, CD de salvación, modificador de ataque mágico y aptitud mágica). Su lugar/condicionalidad y la convivencia con Ataques/Rasgos aún NO están aprobados. La capacidad y mecanismo de expansión de la sección izquierda siguen pendientes de validación técnica y no autorizan implementación.

Consultar una sola decisión cada vez, registrar su estado y detenerse antes de implementar. El propietario resuelve las decisiones de comportamiento y UX.

## Restricciones

- Trabajo documental solamente; no código, plantillas, PDF ni nuevas pruebas de renderer.
- Priorizar integridad de datos, legibilidad y utilidad en papel.
- Conservar antecedentes históricos y distinguir la exportación ya existente del rediseño futuro.
