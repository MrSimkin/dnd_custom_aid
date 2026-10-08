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

Consultar una sola decisión cada vez, registrar su estado y detenerse antes de implementar. El propietario resuelve las decisiones de comportamiento y UX.

## Restricciones

- Trabajo documental solamente; no código, plantillas, PDF ni nuevas pruebas de renderer.
- Priorizar integridad de datos, legibilidad y utilidad en papel.
- Conservar antecedentes históricos y distinguir la exportación ya existente del rediseño futuro.
