# Guía de QA del propietario — V11 `0.5.0-preqa.13`, build 51300

**Fecha:** 2026-11-08. **Para:** el propietario. **Hecha por:** el Worker.

**Decisión del propietario (2026-11-08):** «no, estamos dando mil vueltas en revisiones independientes, es hora de; preparar 3 PJs para probar, darme el APK, preparar el QA». Este QA se hace **sin** que el control final haya pasado. Ver «Lo que no está demostrado», al final.

## 1. Qué recibes

Todo está en `D:\DnD_Aid\APKs`.

| Archivo | Qué es |
|---|---|
| `dnd-custom-aid-0.5.0-preqa.13-build-51300.apk` | La App. SHA-256 `6f586093debc9bd54d5ded260e70b2292b15813f748537ffb07be24f1f226ad7` |
| `QA preqa.13\PJ-QA-1-Aldren-Vale.json` | Guerrero de nivel 5. Ficha ordinaria, sin conjuros propios |
| `QA preqa.13\PJ-QA-2-Ilyra-Quill.json` | Maga de nivel 5, con 25 conjuros. Para la página de conjuros y el Libro |
| `QA preqa.13\PJ-QA-3-Mara-de-los-Siete-Umbrales.json` | Personaje extremo, con contenido propio y mucho equipo. Para las hojas de continuación |
| `QA preqa.13\golden\` | Los golden con los que comparar |

## 2. Instalar

- **Emulador:** arrastra el APK sobre la ventana del emulador.
- **Teléfono:** copia el APK al teléfono y ábrelo desde Archivos.

Se instala encima de la versión anterior y conserva tus datos. **Si Android dice que no puede instalarse sobre la App que ya tienes, no la desinstales:** desinstalar borra los datos guardados en el aparato. Avísame y lo vemos.

## 3. Poner los 3 PJ en el aparato

- **Teléfono:** copia los tres `.json` a la carpeta Descargas.
- **Emulador:** en PowerShell:

```powershell
Set-Location "D:\DnD_Aid\APKs\QA preqa.13"
adb push "PJ-QA-1-Aldren-Vale.json" /sdcard/Download/
adb push "PJ-QA-2-Ilyra-Quill.json" /sdcard/Download/
adb push "PJ-QA-3-Mara-de-los-Siete-Umbrales.json" /sdcard/Download/
```

En la App, para cada uno: **Personajes → Importar → Continuar →** elige el archivo **→ Abrir personaje**.

## 4. Sacar los PDF

En el personaje: **Ajustes** (arriba a la derecha) **→ Hoja de personaje PDF**. En «Salida PDF de diagnóstico» elige **V11 prueba interna A** o **V11 prueba interna B**. Para el Libro, activa **Incluir descripciones de conjuros**. Luego **Guardar…** y guarda en Descargas.

| PJ | Exportar |
|---|---|
| Aldren | A y B |
| Ilyra | A, y B con el Libro |
| Mara | A, y B con el Libro |

Opcional:
- **Retrato:** en Editar, «Retrato y token» → Elegir, y vuelve a exportar en los modos «Recortar para llenar» e «Imagen completa».
- **Compartir:** prueba el botón «Compartir».

## 5. Traer los PDF al PC

```powershell
New-Item -ItemType Directory -Force -Path "D:\DnD_Aid\PDF Check" | Out-Null
Set-Location "D:\DnD_Aid\PDF Check"
adb pull "/sdcard/Download/." "."
```

## 6. Qué mirar

Compara cada página con su golden: `v11_ordinario_6mas2_variante_A/B` para Aldren e Ilyra, `v11_extremo_variante_A/B` y `v11_comparador_C2_extremo` para Mara.

| Página | Mira sobre todo |
|---|---|
| P1 Personaje | Cabecera con el nombre, atributos y habilidades, ataques, combate, conjuros de la ficha, municiones |
| P2 Equipo y tesoro | Equipo, tesoro, Equipo Especial con sus 8 filas, rasgos |
| P3 Trasfondo | Campos de trasfondo y la historia |
| Hojas de continuación (C2) | Que lo que no cupo siga completo y en orden, con «VIENE DE PÁG.» y «CONTINÚA EN PÁG.» correctos, y NOTAS en el papel libre |
| P4 Conjuros | Niveles 0 a 9, cada conjuro con su fuente, casillas para lápiz |
| Notas | Tus notas digitales completas |
| Libro (Ilyra y Mara B) | Cada ficha con nombre, NIVEL y descripción entera |
| Última hoja NOTAS | Solo renglones y cuadrícula para escribir |

En todas: que no falte ni se repita ningún dato, que nada se corte ni se monte, y que la hoja se parezca al golden.

## 7. Cómo darme tus comentarios

Escríbeme en el chat, numerados, con el PJ, la variante y la página. Por ejemplo: «3. Mara B, pág. 5: …». Una captura ayuda, pero no hace falta. Si algo está bien, dilo también.

Copio cada comentario tal cual en `qa/REGISTRO_QA_V11.json` y en `qa/rondas/QA-PREQA13.md`, y no lo cierro hasta que veas el arreglo en un APK nuevo.

## 8. Lo que no está demostrado

- **Control final:** no pasa. Falta la revisión independiente de `preqa.13`, y no están confirmadas las 48 secciones, los 8 golden ni las 9 reglas finales.
- **Revisión de `preqa.12`:** de sus 8 hallazgos, 7 están corregidos en esta versión, sin que nadie más lo haya confirmado. El octavo, «Clase —», queda igual a propósito.
- **Prueba impresa (§3.12):** solo tú puedes hacerla.
- **Ya sabido, no hace falta reportarlo:**
  - El pie «PRUEBA INTERNA V11 - NO APROBADA», «PREQA» en el nombre del archivo y la opción de depuración se mantienen a propósito.
  - El alcance largo de un ataque sigue como renglón de texto dentro de ATAQUES; el golden no tiene ese caso.
  - Una fuente de conjuros sin valor guardado muestra «—».
  - El selector de Compartir abre, pero no se completó un envío real.
