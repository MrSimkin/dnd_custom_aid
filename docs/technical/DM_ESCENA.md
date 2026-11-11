# Base para DM · Mesas compartidas y Mesa de Escena (paquete 6)

## Tablet y Windows con las mismas mesas

Pedido del propietario (2026-11-11): todo lo del DM va en la APK y también en la App de Windows.

- **Dónde están:** el Escritorio, la Pantalla del DM, la Mesa de Combate y la Mesa de Escena están en `dmUi/src/kotlin`. La App Android y la App de Windows compilan esas mismas fuentes, cada una con su propia versión de Compose.
  - No es un módulo de Gradle porque Compose Multiplatform 1.12 arrastraría a Android una versión de Compose más nueva que la de la App de PJ revisada, y además exigiría el SDK 37.
- **Lo propio de cada plataforma:** pasa por `DmPlatform`:
  - dónde se guardan los ajustes del aparato;
  - cómo se lee el SRD incluido;
  - cómo se decodifican las imágenes;
  - las tarjetas de dados (en Android, las de la hoja de PJ);
  - la sincronización.
- **En la tablet:** `AndroidDmWorkspaceScreen` (SharedPreferences, assets y `hostedBootstrap.refresh()`).
- **En Windows:** «Escritorio DM (mesa en vivo)» en el menú de la mesa de trabajo, que ocupa toda la ventana.
  - Usa `DesktopDmWorkspaceScreen`, con Material 3 en el mismo modo claro u oscuro.
  - Guarda los ajustes en `dm_workspace.properties`, junto a la base de datos.
  - El SRD va como recurso, tomado de `androidApp/src/main/assets`.
  - Sincroniza con `syncDmContent()`.
- **Pruebas:**
  - `DesktopDmWorkspaceTest` recorre las mesas en Windows y las dibuja en `desktopApp/build/dm-desk-proofs/` para revisarlas.
  - El control de campos compactos (`check_player_control_geometry.py`) también revisa `dmUi`.

## Vínculos elegidos de una lista (P35, P36)

`ContentLinks` (migración 31, tabla `dm_content_links`) guarda los vínculos de cada ficha. Se sincronizan dentro del payload, bajo la clave `"links"`; las Apps antiguas la ignoran.

| Ficha | Vínculos |
| --- | --- |
| PNJ | lugares, zonas, escenas, grupo o facción, retrato |
| Lugar o tienda | su zona, quién atiende, qué vende (artículo, precio, ⭐, nota; sin existencias), mapa |
| Zona | PNJ aquí, mapa |
| Escena | aventura, orden, desvío, escenas siguientes, dónde ocurre, PNJ aquí |
| Handout de mapa | marcas (x, y entre 0 y 1, la ficha que abre y una etiqueta) |

- **Cómo se editan:** en la App de PC, cada editor (PNJ, Lugar/Tienda, Zona, Escena e imagen de tipo Mapa) tiene la sección «Vínculos para la Mesa de Escena». Se elige de listas y cada cambio se guarda al momento.
- **Las marcas del mapa:** se ponen haciendo clic sobre la imagen.
- **Los textos libres:** siguen igual y se siguen buscando.

## Mesa de Escena

`SceneIndex` (en shared) lee la campaña una vez por mesa y responde:

- **búsqueda por idea o contexto:** todas las palabras, sin acentos; abarca papel, apariencia, lugares, escenas, grupos y los nombres vinculados;
- **«PNJ aquí»:** cuenta los vínculos desde los dos lados, y una zona suma los PNJ de sus lugares;
- **el camino zona › lugar:** usa el vínculo o, si no hay, el texto «área»;
- **los recorridos:** por función y por aventura (en orden, con los desvíos marcados);
- **las agrupaciones de PNJ:** por zona o lugar, escena, grupo y papel.

La mesa, en `dmUi/SceneDesk.kt`, sigue la sección 4 del diseño aprobado:

- **Izquierda:**
  - «Buscar», con filtros Todo, PNJ, Tienda, Lugar y Escena, 📌 Fijados (toque largo) y 🕘 Recientes;
  - «Recorrer», por zona, por función, por aventura o 🗺 Mapa.
- **Centro:** la ficha.
  - Muestra el camino, la referencia al papel y cada parte marcada «👁 Para jugadores» o «🔒 Solo DM».
  - Una tienda muestra qué vende, con ⭐ y precios.
  - Toda ficha que no sea de PNJ muestra «PNJ aquí».
  - En el mapa, una marca abre su ficha.
- **Derecha:** lo relacionado.
  - La ficha rápida de quien atiende, con «cómo llevarlo» y la ficha completa a un toque.
  - Lo demás de la zona.
  - La escena siguiente.
  - Para un PNJ: dónde está y en qué escenas aparece.
- **Lo que no hace:** abrir una ficha no mueve al grupo. Marcar dónde está el grupo y el PNJ improvisado en dos toques quedan para después, según el diseño.
