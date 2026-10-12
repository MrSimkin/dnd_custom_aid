# G3 — Libro, índice físico, referencias, C2 mixto y C-N3 (2026-10-11)

**Mandato del propietario:** contrato A/B visual APROBADO. Implementar PDFs reales en Android y Desktop; NO pedir revisión visual manual antes de QA formal. Única rama PR #173 DRAFT/sin merge; cuatro renderers anteriores intactos.

## Implementado

- `20c90da4a6f94458e05ff299f49f1a9783a0781f`: fichas de Libro con nombre/nivel, lanzamiento, alcance, componentes y material real, duración, concentración, ritual, asociaciones de fuentes/preparación, CD/bono solo cuando conocidos, descripción íntegra y notas; dos columnas, altura variable, continuación lossless. No se inventa escuela (ausente de CharacterSpell). Índice previo con referencia a página física final; soporta múltiples páginas de índice. Libro opcional, sin páginas fantasma. Tests Ilyra + conjuro extremadamente largo. Scaffold `38195427106` SUCCESS.
- `f17bf537bab71272d7702f1b71fef8c4d79fcf20`: cada página PDF se registra al crearla. Al final se añaden HOJA n/N y referencias físicas P2→C2 / C2→P2, más anterior/siguiente de módulo. Última página manuscrita sin pie digital. Test PDFBox Mara con páginas efectivas. Scaffold `38195651500` pendiente al registrar.
- `adc2852460c9ef49f20fc778606f2bd9916613bc`: habilidades desligadas, ataques prolongados, recursos y municiones comparten UNA secuencia compacta C2 a dos columnas con encabezados internos cuando hay contenido. Mantiene IDs/cantidades/casillas; Especial conserva ancho completo e inventario las parejas. Scaffold `38195803923` pendiente; owner traceability `38195803772` SUCCESS.
- `0be20f497061027cb33c38c79f61ce714c9b5f7a`: pruebas PDFBox reales Mara: notas generales y 9 tarjetas solo en C-N3; vaciar notas elimina las C-N3, pero conserva la última NOTAS manuscrita. Scaffold `38195914022` pendiente.

## Siguiente paso obligatorio

Consultar resultados CI exactos, corregir cualquier error de compilación/test demostrado, verificar artefactos generados por la App y continuar implementando módulos contractuales hasta QA formal. No reabrir diseño, no solicitar más revisiones visuales, no fusionar PR. G1 semántico PASS; G2 en desarrollo; G3 PDF runtime parcial; G4–G6 pendientes. Histórico Mara M50800 OPEN, RCR-1 0/2.