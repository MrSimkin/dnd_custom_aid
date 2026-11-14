# QA del rediseño DM — 0.7.0-preqa.2

Candidata para el QA del propietario después del rediseño R1 a R7 ([REDISENO_BASE_DM.md](REDISENO_BASE_DM.md)). Cubre los puntos A1 a A14 del libro de aceptación ([QA_BASE_DM_0.6.0-preqa.2_OBSERVACIONES.md](QA_BASE_DM_0.6.0-preqa.2_OBSERVACIONES.md)). También repite las pruebas de las mesas (paquetes 1 a 8) que antes no se pudieron hacer porque la cuenta Gmail era Jugador.

## Candidata

| Pieza | Identidad | SHA-256 |
| --- | --- | --- |
| APK | `dnd-custom-aid-0.7.0-preqa.2-build-70200.apk` (versionName `0.7.0-preqa.2`, versionCode 70200) | `ee1e6f658b98f09f347b887fc7fc52841b232346a0df5c20e60494c1fa8b1b7a` |
| Windows | `dnd-custom-aid-windows-0.7.0-preqa.2.zip` (packageVersion 0.7.1) | `460190ddfe2971fc639c0ba805694d707b59d5408ce3844c29c3d9171fa6af0f` |
| Worker | `dnd-custom-aid-api`, versión `78552ccb-e244-4d31-ac24-ccf06eb5fb7a` | — |

- Commit de origen: el commit que agrega esta guía en `feature/dm-ux-redesign`.
- Carpeta local del propietario: `D:\DnD_Aid\APKs\QA 0.7.0-preqa.2 Rediseño DM`.

## Cambios desde 0.7.0-preqa.1

- Observación S1 del propietario: «Sincronizar» muestra cuánto va y cuánto falta, por ejemplo «Enviando 535 de 1.090 · faltan unos 7 min».
  - En Windows aparece en la barra superior, con una barra de avance, y en el Escritorio DM.
  - En la tablet aparece en «Tus campañas» y en el Escritorio DM.
- La preqa.1 ya no se usa.

## Cuentas

- Gmail: DM, y también jugador (decisión D4).
- Outlook: jugador.

## Set de prueba

- El contenido Campus Celeste vive solo en la carpeta privada `D:\DnD_Aid\Set de prueba Campus Celeste` y no entra a este repositorio público.
- El paquete se carga así:
  - en Windows: Diagnóstico → «Elegir paquete y cargar», con todo el contenido;
  - en la tablet: «📦 PJ de un paquete», solo los PJ.
- Los ids son estables, así que cargar dos veces no duplica nada.

## Ventana de QA

- Ventana: artefacto «QA Rediseño DM» (https://claude.ai/artifact/ALb1v42XTUs3oVZjVR2B99), 71 pruebas.
- Secciones:
  - P: preparación;
  - A: cuenta y campañas;
  - R: Preparar;
  - G: dungeons;
  - H: handouts;
  - L: reglas;
  - J: personajes;
  - M: la tablet en la mesa;
  - E, S, C, D: las mesas;
  - W: Windows como PC.
- Marcas: Bien · Bien, pero no lo esperado · En parte · Falla · No aplica.
- Cada prueba tiene:
  - «Comentario» y «Mis comentarios adicionales»;
  - «Limpiar marca».
- Cada sección tiene «Limpiar marcas de la sección».
- Al final hay un comentario general y «Limpiar todas las marcas».

## Límites conocidos

- El dominio de Silas no está definido: lo elige el propietario.
- La semirraza de Arruk está como regla homebrew; su rasgo se confirma en la hoja.
- Los conjuros del Tomo traen nombre, nivel y página. Sus descripciones son imágenes en el PDF de origen.
- El jugador de cada PJ se guarda solo en el aparato (migración 35), porque no viaja al servidor.
- La hoja de PJ (PDF V11) no cambió.

## Verificación técnica

- La ejecución completa pasa 814 pruebas, 0 fallas:
  - `:shared:desktopTest`
  - `:desktopApp:test`
  - `:androidApp:assembleDebug`
  - `:desktopApp:createDistributable`
- El backend pasa 48 pruebas.
- Con el set privado (`DND_CONTENT_BUNDLE`), el paquete carga sin problemas:
  - 67 lugares, 103 PNJ, 40 escenas;
  - 2 dungeons con 25 zonas;
  - 11 encuentros, 22 handouts, 50 notas;
  - 770 reglas homebrew;
  - 4 PJ.
