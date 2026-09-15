# Handoff — Android hosted campaign + PC sync after physical recovery

**Date:** 2026-09-15 (Chile local time)  
**Status:** IMPLEMENTATION COMPLETE / CI PASS / OWNER-PHYSICAL RECOVERY PASS / ONE RESIDUAL MANUAL NO-OP CHECK CARRIED FORWARD  
**Integrated PR:** #34 — `android: deliver campaign and PC state to hosted server`  
**Exact final PR head:** `ff7d96d6d5806fcf9969490d288c0d25b00d62fe`  
**Merged `main` commit:** `75d5acf354b41185255ff7d1a5eb4a689f300721`  
**Exact-head PR Scaffold run:** `35027987125` / #1939 — **SUCCESS**  
**Post-merge Scaffold run:** `35028893643` / #1940 — **SUCCESS**

## Why this handoff exists

This file is the precise continuation record after the first real Android hosted campaign + PC synchronization batch and the physical defect/recovery loop.

It also corrects one overstatement in the earlier completion checkpoint: the owner confirmed the recovered blocked PC mutation was acknowledged and the outbox became empty, but did **not** separately report the final unchanged repeat-sync + second empty-outbox observation before asking to consolidate and move chats.

Therefore:

- campaign delivery + PC snapshot delivery/recovery are physically proven;
- the serializer repair is physically proven against the same preserved failed mutation;
- the durable outbox recovery path is physically proven;
- the final unchanged-repeat no-op behavior is integrated and CI-covered, but its **owner physical repeat-sync confirmation remains pending** and should be carried into the next physical gate rather than falsely recorded as already observed.

If this file conflicts with `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md` on that one final owner-observation detail, this handoff controls.

## Integrated behavior

PR #34 integrated the ordinary Android Player with the already-existing shared hosted contracts:

- local-first campaign creation;
- atomic durable outbox enqueue for hosted campaign delivery;
- idempotent authenticated campaign delivery and read-back;
- PC snapshot pull into stable local identity;
- PC snapshot push only when new/changed relative to the same hosted revision;
- durable PC snapshot outbox delivery;
- authoritative revision acknowledgement after successful hosted write;
- local data preservation across auth/network/provider failure;
- debug-only outbox diagnostics and safe recovery instrumentation.

No second authentication/network/sync architecture was introduced.

## Physical Android evidence actually observed

The owner exercised the accumulated Android batch and reported that synchronization kept showing one remaining local change.

The debug-only outbox diagnostic identified the preserved mutation exactly as:

```text
Outbox local: total=1, READY=0, BLOCKED=1
PC_SNAPSHOT_PUT | BLOCKED | intentos=1 | expectedRevision=0 | error=VALIDATION_FAILED
```

That proved the problem was not a transient network retry and not a stale-revision conflict. The Worker rejected the first PC snapshot payload as invalid and the durable outbox correctly preserved it as `BLOCKED` for diagnosis.

## Defect and repair

`CharacterBackupDocument` contains default-valued `format` and `version` properties.

The hosted Ktor JSON serializer had not enabled default-value encoding, while the Worker correctly required `snapshot.format` and `snapshot.version` to be present on the wire.

The local Android snapshot was therefore valid in memory, but the first HTTP payload could omit part of its required envelope and receive `VALIDATION_FAILED`.

The repair:

- keeps Worker validation strict;
- enables default-value encoding in the hosted Android and Desktop HTTP serializers;
- adds regression coverage for the required PC snapshot wire envelope;
- preserves the original blocked mutation rather than deleting/recreating local data;
- allows only the known compatible validation-blocked PC mutation to be safely returned to `READY` for retry through debug-only recovery tooling.

The owner then retried the same preserved mutation through the repaired build and reported:

```text
Outbox local: vacío.
No hay cambios hospedados pendientes ni bloqueados.
```

That is accepted physical evidence that the repaired mutation was successfully acknowledged/removed from the durable outbox after the hosted retry path.

## Residual owner check — not a blocker for the next development batch

One physical observation remains unreported:

1. make no campaign/PC change;
2. press `Sincronizar con servidor` once more in the normal Player;
3. open `DnD Aid - Hosted DEV Auth` -> `Diagnosticar outbox local`;
4. confirm the outbox remains empty.

This is the physical confirmation of unchanged-repeat no-op suppression.

Because the owner explicitly prefers batched development/testing, this residual check does **not** need to force a separate one-change/one-test cycle. Carry it into the next natural physical test session.

Do not clear app data merely to repeat this proof.

## Exact continuation

Wave 4 — Player <-> Server end-to-end — remains active.

Completed/integrated:

```text
remembered Android Descope session/token           COMPLETE
existing HostedAccessTokenProvider                 COMPLETE
owner-facing hosted account/campaign bootstrap     COMPLETE
campaign create + durable hosted delivery          COMPLETE
PC snapshot push/pull                              COMPLETE
blocked PC validation diagnosis/recovery           COMPLETE
unchanged-repeat no-op physical confirmation       CARRY-FORWARD MANUAL CHECK
```

The next implementation batch is **multi-client PC convergence safety**.

Do not immediately return to a one-development/one-physical-test cadence. Accumulate the closely related convergence work behind automated CI, then stop at the next meaningful physical boundary.

The next batch should harden these cases:

- fresh second-client observation of the same stable hosted campaign/PC identity;
- offline local edit followed by reconnect when the server did not change;
- server-newer state applied automatically only when the local old copy remained clean;
- concurrent local edit + remote advance preserved as an explicit conflict rather than silently overwritten;
- durable retry/idempotency/revision behavior across reconnect;
- retain local-first recovery and tombstone/non-resurrection guarantees.

The key correctness rule is:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

Add enough local knowledge of the last synchronized PC snapshot/revision to distinguish a clean old local copy from a locally modified old copy.

Membership revoke enforcement and Player/DM authorization validation remain the following separate boundary after this convergence batch.

## Test cadence requested by owner

The owner explicitly prefers:

```text
several related implementation steps
        +
automated CI at coherent boundaries
        +
one consolidated physical Android test session
```

Do not ask for an APK install after every small change unless a new high-risk boundary makes that necessary.

## Security and operational constraints carried forward

- external-service operating budget remains **USD $0** unless explicitly changed;
- repository is intentionally public;
- never commit secrets;
- do not read/copy/commit the owner's plaintext credential file outside the repo;
- keep Descope as identity proof and application-owned authorization on the server;
- keep local-first semantics, stable IDs, revisions, mutation idempotency, tombstones, explicit conflicts, DM authority vs PC ownership, and owner vs controller distinction;
- debug-only `DnD Aid - Hosted DEV Auth` remains verification infrastructure, not final login UX;
- object storage remains deferred;
- Workers AI remains later/conditional;
- existing security residuals in `docs/checkpoints/LATEST.md` remain open follow-up work.
