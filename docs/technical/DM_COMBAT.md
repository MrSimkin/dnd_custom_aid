# Base para DM · Paquete 5 · Mesa de Combate

Estado: implementado en la rama `feature/dm-combat`, construida sobre el paquete 4, el 2026-11-11. Lo verifica el QA único de la «Base para DM».

## Decisiones que sigue

- **P11:** los monstruos llevan sus PV en la App. Cada PJ tiene un campo opcional «PV (nota)» como referencia; no es obligatorio, el control principal sigue en papel y no toca la hoja del PJ.
- **P12:** cada criatura tiene su casilla de iniciativa y «Tirar» (d20 + DES), uno por uno o «Tirar iniciativa (criaturas)» para todas. Lo tirado se corrige a mano. La iniciativa de los PJ se escribe.
- **P13:** los PV de cada monstruo parten con el promedio de su bloque, o el promedio de sus dados si el bloque no trae número. Se pueden escribir a mano («PV máx.») o tirar con «Tirar PV (dados)».
- **P14:** un combate se arma desde un encuentro preparado, que entra entero, o improvisado: del bestiario con cantidad, o con nombre libre, PV y CA a mano. Se pueden sumar participantes a un combate ya empezado.
- **P19 y D-0068 §13 (Preparar encuentro):**
  - cantidad, duplicar, «cambiar por otra criatura», reserva y ajustes rápidos temporales (CA, PV máx., bono de ataque, dados de daño y bono de daño), sin quejarse si los números no cuadran;
  - paquetes que se arman en la App de PC y se aplican de un toque a una criatura o a todo su grupo;
  - todo es reversible, y el bestiario base no cambia;
  - «Empezar combate» hereda lo preparado.
- **D-0068 §2 y §4:** puede haber varios combates por campaña. La App asiste y nunca arbitra.

## Diseño

- **Modelo** (`shared/dm/Combat.kt`):
  - `CombatDocument` pasa por tres estados: «En preparación», «En curso» y «Terminado»;
  - cada `Combatant` es un PJ, una criatura o un participante improvisado; las copias de un mismo bloque comparten grupo;
  - `CombatEngine` hace operaciones puras, cada una devuelve un documento nuevo;
  - valores efectivos: base + paquetes, y un ajuste manual siempre gana;
  - el orden va por iniciativa, luego por DES y luego por nombre. La reserva queda fuera del orden, y los derrotados se saltan;
  - los PV nunca bajan de 0, y curar se detiene en el máximo;
  - un paquete con PV suma o resta esos PV también a los actuales, salvo que el máximo se haya escrito a mano.
- **Guardado** (`DmCombatRepository`, SQLDelight 30, tabla `dm_combat`):
  - un documento JSON por combate, que se guarda al instante en el aparato;
  - es estado de mesa en vivo y local; no viaja al servidor en este paquete.
- **Paquetes** (familia `PACKAGE`, `PackageContent.kt`, tabla `package_payload`):
  - un paquete suma piezas (acción, rasgo, salvación, conjuro o reacción) y quita cosas por nombre, que se muestran tachadas;
  - además ajusta PV, CA, bono de ataque y bono de daño;
  - viaja a la tablet con el resto del contenido del DM (paquete 1).
- **App de PC:** en Gestores, la sección «Paquetes» permite crear, editar piezas, lo que quita, ajustes y notas, y borrar.
- **Tablet:** en el Escritorio DM, «Mesa de Combate» → «Abrir».
  - **Lista de combates:** «Nuevo combate» agrega solo los PJ de la campaña.
  - **Fila de cada participante:** iniciativa con «Tirar», nombre y CA, estados, concentración y paquetes, PV actuales y máximos con «Daño» y «Curar». Para un PJ, en vez de PV muestra «PV (nota)».
  - **Botón «Más»:**
    - los 15 estados del SRD, «Concentración» y «Derrotado»;
    - los ajustes rápidos, con «A todo el grupo»;
    - los paquetes;
    - «Duplicar», «Cambiar por otra criatura», reserva, «Quitar ajustes y paquetes» y «Quitar del combate»;
    - la referencia del bloque, con lo que suman o quitan los paquetes.

## Verificación

- `CombatTest`, 6 pruebas:
  - dados;
  - encuentro preparado completo con reserva, y el combate empezado conserva los ajustes;
  - paquetes que suman, ajustan, mueven los PV actuales y se deshacen, sin tocar el bloque base;
  - iniciativa, orden, turnos que saltan a los derrotados, rondas, daño, curación, PV tirados y estados;
  - PJ sin duplicar, con nota de PV y sin iniciativa tirada;
  - varios combates por campaña.
- `HostedDmContentSyncTest` sigue verde. Ahora usa una familia todavía desconocida, `DUNGEON_ACTIVITY`, para probar las versiones más nuevas.
- Guardias Python del CI que no necesitan PDF: pasan. La casilla «A todo el grupo» usa la casilla propia de la App.
- En el emulador de tablet en modo de solo lectura, con datos desechables, se recorrió:
  - nuevo combate;
  - encuentro preparado: 3 goblins con PV promedio y el oso lechuza en reserva;
  - «Más», con el paquete «Bruto» aplicado a todo el grupo (CA 16 y PV 32/32);
  - tirar iniciativa, empezar, daño de 10 (32 → 22) y siguiente turno.

## Límites

- El combate vive solo en la tablet. Compartirlo entre aparatos queda para cuando haga falta.
- La ronda sorpresa tras una emboscada (P22) llega con la Mesa de Dungeon (paquete 7).

## Rediseño aprobado (P32–P34)

- **Iniciativa por grupo:** `sharedInitiativeGroups` en el documento. Un grupo con una sola iniciativa es un solo turno (`turnSlots`, `actingIds`), y «Siguiente turno» salta el grupo entero.
- **Tiradas con el motor de la hoja de PJ:** `rollInitiativeFor`, `rollGroupInitiative` y `rollAllCreatureInitiative` usan `resolveCharacterD20Roll`. El dado queda en `initiativeDie`, y la ventana muestra la tarjeta de resultado de la hoja de PJ según «Dados visibles» o compacto.
- **Variante propia:** `CreatureEdits` guarda características, velocidad, defensa, ataques estructurados (`CombatAttack`), rasgos, acciones y conjuros. El ● marca `hasOwnChanges`, y `resetAll` deshace todo.
- **Guardar como criatura nueva:** `variantPayload` escribe lo propio sobre el bloque base y crea una criatura de campaña. Llega a la App de PC al sincronizar, y el bestiario no cambia.
- **Bloques:** `setGroupQuantity` cambia la cantidad con − y +.

