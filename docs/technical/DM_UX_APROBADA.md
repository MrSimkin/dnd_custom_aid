# Base para DM · Diseño de uso aprobado (tablet)

**Estado:** aprobado por el propietario el 2026-11-11, propuesta por propuesta. Corresponde a las respuestas P28 a P36 de `BASE_DM_REVISION_Y_PROPUESTAS.md`.

**Antecedente:** la primera versión de las pantallas (paquetes 2, 3 y 5) cumplía las funciones, pero no seguía D-0068/D-0069. Le faltaban la barra de mesas, la hoja rápida de PJ y la guía táctica visible, y en vivo pedía demasiadas casillas. El propietario detuvo el trabajo y pidió revisar el diseño antes de seguir.

Este documento describe lo aprobado. Las maquetas se mostraron en la conversación y no quedan guardadas, así que este texto manda.

## Reglas comunes

- **Orientación:** el diseño principal es la tablet horizontal, en columnas. En vertical, las columnas se apilan y sirven de respaldo (P31). En el teléfono, la barra pasa abajo (🏠, mesa actual, Reglas, ☰) y las columnas se apilan; queda a criterio del Worker.
- **Presupuesto de atención (D-0068 §1):** en vivo, lo mejor es no tener que escribir nada; si hace falta, un toque. Escribir en vivo es la excepción.
- **«Para jugadores» y «solo DM»:** cada parte de una ficha lleva su marca.
- **Mejoras propias marcadas:** cada mejora que propuso el Worker se señaló como tal y quedó aprobada junto con su propuesta.
- **Números y dados (P34):** se usa el mismo sistema de la hoja de PJ: el mismo motor de tiradas, la misma presentación de resultados («Dados visibles» o compacto, según Configuración) y los mismos campos numéricos.

## 1. Escritorio DM (P28, P29, P30)

- **Barra lateral fija** a la izquierda, siempre visible; un toque cambia de mesa y cada mesa recuerda dónde quedó.
  - **🏠 Escritorio:** vista general con tarjetas de cada mesa y su estado: ronda, quién actúa, zona y turno, relojes por llenarse, último lugar abierto. Debajo, «Recientes»: handouts, notas y reglas usados hace poco.
  - **Mesas fijas:** Pantalla del DM, Escena y Dungeon. Debajo de cada nombre, dónde quedaste (lugar, o zona y turno).
  - **Combates:** aparecen al crearlos, con su ronda y quién actúa, o «en pausa». Puede haber varios, y se cierran al terminar.
  - **＋ Nueva mesa:** combate (desde un encuentro o improvisado), o una segunda Dungeon si el grupo se separa.
  - **Herramientas abajo:** 📖 Reglas, 🖼 Handouts, 📝 Notas y ⏱ Relojes. No son mesas: se abren como panel al costado sobre la mesa actual y se cierran de un toque (P30).
  - **☁ Estado de sincronización** («Sincronizado hace 5 min»). Al tocarlo, sincroniza. Reemplaza el botón y la lista «Tu contenido».
- **Alternativa en ⚙ Configuración:** pestañas arriba con las mismas mesas y herramientas en una fila (P28).
- **Paneles al costado (P30):** siempre se ve una mesa a la vez. Reglas, referencia, la hoja rápida de un PJ, handouts y notas se abren al costado sin salir de la mesa.

## 2. Pantalla del DM (D-0069 §3, P9, P10, P4)

- **Columna izquierda, Grupo:**
  - la hoja de grupo, con una ficha compacta por PJ (nombre, clase y nivel, CA, PV máx., las tres pasivas, velocidad y sentidos) y el mejor valor de cada dato resaltado;
  - el interruptor **Fichas | Vista Grupo** muestra la tabla de P9;
  - al tocar un PJ, su **hoja rápida** se abre al costado: CA, PV máx., velocidad, salvaciones destacadas, pasivas, sentidos, idiomas, ataques principales y rasgos clave;
  - desde ahí, **«Abrir hoja completa»** la muestra **en modo lectura**, sin entrar al editor del jugador.
- **Columna derecha:**
  - la barra pequeña **«Pregunta una regla…»**: hoy hace búsqueda sin conexión, y con el paquete 8 responderá con IA citando el SRD;
  - la **Referencia rápida**, con las 10 fichas y el selector de versión de la campaña. Cada ficha se abre al costado con su texto, su página y «ver la otra versión»;
  - **«Consultadas hace poco».**

## 3. Mesa de Combate (P11–P14, P19, P22, P32, P33, P34; D-0068 §13, D-0069 §10–11)

### Paso 1 · Preparar
- **Izquierda, participantes agrupados por bloque** («Goblin jefe ×3», con − y + para la cantidad).
  - ▾ despliega a cada criatura, y la que tiene cambios propios lleva un ●.
  - Los PJ se agregan solos. La reserva va aparte y entra con «a la mesa».
- **Centro, lo que tocaste** (el grupo o una criatura), con «aplicar a: los N | uno».
  - 5 ajustes rápidos: CA, PV (con 🎲), ataque, dados y bono de daño.
  - Paquetes de un toque.
  - Editar completa, Duplicar, Cambiar criatura, A reserva y Deshacer todo.
- **Derecha, agregar:** encuentro preparado, bestiario (por nombre o VD) o improvisado. Debajo, la **guía del encuentro** a la vista y **«Empezar combate»**.

### Paso 2 · Iniciativa
- Una ventana que aparece una sola vez al empezar (P12 y P33).
- **Los PJ se escriben.**
- **Cada grupo elige** «Una para el grupo», con un número tirado o escrito con el que actúan juntos, o «Cada uno la suya», con número y 🎲 por criatura, más «tirar a cada uno» y «mismo número».
- «Tirar todas las criaturas» respeta lo elegido en cada grupo, y todo se corrige con el teclado numérico de la App.
- Así, las filas en vivo no llevan casillas de iniciativa.

### Paso 3 · En curso
- **Izquierda, el orden.** ▶ marca el turno; cada criatura lleva una barra de PV; los derrotados van tachados; los PJ muestran su nota de PV (P11). Abajo, «Siguiente turno» y «＋ Agregar» (P14).
- **Centro, la criatura que tocas**, que no tiene por qué ser la que actúa.
  - Los PV grandes, con daño y curación de un toque (−10, −5, −1, +1, +5) y teclado para un número exacto.
  - Estados de un toque, Concentración y Derrotado.
  - «Editar esta criatura».
- **Derecha, la guía:** «Ahora actúa» sigue sola al turno, con las tácticas y los ataques de esa criatura (paquetes sumados y lo quitado tachado), la guía del encuentro y el bloque completo.

### Paso 4 · Editar una criatura (P32)
- Para una criatura o para su grupo, al preparar o en pleno combate.
- **Secciones:** Rápido, Características, Defensa, Ataques, Rasgos y acciones, Conjuros, Paquetes, y Nombre y nota.
- **Ataques:** agregar, cambiar o quitar, cada uno con nombre, bono, daño, tipo y alcance, o traer uno «desde el inventario de piezas».
- **Lo propio de esa criatura va en naranja** y se puede deshacer.
- Cambiar una característica no recalcula sola la CA ni los ataques.
- **«Guardar como criatura nueva»** manda la variante a la App de PC (D-0068 §14). El bestiario no cambia.

### Después
La ronda sorpresa viene desde la Mesa de Dungeon (P22).

## 4. Mesa de Escena (P15, P35, P36; D-0069 §5–9)

- **Buscar:**
  - el buscador va arriba, con **filtros por tipo**: Todo, PNJ, Tienda, Lugar y Escena;
  - con un filtro activo y sin escribir, se lista todo lo de ese tipo;
  - debajo, 📌 Fijados y 🕘 Recientes; fijar o soltar es un toque largo.
- **PNJ por idea o contexto (P36):**
  - la búsqueda abarca papel, apariencia, lugares, escenas y grupos;
  - cada PNJ se muestra con el papel primero, el nombre después, un rasgo para reconocerlo, dónde está y su retrato si lo tiene;
  - se pueden agrupar por zona o lugar, por escena, por grupo o facción, y por papel.
- **Recorrer:**
  - Por zona, como paseo por el lugar;
  - Por función, como «dónde comprar armadura»;
  - Por aventura, con la secuencia de escenas y sus desvíos;
  - **🗺 Mapa**, solo en los lugares que tengan mapa: la imagen de un handout tipo Mapa, con zonas y marcas puestas en la App de PC, donde una marca abre su ficha (P35).
- **Ficha abierta:**
  - al centro, el camino (zona › lugar), la referencia al papel («Carpeta B, pág. 17») y las partes marcadas como para jugadores o solo DM;
  - en una tienda, qué vende, cuánto cuesta, lo destacado ⭐ y quién atiende, sin existencias;
  - a la derecha, lo relacionado: la ficha rápida del PNJ que atiende («cómo llevarlo» y la ficha completa a un toque), lo demás de esa zona y la escena siguiente;
  - toda ficha de lugar, escena o zona muestra «PNJ aquí».
- **Abrir una ficha no mueve al grupo.** Marcar dónde está el grupo y el PNJ improvisado en dos toques quedan para después.
- **App de PC (P36):** vincular PNJ con lugares, zonas, escenas y grupos **eligiendo de una lista**. Hoy son texto libre, y la búsqueda sigue mirando ese texto.

## 5. Mesa de Dungeon (P16–P18, P20–P23, P36)

1. **Mapa:**
   - un diagrama de zonas unidas por pasajes, incluidos los secretos, sin cuadrícula, armado en la App de PC;
   - cada zona muestra su estado (✓ explorada, 💰 saqueada, 🔒 bloqueada, ⚠ peligro activo), y un toque abre su ficha;
   - el grupo (G) y las criaturas (🔴 con su cantidad) se arrastran o se ubican con un toque; nada se mueve solo;
   - ＋ permite una segunda Dungeon.
2. **Ficha de zona:**
   - las zonas abiertas son papeles en pestañas, y cerrar una no la marca como nada;
   - barra lateral de secciones (Presentación, Espacio, Exploración, Hallazgos, Ayuda DM, Encuentro, Referencias y PNJ aquí), con las vacías ocultas;
   - cada parte marcada como para jugadores o solo DM;
   - marcas de un toque: Explorada, Saqueada, Despejada, Peligro activo, Bloqueada y Secreto hallado.
3. **Turno de Dungeon (10 min):**
   - cada PJ elige su actividad de un toque entre las 15 de la regla beta, más «Exploración normal *» y «Ocultarse *»; la anterior se ve, pero nunca se arrastra;
   - el avance del grupo se muestra solo como dato;
   - botones Cerrar turno, Separar grupo e Interrumpido por combate;
   - el registro de turnos se arma solo;
   - las actividades se ajustan en la App de PC.
4. **Relojes y notas:**
   - un reloj tiene nombre y segmentos, y avanza +1 por turno al cerrar o a mano con − y +; avisa al llenarse y no aplica consecuencias;
   - las notas se pegan solas al contexto, se cambian con un toque, siempre se guardan y viajan a la App de PC;
   - son las mismas herramientas de la barra.
5. **Emboscada → combate:**
   - si la emboscada salió bien, la App propone la ronda sorpresa con los PJ marcados, y el DM confirma (o empieza sin sorpresa);
   - «Preparar ▸ ⚔» en el Encuentro de una zona abre una Mesa de Combate con ese encuentro cargado.

## Orden de implementación acordado

1. Corregir lo ya hecho:
   - el **Escritorio**, con la barra lateral, la vista general, los paneles, la sincronización y la opción de pestañas;
   - la **Pantalla del DM**, con hoja de grupo, hoja rápida y hoja completa en lectura;
   - la **Mesa de Combate**, con sus 4 pasos y el editor completo.
2. Construir la **Mesa de Escena** (paquete 6) y la **Mesa de Dungeon** (paquete 7) sobre este diseño, con sus partes en la App de PC: vínculos de PNJ, mapas con marcas, mapa de dungeon y actividades.
3. Paquete 8, Consultar reglas con IA.
4. Un solo QA del propietario al final.
