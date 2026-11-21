# QA de las reparaciones del rediseño DM: 0.7.0-preqa.4

Esta candidata reemplaza a 0.7.0-preqa.3, que nunca llegó a QA. Trae:
- el refactor F1 a F5 (`REVISION_CRITICA_2026-11-14.md`, «Avance del refactor»);
- lo que encontró el agente al recorrer la App real antes de entregarla (AGENTS §6.4).

Las pruebas son las mismas de la ventana de QA de preqa.3 (observaciones del QA de preqa.2, registradas en [QA_REDISENO_DM_0.7.0-preqa.2_OBSERVACIONES.md](QA_REDISENO_DM_0.7.0-preqa.2_OBSERVACIONES.md)). Se agregan tres pruebas para lo reparado en esta pasada.

## Candidata

| Pieza | Identidad | SHA-256 |
| --- | --- | --- |
| APK | `dnd-custom-aid-0.7.0-preqa.4-build-70400.apk` (versionName `0.7.0-preqa.4`, versionCode 70400) | `0d22769d109e8c399f4f4b00bbaa718d467b67048d526eced52e527ec84462af` |
| Windows | `dnd-custom-aid-windows-0.7.0-preqa.4.zip` (packageVersion 0.7.3) | `a2c1e5d2930858e48db7ffbce3e488f5179aceda2e3d67381127d9720f805b8e` |
| Fuente | commit `13366390` en `feature/dm-ux-redesign` | — |
| Worker | `dnd-custom-aid-api`, sin cambios | — |

- La carpeta local del propietario es `D:\DnD_Aid\APKs\QA 0.7.0-preqa.4 Reparaciones DM`.
- La evidencia (capturas) está en `verificacion\windows` y `verificacion\tablet` de esa carpeta. Muestra el set privado, así que no entra al repositorio.

## Cómo se verificó (AGENTS §6.4)

**Windows**
- Se usó la distribución exacta, con la carpeta de datos de prueba (`DND_CUSTOM_AID_DATA_DIR`).
- Se entró con «Seguir sin cuenta (datos de prueba)» y se creó «Serpiente Vieja (prueba)».
- El set Campus Celeste se cargó con el selector real de archivos. Al cargarlo otra vez dijo «No había nada nuevo que cargar. 1083 ya estaban».
- Todo con clics, arrastres y teclado reales (`scripts/qa_real_app.py`).
- Después de cerrar y volver a abrir la App seguían en su lugar:
  - la zona movida;
  - la marca del mapa;
  - el handout «Enviado»;
  - el combate en curso.
- Al cerrar, la herramienta confirmó que la carpeta real del propietario no cambió.

**Tablet**
- Emulador Lenovo P11 `-read-only -no-snapshot-save -no-window`.
- La APK se instaló encima de la 0.7.0-preqa.2 y conservó los PJ.
- El emulador tiene la cuenta Gmail del propietario y 3 cambios pendientes. Por eso **no se sincronizó**: habría enviado datos al servidor real.

## Resultado por prueba

| Prueba | Resultado del agente | Notas |
| --- | --- | --- |
| P1 Instalar la APK | VISTO FUNCIONANDO | Encima de preqa.2, conserva los PJ; el sistema muestra 0.7.0-preqa.4 / 70400. |
| P2 Abrir Windows nuevo | NO VERIFICABLE | Necesita la cuenta Gmail recordada y los datos del propietario. |
| P3 Una sola copia | VISTO FUNCIONANDO | Aviso «D&D Custom Aid ya está abierta en este equipo». |
| P4 Cargar Campus Celeste | VISTO FUNCIONANDO (sin sincronizar) | Ahora se carga desde **Campañas** (antes Diagnóstico). Trae 14 criaturas; la segunda carga no duplica. La sincronización necesita la cuenta. |
| P5 Tablet al día | NO VERIFICABLE | Sincronizar con la cuenta del propietario. |
| A1 Outlook no es DM | NO VERIFICABLE | Cuenta Outlook. |
| A2 Sincronizar desde el Escritorio | NO VERIFICABLE | Cuenta y servidor. |
| A3 Notas que viajan | NO VERIFICABLE | Sincronización. |
| A4 PJ solo de la campaña | VISTO FUNCIONANDO | Chips «Solo …» y «Todas las campañas». |
| A5 Tu homebrew | EN PARTE | Sin cuenta, las 770 reglas quedan en la campaña y Reglas → Homebrew las muestra por familia. La «Biblioteca personal de …gmail…» necesita la cuenta. |
| F1 SRD por tipo con formato | VISTO FUNCIONANDO tras reparar | Ver «Lo que se reparó». |
| F2 Texto completo | VISTO FUNCIONANDO | Búsqueda «furia», página con títulos, negritas, viñetas y tabla. |
| F3 Raza, nunca Especie | VISTO FUNCIONANDO | Reglas, nuevo personaje y hoja. |
| F4 Solo la hoja V11 | VISTO FUNCIONANDO tras reparar | Solo A y B; «Guardar PDF…» de Arruk fallaba (ver abajo). |
| R1 Liora con su bloque | VISTO FUNCIONANDO | CA, PG, características, salvaciones, habilidades, rasgos y acciones en metros. |
| R2 Escenas con secciones | VISTO FUNCIONANDO en Windows | La tablet recibe las secciones al sincronizar: NO VERIFICABLE allí. |
| R3 Plano completo | VISTO FUNCIONANDO | «Ajustar a la ventana». |
| R4 Mover zonas | VISTO FUNCIONANDO | Arrastre con el ratón real; sigue en su lugar tras reiniciar la App. |
| R5 Ficha de zona | VISTO FUNCIONANDO | «1 × Entidad de juramento roto» en ENCUENTRO; el mapa tiene Normal / Grande / Muy grande. |
| R6 Handouts ordenados | VISTO FUNCIONANDO | Buscador, filtros, carpetas, miniaturas. |
| R7 Marcas en el mapa | VISTO FUNCIONANDO en Windows | Clic, arrastre real, «Guardado ✓», sigue tras reiniciar. La tablet: NO VERIFICABLE (sincronización). |
| H1 Enviar desde Windows | EN PARTE | Guardar, volver a abrir y filtro «📤 Enviados»: bien. La sincronización necesita la cuenta. |
| H2 Enviar desde la tablet | NO VERIFICABLE | Cuenta y sincronización. |
| H3 Lo ve el jugador | NO VERIFICABLE | Teléfono con Outlook. |
| C1 Encuentro con sus monstruos | VISTO FUNCIONANDO | 2 Obreros (CA 14, PV 31) y el Encargado (CA 15, PV 44) más los 4 PJ. |
| C2 Criaturas del SRD | VISTO FUNCIONANDO | Goblin (5e) ×3, CA 15, PV 7. |
| C3 Iniciativa y turnos | VISTO FUNCIONANDO | Iniciativas, «Tirar todas», daño, «Asustado», siguiente turno, «Deshacer», «Terminar». |
| C4 Turno de dungeon | VISTO FUNCIONANDO | Tablet y Windows: los cuatro PJ con sus actividades. |
| C5 Desde la zona y emboscada | VISTO FUNCIONANDO | «Preparar ▸ ⚔» trae las criaturas; «Ronda sorpresa · Actúa Arruk». |
| J1 Paso a paso | VISTO FUNCIONANDO tras reparar | Ver «Lo que se reparó». |
| J2 La hoja queda llena | VISTO FUNCIONANDO tras reparar | PV 66, rasgos, dote, equipo; bono y CA corregidos. |
| J3 Con tu homebrew | NO VERIFICADO | El homebrew de la tablet llega con la cuenta. |
| J4 Ataque vinculado a un arma | VISTO FUNCIONANDO tras reparar | «Hacha a dos manos (+6) · 1d12 cortante · +3 · 🗡 hacha a dos manos», también al volver a abrir. |
| J5 Ataque de conjuro | VISTO FUNCIONANDO | Bola de fuego: 45 m, 8d6 fuego, notas. |
| W1 Menos margen en Windows | VISTO (juicio del propietario) | Pantallas recorridas; la valoración es del propietario. |
| W2 Escena todo a la vista | VISTO FUNCIONANDO | Buscar, Por zona, Por función, Por aventura, Mapa. |

## Lo que se reparó en esta pasada

**Personajes**

- **Bono de competencia y CA de un PJ nuevo.**
  - Un PJ de nivel 7 quedaba con +2, y la hoja lo leía como un ajuste de −1. Por eso Atletismo daba +5 en vez de +6.
  - La CA no aplicaba «Defensa sin armadura».
  - Ahora queda en +3 y CA 13 para el Bárbaro. Prueba: `CharacterBuilderTest`.
- **PJ del paquete.**
  - Tenían el mismo problema de bono.
  - Llegaban con PV 1. Ahora traen el promedio de sus dados (Arruk 54).
  - La historia se guarda sin caracteres invisibles.
  - Prueba: `ContentBundleImporterTest`.
- **Elegir raza, clase, subclase y trasfondo.**
  - Mezclaban 5e y 5.5e, y la subclase mostraba las de todas las clases.
  - Ahora solo aparece la edición elegida y las subclases de esa clase.
- **J4.**
  - El borrador del editor perdía el vínculo del ataque con su arma o conjuro.
  - Ahora el arma del SRD también llena el bono de ataque y el de daño. Prueba: `RulesPickTest`.
- **Guardar PDF.**
  - Fallaba con «No glyph for U+000C»: la historia de Arruk trae un salto de página copiado de un PDF.
  - La hoja se imprime desde una copia sin esos caracteres. El PJ no cambia, y la hoja V11 tampoco: pasan todas sus pruebas.
  - Prueba: `DesktopPcSheetV11ExportUiTest`.

**Reglas**

- **En la tablet:**
  - el fondo era blanco y las pestañas se veían desteñidas;
  - las tablas de dos columnas cortaban el valor;
  - las tablas anchas no cabían y no avisaban (ahora caben o dicen «↔ Desliza»).
- **En el texto:**
  - frases partidas por el PDF tras una etiqueta en negrita o en palabras con guion;
  - «Bola de fuego» traía el conjuro siguiente;
  - etiquetas sin acento («Duracion»).
- Pruebas: `SrdLayoutTest`.

**Windows**

- **Textos técnicos que quedaban:**
  - «Adventure / Scene Spine»;
  - «Zone Brief… VTT»;
  - la hoja del PJ en inglés.
- **Detalles de pantalla:**
  - los nombres salían del diagrama de la zona;
  - «Nombre de el dungeon»;
  - las tarjetas de Preparar cortaban el texto;
  - «Empezar combate» quedaba bajo la guía;
  - las zonas se ordenaban como texto (1, 10, 2).

**Tablet**

- En el plano del dungeon, las zonas una al lado de la otra ya no se tapan.

## Observaciones que no son de la App

- **El set Campus Celeste trae:**
  - PNJ cuyo nombre es una frase («Barek Surn atiende turnos…»);
  - un «|» al final de los rasgos de Liora;
  - grupos llamados «Lugar | …».
- **Los PJ del paquete:** el formato no lleva características, así que quedan en 10 y CA 10. Es parte del importador pendiente (punto 1).

## Verificación técnica

- La ejecución completa pasa sin fallas:
  - `:shared:desktopTest`
  - `:desktopApp:test`
  - `:androidApp:assembleDebug`
  - `:desktopApp:createDistributable`
- Pasan las guardas del CI de controles, casillas, modo mesa y ajustes, la de copia única del renderizador V11 y la de entrega del PDF.
- La compuerta pre-QA V11 en modo release no aplica: esta candidata no cambia la hoja V11.
