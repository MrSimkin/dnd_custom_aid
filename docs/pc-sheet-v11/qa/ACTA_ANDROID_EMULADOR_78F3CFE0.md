# Prueba en la App de Android instalada, sobre `78f3cfe0`

**Fecha:** 2026-11-06. **Hecha por:** el Worker, con autorización del propietario («Sí, sin guardar»).

**Qué es:** evidencia previa al candidato. **No cierra** QA03, IN01, IN06 ni G4, porque el binario no es un candidato identificado: conserva la versión `0.5.0-preqa.8` (50800) y AGENTS.md §6.3 exige una identidad nueva para cada candidato.

## Entorno

- Emulador `Redmi_Note_11_Pro_5G_Proxy`, Android 13, 1080×2400, arrancado con `-read-only -no-snapshot-save`.
- APK de depuración compilado localmente desde `78f3cfe0`. SHA-256 `c59a80fbf276cc5f2cee53f684faa9404bdee6985dd6cf187fcf8e5f103b66d8`.
- El emulador tenía instalada la versión `0.5.0-preqa.7` con datos. Se instaló encima, así que la prueba también cubre la actualización real de la base de datos.
- Red: Wi-Fi y datos apagados durante Guardar y Compartir.
- **Sin cambios en el emulador:** tras cerrarlo se arrancó de nuevo en solo lectura. Seguía en `0.5.0-preqa.7`, con la base de datos del 2026-09-26, los personajes originales y solo el PDF antiguo en Descargas.

## Resultado, con toques reales en pantalla

| Prueba | Resultado |
|---|---|
| Abrir la App sobre los datos de preqa.7 | Abre sin fallo y los personajes siguen ahí |
| «Habilitar niveles épicos» en el editor de clase | Aparece, se activa, se guarda y sigue activa tras cerrar y reabrir la App |
| PDF de Aldren con esa opción, A y B | P1 imprime N10, N11 y N12 |
| Guardar V11 A y B, selector real del sistema, sin red | Abre el selector con `… - Hoja de PJ - PREQA V11 A.pdf` y guarda |
| Compartir V11, sin red | Abre el selector del sistema (`ACTION_CHOOSER`) |
| Importar respaldos de Aldren, Ilyra y Mara, selector real | Importan como copia nueva |
| Fantasy Sheet, Custom v2 Atributo y Custom v2 Habilidad con Mara | Generan PDF de 45, 28 y 27 páginas |
| Custom v1 con Aldren | Genera PDF |
| Custom v1 con Mara | **Falla:** `Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios'` |

## Android frente a Desktop, con la misma ficha

| PDF | Páginas | Fuentes | Dibujos por página | Píxeles distintos por página, máximo, a 150 ppp |
|---|---|---|---|---|
| Mara-A | 10 = 10 | iguales, incrustadas | iguales | 124, la ligadura «fi» |
| Mara-B, con Libro | 12 = 12 | iguales, incrustadas | iguales | 124, la ligadura «fi» |
| Aldren-A | 6 = 6 | iguales, incrustadas | iguales | 0 |
| Ilyra-B, con Libro | 9 = 9 | iguales, incrustadas | iguales | 1 |

## Diferencias reales entre Android y Desktop

1. **Ligadura:** Desktop dibuja «ﬁ» unida; Android dibuja «f» más «i», con el mismo ancho. Solo se nota con mucha ampliación.
2. **Texto extraíble del símbolo:** la fuente v8 tiene el mismo dibujo en dos códigos. Android escribe `w` en su tabla de texto y Desktop `U+E108`. Se ve igual, pero al copiar del PDF de Android sale `wContratos`. No se corrigió. La auditoría `audit_v11_p3_c2_manuscript.py` lo tomaba por una colisión; ahora reconoce la marca por su fuente.

## Auditorías sobre los 6 PDF de Android

Pasan 15 de las 16 auditorías, sin errores. Nueve de ellas necesitaron bajar su mínimo de PDF, porque exigen un corpus de 48 o más: `c207_vacancy`, `double_outer_frame_golden`, `final_notes_golden`, `header_logo_visibility`, `p1_clipped_cards`, `p3_c2_manuscript`, `p4_slot_meters`, `portrait_placeholder_golden` y `spellbook_cards`. `p3_c2_manuscript` pasa después de corregir la nota anterior, y sigue pasando en los 156 PDF de Desktop.

**`golden_geometry` no da resultado en Android:** pide archivos concretos que este conjunto no tiene.

## Lo que no se probó

- Tablet Lenovo ni el teléfono o la tableta físicos.
- Retrato real en Android.
- Portapapeles en Desktop y selector de archivos real de Desktop.
- Completar el envío de Compartir a otra aplicación: se llegó hasta el selector del sistema.

## Observaciones

- **Custom v1 con Mara:** es el fallo que el propietario reportó el 2026-09-28 (M50800-02). Ese renderizador no cambia en esta rama, así que no es una regresión de V11, pero sigue sin resolverse. Afecta a QA05.
- **Solo depuración:** la opción V11 de Android existe solo en compilaciones de depuración, y la pantalla, el pie y el nombre de archivo la llaman prueba interna. Es correcto antes de la aprobación, pero queda pendiente para la salida final.

## SHA-256 de los PDF de Android

| Archivo | SHA-256 |
|---|---|
| `Mara-A.pdf` | `05f66c906a2d2a08b712af5cfbc86e212c037e9fa2b00f3b9f91fb2c279ca5de` |
| `Mara-B.pdf` | `eba6ac0051dcdb690444ac9497d01cf2096a655c1c44df0416f5cdaf892590f9` |
| `Aldren-A.pdf` | `475c0ba12641d79aee15585bf965ced6f6dad38375e523622e91e81c2ae7926a` |
| `Ilyra-B.pdf` | `7d8bb0927089c989ea355b97bf7157082869a25bbaecd9196ee7f6f897b9eabc` |
| `Aldren-epic-A.pdf` | `bda6c02856ce7a71d0651c54bf15ba8aa80250c96ac5f64c28114e76934c05b0` |
| `Aldren-epic-B.pdf` | `3492dbd8c15e2f7f4d680c20047c2502318c4b9a6c12ae1eb6bcba10e4e0f820` |
