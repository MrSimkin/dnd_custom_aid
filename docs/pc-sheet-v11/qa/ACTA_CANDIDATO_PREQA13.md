# Candidato V11 `0.5.0-preqa.13`, build 51300

**Fecha:** 2026-11-08. **Hecha por:** el Worker, que también implementa el renderizador, así que **esto no es la revisión independiente**.

**Estado:** falta la revisión independiente. Este binario **no se ofrece al propietario** hasta pasar el control final.

## Identidad

| Campo | Valor |
|---|---|
| Commit | `bdfdaa292e319dc0c5a5a9bce62ad1f619c4b477` |
| CI | [Scaffold 42493357978](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/42493357978), verde |
| APK en CI | artefacto `dnd-custom-aid-debug-apk`, id 13622832102 |
| APK SHA-256 | `6f586093debc9bd54d5ded260e70b2292b15813f748537ffb07be24f1f226ad7` |
| Nombre para el propietario | `dnd-custom-aid-0.5.0-preqa.13-build-51300.apk` |
| PDF de CI | artefacto 13622727830 |
| Auditoría de CI | artefacto 13622698234 |

Reúne las correcciones de la revisión de `preqa.12` (C468).

## Prueba con ese APK exacto

Emulador `Redmi_Note_11_Pro_5G_Proxy`, Android 13, `-read-only -no-snapshot-save`, con toques reales en pantalla. Al cerrarlo se comprobó que sigue en `0.5.0-preqa.7`, con su base de datos del 2026-09-26 y solo el PDF antiguo en Descargas.

| Prueba | Resultado |
|---|---|
| Importar Aldren, Ilyra, Mara y la ficha de estrés por el selector real | Bien |
| Crear un personaje nuevo y exportar A y B | Bien, 4 páginas |
| «Habilitar niveles épicos», guardar y exportar A | Bien, 6 páginas |
| Guardar por el selector real | Bien. Un guardado de Ilyra A cortado por el límite de tiempo dejó un archivo en Descargas; los siguientes se llamaron «… A (1).pdf» y se tomó siempre el más nuevo |
| Compartir A | Abre el selector del sistema y la App dice «PDF preparado para compartir». No se completó el envío a otra aplicación |
| Retrato real, «Recortar para llenar» e «Imagen completa» | Bien. Imagen de 600×300: 123,1×61,5 recortada al marco y 69,4×34,7 entera |
| Cierres inesperados en el registro del sistema | Ninguno |

## PDF generados por ese APK

Comparación contra el PDF de la misma ficha en el artefacto de CI, en gris a 100 ppp, en la peor página: píxeles con diferencia de gris mayor que 60, y mayor que 8. **No son idénticos:** con el umbral de 8 quedan diferencias invisibles por la unión «fi», que Desktop hace y Android no, y por cómo se guardan las marcas en la capa de texto.

| Archivo | Páginas | Frente a CI | SHA-256 |
|---|---|---|---|
| `Aldren-A.pdf` | 6 | 6 páginas, dibujos iguales, 0 y 53 píxeles | `367467574fc11f9104f45e7d732fdc2150994d8ffe5ee6241c75a5b723c8f716` |
| `Aldren-B.pdf` | 6 | sin par en CI | `42d62a8b331b2ebe7774d04f3cc41d67d443bdc68706962ed42e02e2e353464d` |
| `Aldren-epic-A.pdf` | 6 | sin par exacto: en CI es otra ficha | `bdcd9280eba69d5ca3d67ba3febc3f2b7eda94c51c0c5cbab9faa37b12f9f883` |
| `Ilyra-A.pdf` | 7 | sin par en CI | `03e8b6572ec521fac3c818f521675a06dede6c5da6cf92f6907fb000eef8892b` |
| `Ilyra-B.pdf` | 9 | 9 páginas, dibujos iguales, 0 y 41 píxeles | `2b61de0c7855803b90045592df4f27b1d4af6064406534a8fe2cada3790819db` |
| `Mara-A.pdf` | 9 | 9 páginas, dibujos iguales, 0 y 144 píxeles | `25be77e790240b002ee22a8a6854aa1576004184fb0ccef4202695a9084e5be2` |
| `Mara-B.pdf` | 11 | 11 páginas, dibujos iguales, 0 y 216 píxeles | `68270025e66c62895f1803a3a7a9043eef1d6c7745d2706b5275439ef7b7da87` |
| `Portrait-CROP-A.pdf` | 7 | sin par: imagen propia de esta prueba | `9f98f187ded7adc641eacbb565e31e3c24258ab9a4958b1acd63dce810892226` |
| `Portrait-FIT-A.pdf` | 7 | sin par: imagen propia de esta prueba | `fa331b4969e7f19630bc9ab75d7395e21c9dff997beb2ab823aac04b8225f423` |
| `Stress-A.pdf` | 11 | 11 páginas, dibujos iguales, 0 y 1610 píxeles | `beb3039ab4619f7540e62a3be2ecdabd32be1aae3ba0133378acb6490bab6141` |
| `Stress-B.pdf` | 11 | 11 páginas, dibujos iguales, 0 y 1610 píxeles | `14e7f8db2f7de5c518203cd9a2ab4bd43ae8d362d9586aaa3014d6d000413a26` |
| `new-character-A.pdf` | 4 | 4 páginas, dibujos iguales, 641 y 981 píxeles, por el nombre distinto del personaje | `0d9935b008826210d62d058ee22901e7110240c82378c316ff2edf36e89f95ea` |
| `new-character-B.pdf` | 4 | 4 páginas, dibujos iguales, 641 y 981 píxeles, por el nombre distinto del personaje | `9d7f2d9d86b373f7d7599823350702fd4aed2061b0bb0987f9d5101c6696bf2d` |

## Abierto en este candidato

- **M12:** el alcance que sigue en C2 sale como un renglón de texto dentro de ATAQUES. No hay golden para ese caso.
- **H8:** «Clase —» en las tarjetas mágicas, a propósito (regla C221).
- **ACTIVO marcado:** la App no guarda ese dato.
- **Reglas:** ninguna pasa a aceptada por esta acta. Falta la revisión independiente sobre este candidato.
- **Sin probar:** tablet Lenovo, aparatos físicos, prueba impresa (§3.12), y el selector y el portapapeles reales de Desktop.
