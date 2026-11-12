# QA único «Base para DM» · candidato 0.6.0-preqa.1

Es el QA único acordado en P6. Cubre los 8 paquetes de la «Base para DM», en la tablet (APK) y en la App de Windows, con el servidor DEV ya publicado.

## Candidato

| Pieza | Archivo | SHA-256 |
| --- | --- | --- |
| APK Android (versión `0.6.0-preqa.1`, build `60100`) | `dnd-custom-aid-0.6.0-preqa.1-build-60100.apk` | `b2243e9ff41aa881b175697c76a10ad1bc51a69c827ff25266028f3a5994c3f2` |
| App de Windows (versión `0.6.1`) | `dnd-custom-aid-windows-0.6.1-preqa.1.zip` | `e714303ef2a57958f68293cca02334003a8415b41b4532ed82871c5013e52062` |

- **Carpeta local:** `D:\DnD_Aid\APKs\QA 0.6.0-preqa.1 Base DM`.
- **Fuente:** rama `feature/dm-ux-redesign`, en el commit que agrega esta guía (PR #181). Se construyó sobre `c3ee8bef` más el cambio de versión y de empaque de Windows, sin otro cambio de código.
- **Servidor:** Worker `dnd-custom-aid-api`, versión `a63a59c5-8b27-451c-8bf8-b1185b6aa1f3`, con KV `MEDIA` e IA (`DM_REGLAS_IA.md` §5).

**Fuera de este QA:** la hoja de PJ V11 no cambió desde preqa.14. El control previo al QA de V11 (`check_v11_pre_qa_gate.py --mode release`) sigue bloqueado por las revisiones pendientes de la hoja de PJ, que no son parte de este QA. Esta entrega usa la misma excepción explícita del propietario que preqa.13 y preqa.14.

## Instalar

- **Tablet:** instala la APK como siempre (en el emulador, arrastrándola).
- **Windows:** descomprime el ZIP y abre `DnD Custom Aid\DnD Custom Aid.exe`. Usa tus mismos datos de la App de PC de siempre (`.dnd_custom_aid` en tu carpeta de usuario).

## Qué probar (en orden)

### 1. App de PC (Windows): preparar contenido
1. Entra con tu cuenta en «Administración de campaña» y elige la campaña donde eres DM.
2. En **Gestores**, crea o abre lo siguiente, y llena la sección «Vínculos para la Mesa de Escena» de cada uno:
   - **un PNJ:** con lugar, grupo y retrato (si tienes un handout de tipo Retrato);
   - **una tienda:** con su zona, quién atiende y 2 o 3 artículos con precio y ⭐;
   - **dos o tres zonas:** con el mismo nombre de mazmorra, ubicadas en el diagrama con un clic, unidas con pasajes (uno secreto) y con un encuentro;
   - **dos escenas:** con aventura, orden y escena siguiente;
   - **un handout de tipo Mapa:** con dos marcas que abran fichas.
3. Pulsa «Sincronizar contenido del DM».

### 2. Tablet: el Escritorio DM
1. Entra con tu cuenta, abre la campaña como DM y toca **☁ Sincronizar**.
2. Revisa cada mesa según el diseño aprobado (`DM_UX_APROBADA.md`):
   - **Escritorio:** barra lateral, vista general y paneles al costado. En ⚙ se pueden poner las pestañas arriba.
   - **Pantalla del DM:**
     - fichas del grupo, o Vista Grupo;
     - hoja rápida y hoja completa en lectura;
     - Referencia rápida con 5e y 5.5e.
   - **📖 Reglas:**
     - busca algo, por ejemplo «media cobertura»;
     - luego toca **«Responder con IA»**: la respuesta debe citar [1], [2]… y los fragmentos deben verse abajo.
   - **Mesa de Escena:**
     - busca un PNJ por una idea, no por su nombre;
     - abre la tienda (precios, ⭐ y quién atiende);
     - recorre por zona, por función y por aventura;
     - abre el 🗺 mapa y toca una marca.
   - **Mesa de Combate:**
     - nueva mesa, desde un encuentro, con bloques − y +;
     - «aplicar a: los N | uno»;
     - el editor completo y «Guardar como criatura nueva»;
     - iniciativa «Una para el grupo» o «Cada uno la suya»;
     - en curso: daño, estados y «Ahora actúa».
   - **Mesa de Dungeon:**
     - empieza en tu mazmorra;
     - elige actividades de un toque (Buscar con su foco);
     - abre una zona y pon marcas de un toque;
     - crea un reloj;
     - Cerrar turno, Separar grupo e Interrumpido por combate;
     - declara una **Emboscada**, cierra el turno y elige «Ronda sorpresa ▸ ⚔».
   - **📝 Notas:** escribe una con una zona abierta; debe pegarse a esa zona.
3. Toca **☁ Sincronizar** otra vez.

### 3. Windows: el mismo Escritorio DM
1. Abre **«Escritorio DM (mesa en vivo)»**.
2. Revisa que estén las mismas mesas, y que la nota escrita en la tablet aparezca en 📝 Notas después de sincronizar.

## Qué devolver

- Por cada cosa que no esté como en el diseño aprobado: la mesa, qué hiciste, qué esperabas y una captura.
- Si algo se cae o se cierra solo: qué tocaste justo antes.
- La versión que ves en la App (`0.6.0-preqa.1` en Android).
