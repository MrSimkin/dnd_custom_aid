# Hosted PC explicit conflict resolution — ready for physical QA

**Date:** 2026-09-15 (Chile local time)  
**Branch:** `wave4/qa-log-baseline-recovery`  
**PR:** #40  
**Verified implementation head:** `008a73c20101a127edd82947af71c6024f894609`  
**Scaffold run:** `35044956294` — **SUCCESS**  
**QA build:** `0.4.0-preqa.15` / `41500`

## Physical finding that triggered this package

The owner preserved two different client states intentionally:

- phone: original PC state;
- emulator: locally modified PC state that had not been uploaded.

Running the `preqa.14` QA sync on the emulator produced a deterministic conflict for PC `ac0c0936-577c-421a-a26f-2db459c854e2` (`HBT PJ Test 2 B OFFLINE`):

- conflict reason: `LOCAL_AND_HOSTED_CHANGED`;
- local sync revision: `7`;
- hosted revision: `8`;
- baseline present: `YES`;
- baseline revision: `7`;
- local differs from baseline: `YES`;
- local equals current hosted: `NO`;
- pending outbox mutation: `NO`;
- conflict observed in both `INITIAL_PULL` and `FINAL_PULL`.

This is evidence that the PR #39 no-silent-overwrite protection worked: the client correctly refused to upload revision-7 local data blindly over a different hosted revision 8. The missing product/QA capability was an explicit user-controlled conflict-resolution action.

Neither the immediately preceding phone QA run nor this emulator QA run created hosted revision 8: both reported zero queued/acknowledged mutations. The origin of revision 8 is not required to resolve the known state safely.

## Implemented explicit keep-local resolution

`preqa.15` adds a debug-QA action:

`Resolver conflicto: conservar PC local`

The action is deliberately separate from ordinary synchronization and requires explicit confirmation. It only accepts exactly one reviewed `FINAL_PULL / LOCAL_AND_HOSTED_CHANGED` conflict.

Before queueing anything it verifies that:

- the local PC is not tombstoned;
- local sync revision still equals the diagnosed local revision;
- the stored baseline still exists and matches that local revision;
- the local PC still differs from the baseline;
- no pending PC snapshot mutation already exists for that PC;
- no unrelated READY outbox mutation would be delivered at the same time;
- the hosted PC still exists and is not tombstoned;
- a fresh server read still reports **exactly the hosted revision that the owner reviewed**;
- local and current hosted snapshots still differ.

Only then does it queue the local snapshot using the reviewed hosted revision as `expectedRevision` and deliver through the normal durable outbox. Therefore the hosted compare-and-swap contract remains active. If another client advances the hosted PC again between re-check and PUT, the stale expected revision prevents a silent overwrite.

On successful delivery, normal acknowledgement advances local hosted metadata and the normalized baseline atomically to the resulting server revision.

## Automated evidence

A focused shared regression proves the core transition:

- local metadata revision `7`;
- explicit local-wins queue against reviewed hosted revision `8`;
- queued payload uses `expectedRevision=8` while local metadata remains `7` before acknowledgement;
- simulated hosted acceptance returns revision `9`;
- acknowledgement removes the mutation and advances local sync metadata + baseline to revision `9` with the uploaded normalized snapshot.

Exact implementation head `008a73c20101a127edd82947af71c6024f894609` passed Scaffold `35044956294`, including shared tests, Android build, backend type-check, hosted DB contracts and debug APK upload.

## Exact next physical gate

Use the **emulator only** first. Do not synchronize or edit the clean phone yet.

1. Install `0.4.0-preqa.15` over the existing emulator installation. **Do not uninstall and do not clear app data.**
2. Open `DnD Aid - QA DEV`.
3. Tap `Ejecutar sincronización QA`.
4. Confirm the log still contains the final-pull `LOCAL_AND_HOSTED_CHANGED` conflict for the modified PC. If the hosted revision is no longer the reviewed revision, stop and inspect the new log instead of resolving blindly.
5. Tap `Resolver conflicto: conservar PC local`.
6. Read the confirmation and choose `Sí, conservar local`.
7. The action re-runs QA automatically. Tap `Copiar log QA` and paste the entire resulting log into the technical-assistant chat.

Expected success evidence is an appended section similar to:

`=== LAST EXPLICIT KEEP-LOCAL RESOLUTION ===`

`SUCCESS | ... | reviewedHostedRevision=8 | resultingRevision=9`

followed by a clean refreshed sync with no PC conflict and an empty outbox.

If the result is `REFUSED`, `PENDING` or `FAILURE`, do not retry destructively; share the complete log. Local state is designed to remain preserved.

Only after emulator success is reviewed should the phone synchronize. The phone should then exercise the separate `server-newer + clean local` convergence case by receiving the emulator-selected hosted version.

## Boundaries

This package does not:

- automatically choose local or hosted data;
- treat an empty outbox as proof of local cleanliness;
- clear or discard conflict mutations automatically;
- rebuild an ambiguous baseline;
- reset the database or delete local PCs;
- weaken stale-revision/CAS protection;
- implement membership revoke or Player/DM authorization enforcement.

PR #40 remains open pending this physical result.
