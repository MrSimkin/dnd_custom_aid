# Acta del candidato `0.5.0-preqa.14`, build 51400

**Fecha:** 2026-11-10. **Hecha por:** el Worker. **Para:** QA del propietario, sin el control final, por su decisión del 2026-11-08.

## Identidad

| Dato | Valor |
|---|---|
| Commit | `930812da7353b443c7d141b78d15dbdd0b85e74c` |
| CI | Scaffold checks, run 42889592329 (verde); PC Sheet V11 owner QA traceability, run 42889592359 (verde) |
| APK | artefacto 13797998051 `dnd-custom-aid-debug-apk` |
| APK del propietario | `D:\DnD_Aid\APKs\dnd-custom-aid-0.5.0-preqa.14-build-51400.apk` |
| SHA-256 del APK | `4995334b5a66b18d14b1c928cab24b57b6490dd2b56def73d551fe4527bc3f63` |
| PDF de prueba de escritorio | artefacto 13798366734; auditoría 13798910646 |

## Qué cambia (C473)

Arreglos de los comentarios del QA de `preqa.13` y de las decisiones del propietario del 2026-11-10: subclase (V11-QA-009), retrato de línea (V11-QA-008), tinta clara opcional para A y B (V11-QA-010), Equipo Especial a su altura con filas libres vacías (V11-QA-011, V11-QA-016), sangría en RASGOS (V11-QA-012), ACCIONES y RECURSOS en C2 (V11-QA-013), espacios de conjuro por columnas (V11-QA-015), EQUIPO en C2 con la forma de P2 (V11-QA-017), B alfabético, pantalla de exportación solo V11 (V11-QA-007).

## Verificación hecha

- 748 pruebas de escritorio y compartidas, 16 auditorías de PDF y los controles de la CI, en verde.
- **APK exacto de la CI** instalado en el emulador `Redmi_Note_11_Pro_5G_Proxy` en modo solo lectura (no guarda cambios), con los PJ sincronizados del propietario. Desde la pantalla nueva se exportaron Aldren A con tinta clara (SHA-256 `c26e524fa23af5abb2cbe87d5f2bcc817f46db649a4d00a95d69109f9f6a9ddf`) y Mara B con Libro (`dd63809c709a766e53b517fd007d7169ca4390dc0b9e33f9c63e7f4bb6d8aa92`): subclase impresa, sin rótulos en filas libres, tinta clara presente solo donde se pidió, sin cierres de la App.
- Con el APK local del commit `ed7b2de3` se revisaron a la vista Aldren A, A tinta, B, Ilyra A y B tinta, Mara A y B: subclase, sangrías, casillas por columnas, Equipo Especial, Spellbook, EQUIPO en continuación de Mara, página 4 y B alfabético.

## Lo que no está demostrado

- Control final G4: 0/9; ninguna revisión independiente.
- El propietario debe confirmar la subclase en CLASE / NIVEL: su pedido del 2026-11-10 contradice GLB14 (2026-11-06, subclase solo en App).
- Ningún comentario está cerrado: los diez arreglos están en IMPLEMENTED_UNVERIFIED hasta que el propietario los vea en este APK.
