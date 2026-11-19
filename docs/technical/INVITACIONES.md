# Invitar jugadores a una campaña

Punto 9 de la revisión crítica y `PRODUCT.md` §6. Decisión del propietario (18-11-2026): «ambas, pero como "y/o", además y/o QR con fecha de vencimiento». Así quedó.

## Qué ve el DM

En **Jugadores de la campaña** (Windows) hay una tarjeta «Invitar a un jugador»:

- un **código de seis letras** para dictar por teléfono (sin I ni O, que se leen como 1 y 0);
- el **enlace** con ese mismo código, para enviarlo por donde quiera;
- el **QR** del mismo enlace, para que lo escaneen desde la mesa;
- la **fecha de vencimiento**, a la vista.

Hay una sola invitación viva por campaña. «Cambiar el código» crea otra y anula la anterior; «Anular la invitación» deja la campaña sin código. Por defecto vale 7 días, y se puede pedir de 30.

## Qué hace el jugador

En su App, **Tus campañas → ＋ Unirme**, escribe las seis letras y entra como jugador. Después el DM le asigna su PJ. Si ya estaba en la campaña, se lo dice y no pasa nada. Si fue expulsado, puede volver con una invitación válida; si fue vetado, no.

## Cómo está hecho

- Base: `database/migrations/0003_campaign_invitation.sql`. Una fila por invitación, con un índice único parcial que garantiza una sola viva por campaña.
- Servidor: `backend/src/invitationStore.ts` y las rutas `GET/POST/DELETE /v1/campaigns/{id}/invitation` y `POST /v1/invitations/{código}`. Solo el DM activo de la campaña puede mirar, crear o anular. El código se normaliza a mayúsculas.
- App: `HostedCampaignAdministrationClient.invitation / createInvitation / revokeInvitation / joinWithCode`.
- QR: lo dibuja la App, sin librerías externas (`shared/.../qr/QrCode.kt`). Las pruebas lo leen de vuelta como lo haría un lector: encuentran la máscara, deshacen el enmascarado, leen los módulos y comprueban que cada bloque divide sin resto por su generador.

## Pendiente del propietario (acción en el proveedor, AGENTS §5)

1. Aplicar la migración `0003_campaign_invitation.sql` en Neon.
2. Desplegar el Worker.

Mientras eso no ocurra, la App muestra la tarjeta y el servidor responde «Las invitaciones todavía no están disponibles en el servidor», sin romper nada más.
