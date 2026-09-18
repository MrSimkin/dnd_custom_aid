# Checkpoint — Wave 7 PC authority repository core integrated; DEV deployment pending

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated repository merge:** `2e12400ee18026c702d6727793a3aea5d23d07b4`  
**PR:** #79 — `feat: add PC ownership and controller administration`  
**Initial implementation head:** `5de58771702dd2f1f548ca00f066318c630bb622`  
**Final implementation head:** `418ac19d4d247cfbf19d6fb7f9b158df5c900bdc`  
**Initial push Scaffold:** `35290905581` — FAILED (backend row typing + Kotlin visibility; hosted DB contract passed)  
**Corrected push Scaffold:** `35291183960` — SUCCESS  
**PR Scaffold:** `35291417403` — SUCCESS  
**Post-merge Scaffold:** `35291685597` — SUCCESS  
**DEV Worker deployment:** PENDING

## Repository milestone status

The bounded **PC ownership/controller administration repository core** is merged into `main` and passes all repository CI gates.

It is **not yet declared operational in DEV** because this package materially changes Cloudflare Worker/API code and the existing DEV Worker has not been redeployed/verified from the merged commit.

No hosted database migration was needed. The existing `pc.owner_user_id` and `pc.controller_user_id` columns remain the authority storage.

## Integrated authority contract

The new hosted authority mutation is explicit and narrow:

- route: `PUT /v1/pcs/{pcId}/authority`;
- actor must be authenticated and an active DM in the requested campaign;
- owner and controller are independent nullable identities;
- both fields must be explicitly present in the request;
- `null` means intentional unassignment;
- every non-null target must be an active member of the same campaign;
- inactive/cross-campaign targets fail closed;
- PC/campaign mismatch fails closed;
- missing PC returns typed not-found behavior;
- tombstoned PC returns gone behavior;
- setting the already-current authority is an idempotent no-op.

Critically, authority changes do **not** update the PC snapshot JSON and do **not** advance the PC snapshot synchronization revision.

## Explicit-null wire correction

The project-wide hosted JSON configuration intentionally uses `explicitNulls = false`.

That is correct for existing traffic but would make PC authority unassignment ambiguous by omitting null properties. The authority client therefore builds its request as a `JsonObject` with explicit `JsonNull` entries.

The backend rejects missing owner/controller fields, preserving full-replacement semantics and preventing an omitted value from being mistaken for an intentional clear.

## Desktop behavior

Desktop Campaign Administration now hydrates the hosted member roster into local account/membership state before an authority mutation.

PC Manager:

- shows current owner/controller;
- offers a separate explicit `Administrar propiedad / control` flow;
- only lists active campaign members as selectable targets;
- supports `Sin asignar` independently for owner and controller;
- sends the desired full authority state to the hosted API;
- converges local `PcAuthority` only from the authoritative hosted response.

Campaign DM role does not automatically create PC ownership/control.

## Validation evidence

Repository validation covers:

- API route request/response contract;
- explicit assignment and unassignment;
- requirement that nullable fields be explicitly present;
- generic authorization failures;
- typed missing/tombstoned PCs;
- database-level independent owner/controller assignment;
- idempotent repeat;
- explicit unassignment;
- non-DM rejection;
- inactive/cross-campaign target rejection;
- invariant that snapshot content/revision remain unchanged;
- hosted-client bearer authentication;
- explicit-null serialization;
- authoritative identity validation;
- Desktop active-member eligibility;
- full shared/Desktop/Android build.

The first branch CI exposed only compile-level defects:

1. two Neon diagnostic query results needed explicit array typing for TypeScript;
2. the public PC Manager composable exposed an internal hosted-controller type.

Both were corrected without changing product behavior.

## Required provider handoff

The merged Worker must now be deployed to the existing DEV Worker:

`dnd-custom-aid-api`

Required verification after deployment:

1. deploy Worker code from current `main@2e12400ee18026c702d6727793a3aea5d23d07b4`;
2. confirm `/health` remains healthy;
3. confirm `PUT /v1/pcs/{pcId}/authority` is deployed;
4. confirm an unauthenticated request remains rejected;
5. where a normal authenticated DEV session is available, exercise one non-destructive authority read/assignment scenario or an idempotent same-state scenario;
6. do not paste credentials, OTPs or access tokens into Git/chat.

The current execution environment has no Cloudflare integration available, so deployment is a bounded manual/provider handoff rather than something to simulate.

## Next implementation-ready package after provider closure

**PC Sheet PDF Export — shared semantic/render-plan foundation** under D-0074.

This is selected because D-0074 already closes its product behavior in detail and the canonical PC is coherent enough for a shared export model.

Initial direction:

- canonical export snapshot;
- Permanent vs Current Snapshot state;
- selected sheet family;
- custom-stat presentation mode;
- overflow/Extended-page semantic render plan;
- portrait behavior;
- optional Spellbook inclusion;
- platform-neutral semantics with local/offline operation.

## Deferred PC administration clarification

D-0072 also requires freeze/unfreeze, broader lifecycle administration and duplication where appropriate.

The current repository has no freeze field/contract, and the approved product records do not define what freezing a PC must prevent. Freeze/unfreeze therefore needs an explicit product-behavior definition before implementation; do not equate it automatically with `CharacterStatus.INACTIVE` or with hosted deletion/tombstoning.

## Cost/security state

- hard external-service budget remains USD $0;
- repository is public;
- no secrets/tokens belong in Git or chat;
- no new hosted database migration is required for this authority package.
