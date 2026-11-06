# Candidato V11 `0.5.0-preqa.9`, build 50900

**Fecha:** 2026-11-06. **Hecha por:** el Worker, que también implementa el renderizador, así que **esto no es la revisión independiente**. G4 sigue sin pasar y este candidato **no se ofrece al propietario** hasta que pase el control final.

## Identidad

| Campo | Valor |
|---|---|
| Commit | `87533aa550a6089cdabaca71960be5ceffb96d99` |
| CI | [Scaffold 42298066622](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/42298066622), verde |
| APK en CI | artefacto `dnd-custom-aid-debug-apk`, id 13543865291 |
| APK SHA-256 | `d44067796ed6baa2cbd9c8b21e955dc514a4a3c60e8488d8caf46c0b2a50d025` |
| Nombre para el propietario | `dnd-custom-aid-0.5.0-preqa.9-build-50900.apk` |
| PDF de CI | artefacto 13543875260 |
| Auditoría de CI | artefacto 13543700476 |

El pie «PRUEBA INTERNA V11 - NO APROBADA», el nombre `PREQA` y la opción solo en depuración se mantienen a propósito: el documento G3 prohíbe confundir una prueba con el PDF final, y solo el propietario aprueba.

## Prueba con ese APK exacto

Emulador `Redmi_Note_11_Pro_5G_Proxy`, Android 13, `-read-only -no-snapshot-save`, Wi-Fi y datos apagados, con toques reales en pantalla. Al cerrarlo se comprobó que sigue en `0.5.0-preqa.7`, con su base de datos del 2026-09-26 y solo el PDF antiguo en Descargas.

| Prueba | Resultado |
|---|---|
| Importar Aldren, Ilyra y Mara por el selector real | Bien |
| Crear un personaje nuevo y exportar A y B | Bien, 4 páginas |
| «Habilitar niveles épicos», guardar y exportar A | Bien, P1 imprime N10, N11 y N12 |
| Guardar A y B por el selector real | Bien |
| Compartir B | Abre el selector del sistema; no se completó el envío a otra aplicación |
| Fantasy Sheet, Custom v2 Atributo y Custom v2 Habilidad con Mara | Bien, 45, 28 y 27 páginas |
| Custom v1 con Mara | **Falla, M50800-02** |

## PDF generados por ese APK

La comparación es contra el PDF de la misma ficha en el artefacto de CI, en gris a 100 ppp.

| Archivo | Páginas | Frente a CI | SHA-256 |
|---|---|---|---|
| `Aldren-A.pdf` | 6 | 6 páginas, dibujos iguales, 0 píxeles | `38820a4aeeece9b6b0edcd2f585ce2a7ca962c502f3f7810421b7e7d98aeab43` |
| `Aldren-B.pdf` | 6 | sin par en CI | `a3aca8132504a55db2ecad86bd78cad9159db4241c9159fd8813b972ee69d6e3` |
| `Aldren-epic-A.pdf` | 6 | sin par en CI | `f6046804becf6199559b73585214a543315a185dffefc91c71d225ebf441a742` |
| `Ilyra-A.pdf` | 7 | sin par en CI | `39fca6346989adeaa5e0f36db0b26644c8a62bfbbbe62efe2752433f51a2d9e3` |
| `Ilyra-B.pdf` | 9 | 9 páginas, dibujos iguales, 0 píxeles | `2e8d17281d681e1a6f23ebc8940ec7cfc12fc9e5a740f0f3f29acaafa79397dc` |
| `Mara-A.pdf` | 10 | 10 páginas, dibujos iguales, 0 píxeles | `79672d0802fe98389e385f88e2a3bae30277a2e6576d11e14b5f7a229bbf5be1` |
| `Mara-B.pdf` | 12 | 12 páginas, dibujos iguales, 0 píxeles | `3fcee6df3050258856fa0d2fd958666a203901226b36ba7b772f5c833cb31054` |
| `new-character-A.pdf` | 4 | 4 páginas, dibujos iguales, 641 píxeles, por el nombre distinto del personaje | `f1ee4d13af79a2fcfb8990a34ca3bf1c0428aff06588237ded4181ca760e62b1` |
| `new-character-B.pdf` | 4 | 4 páginas, dibujos iguales, 641 píxeles, por el nombre distinto del personaje | `170e5f225574678c1f121b60e6e23d042caee1e638a4d1eba9c1cdaf08f4f123` |

## Abierto en este candidato

- **Custom v1 con Mara:** reparado aparte, en la rama `fix/custom-v1-mara-long-skill-label` desde `main` (`3c6ede2e`). No entra aquí porque GLB12 pide no alterar las familias anteriores dentro de V11. En esa rama la App de Android genera 41 páginas. Los defectos de diseño de esa familia, como M50800-19, siguen abiertos.
- **Reglas:** ninguna pasa a PASS por esta acta. Falta la revisión independiente sobre este candidato.
- **Sin probar:** tablet Lenovo, aparatos físicos, retrato real en Android, y el selector y el portapapeles reales de Desktop.
