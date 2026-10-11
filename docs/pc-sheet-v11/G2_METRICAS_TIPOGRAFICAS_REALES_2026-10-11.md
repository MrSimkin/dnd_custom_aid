# V11 — G2 medición tipográfica real compartida (2026-10-11)

**Rama:** `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT/sin fusionar. **Gate:** G1 semántico PASS; G2 EN DESARROLLO, sin PASS ni PDF V11 listo. G0 QA histórico Mara pendiente de contraste con PDFs producidos por el propietario.

## Cambio funcional efectivo

Commit `4414b27f77e4d228a36b2a84ca092aa93498c74f`:

1. `shared/.../PcSheetV11Typography.kt`: tipos V11 independientes de los cuatro themes heredados, selección explícita **Fira Sans Regular** para cuerpo y **Barlow Condensed Bold** para rótulos. `PcSheetV11ResolvedFont` contiene la métrica vertical PDF real más el medidor de ancho asociado al **mismo objeto de fuente**, y pasa ambos al posicionador G2; no se infiere el ancho desde caracteres.
2. `desktopApp/.../DesktopPcSheetV11FontMeasurer.kt` y `androidApp/.../pdf/AndroidPcSheetV11FontMeasurer.kt`: cargadores `PDType0Font` desde los mismos assets versionados `assets/fonts/pdf/text/`. Desktop usa Apache PDFBox; Android usa el fork Tom Roush ya integrado en el proyecto. Reciclan la fuente por tipo **dentro del documento concreto**, no como singleton global. Los medidores convierten `getStringWidth(text)` (unidades de milésima de em) a puntos usando el tamaño exacto. El cálculo vertical usa ascent/descent real del descriptor y leading explícito. Un recurso ausente, tamaño inválido o descriptor inconsistente falla cerrado, sin sustituir tipografía silenciosamente.
3. `desktopApp/src/test/.../DesktopPcSheetV11FontMeasurerTest.kt`: test con fuentes empaquetadas auténticas, diferencias entre ancho `MMM` y `iii`, Fira/Barlow, escalamientos tipográficos y acentos; divide contenido largo entre columnas/páginas y comprueba cada celda con anchura medida, baseline dentro de rectángulo y cero saltos de texto. Segundo escenario decodifica el fixture verdadero de **Mara** y compone narrativa, notas digitales, rasgos, equipo especial y Libro usando Fira Sans real sin perder contenido. Guardas de ausencia de font y magnitudes inválidas.

**Rutas deliberadamente intactas:** `DesktopPdfRenderingPrimitives.kt`, `AndroidPdfRenderingPrimitives.kt` (generated), renderers y servicios de exportación legacy, configuración de build, backend, persistencia, `main`. No se genera todavía un PDF final ni cambia la UI; el módulo V11 permanece separado y opt-in a nivel de contrato.

## Verificación y condiciones explícitas

- **Previa:** commit `9b1353971ed3748830196514830f324f0f18b451` tenía `Scaffold checks` y QA registry SUCCESS. El lote inicial de geometría G2 `1109a18` también quedó verificado SUCCESS antes de comenzar el trabajo tipográfico.
- **Nueva implementación:** GitHub Actions `Scaffold checks` run `38175718653` (contiene compilación Android, build Desktop y tests Desktop) y `PC Sheet V11 owner QA traceability` run `38175718666` iniciados para `4414b27`. **Comprobar sus conclusiones exactas** antes de certificar las nuevas métricas; al documentar aún en ejecución.
- La compilación Android no implica que se midió un PDF V11 generado **en un emulador** ni demuestra igualdad de píxeles/contenido entre plataformas. Los tests Desktop tampoco prueban todavía que los módulos del golden V11 se alineen; los rectángulos y tamaños de test siguen siendo escenarios técnicos, no coordenadas diseñadas y aprobadas.
- G2 pendiente: posiciones/contenido físico de P1–P4, C2, C-N3, Libro, cuadrículas y página final; tablas/rasgos/casillas de conjuros, referencias físicas ida/vuelta y métricas tipográficas por módulo del golden. La fragmentación de texto ya cuenta con posiciones y font real, pero no hay PDF renderizado de producción.

**Próxima acción segura:** si el CI del HEAD sale SUCCESS, dejar constancia y utilizar estas métricas en un compositor de módulos verdaderamente guiado por el diseño V11. Si falla, inspeccionar logs Kotlin y corregir SOLO el fallo demostrado. No fusionar PR #173 ni abrir QA del propietario prematuramente. 32 M50800 y V11-QA-001/002 conservan su estado OPEN; RCR-1 0/2.
