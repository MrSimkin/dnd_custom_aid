# Observaciones del propietario · QA «Base para DM» 0.6.0-preqa.2

Este registro es el libro de aceptación de la próxima corrección (AGENTS §6.3). Ninguna observación se cierra por documentación, por una prueba verde ni por un dato de prueba cambiado. Se cierra solo con evidencia del candidato instalado.

## Datos del QA

- **Candidato probado:**
  - APK `0.6.0-preqa.2` (build 60200), SHA-256 `8aade822…`;
  - App de Windows `0.6.2`, SHA-256 `9d01b038…`.
- **Fuente:** commit `b333fcd1` en `feature/dm-ux-redesign`.
- **Ventana de QA:** https://claude.ai/artifact/KJaAuzENdus3wmHuxGL6Bq, colección `resultados`, respuestas del 2026-11-13 entre 15:02 y 15:45 UTC.
- **Resumen:** 7 bien · 5 falla · 33 no aplica.

## Observaciones originales, al pie de la letra

| Id | Marca | Texto del propietario | Estado |
| --- | --- | --- | --- |
| P1 | Bien | — | — |
| P2 | Bien | — | — |
| P3 | Bien | «El nombre gestores es poco claro y la forma del menu es poco clara, no queda claro que es cada funcion. No tuve que loguearme, asique no se que usuario estoy operando» | ABIERTA |
| P4 | Bien | — | — |
| P5 | Falla | «quiero que uses el termino dungeon, nunca mazmorra, no me gusta masmorra / no puedo crear un dungeon, solo lugares zonas / Necesito que releas el documento o pienses en un mejor UX, porque no entiendo como crear varias zonas y conectarlas,» | ABIERTA |
| P6 | Falla | «Necesito que » (el texto quedó cortado en la ventana) | ABIERTA · falta el texto completo |
| P7 | Bien | «quiero que el explorador se parezca al de windows, o por lo menos que me permita guardar o setear carpetas tipo, es una joda navegar hasta la carpeta de una campaña, no puedo mover las marcas» | ABIERTA |
| P8 | Falla | «use gmail / si siquiera veo el botón que me dices» | ABIERTA |
| E1 | Falla | «Solo pude entrar como jugador, no hay un menu claro y no pude sincronizar nada» | ABIERTA |
| E2–E7 | No aplica | — (bloqueadas por E1) | BLOQUEADAS |
| R1 | No aplica | «pero puede verlo en el PC» | BLOQUEADA |
| R2–R3, S1–S7, C1–C5, D1–D9 | No aplica | — (bloqueadas por E1) | BLOQUEADAS |
| N1 | Bien | «lo puede hacer en PC, pero no puedo probrlo en tablet y no encuentro las notas» | ABIERTA |
| N2 | No aplica | — | BLOQUEADA |
| W1 | Bien | — | — |
| W2–W3 | No aplica | — | BLOQUEADAS |
| W4 | Falla | «no encuentro eso» | ABIERTA |

## Mensaje del propietario en el chat (2026-11-13), al pie de la letra

- «Me falta la opcion, funciona a medias en la amrca de QA»
- «y observaciones relacionadas pero no de la prueba especifica y observaciones adicional al final del QA, tuve varios problemas»
- «probablemente tengas dudas»
- «La UX me confunde mucho, no entiendo nada»
- «Si te envio un DOCX con una ciudad entera, aventuras y dungeons, puedes crear una versión parcial (con solo una parte del contenido) para hacer pruebas?»

## Primera lectura técnica

Esta lectura **no** es un diagnóstico cerrado. Solo explica qué falta comprender antes de corregir.

1. **E1, P8 y la mayoría de los «No aplica».**
   - El rol en la tablet sale de la membresía alojada de la cuenta (`campaignRoleFor`).
   - Con Gmail, la cuenta es Jugador o no tiene membresía, así que la tablet solo ofrece «Abrir» de jugador.
   - En Windows, Administración de campaña muestra «Sincronizar contenido del DM» solo con una membresía DM activa.
   - La ventana pedía Outlook, pero ni la App ni la ventana muestran con qué cuenta y con qué rol estás.
   - Es un fallo de experiencia, aunque el permiso funcione como se diseñó.
2. **P3 y «no sé qué usuario estoy operando».**
   - Windows trabaja en local sin pedir sesión, y la sesión vive dentro de Administración de campaña.
   - «Gestores» es un nombre técnico.
3. **W4.**
   - «Responder con IA» aparece solo con una sesión iniciada (`rulesAiAvailable`).
   - Sin sesión, el botón no se muestra y nada explica por qué.
4. **P5.**
   - No existe un objeto «dungeon» que se pueda crear: el dungeon es un texto repetido en cada zona.
   - Conectar zonas exige abrir cada una por separado.
   - Además, el término debe ser «dungeon» en toda la App.
5. **P7.** Selector de archivos propio sin carpetas favoritas; las marcas del mapa no se pueden arrastrar.
6. **N1.** En Windows la nota se puede crear; en la tablet quedó bloqueada por E1. Falta aclarar dónde buscó las notas y no las encontró.
7. **Ventana de QA.** Faltaban «A medias», una observación por prueba fuera de la prueba y un cuadro final. Se agregaron en la versión 3 de la ventana, con las mismas pruebas.

## Decisiones del propietario

| # | Fecha | Pregunta | Respuesta, al pie de la letra | Consecuencia |
| --- | --- | --- | --- | --- |
| D1 | 2026-11-13 | ¿En la tablet quieres poder ser DM también sin iniciar sesión? (se recomendó que sí) | «No, solo con cuenta» | El Escritorio DM de la tablet exige una sesión con rol DM en la campaña. La corrección no agrega un DM local sin cuenta; debe hacer visible la cuenta y el rol, y explicar por qué no aparece el Escritorio DM. |
| D2 | 2026-11-13 | ¿La App de Windows debe pedir iniciar sesión al abrirse, igual que la tablet? | «si, al abrirse, y con "recordar" automatico, si quiero cambiar despues, necesito "cerrar sesion" o "cambiar sesion", aunque deberia ser usuario no sesion, tu me entiendes» | Windows pide la cuenta al abrirse y la recuerda sola. Para cambiarla hay «Cerrar sesión» y «Cambiar usuario» (se dice usuario, no sesión). |
| D3 | 2026-11-13 | (comentario del propietario, sin pregunta) | «entiendo que ahora la sincronizacion está en sebug, pero tiene que estar clarita en menu normal tambien, para probar, hemos avanzado de eso» | Sincronizar sale de Administración de campaña y queda visible en el menú normal de Windows. |

## Próximo paso

1. Completar P6 y las observaciones generales con el propietario.
2. Resolver, una a la vez, las dudas que son decisión del propietario (entrada como DM e identidad visible, modelo de dungeon).
3. Recién entonces, corregir.
