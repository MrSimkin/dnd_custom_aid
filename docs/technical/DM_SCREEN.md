# Base para DM · Paquete 3 · Pantalla del DM

Estado: implementado en la rama `feature/dm-screen`, construida sobre el paquete 2, el 2026-11-10. Lo verifica el QA único de la «Base para DM» (P6).

## Decisiones del propietario

- **P9 (Vista Grupo):** una fila por dato y una columna por PJ. Los datos son CA, PV máximos, Percepción, Perspicacia e Investigación pasivas, velocidad, sentidos e idiomas. Se destaca el mejor valor de cada fila. Solo datos fijos de la hoja, sin PV actuales.
- **P10 (Referencia rápida):** diez fichas, el texto del SRD en español en la versión de la campaña, y un toque para ver la otra.
- **P4:** cada regla cita su texto del SRD, y la búsqueda funciona sin conexión.
- **P26 (2026-11-10):** sobre descargar los SRD, el propietario dijo: «Si, descargalos, pero todo debe ir en la App, usaremos todo lo que esté disponible». Por eso los dos SRD completos van dentro de la App, no solo las diez fichas.

## Fuentes del SRD

Los dos son oficiales en español y tienen licencia CC BY 4.0 de Wizards of the Coast LLC:

| Versión | Edición | Archivo | SHA-256 |
|---|---|---|---|
| SRD 5.1 | 5e | `https://media.wizards.com/2023/downloads/dnd/SRD_CC_v5.1_ES.pdf` | `75c42a82…254651` |
| SRD 5.2.1 | 5.5e | `https://media.dndbeyond.com/compendium-images/srd/5.2/SP_SRD_CC_v5.2.1.pdf` | `9ecc1b98…8088b5` |

- **Cómo se construye.**
  - Los PDF no se guardan en el repositorio.
  - `scripts/srd/SrdOutline.java` (PDFBox) los pasa a texto.
  - `scripts/build_srd_corpus.py` genera `androidApp/src/main/assets/srd/`, con estos archivos:
    - `srd_5_1_es.json` y `srd_5_2_1_es.json`: el SRD completo por página, sin encabezados ni pies. Los párrafos se rearman y los espacios y guiones especiales se normalizan para la búsqueda;
    - `quick_reference_es.json`: las diez fichas. Cada sección tiene su título y su página en cada versión.
- **Cortes de las fichas.** Cada sección va desde su título hasta el título siguiente. En el glosario a dos columnas del 5.2.1, el corte se detiene en la primera entrada vecina, para que no se cuelen entradas como «Arrastrarse» o «Competencia».
- **Atribución.** Cada archivo trae el texto de atribución exigido por cada SRD, y la Referencia rápida lo muestra al pie.

## Qué hace la App

- En el Escritorio DM, la Pantalla del DM ahora tiene un botón «Abrir». Las otras tres mesas siguen en preparación.
- **Hojas de PJ:** los PJ de la campaña con su clase, CA y PV máximos. «Abrir hoja» abre la hoja completa, y Atrás vuelve a la Pantalla del DM.
- **Vista Grupo** (`shared/dm/DmScreenProjection.kt`):
  - el mejor valor numérico de cada fila se resalta; los empates comparten la marca, y con un solo PJ no se marca nada;
  - sentidos e idiomas se muestran como están escritos.
- **Referencia rápida:**
  - «Versión de la campaña: 5e (SRD 5.1) | 5.5e (SRD 5.2.1)». La campaña no tenía un campo de versión de reglas, así que se elige aquí y se recuerda por campaña en ese aparato. Por defecto es 5.5e;
  - las diez fichas, en el orden de P10;
  - «Ver en 5e» o «Ver en 5.5e» muestra la otra versión;
  - cada sección indica su página, y al pie va la atribución.
- **Consultar reglas:**
  - búsqueda por palabras en los dos SRD, sin internet ni IA (`searchSrd`);
  - no distingue mayúsculas ni tildes, y cada resultado cita su versión, su página y su sección;
  - la respuesta con IA y cita es el paquete 8.

## Verificación

- `DmScreenTest`: 5 pruebas.
  - Vista Grupo: mejor valor, empates, un solo PJ, filas de texto y orden de P9.
  - El SRD completo de las dos versiones y su licencia.
  - Las diez fichas, con texto en las dos versiones, los 15 estados del 5.2.1 sin entradas vecinas y CD típicas.
  - La búsqueda sin tildes, con cita.
- `:shared:desktopTest` 494/494, `:desktopApp:test` 267/267, y `:androidApp:assembleDebug` compila.
- En el emulador de tablet en modo de solo lectura, con datos de prueba desechables, se recorrieron:
  - el botón Abrir de la Pantalla del DM;
  - Hojas de PJ, abriendo la hoja y volviendo con Atrás;
  - Vista Grupo;
  - la Referencia rápida en 5.5e y con «Ver en 5e»;
  - Consultar reglas con «cobertura tres cuartos».

## Límites conocidos

- La versión de reglas se recuerda en cada aparato, no se sincroniza. Si más adelante la campaña tiene un campo propio, este valor se moverá ahí.
- Las tablas del PDF salen como texto en filas («Rápido 120 m 6 km 45 km …»). Se leen bien, pero no tienen formato de tabla.
- Para regenerar los archivos hacen falta los PDF. En CI no se puede comprobar que los archivos coincidan con los PDF; las pruebas comprueban su contenido.
