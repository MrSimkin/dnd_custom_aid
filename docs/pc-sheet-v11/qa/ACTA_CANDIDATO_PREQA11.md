# Candidato V11 `0.5.0-preqa.11`, build 51100

**Fecha:** 2026-11-07. **Hecha por:** el Worker, que también implementa el renderizador, así que **esto no es la revisión independiente**.

**Resultado de la revisión independiente:** no pasa (`ACTA_REVISION_INDEPENDIENTE_PREQA11.md`). Este binario **no se ofrece al propietario**.

## Identidad

| Campo | Valor |
|---|---|
| Commit | `2eff0e7ced14650dd9d45ed3f541b61c855a3cb4` |
| CI | [Scaffold 42330874169](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/42330874169), verde |
| APK en CI | artefacto `dnd-custom-aid-debug-apk`, id 13557418890 |
| APK SHA-256 | `5800093fdae0a931fa77d89981326fe1c6b543092ac9fa095d77cc07135a240f` |
| Nombre para el propietario | `dnd-custom-aid-0.5.0-preqa.11-build-51100.apk` |
| PDF de CI | artefacto 13557533482 |
| Auditoría de CI | artefacto 13557493835 |

El commit anterior `c9cc00ed` produjo un APK con el mismo SHA-256: entre ambos solo cambia la ficha de estrés, que no entra en el APK. Por eso los PDF de abajo valen para este candidato. La ficha de estrés se volvió a exportar con ella ya corregida.

## Prueba con ese APK exacto

Emulador `Redmi_Note_11_Pro_5G_Proxy`, Android 13, `-read-only -no-snapshot-save`, con toques reales en pantalla. Al cerrarlo se comprobó que sigue en `0.5.0-preqa.7`, con su base de datos del 2026-09-26 y solo el PDF antiguo en Descargas.

| Prueba | Resultado |
|---|---|
| Importar Aldren, Ilyra, Mara y la ficha de estrés por el selector real | Bien |
| Crear un personaje nuevo y exportar A y B | Bien, 4 páginas |
| «Habilitar niveles épicos», guardar y exportar A | Bien, 6 páginas |
| Guardar por el selector real | Bien |
| Compartir A | Abre el selector del sistema y la App dice «PDF preparado para compartir». No se completó el envío a otra aplicación |
| Retrato real elegido en la pantalla de edición, «Recortar para llenar» e «Imagen completa» | Bien. Imagen de 600×300: se dibuja a 123,1×61,5 recortada al marco, y a 69,4×34,7 entera. Las dos conservan su proporción |
| Cierres inesperados en el registro del sistema | Ninguno |

## PDF generados por ese APK

La comparación es contra el PDF de la misma ficha en el artefacto de CI, en gris a 100 ppp, en la peor página. La columna da dos cuentas: píxeles con una diferencia de gris mayor que 60, y mayor que 8. **Los dos PDF no son idénticos.** Con el umbral de 8 quedan diferencias invisibles en la hoja: Desktop une «fi» en una sola letra y Android no. En la ficha de estrés, la cuenta por página crece con las «fi» de esa página (1.610 píxeles con 162 «fi»), lo que apoya esa causa; no se midió letra por letra.

| Archivo | Páginas | Frente a CI | SHA-256 |
|---|---|---|---|
| `Aldren-A.pdf` | 6 | 6 páginas, dibujos iguales, 0 y 53 píxeles | `af25d894a020ad88f04cb863e08585eacc215f982a0f695e8d4160e0fe94a178` |
| `Aldren-B.pdf` | 6 | sin par en CI | `720cb1914aa2382a118c3af440b56e2bb87fefdc63754fab22f8a0d620c98f1e` |
| `Aldren-epic-A.pdf` | 6 | sin par exacto: en CI es otra ficha | `c0f3753eaa0ae8dce10d16cacce548d3378f44457b303255389a7e992e6a3ca3` |
| `Ilyra-A.pdf` | 7 | sin par en CI | `d08c8a9b9050b7177165fc185b96f152a93fe77ba77cb8197d089a6ecd7ff736` |
| `Ilyra-B.pdf` | 9 | 9 páginas, dibujos iguales, 0 y 41 píxeles | `e1df92eb06bfbee0a8aec1b4fc67c361756db6873fe5774816f02d92967fcd71` |
| `Mara-A.pdf` | 10 | 10 páginas, dibujos iguales, 0 y 144 píxeles | `88018a161d612bbdcdab4308db1fc26c4f44d774347ad31b7fb3ab6f024adda2` |
| `Mara-B.pdf` | 12 | 12 páginas, dibujos iguales, 0 y 216 píxeles | `06a69215d08fa6fbc917a7edd8edfe6e3261091d572fc4e70b509cbe0f7b179d` |
| `Portrait-CROP-A.pdf` | 7 | sin par: imagen propia de esta prueba | `8b69a59dd2d6e19bce2540c15e606327ba9d3813c5648056f59f92f395fef8b3` |
| `Portrait-FIT-A.pdf` | 7 | sin par: imagen propia de esta prueba | `9ebf5fb22de47faa45ac3181380f13e39ef29ade980a6f18fd431b196290db51` |
| `Stress-A.pdf` | 12 | 12 páginas, dibujos iguales, 0 y 1610 píxeles | `665a93492c2bf8611e73959cc08f537dcf3c33d85bad095f81cbe953610ee4e2` |
| `Stress-B.pdf` | 12 | 12 páginas, dibujos iguales, 0 y 1610 píxeles | `61078297fcb4610809a7ce03c9e7a5ebcefa1e42259a82d8ac4b27ef901ec45e` |
| `new-character-A.pdf` | 4 | 4 páginas, dibujos iguales, 641 y 981 píxeles, por el nombre distinto del personaje | `438e323517513864a7cfc82d854cdd8ce16bf890131df939ef3ce237b6cc024c` |
| `new-character-B.pdf` | 4 | 4 páginas, dibujos iguales, 641 y 981 píxeles, por el nombre distinto del personaje | `cc04b5d21688ca263456894d4a1a59daf4d5b717e1ba2ae5aab8fc5124b76223` |

## Casos que la revisión anterior no encontró

| Caso | Dónde está ahora |
|---|---|
| Subraza | `V11-INTERNAL-PROOF-subrace-A.pdf` y `-B`, en CI |
| 6+2 exacto | `V11-INTERNAL-PROOF-six-plus-two-A.pdf` y `-B`, en CI |
| Alcance que sigue en C2 con el nombre en P1 (P126) | `V11-INTERNAL-PROOF-P126-range-in-C2-A.pdf` y `-B`, en CI, y en `Stress-A.pdf` |
| Retrato real en Android | `Portrait-CROP-A.pdf` y `Portrait-FIT-A.pdf` |
| Estrés en Android: ataques largos, más de 20 municiones, Libro largo, seis fuentes, 16 clases | `Stress-A.pdf` y `Stress-B.pdf`, con la ficha `qa/pc-sheet/fixtures/04_estres_v11_android.json` |

## Abierto en este candidato

- **ACTIVO marcado:** la App no guarda ese dato, así que ningún PDF puede imprimirlo marcado. La casilla queda para el lápiz.
- **Reglas:** ninguna pasa a aceptada por esta acta. Falta la revisión independiente sobre este candidato.
- **Segunda línea de la cabecera** («… / 1»): queda para que la revise el propietario, que se reservó ese control.
- **Sin probar:** tablet Lenovo, aparatos físicos, prueba impresa (§3.12), y el selector y el portapapeles reales de Desktop.
