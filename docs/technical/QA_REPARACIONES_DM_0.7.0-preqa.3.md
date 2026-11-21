# QA de las reparaciones del rediseño DM: 0.7.0-preqa.3

Esta candidata repara las observaciones del QA del propietario sobre la versión 0.7.0-preqa.2. El registro está en [QA_REDISENO_DM_0.7.0-preqa.2_OBSERVACIONES.md](QA_REDISENO_DM_0.7.0-preqa.2_OBSERVACIONES.md) y cada observación lleva su prueba en la ventana de QA.

## Candidata

| Pieza | Identidad | SHA-256 |
| --- | --- | --- |
| APK | `dnd-custom-aid-0.7.0-preqa.3-build-70300.apk` (versionName `0.7.0-preqa.3`, versionCode 70300) | `ab0a804890417c16062f007cb51380142b5c55de85b75cf0e2110098dd94fea0` |
| Windows | `dnd-custom-aid-windows-0.7.0-preqa.3.zip` (packageVersion 0.7.2) | `10a58f47b3f548655eb97b1d5ab7d62ba4a275d34677b35380e3a9ff8960bef5` |
| Worker | `dnd-custom-aid-api`, versión `78552ccb-e244-4d31-ac24-ccf06eb5fb7a` (sin cambios) | — |

La carpeta local del propietario es `D:\DnD_Aid\APKs\QA 0.7.0-preqa.3 Reparaciones DM`.

## Qué trae

- **Base de datos.** Tres migraciones nuevas:
  - 36: handouts con envío a jugadores, carpeta y etiquetas.
  - 37: secciones de escena.
  - 38: vínculo de un ataque con su arma o su conjuro.
- **Datos del SRD.** Hechos desde los PDF oficiales (mismos SHA-256 que el corpus):
  - `srd_*_es.layout.json`: el texto con formato.
  - `srd_monsters_es.json`: 283 monstruos de 5e y 323 de 5.5e.
  - `srd_rules_es.json`: clases, subclases, razas, trasfondos y dotes para crear PJ.
- **Herramientas.**
  - `scripts/srd/SrdStructured.java`
  - `scripts/build_srd_layout.py`
  - `scripts/build_srd_monsters.py`
  - `scripts/build_srd_rules.py`
- **Paquete de contenido.** Ahora admite:
  - criaturas y las criaturas de cada encuentro;
  - carpeta y etiquetas de los handouts;
  - una nueva carga que actualiza los objetos que el DM no editó desde que se cargaron.
- **Set privado.** El generador del set Campus Celeste se actualizó fuera del repositorio. Trae 14 criaturas, el bloque de combate de 31 PNJ, escenas con secciones y handouts con carpeta.

## Ventana de QA

Artefacto «QA Reparaciones DM» (https://claude.ai/artifact/Ktxa9NgsKqjbExMSRGPsjT), con secciones P, A, F, R, H, C, J y W:
- Las cinco marcas.
- «Comentario» y «Mis comentarios adicionales» en cada prueba.
- «Limpiar marca», «Limpiar marcas de la sección» y «Limpiar todas las marcas».
- El comentario general al final.

Para ver lo que ve un jugador se usa la campaña «QA - PC Sheet PDF Runtime»: ahí Gmail es DM y Outlook es Jugador (decisión D6).

## Verificación técnica

- La ejecución completa pasa 824 pruebas, 0 fallas:
  - `:shared:desktopTest`
  - `:desktopApp:test`
  - `:androidApp:assembleDebug`
  - `:desktopApp:createDistributable`
- Con el set privado (`DND_CONTENT_BUNDLE`) el paquete carga sin problemas. Trae 14 criaturas además de lo anterior.
