# QA del rediseño DM 0.7.0-preqa.2: observaciones del propietario

Registro textual del QA del propietario (2026-11-14), sobre la candidata `0.7.0-preqa.2` (build 70200, Windows 0.7.1). Se usó la ventana de QA «QA Rediseño DM». Este es el libro de aceptación de la próxima reparación (AGENTS §6.3). Ninguna observación se cierra solo con documentación, con una prueba verde o con un cambio en el paquete de prueba.

## Resultado textual

| Prueba | Marca | Comentario textual |
| --- | --- | --- |
| P1 Instalar la APK | BIEN | |
| P2 Windows pide la cuenta al abrir | BIEN | |
| P3 La cuenta se recuerda | BIEN | |
| P4 Nueva campaña | BIEN | |
| P5 Cargar Campus Celeste | BIEN | |
| P6 Sincronizar arriba, con avance | BIEN | |
| P7 Tablet: cuenta y campaña | BIEN | |
| P8 Tablet: los PJ del paquete | BIEN, PERO NO LO ESPERADO | «aparecen incluso los PJs que no son de la campaña» |
| A1 Barra superior de Windows | BIEN | |
| A2 Entrar como jugador explica | FALLA | «entro como si fuera el DM a pesar que estoy como Outlook» |
| A3 Tablet: rol de cada campaña | BIEN | |
| A4 Jugadores de la campaña | BIEN | |
| A5 Menú de Windows | BIEN | Adicional: «En Reglas "Texto completo del SRD" no tiene formato, y eso está mal» |
| R1 Portada de Preparar | BIEN | |
| R2 Una tienda con su stock | BIEN | |
| R3 Un PNJ completo | SIN MARCA | Adicional: «AL cargar Liora pusiste sus stats de combate en notas en vez de en la opcion mecanicas de combate» |
| R4 Escenas en orden | BIEN, PERO NO LO ESPERADO | «las escenas no tienen los espacios para la carga, de hecho todo se metio en proposito en vez de secciones dedicadas "leer proposito cargado para entender"» |
| R5 Notas en un solo lugar | BIEN | |
| G1 Sitio 021 dibujado | BIEN | Adicional: «no puedo ver todo el plano o diagrama en Windows, no cabe en pantalla sin scroll ni nada» |
| G2 Armar a mano | BIEN | |
| G3 Nuevo dungeon | BIEN | «no puedo mover las zonas dentro del área de trabajo» |
| G4 Ficha completa de una zona | BIEN | «no puedo ver los encuentros, el mapa es ilegible» |
| G5 Nunca «mazmorra» | BIEN | |
| H1 Explorador de Windows | BIEN, PERO NO LO ESPERADO | «no hay orden posible, carpetas, tags, nada y nada que me ayude a decir que es cada cosa» |
| H2 Marcas que se arrastran | NO APLICA | «no enttiendo que me pides» |
| H3 Enviar a los jugadores (Windows) | BIEN | |
| H4 Enviar desde la tablet | NO APLICA | «no tengo como comprobar que los jugadores lo recibieron y lo ven, porque no me diste una forma en el QA» |
| H5 Lo ve el jugador | NO APLICA | igual que H4 |
| L1 El SRD por tipo | EN PARTE | «la forma de mportación es ilegible, no armaste todo bien y leible» |
| L2 Texto completo del SRD | BIEN, PERO NO LO ESPERADO | «la forma de mportación es ilegible, no armaste todo bien y leible» |
| L3 Tu homebrew cargado | SIN MARCA | «el homebrew está hacio» |
| L4 Crear homebrew | BIEN | |
| L5 Separados | BIEN | |
| J1 Nuevo PJ desde el SRD | EN PARTE | «la forma de hacerlo es horrible, no permite multiclase, es extraño y aparece el termino especie, y la regla dice que siempre será Raza, nunca Especie. cargo todo mal porque no aplicó trasfondo, no le dió los rasgos, no varió stats, no le dió equipo, no hizo lo que tendría que hacer si precarga los datos que me pediste precargar en el PJ» |
| J2 Añadir conjuro | BIEN | |
| J3 Añadir objeto | BIEN | «funciona» |
| J4 Añadir ataque | EN PARTE | «si añado un ataque vinculado a un arma deberia ser asociado y no permite asociar conjuros, deberias poder» |
| J5 Añadir rasgo o dote | BIEN | |
| J6 Clase y subclase | BIEN | |
| J7 Trasfondo, especie y semirraza | (no aparece en el resultado) | |
| J8 Quién juega cada PJ | BIEN | Adicional: «todavia veo que "exporta fantasy sheet custom v1" y eso esta mal, para eso hicimos una hora nueva» |
| M1 A un toque | BIEN | |
| M2 ¿Cómo se usa? | BIEN | |
| M3 Escena con Campus Celeste | BIEN | |
| M4 Notas que viajan | FALLA | «"No se puedo sincronizar: no hay conexion con el servidor o tu cuenta venció. Lo preparado sigue en este equipo." no sincronizó» |
| E1 a E6 | BIEN | |
| S1 a S4 | BIEN | |
| S5 Recorrer | NO APLICA | «no se donde está nada» |
| S6 Mapa con marcas | NO APLICA | «idem anterior» |
| C1 Preparar desde un encuentro | FALLA | «pero no carga monstrups» |
| C2 a C4 | NO APLICA | |
| D1 Empezar y mapa | BIEN | |
| D2 Turno de un toque | FALLA | (sin comentario) |
| D3 Cerrar turno y registro | BIEN | |
| D4 Ficha de zona | BIEN | |
| D5 Relojes | BIEN | |
| D6 Emboscada y desde la zona | SIN MARCA | «no puedo agregar criaturas, no hay» |
| W1 a W3 | BIEN | |

**Comentario general, textual:** «en windows, hay mucho margen vertical, se necesita compresion de espacio para poder leer todo comodamente en tablet.»

## Reglas del propietario que salen de este QA

- **Raza, nunca Especie.** «la regla dice que siempre será Raza, nunca Especie». Vale en toda la App, en la tablet y en Windows, y también para el SRD 5.5e, que usa «especie».
- **Solo la hoja V11.** No debe quedar ninguna salida visible a «Fantasy sheet» ni a «Custom v1» (ver la memoria «V11 reemplaza las hojas antiguas»).

## Estado

Todas las observaciones quedan ABIERTAS. El diagnóstico y el plan de reparación se agregan abajo, antes de cambiar código.

## Diagnóstico y plan (antes de cambiar código)

La evidencia sale del código y de la base local de la App de Windows del propietario, leída sin modificarla. La causa de cada observación es la siguiente.

| Obs. | Qué está mal, en concreto | Causa | Reparación y evidencia |
| --- | --- | --- | --- |
| A2 | Con Outlook, Windows abre Escritorio DM y Preparar en «Serpiente Vieja (prueba)». | Una campaña existe en el servidor pero la cuenta abierta no es miembro. Windows la cuenta como «Solo en este equipo» y solo bloquea a quien es Jugador. | Nuevo acceso «No eres parte», bloqueado igual que Jugador. Lista de campañas por cuenta. Prueba de acceso. |
| M4 | La sincronización desde el Escritorio DM de Windows falló con el mensaje genérico. | El mensaje no dice la causa. El propietario tenía dos copias de la App abiertas a la vez, que comparten la cuenta recordada; la otra copia pudo renovar la cuenta. | La App de Windows se abre una sola vez (la segunda copia avisa y se cierra). Si la cuenta se renovó en otra copia, se relee antes de pedir entrar. El mensaje muestra la causa. Prueba de la cuenta recordada. |
| L3 | En Reglas → Homebrew no aparecen las 770 reglas. | El paquete guarda el homebrew en la biblioteca personal de la cuenta Gmail. Con otra cuenta, o en la tablet sin cuenta guardada, la lista sale vacía sin explicar por qué. | La pantalla dice de qué cuenta es la biblioteca y, si está vacía, por qué. La tablet usa la cuenta guardada. Prueba de la biblioteca. |
| P8 | La lista de PJ de la tablet muestra los PJ de todas las campañas. | La lista de Personajes es un directorio global. | Por defecto muestra solo la campaña abierta, con «Todas las campañas» como opción. |
| J8 (adicional) | Windows todavía ofrece «Fantasy Sheet», «Custom v1» y «Custom v2». | La exportación de Windows no se actualizó cuando V11 pasó a ser la única hoja. | Windows exporta solo V11 A/B, igual que Android. La hoja V11 no cambia. |
| J1 «Raza» | La App dice «Especie» y «Subespecie». | Nombres del SRD 5.2.1. | «Raza» y «Subraza» en toda la App. |
| A5, L1, L2 | El texto del SRD se ve sin formato y cuesta leerlo. | Viene del PDF línea por línea. | Lector con párrafos unidos, títulos, listas y tablas, en el SRD por tipo y en el texto completo. |
| R3 | El bloque de combate de Liora quedó en notas. | El generador privado del paquete no usa el campo de mecánicas de combate. | El generador llena las mecánicas de combate; el importador las respeta. |
| R4 | Todo el texto de las escenas quedó en «Propósito». | Lo mismo: el generador no separa las secciones de la escena. | El generador reparte cada parte en su sección. |
| G1 | El plano de Windows no cabe y no tiene desplazamiento. | El plano mide lo mismo que la ventana. | Zoom, «Ajustar a la pantalla» y desplazamiento. |
| G3 | En el dungeon nuevo, las zonas no se mueven. | En la base local, ninguna zona de «Basico test 1» cambió de posición: el arrastre no llega en el uso real. | Arrastre sin conflicto con el desplazamiento; prueba de interfaz con el plano dentro del desplazamiento. |
| G4 | En la ficha de zona no se ven los encuentros y el mapa no se lee. | La ficha no muestra el encuentro vinculado y el mapa sale pequeño. | Encuentros visibles con «Abrir», y el mapa con zoom. |
| H1 | Los handouts y mapas no tienen orden ni etiquetas que digan qué es cada uno. | La lista es plana. | Agrupar por tipo y por carpeta, con etiquetas, búsqueda y la descripción visible. |
| H2 | El propietario no entendió la prueba. | La instrucción del QA no era clara. | Se reescribe la prueba paso a paso. |
| H4, H5 | No hubo forma de comprobar lo que ve el jugador. | El QA no indicó cómo. | Paso con el teléfono y Outlook como Jugador. |
| J1 | Crear un PJ desde el SRD está incompleto. | El asistente solo copia el nombre de la clase, la raza y el trasfondo. | Asistente nuevo: raza (y subraza), clase o multiclase, trasfondo con competencias, rasgos, ajuste de características y equipo inicial, en 5e y 5.5e. |
| J4 | Un ataque no se puede vincular a un arma ni a un conjuro. | El ataque no guarda el vínculo. | El ataque guarda su arma o su conjuro, y el selector lo vincula. |
| C1, D6 | No hay criaturas: los encuentros no traen monstruos y no se pueden agregar. | El paquete no crea criaturas y el SRD no ofrece monstruos. | Monstruos del SRD en el catálogo y «Añadir criatura» desde SRD → homebrew → custom. El paquete crea las criaturas de sus encuentros. |
| S5, S6 | El propietario no encuentra «Recorrer» ni el mapa con marcas. | El botón está escondido en la Mesa de Escena. | «Recorrer» y «Mapa» quedan visibles arriba de la mesa y en la ayuda. |
| General | Hay demasiado espacio vertical. | Márgenes amplios. | Diseño más compacto en Windows y en la tablet. |
| D2 | Respuesta del propietario (2026-11-14): «no aparecían ni los PJ, ni las habilidades, no apareció en nada esa opción». | La mesa toma el grupo cuando empieza la exploración. Un PJ que no estaba entonces (por ejemplo, cargado después en la tablet) no aparece, y sin PJ tampoco se ven las actividades. En Windows, Sitio 021 sí tiene los 4 PJ. | Al abrir la mesa, los PJ de la campaña que falten se suman al grupo. Prueba del motor. |

## Estado tras la reparación (candidata 0.7.0-preqa.3)

Todas las observaciones quedan en **REPARADO, pendiente del QA del propietario**. Ninguna se da por cerrada sin su marca en el QA de la candidata. La guía es [QA_REPARACIONES_DM_0.7.0-preqa.3.md](QA_REPARACIONES_DM_0.7.0-preqa.3.md).

| Obs. | Qué cambió | Evidencia automática | Prueba del QA |
| --- | --- | --- | --- |
| A2 | Nuevo acceso «No eres parte». Bloquea Escritorio DM, Preparar, Handouts y los PJ de otra cuenta. | `DesktopCampaignAccessTest` | A1 |
| M4 | Una sola copia de la App en Windows. La cuenta se relee si otra copia la renovó. Una cuenta vencida pide entrar de nuevo. El error dice la causa. | — (comportamiento de proceso) | P3, A2, A3 |
| L3 | La biblioteca personal dice de qué cuenta es y explica por qué está vacía. | — | A5 |
| P8 | La lista de PJ de la tablet empieza solo con la campaña abierta. | — | A4 |
| J8 | Windows exporta solo la hoja V11 A/B. | `DesktopPcSheetV11ExportUiTest` | F4 |
| Raza | «Raza» y «Subraza» en toda la App. | — | F3 |
| A5, L1, L2 | El SRD con títulos, tablas, etiquetas en negrita y listas, rehecho desde los PDF oficiales (`scripts/srd/SrdStructured.java`, `scripts/build_srd_layout.py`). | `SrdLayoutTest` | F1, F2 |
| R3 | El generador privado llena «Mecánicas de combate» (31 PNJ con bloque). | Prueba del paquete privado | R1 |
| R4 | La escena tiene 16 secciones propias (migración 37). El generador las llena y una nueva carga actualiza lo no editado. | `ContentBundleImporterTest` | P4, R2 |
| G1 | Zoom, «Ajustar a la ventana» y desplazamiento en el plano. | `DesktopDungeonEditorTest` | R3 |
| G3 | El arrastre ya no se reinicia al moverse la zona (lee la posición actual). | `DesktopDungeonEditorTest` | R4 |
| G4 | La ficha de zona muestra sus encuentros con criaturas. El mapa tiene tamaños. | — | R5 |
| H1 | Búsqueda, filtros, carpetas, etiquetas y miniaturas (migración 36). | `HostedDmContentSyncTest` | R6 |
| H2 | La prueba se reescribió paso a paso. | — | R7 |
| H3, H4, H5 | **Defecto encontrado al reparar:** «Enviar a los jugadores» no se guardaba en la base local, así que nunca llegaba al servidor. Ahora se guarda (migración 36). El QA indica la campaña y la cuenta para ver como jugador. | `HostedDmContentSyncTest.aHandoutSentToThePlayersStaysSentWithItsFolderAndTagsOnEveryDevice` | H1, H2, H3 |
| C1, D6 | Monstruos del SRD (≈280 de 5e y ≈320 de 5.5e, `scripts/build_srd_monsters.py`) en «Agregar criaturas» y en Preparar → Criaturas. El paquete trae 14 criaturas del escenario dentro de sus encuentros. | `ContentBundleImporterTest` | C1, C2, C5 |
| D2 | Los PJ que falten se suman al grupo al abrir la mesa. | `DungeonTest` | C4 |
| J1 | Asistente nuevo: raza y subraza, varias clases (multiclase), subclase, trasfondo, características (arreglo, compra de puntos o a mano) con bonos, habilidades y equipo. Llena PV, salvaciones, competencias, rasgos por nivel, dote y monedas (`scripts/build_srd_rules.py`). | `CharacterBuilderTest` | J1, J2, J3 |
| J4 | Un ataque se vincula a su arma o a un conjuro (migración 38). Hay un selector de conjuros. | — | J4, J5 |
| S5, S6 | Los cinco modos de la Mesa de Escena están a la vista. | `DesktopDmWorkspaceTest` | W2 |
| General | Windows sin el margen táctil de 48 dp y con títulos y relleno más compactos. | — | W1 |

**Límites conocidos:**
- Algunas cabeceras de las tablas de clase del SRD 5.1 salen partidas.
- Un vínculo de ataque se hace solo con un arma que ya esté en Equipo.
- Una carga nueva del paquete no actualiza lo que el DM ya editó.
