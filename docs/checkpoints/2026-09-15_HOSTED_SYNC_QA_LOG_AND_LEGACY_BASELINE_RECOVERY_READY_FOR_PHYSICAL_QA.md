# Hosted sync QA log + legacy baseline recovery — ready for physical QA

**Date:** 2026-09-15 (Chile local time)  
**Package:** Wave 4 multi-client PC convergence follow-up  
**Starting `main`:** `94d27dda71f87cbb6167886b2d55f2b3fd1120dc` (PR #39 merge)  
**Implementation branch:** `wave4/qa-log-baseline-recovery`  
**PR:** #40 — `qa: add reusable sync log and safe legacy baseline recovery`  
**QA build:** `0.4.0-preqa.14` / versionCode `41400`  
**Status:** **IMPLEMENTED / AUTOMATED VERIFIED / OWNER-PHYSICAL QA NEXT**

## Why this follow-up exists

The first physical multi-client convergence attempt after PR #39 did not produce enough evidence for a PASS/FAIL verdict.

Observed on the existing Android phone:

- hosted sync reported one local conflict preserved without overwrite;
- the local hosted outbox diagnostic was empty;
- the generic UI did not expose the exact PC conflict reason;
- campaign selection wording made it appear that only the first/active campaign could be synchronized.

The physical convergence gate therefore remains **inconclusive**, not failed and not passed.

## Confirmed architecture facts

The locally selected/active campaign and hosted synchronization scope are different concepts:

- the radio-selected campaign controls local Player context;
- hosted synchronization processes all hosted campaigns that the membership bootstrap marks eligible.

An empty outbox also does not rule out a pull-side conflict. `AndroidHostedCampaignBootstrapController.refresh()` performs an initial PC pull before deciding whether a local PC snapshot should be queued. If the hosted revision is already newer and the initial pull reports a conflict, the queue loop deliberately skips that revision-mismatched PC, so no outbox row is required for the conflict to exist.

## Root-cause theory and bounded repair

The strongest theory is the explicit legacy-upgrade path introduced with PR #39:

1. an existing client already has hosted revision metadata;
2. durable PC sync baselines did not exist on that client before the PR #39 schema;
3. another client advances the hosted PC before the legacy client gets one equal-revision synchronization;
4. the legacy client then has `baseline == null` while `hostedRevision > localRevision`;
5. the convergence layer conservatively reports `SYNC_BASELINE_MISSING` rather than guessing whether the local aggregate contains an unsent edit.

That conservative behavior remains correct when local and hosted content differ. An empty outbox is not proof that the local aggregate was never edited offline.

This package adds one safe automatic recovery case:

> When the historical baseline is missing, the hosted revision is newer, and the complete normalized local PC aggregate is already identical to the complete normalized hosted aggregate, the client may safely accept the newer hosted revision and establish the missing durable baseline.

No information can be lost in that case because the two complete states already agree. Export timestamps are normalized out of the comparison.

If local and hosted aggregates differ, the client still reports `SYNC_BASELINE_MISSING` and preserves the local state. It does **not** infer cleanliness from an empty outbox.

## Permanent QA/debug facility

The debug Android package now has a reusable owner QA console exposed as the launcher:

`DnD Aid - QA DEV`

The facility is intentionally persistent and should be extended for future physical QA packages rather than replaced with one-off diagnostic screens.

For hosted synchronization it currently records:

- app version/build identity;
- hosted / eligible / applied campaign counts;
- campaign conflict count and per-campaign eligibility state;
- hosted / applied / unchanged / tombstoned PC counts;
- queued / acknowledged / retryable / blocked mutation counts;
- PC conflicts from both the **initial pull** and **final pull**;
- exact `HostedPcPullConflictReason`;
- local and hosted revisions;
- baseline presence and revision;
- whether local differs from baseline;
- whether local equals current hosted state;
- whether a pending PC outbox mutation exists and its retry state;
- current hosted outbox diagnostic.

The QA console provides:

- `Ejecutar sincronización QA`
- `Copiar log QA`
- `Compartir log QA`

The plain-text report explicitly excludes JWTs, refresh tokens, authorization headers, provider secrets and database credentials.

## Campaign UX clarification

The normal Campaigns screen now states that the active campaign is for local use and that hosted sync reviews all eligible hosted campaigns.

The action is labeled:

`Sincronizar campañas hospedadas`

The selected campaign card is labeled:

`Campaña activa para uso local`

Generic sync status also separates campaign conflicts from PC conflicts instead of collapsing both into one unexplained count.

## Automated verification

The implementation head `cf11c88cf7a424793c40f3d7bcb57b859e058b1e` completed Scaffold run `35043186839` successfully:

- backend type-check — PASS;
- hosted database migrations/contracts — PASS;
- Player guard scripts — PASS;
- Shared desktop tests — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- Android debug APK upload — PASS.

The shared regression suite includes both sides of the legacy-baseline boundary:

- differing local/hosted state + missing baseline + hosted newer -> `SYNC_BASELINE_MISSING`, local state/revision preserved;
- identical normalized local/hosted state + missing baseline + hosted newer -> safe convergence, local revision advances and baseline is recorded.

A later launcher-label-only commit renamed the debug launcher to `DnD Aid - QA DEV`; the branch's final Scaffold run should remain green before the APK is handed to the owner.

## Safety invariants preserved

This package does **not**:

- auto-select local vs hosted when states differ;
- treat an empty outbox as proof of cleanliness;
- rebuild an ambiguous baseline;
- clear or rewrite outbox state;
- delete local PCs;
- reset/reinstall application data;
- silently overwrite local edits;
- bundle membership-revoke enforcement;
- bundle Player/DM authorization enforcement.

Stable IDs, stale-revision checks, idempotency, tombstones, local recovery, DM authority vs PC ownership and owner vs controller semantics remain in force.

## Exact next owner physical step

Install `0.4.0-preqa.14` **over the existing phone installation without uninstalling or clearing app data**. The retained legacy state is part of what this test is meant to diagnose.

Then:

1. open the launcher `DnD Aid - QA DEV`;
2. use the remembered hosted session, or authenticate if Android requires it;
3. tap `Ejecutar sincronización QA`;
4. tap `Copiar log QA`;
5. paste the complete log into the active ChatGPT technical-assistant conversation.

Do not manually clear the outbox, reset the database, recreate campaigns/PCs or reinstall before this first diagnostic sync.

## How the next log will be interpreted

- **No PC conflict / expected convergence:** continue the consolidated physical multi-client scenarios from the now-observable state.
- **`SYNC_BASELINE_MISSING` + local equals hosted = NO:** ambiguity is real; preserve both states and decide a deliberate recovery path from the evidence.
- **`LOCAL_AND_HOSTED_CHANGED`:** true concurrent divergence is being detected; verify preservation and conflict UX.
- **baseline mismatch / hosted changed without revision / other reason:** diagnose that exact invariant from the logged revisions and baseline evidence before changing convergence policy.

The PR #39 multi-client owner gate remains **pending** until this QA build is physically exercised. Automated green alone is not physical acceptance.

## Following boundary

Membership revoke plus Player/DM authorization enforcement remains the next separate Wave 4 boundary and must not be bundled into this convergence QA package.
