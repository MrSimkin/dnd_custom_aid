# Base para DM · Paquete 2 · Modo DM en la App Android

Estado: implementado en la rama `feature/dm-mode-android`, construida sobre el paquete 1 (`feature/dm-content-sync`), el 2026-11-10. Lo verifica el QA único de la «Base para DM» (P6).

## Decisiones del propietario

- **P7 «Una sola App»:** una sola entrada para jugadores y DM. Luego se elige la campaña, y la App abre el Escritorio DM o los PJ según el papel en esa campaña.
- **P25 «A · Opcional»:** la cuenta es opcional. La entrada ofrece «Recibir código» (correo y código) y «Seguir sin cuenta». Sin cuenta, los PJ quedan solo en ese aparato.
- **Nombres (P24):** Escritorio DM, Pantalla del DM, Mesa de Combate, Mesa de Escena, Mesa de Dungeon, Vista Grupo, Referencia rápida y Consultar reglas.

## Qué hace

- **Entrada** (`AppEntryScreen`, en todas las builds y no solo en debug; cubre V11-QA-006):
  - el inicio de sesión es el mismo de correo y código de Descope DEV que ya existía, sin cambios en el proveedor;
  - al entrar, la App sincroniza y abre «Tus campañas».
- **Tus campañas:**
  - cada campaña muestra «DM» o «Jugador» según la membresía activa de la cuenta;
  - «Abrir Escritorio DM» lleva al Escritorio DM de esa campaña, y «Abrir», a los PJ;
  - en la tarjeta «Servidor» están «Sincronizar», «Cerrar sesión» y, sin sesión, «Entrar con mi cuenta».
- **Escritorio DM** (`DmDeskScreen`):
  - muestra las cuatro mesas con su propósito, marcadas «En preparación»: son los paquetes 3, 5, 6 y 7;
  - en «Tu contenido» muestra el contenido del DM de la campaña por familia, con el conteo de la biblioteca personal y el botón «Sincronizar» (paquete 1).
- **Al abrir la App:**
  - sin sesión y sin haber elegido «Seguir sin cuenta», muestra la entrada;
  - con sesión y un Escritorio DM abierto la última vez, vuelve a ese Escritorio;
  - en otro caso, abre los PJ, como antes.
- **Memoria por aparato** (`AppEntryStore`): la elección «sin cuenta», el identificador de la cuenta alojada y el último Escritorio DM abierto. Cerrar sesión borra la cuenta y vuelve a la entrada; los datos locales no se borran.

## Verificación

- `:androidApp:assembleDebug` compila.
- Guardias Python del CI que no necesitan PDF: pasan. Las auditorías de PDF corren en CI con sus argumentos.
- Emulador de tablet Lenovo en modo de solo lectura (`-read-only -no-snapshot-save`): nada se guardó al cerrarlo. Se recorrió:
  - la entrada, en tema claro y oscuro;
  - «Seguir sin cuenta», que lleva a los PJ;
  - Tus campañas, con «Entrar con mi cuenta»;
  - Atrás desde la entrada, que vuelve a Tus campañas.
- Para ver el Escritorio DM se sembró en esa copia desechable una cuenta DM de prueba con seis elementos de contenido. Se vieron la marca DM, «Abrir Escritorio DM», las cuatro mesas y el contenido agrupado.
- No se probó un inicio de sesión real, porque requiere el correo del propietario. Tampoco la sincronización real, porque falta publicar el Worker del paquete 1.
