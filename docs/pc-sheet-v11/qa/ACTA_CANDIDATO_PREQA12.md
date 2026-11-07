# Candidato V11 `0.5.0-preqa.12`, build 51200

**Fecha:** 2026-11-07. **Hecha por:** el Worker, que también implementa el renderizador, así que **esto no es la revisión independiente**.

**Estado:** falta la revisión independiente. Este binario **no se ofrece al propietario** hasta pasar el control final.

## Identidad

| Campo | Valor |
|---|---|
| Commit | `07e5aca0e1b9add6457d0be2c36a6c407617c94e` |
| CI | [Scaffold 42401782030](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/42401782030), verde |
| APK en CI | artefacto `dnd-custom-aid-debug-apk`, id 13585599959 |
| APK SHA-256 | `9f16b03b2b060667bc6be4f29ddd56920f992f8d514cdfff6a84662619c90cd9` |
| Nombre para el propietario | `dnd-custom-aid-0.5.0-preqa.12-build-51200.apk` |
| PDF de CI | artefacto 13586580959 |
| Auditoría de CI | artefacto 13585947671 |

Reúne las decisiones del propietario del 2026-11-07 y las correcciones del golden de la revisión de `preqa.11` (C463, C464).

## Prueba con ese APK exacto

Emulador `Redmi_Note_11_Pro_5G_Proxy`, Android 13, `-read-only -no-snapshot-save`, con toques reales en pantalla. Al cerrarlo se comprobó que sigue en `0.5.0-preqa.7`, con su base de datos del 2026-09-26 y solo el PDF antiguo en Descargas.

| Prueba | Resultado |
|---|---|
| Importar Aldren, Ilyra, Mara y la ficha de estrés por el selector real | Bien. El primer intento falló porque las fichas se copiaron antes de que el emulador terminara de arrancar; se repitió |
| Crear un personaje nuevo y exportar A y B | Bien, 4 páginas |
| «Habilitar niveles épicos», guardar y exportar A | Bien, 6 páginas |
| Guardar por el selector real | Bien |
| Compartir A | Abre el selector del sistema y la App dice «PDF preparado para compartir». No se completó el envío a otra aplicación |
| Retrato real, «Recortar para llenar» e «Imagen completa» | Bien. Imagen de 600×300: 123,1×61,5 recortada al marco y 69,4×34,7 entera |
| Cierres inesperados en el registro del sistema | Ninguno |

## PDF generados por ese APK

Comparación contra el PDF de la misma ficha en el artefacto de CI, en gris a 100 ppp, en la peor página: píxeles con diferencia de gris mayor que 60, y mayor que 8. **No son idénticos:** con el umbral de 8 quedan diferencias invisibles por la unión «fi», que Desktop hace y Android no, y por cómo se guardan las marcas en la capa de texto.

| Archivo | Páginas | Frente a CI | SHA-256 |
|---|---|---|---|
| `Aldren-A.pdf` | 6 | 6 páginas, dibujos iguales, 0 y 53 píxeles | `e66e3182c2210776532a0a5609cf1ccbd5542a985ed18f62e7f30d2e649dc4ef` |
| `Aldren-B.pdf` | 6 | sin par en CI | `7b82f89d8ca79f53be5cd76dfd790e33ca50b725ef3ee64d4780e71221772d6a` |
| `Aldren-epic-A.pdf` | 6 | sin par exacto: en CI es otra ficha | `c585bf965fbe1f3c736f8f8a48c3107dbf6e82cb2a205e499c0a1325472966c8` |
| `Ilyra-A.pdf` | 7 | sin par en CI | `456f8798ba840ce85a2940f597e34d0a93e47b0f173d2005131ede1115791616` |
| `Ilyra-B.pdf` | 9 | 9 páginas, dibujos iguales, 0 y 41 píxeles | `abd17a884013f4eeee122e26e9b254689f8dd5b29b763aae5d280af8aafd60d1` |
| `Mara-A.pdf` | 10 | 10 páginas, dibujos iguales, 0 y 144 píxeles | `50a05a85e6a75f90a86ae26df52bd4ae6696c173b3ab70843745ef4937cfdc0e` |
| `Mara-B.pdf` | 11 | 11 páginas, dibujos iguales, 0 y 216 píxeles | `9b2f4845235b7886909563da5f2038c58bdcfeff0db155097a142de58fb2419e` |
| `Portrait-CROP-A.pdf` | 7 | sin par: imagen propia de esta prueba | `118bd8e7d04abd61934e133a1ea29e89d7efdfde689c76cc8f3c6dc1a52579e2` |
| `Portrait-FIT-A.pdf` | 7 | sin par: imagen propia de esta prueba | `b9140fd73f7e2d6efd2d753145e105d6a79b6197934a2453a5d38d592073bf3a` |
| `Stress-A.pdf` | 12 | 12 páginas, dibujos iguales, 0 y 1610 píxeles | `9b920dc241d8b0bcf5eff2b36c4ad3dc84cb1f8586c528ff92f94d8a8deb636a` |
| `Stress-B.pdf` | 12 | 12 páginas, dibujos iguales, 0 y 1610 píxeles | `f4cab19cc58ad89afcd90b85ef98f6d1ce52537993a9e29134e6fe2786409cb1` |
| `new-character-A.pdf` | 4 | 4 páginas, dibujos iguales, 641 y 981 píxeles, por el nombre distinto del personaje | `944265d8e623e715ced37b61c66d2980170422d99db1ea4cd6e0d69072035ef9` |
| `new-character-B.pdf` | 4 | 4 páginas, dibujos iguales, 641 y 981 píxeles, por el nombre distinto del personaje | `b75c72c01837c27f35e8a53ea80c4688cb56953763e578782cbd65efafb7cb50` |

## Abierto en este candidato

- **M12:** el alcance que sigue en C2 sale como un renglón de texto dentro de ATAQUES. No hay golden para ese caso.
- **ACTIVO marcado:** la App no guarda ese dato.
- **Reglas:** ninguna pasa a aceptada por esta acta. Falta la revisión independiente sobre este candidato.
- **Sin probar:** tablet Lenovo, aparatos físicos, prueba impresa (§3.12), y el selector y el portapapeles reales de Desktop.
