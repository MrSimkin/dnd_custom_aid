# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign bootstrap:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign + PC sync:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Unchanged-sync/no-op confirmation:** **OWNER-PHYSICAL PASS**  
**Multi-client PC convergence safety:** **IMPLEMENTED / AUTOMATED VERIFIED / PHYSICAL QA CONTINUES**  
**Convergence base PR:** #39  
**QA/recovery/conflict-resolution PR:** #40  
**QA build:** `0.4.0-preqa.15` / `41500`  
**Verified behavior head for APK:** `008a73c20101a127edd82947af71c6024f894609`  
**Scaffold:** `35044956294` — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-15_HOSTED_PC_EXPLICIT_CONFLICT_RESOLUTION_READY_FOR_PHYSICAL_QA.md` — **current Wave 4 checkpoint and exact next owner action**;
3. `docs/checkpoints/2026-09-15_HOSTED_SYNC_QA_LOG_AND_LEGACY_BASELINE_RECOVERY_READY_FOR_PHYSICAL_QA.md` — permanent QA log + legacy-baseline recovery package;
4. `docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md` — PR #39 convergence design and original physical gate;
5. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md` — completed campaign/PC delivery physical evidence;
6. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_BOOTSTRAP_COMPLETE.md` — completed ordinary-Player hosted bootstrap proof;
7. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` — completed Android hosted-session edge;
8. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — hosted DEV provider/environment evidence;
9. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
10. `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/ROADMAP.md`, `docs/ARCHITECTURE.md` and `docs/TESTING.md` as needed.

If older operational prose conflicts with this file or the current specific checkpoint, the newer specific checkpoint controls unless an even later approved decision/checkpoint supersedes it.

## Current Wave 4 state

```text
remembered Android Descope session/token             COMPLETE
owner-facing hosted account/campaign bootstrap       COMPLETE
campaign create + durable hosted delivery            COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery         COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync no-op confirmation                    COMPLETE / OWNER-PHYSICAL PASS
        |
        v
multi-client PC convergence safety                    IMPLEMENTED / AUTOMATED VERIFIED
        |
        v
permanent hosted-sync QA log                          IMPLEMENTED / PHYSICALLY USEFUL
        |
        v
legacy missing-baseline safe equal-state recovery     IMPLEMENTED / AUTOMATED VERIFIED
        |
        v
real emulator concurrent state observed               LOCAL_AND_HOSTED_CHANGED / PROTECTION WORKED
        |
        v
explicit user-controlled keep-local resolution        IMPLEMENTED / AUTOMATED VERIFIED
        |
        v
EMULATOR KEEP-LOCAL PHYSICAL GATE                     NEXT
        |
        v
PHONE SERVER-NEWER + CLEAN-LOCAL CONVERGENCE          AFTER EMULATOR SUCCESS
        |
        v
remaining multi-client conflict scenarios             THEN
        |
        v
membership revoke + Player/DM authorization          FOLLOWING SEPARATE BOUNDARY
```

## Decisive physical evidence from `preqa.14`

The owner kept the phone PC in its original state and an emulator PC in a locally modified, never-uploaded state. The emulator QA sync reported the same conflict in both initial and final pull:

- `LOCAL_AND_HOSTED_CHANGED`;
- local sync revision `7`;
- hosted revision `8`;
- baseline present `YES` at revision `7`;
- local differs from baseline `YES`;
- local equals current hosted `NO`;
- no pending outbox mutation.

This is **not evidence that conflict detection failed**. It is evidence that the PR #39 no-silent-overwrite protection worked: a revision-7 local edit was not blindly written over a different hosted revision 8. What was missing was an explicit, user-controlled way to choose the local version after reviewing that conflict.

The immediately preceding phone QA run and the emulator QA run both queued/acknowledged zero hosted mutations, so neither created hosted revision 8. Its older origin is not required for safe resolution of the current known state.

## Explicit keep-local resolution in `preqa.15`

The debug QA console now exposes:

`Resolver conflicto: conservar PC local`

It is never automatic. It only accepts one reviewed `FINAL_PULL / LOCAL_AND_HOSTED_CHANGED` conflict. Before queueing, it revalidates local revision/baseline state, local dirtiness, outbox isolation, hosted existence/tombstone status and a fresh hosted read. The hosted revision must still equal the exact revision the owner reviewed.

The selected local snapshot is then queued durably using that reviewed hosted revision as `expectedRevision`. Server compare-and-swap protection therefore remains active. If another client advances again before PUT, stale-revision protection prevents a silent overwrite.

A focused shared regression proves the intended `local 7 -> reviewed hosted 8 -> accepted hosted 9` acknowledgement path, including atomic sync-metadata/baseline advancement. Exact behavior head `008a73c20101a127edd82947af71c6024f894609` passed Scaffold `35044956294`, including shared tests, Android build, backend type-check, hosted DB contracts and debug APK upload.

## Permanent QA console

Debug builds expose `DnD Aid - QA DEV`. Hosted QA synchronization produces a copyable/shareable plain-text report with campaign eligibility, exact PC conflict reasons, revisions, baseline state, local-vs-baseline/current-hosted comparison, pull phase and outbox state.

The log excludes JWTs, refresh tokens, authorization headers, provider secrets and database credentials. Future physical gates should extend this structured diagnostic surface instead of creating unrelated one-off diagnostics.

The active campaign selector is local Player context only. Hosted synchronization reviews **all eligible hosted campaigns**.

## Exact next owner gate

Use the emulator only first. Do not edit or synchronize the clean phone yet.

1. Install `0.4.0-preqa.15` over the existing emulator installation. **Do not uninstall and do not clear app data.**
2. Open `DnD Aid - QA DEV`.
3. Tap `Ejecutar sincronización QA`.
4. Confirm the final-pull `LOCAL_AND_HOSTED_CHANGED` conflict is still present. If hosted revision changed, inspect/share the new log instead of resolving against stale evidence.
5. Tap `Resolver conflicto: conservar PC local` and confirm `Sí, conservar local`.
6. The app re-runs QA automatically. Tap `Copiar log QA` and paste the entire result into the technical-assistant chat.

Expected success evidence includes:

`=== LAST EXPLICIT KEEP-LOCAL RESOLUTION ===`

`SUCCESS | ... | reviewedHostedRevision=8 | resultingRevision=9`

plus a refreshed clean synchronization and empty outbox.

If the action reports `REFUSED`, `PENDING` or `FAILURE`, do not clear/reset/retry destructively; share the complete log. Local state remains protected.

Only after this emulator result is reviewed should the phone synchronize to exercise `server-newer + clean local` convergence.

PR #40 remains open pending physical evidence.

## Important corrections carried forward

- The repository is intentionally **public** under D-0075.
- External-service operating budget remains **USD $0** unless the owner explicitly changes it.
- Neon + Descope + Cloudflare DEV activation is complete; do not restart provider activation.
- Android remembered Descope session and `HostedAccessTokenProvider` are complete; do not create a second authentication/network abstraction.
- Ordinary Player hosted account/campaign bootstrap is complete.
- Campaign local-first creation + durable hosted delivery and PC push/pull are complete.
- The prior PC wire-envelope `VALIDATION_FAILED` defect was repaired; its blocked mutation was recovered and acknowledged.
- The earlier unchanged-sync/no-op physical gate is PASS.
- The real Neon database name is `dnd-custom-aid-dev` with hyphens.
- Secret hygiene remains strict regardless of repository visibility.

## Security residuals carried forward

Important follow-up topics remain:

- JWT/fail-closed verification review;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log secret leakage;
- replay/idempotency authorization;
- exact investigation of the locally reported **3 high severity npm vulnerabilities** — do not run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role;
- production-region/identity configuration review before release.

These residuals do not reopen completed provider/session/bootstrap/campaign/PC packages.

## Historical Player evidence remains bounded

Current integrated CI and hosted physical proofs do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve historical QA evidence for exactly what it tested.
