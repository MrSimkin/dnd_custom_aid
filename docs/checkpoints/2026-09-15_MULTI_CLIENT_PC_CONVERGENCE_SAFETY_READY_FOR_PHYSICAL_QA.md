# Checkpoint — Multi-client PC convergence safety ready for physical QA

**Date:** 2026-09-15 (Chile local time)  
**Status:** IMPLEMENTED / AUTOMATED VERIFIED / OWNER-PHYSICAL QA PENDING  
**Implementation PR:** #39 — `sync: protect multi-client PC convergence`  
**Exact verified PR head:** `c1b3b4d55e9e0ee8b66825c7a248c3198dab74fa`  
**First accumulated Scaffold gate:** `35034261353` / #2012 — **SUCCESS**  
**Exact-head Scaffold gate after two-client sequence:** `35034497843` / #2014 — **SUCCESS**

## Purpose

This Wave 4 batch hardens the existing project-specific local-first PC synchronization model for multiple clients without introducing a second sync architecture.

The critical invariant is now enforced client-side:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

## Implemented convergence model

Each synchronized local PC now keeps a durable normalized copy of its **last synchronized baseline** together with the already-existing hosted revision metadata.

That gives reconciliation enough evidence to distinguish:

- **clean old local copy** — current local PC still equals its last synchronized baseline, so a newer hosted snapshot may safely replace it;
- **concurrent local + hosted changes** — current local PC differs from its baseline while the hosted revision also advanced, so local state is preserved and an explicit conflict is returned;
- **offline local edit with unchanged server** — equal hosted revision leaves the local edit intact, after which the normal durable outbox can deliver it;
- **fresh/second client** — hosted state creates the same stable local PC identity and immediately establishes its synchronized baseline.

The normalized baseline ignores `exportedAtEpochSeconds`, so ordinary backup/export timestamps do not create false dirty-state conflicts.

## Durability and crash consistency

Successful PC delivery now atomically performs all three local acknowledgement effects in the same SQLDelight transaction:

1. advance the local hosted revision metadata;
2. persist the normalized last-synchronized PC baseline at that authoritative revision;
3. remove the acknowledged durable outbox mutation.

A crash therefore cannot intentionally leave the normal acknowledgement path with an advanced revision but stale baseline, or with a new baseline while the mutation remains pending.

The schema addition is local-only:

`pc_sync_baseline(pc_id, revision, snapshot_json)`

Migration `17.sqm` upgrades the current local database schema to add that table while preserving existing character data.

## Legacy upgrade behavior

A device upgrading from the earlier schema naturally has revision metadata but no historical baseline snapshot.

The client does **not** guess that such a local PC is clean when the server is already newer. In that ambiguous case it preserves the local PC and reports `SYNC_BASELINE_MISSING` rather than overwriting data.

When local and hosted revisions are still equal, the hosted snapshot can safely establish the missing baseline without overwriting the current local aggregate. Any differing local aggregate then remains an unsent local edit and follows the normal outbox path.

## Conflict behavior

The shared reconciliation layer now distinguishes explicit PC conflict reasons including:

- `LOCAL_REVISION_AHEAD`;
- `LOCAL_TOMBSTONE`;
- `LOCAL_AND_HOSTED_CHANGED`;
- `SYNC_BASELINE_MISSING`;
- `SYNC_BASELINE_REVISION_MISMATCH`;
- `HOSTED_CHANGED_WITHOUT_REVISION`.

For concurrent local + remote edits, the current local PC, its sync revision and its previous baseline are left unchanged. No automatic merge or destructive overwrite is attempted.

The existing ordinary Player server-status surface now includes PC convergence conflicts in its visible count of local conflicts preserved without overwrite, so the conflict is not only an internal shared-layer result.

## Tombstones and authority boundaries preserved

This batch preserves the prior semantics:

- local tombstones block stale hosted resurrection;
- a hosted deletion keeps the local character copy for recovery rather than destructively removing it;
- clean hosted deletion still advances synchronized tombstone metadata;
- stable campaign/PC IDs are preserved;
- mutation IDs and optimistic revisions remain the existing idempotency/stale-write mechanism;
- DM campaign authority remains distinct from PC ownership;
- PC owner remains distinct from current controller;
- no generalized CRDT/event-sourcing/realtime architecture was added.

## Automated evidence

Focused tests now cover:

- successful hosted PC acknowledgement advancing revision + baseline + outbox atomically;
- server-newer + clean local -> safe hosted application;
- equal-revision offline local edit -> local preservation for normal later delivery;
- legacy equal-revision baseline bootstrap without overwriting a local edit;
- server-newer with missing legacy baseline -> conservative explicit conflict;
- concurrent local + hosted edits -> `LOCAL_AND_HOSTED_CHANGED`, local state preserved;
- local tombstone non-resurrection;
- clean hosted deletion with non-destructive local recovery;
- fresh second-client pull preserving the same stable PC identity and establishing a baseline;
- local database migration 17 -> 18 preserving existing character data.

A dedicated two-client regression uses two independent local databases and one mutable hosted snapshot to execute this sequence end-to-end:

```text
Client A establishes revision-0 baseline
        |
        v
fresh Client B pulls same stable PC identity
        |
        v
Client B edits offline while server stays revision 0
        |
        v
Client B reconnects and delivers normally -> server revision 1
        |
        v
clean Client A safely converges to revision 1
        |
        +---- Client A makes an unsent local edit
        |
        +---- Client B makes and delivers another edit -> server revision 2
        |
        v
Client A pull => explicit LOCAL_AND_HOSTED_CHANGED
                 local Client A data preserved
                 local revision/baseline remain revision 1
```

Automated gates:

- Scaffold `35034261353` / #2012 — **SUCCESS** after baseline/migration/reconciliation implementation;
- Scaffold `35034497843` / #2014 — **SUCCESS** at exact head `c1b3b4d55e9e0ee8b66825c7a248c3198dab74fa` after the two-client sequence test.

Both aggregate gates include permanent Player guards, shared/Kotlin tests, Android debug assembly, Desktop build, backend checks, hosted PostgreSQL contracts and APK artifact upload.

## Next owner physical gate

Do not split this into repeated install/test cycles. The next owner test is one consolidated physical boundary covering together:

1. the same hosted campaign/PC observed from a second client state;
2. offline local edit -> reconnect -> successful delivery when remote did not change;
3. server-newer + clean local -> safe convergence;
4. concurrent local + remote edits -> explicit conflict with the local edit preserved.

The first synchronization after installing this schema on an existing client may establish a missing equal-revision baseline; that is expected upgrade behavior and should be performed before intentionally creating the concurrent-edit scenario.

## Following boundary — not part of this gate

Membership revoke and Player/DM authorization enforcement remain the next separate Wave 4 boundary after this physical convergence gate.

Do not bundle that authorization work into the current owner test.
