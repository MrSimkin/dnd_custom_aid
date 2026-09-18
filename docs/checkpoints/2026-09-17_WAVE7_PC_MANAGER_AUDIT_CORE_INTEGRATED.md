# Checkpoint — Wave 7 Desktop PC Manager inspection/audit core integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `e14784390971f2e27025dd2fff1f5000658eb2f0`  
**PR:** #77 — `feat: add Desktop PC Manager audit core`  
**Initial implementation head:** `2d792abaa843734acd2a1226f91d2e86e7b2b929`  
**Final implementation head:** `50132cdb295097ac4a7ab91c4d766b900eeb7771`  
**Initial push Scaffold:** `35288970002` — FAILED (Kotlin visibility mismatch)  
**Corrected push Scaffold:** `35289198415` — SUCCESS  
**PR Scaffold:** `35289424011` — SUCCESS  
**Post-merge Scaffold:** `35289703289` — SUCCESS

## Milestone status

The bounded **Desktop PC Manager / Audit — inspection and audited correction core** is integrated into `main`.

The implementation deliberately reuses the canonical Player PC repositories, authority spine, synchronization metadata, durable hosted baseline, outbox and reconciliation checkpoints. No parallel Desktop character model or new local schema was introduced.

The next bounded Wave 7 package is **PC Manager ownership/controller administration core**.

## Integrated PC Manager behavior

The reserved `Personajes jugadores` Desktop destination is now active for the selected campaign.

The Manager provides:

- campaign PC browse/search;
- complete inspection of the canonical `CharacterSheet`;
- inspection of the existing Closure aggregate;
- inspection of the existing Successor/provenance aggregate;
- visible owner and controller authority;
- visible local data freshness;
- visible sync revision;
- visible durable hosted baseline revision;
- visible pending PC outbox state and last error evidence where present;
- reconciliation/history review.

PC ownership, current controller and campaign DM role remain separate concepts.

## Audited DM correction

The first correction path is intentionally bounded to core PC fields rather than duplicating the Player editor.

An explicit `Correct / Edit as DM` action:

- requires one active DM membership in the PC campaign;
- preserves PC identity and campaign identity;
- preserves owner and controller authority;
- refuses locally tombstoned PCs;
- refuses to stack another hosted PC snapshot mutation while an earlier mutation still requires resolution;
- saves the canonical local Character aggregate;
- appends a `Corrección DM` reconciliation checkpoint;
- records the DM-entered reason and a human-readable summary of changed fields;
- exports the resulting canonical aggregate;
- queues the existing hosted PC snapshot mutation using the current synchronization revision.

This is a compensating correction path with preserved evidence, not silent Player impersonation.

## Validation evidence

Focused coverage verifies:

- campaign PC retrieval/search;
- active-DM resolution;
- canonical authority inspection;
- local-vs-sync freshness projection;
- correction preserves owner/controller authority;
- correction appends reconciliation history;
- correction queues the updated full snapshot at the current hosted revision;
- non-DM correction rejection;
- rollback/no partial correction when another PC snapshot mutation already needs resolution.

The first implementation push exposed one compiler-only visibility defect: a public controller method returned the internal `DesktopPcDetails` UI model. Commit `50132cdb295097ac4a7ab91c4d766b900eeb7771` aligned method visibility without changing package behavior or scope.

Exact evidence:

- initial implementation head `2d792abaa843734acd2a1226f91d2e86e7b2b929`;
- initial push Scaffold `35288970002` — FAILED on the visibility mismatch;
- final implementation head `50132cdb295097ac4a7ab91c4d766b900eeb7771`;
- corrected push Scaffold `35289198415` — SUCCESS;
- PR Scaffold `35289424011` — SUCCESS;
- PR #77 merged as `e14784390971f2e27025dd2fff1f5000658eb2f0`;
- post-merge Scaffold `35289703289` — SUCCESS, including backend, hosted database, shared/Desktop tests, Android debug assembly and APK upload.

## Preserved boundaries

This slice does **not** add:

- a second Desktop character-builder/model;
- a new local audit schema or generalized event-sourcing framework;
- automatic ownership/controller changes;
- freeze/unfreeze semantics;
- broader PC lifecycle administration;
- PC duplication;
- PDF export;
- hosted reusable-content synchronization;
- provider deployment changes.

## Next package — ownership/controller administration

Repository evidence for the next bounded gap:

- hosted PC rows/snapshots already store distinct nullable owner/controller IDs;
- local `IntegratedSpineRepository.setPcAuthority` already validates active campaign membership;
- the hosted API can read those fields through campaign PC snapshots;
- there is no dedicated hosted mutation for DM authority administration.

The next package should therefore add explicit DM-only owner/controller administration:

- independent owner/controller assignment;
- explicit unassignment where allowed by the existing nullable model;
- only active same-campaign members are eligible;
- fail closed for non-DM actors, inactive/cross-campaign targets and missing/tombstoned PCs;
- authoritative hosted response drives local authority convergence;
- shared client and Desktop controls;
- backend/database/shared/Desktop tests.

Do not infer owner/controller from DM role and do not mutate PC sheet contents as an authority side effect.

Freeze/unfreeze, broader lifecycle administration, duplication and D-0074 PDF export remain later PC Manager responsibilities.

## Provider/cost state

No Cloudflare, Descope, Neon deployment or object-storage action was required for the inspection/audit core. Hard external-service budget remains USD $0.
