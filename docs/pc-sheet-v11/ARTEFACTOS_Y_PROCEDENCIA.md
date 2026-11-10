# V11 — Artefactos, procedencia y reproducción

**Fecha:** 2026-10-09. **Estado técnico:** fase G0 documental; **no** implica compilación ni integración de renderer.

## Fuente V11 de revisión

- Archivo: `dnd_custom_aid_V11_Carta_revision.zip`
- SHA-256 exacto: `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`
- Tamaño: **1 520 026 bytes**; contiene fuentes Python de maqueta, validadores, imágenes, manifest y ocho PDFs de ejemplo.
- **VERIFICADO:** **R-01 VERIFIED / GOLDEN DURABLE (2026-10-09):** ZIP original archivado en `docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip` sobre esta rama; SHA-256 `35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a`; 1.520.026 bytes; SHA de objeto Git `99ed18a2c59b71440cb8b96fafea5d8df1832bd6`. GitHub Actions `PC Sheet V11 golden archive integrity` run [38009729554](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/38009729554), commit `b7ca78bac9d7d884f6ab37be809685cd59a0d5fc`: comprobación SHA-256 **sobre bytes recuperados mediante checkout**, ZIP testzip PASS, 20 entradas y 8 PDFs. Esto cierra SOLO preservación R-01; G0 sigue PARCIAL y renderer/QA APK no iniciado.
- Paquete auxiliar recibido: `KIT_WORKER_DND_V11_IMPLEMENTACION.zip`, SHA-256 `66da15219289e93f6091354d0bfe82bbbf59642eceb90bf573cea018adb3507a` (solo para historial; el plan y prompt sí se copiaron al repo).
- `PLAN_TECNICO_IMPLEMENTACION_V11.md` original SHA-256 `e0c4f926947a734b3ec1370fd6f6adb2832c32a9fbc389ccb7ab2575bbf458e3` (archivo en repo tiene enmienda de estado de publicación).
- `PROMPT_WORKER_IMPLEMENTAR_V11.md` original SHA-256 `6594f2034e83f6066fc27176c88e97c798c37826da01c5457c0bbcf4442f67e4` (archivo en repo tiene enmienda para ruta de fuentes).

## Inventario comprobado del ZIP

```text
V11_Carta/LEER_PRIMERO.txt
V11_Carta/INFORME_V11.md
V11_Carta/v11_generador.py
V11_Carta/prototipo_v8_base.py
V11_Carta/qa_v11.py
V11_Carta/qa_v11_correcciones.py
V11_Carta/resultados_v11.json
V11_Carta/QA_V11.json
V11_Carta/QA_V11_CORRECCIONES.json
V11_Carta/VISTA_GENERAL_V11.png
V11_Carta/PREVISUAL_CONJUROS_3COLUMNAS.png
V11_Carta/pdfs/v11_comparador_C2_extremo.pdf
V11_Carta/pdfs/v11_extremo_variante_A.pdf
V11_Carta/pdfs/v11_extremo_variante_B.pdf
V11_Carta/pdfs/v11_ordinario_6mas2_variante_A.pdf
V11_Carta/pdfs/v11_ordinario_6mas2_variante_B.pdf
V11_Carta/pdfs/v11_ordinario_6mas3_variante_A.pdf
V11_Carta/pdfs/v11_ordinario_6mas3_variante_B.pdf
V11_Carta/pdfs/v11_personaje_casi_vacio.pdf
V11_Carta/MANIFIESTO_SHA256_V11.json
```

## Certidumbre / límites

- 672/672 checks heredados y 432/432 checks específicos del generador de muestra: **synthetic-only**, no integración ni garantía física.
- Páginas sample: vacío 4; 6+2 A/B 7/7; 6+3 A/B 7/7; extremo A/B 11/10; 386/386 IDs sintéticos para extremo. No usar estas cifras como techos rígidos del usuario real.
- `v11_generador.py` implementa maquetas **Python/ReportLab** con sustitutos de fuentes y texto de logo. No es una dependencia de runtime y **no se porta directamente**.
- La fuente aprobada real está versionada aquí: `assets/fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf`; guía `GUIDE.md`. Verificar empaquetado de otras fuentes y recurso de logo, que no constan como integrados en renderer V11.
- Aprobación visual posterior del propietario **supersede la leyenda anticuada de no-aprobado/PIN activo** dentro de `INFORME_V11.md`; no modificar ese documento histórico de la muestra.
- Para restaurar: 1) leer `RESUME.md`→`LATEST.md`→checkpoint V11; 2) leer `CONTRATO_VISUAL_APROBADO.md` y plan; 3) recuperar ZIP **versionado en esta rama** y comprobar hash exacto; 4) reabrir defectos originales históricos y la salida real antes del primer cambio de código.

**Disponibilidad en Git:** los 8 PDFs de referencia están dentro del ZIP archivado en esta rama; no son PDFs del renderer Kotlin ni del APK. Para reproducir, extraer el ZIP verificado desde Git y aplicar el contrato V11.
