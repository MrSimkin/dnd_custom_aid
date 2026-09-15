# Checkpoint — Android hosted campaign + PC sync batch complete

**Date:** 2026-09-15 (Chile local time)  
**Status:** COMPLETE / VERIFIED / OWNER-PHYSICAL PASS  
**Integrated PR:** #34 — `android: deliver campaign and PC state to hosted server`  
**Exact final tested PR head:** `ff7d96d6d5806fcf9969490d288c0d25b00d62fe`  
**Merged `main` commit:** `75d5acf354b41185255ff7d1a5eb4a689f300721`  
**Exact-head PR Scaffold run:** `35027987125` / #1939 — **SUCCESS**  
**Post-merge Scaffold run:** `35028893643` / #1940 — **SUCCESS**

## Purpose

This checkpoint closes the next accumulated Wave 4 Player <-> Server slice after the previously proven Android session and hosted campaign bootstrap edges.

The ordinary Android Player now uses the existing shared local-first hosted contracts for campaign creation/delivery and PC snapshot push/pull rather than only read/bootstrap.

## What is integrated

- local campaign creation remains immediately durable and usable;
- campaign creation is atomically paired with a durable hosted outbox mutation;
- authenticated sync delivers campaign mutations idempotently and reads hosted state back;
- local PC snapshots are queued only when new or actually changed relative to the same hosted revision;
- hosted PC snapshots can be pulled into the same stable local PC identity;
- successful PC delivery advances local hosted revision state and acknowledges/removes the durable outbox mutation;
- unchanged repeat sync does not manufacture a new PC revision/mutation;
- local data remains non-destructive on auth/network/provider failure;
- the normal Player reports hosted delivery/reconciliation state while the debug-only hosted-auth activity retains diagnostic instrumentation.

## Physical Android proof — accepted

The owner physically exercised the accumulated batch on Android.

The accepted sequence covered:

1. local campaign creation in the normal Player;
2. durable hosted campaign delivery and read-back;
3. creation/synchronization of a PC snapshot;
4. modification of that PC and a second hosted synchronization;
5. preservation of existing campaigns/characters;
6. diagnosis of one real blocked PC mutation found during testing;
7. confirmation that the blocked row was `PC_SNAPSHOT_PUT | BLOCKED | attempts=1 | expectedRevision=0 | error=VALIDATION_FAILED`;
8. repair of the hosted JSON wire serializer so default-valued character-backup `format` and `version` fields are emitted rather than omitted;
9. automated regression coverage for that exact wire-envelope defect;
10. safe reactivation of the same preserved blocked mutation rather than deleting/recreating local state;
11. successful server acceptance/acknowledgement of that original mutation;
12. debug outbox confirmation that no pending or blocked mutation remained;
13. another unchanged Player synchronization followed by another empty-outbox confirmation.

Therefore the accumulated campaign + PC synchronization gate is **PASS**.

## Defect found and repaired during the gate

`CharacterBackupDocument` has default-valued `format` and `version` properties. The hosted Ktor JSON configuration did not explicitly enable default-value encoding, while the Worker correctly required those fields to exist on the wire.

The first real PC request was therefore locally valid but reached the Worker without part of its required envelope and received `VALIDATION_FAILED`.

The repair keeps Worker validation strict and changes the hosted client wire serializer to emit defaults. Android and Desktop hosted HTTP serializers now use that same contract, and automated regression coverage asserts the required snapshot envelope is present.

The durable outbox behavior also proved useful: the failed mutation remained preserved as `BLOCKED`, could be diagnosed, then safely marked retryable after the contract repair and acknowledged after successful delivery.

## Architecture boundary preserved

The active path is now:

```text
ordinary Android Player
        |
        | local-first campaign / PC state
        v
durable SQLDelight hosted outbox
        |
        | remembered Descope session
        v
shared HostedApiClient
        |
        v
Cloudflare Worker
        |
        v
Neon PostgreSQL
        |
        v
hosted read-back + conflict-preserving local reconciliation
```

No second authentication/network/sync stack was introduced.

## Exact continuation

Wave 4 now has the following completed steps:

```text
remembered Android Descope session/token           COMPLETE
existing HostedAccessTokenProvider                 COMPLETE
owner-facing hosted account/campaign bootstrap     COMPLETE
campaign create + durable hosted delivery          COMPLETE
PC snapshot push/pull + no-op suppression          COMPLETE
        |
        v
second-client observation                          NEXT BATCH
        +
offline edit / reconnect / convergence safety      NEXT BATCH
        |
        v
membership revoke + Player/DM authorization        FOLLOWING BOUNDARY
```

The next implementation batch must harden multi-client convergence before asking the owner for another physical test.

In particular, preserve an unsent local PC edit when the hosted PC has advanced on another client. A server-newer revision must not silently overwrite concurrent local state merely because the local sync revision is older. Add enough local baseline knowledge to distinguish a clean old copy from a locally modified old copy, automatically apply a server-newer snapshot only when the old local copy is still clean, and surface an explicit conflict when both sides changed.

The next physical gate should then batch:

- observation of the same hosted campaign/PC from a second client state;
- offline local edit followed by reconnect and successful delivery when the server did not change;
- remote-newer clean-local convergence;
- concurrent local + remote edit preservation as an explicit conflict rather than silent overwrite.

Membership revoke and Player/DM authorization validation remain the next separate authorization-focused boundary.
