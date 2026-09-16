# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign bootstrap:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign + PC sync:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Unchanged-sync/no-op confirmation:** **OWNER-PHYSICAL PASS**  
**Multi-client PC convergence safety:** **COMPLETE / AUTOMATED VERIFIED / OWNER-PHYSICAL PASS**  
**Convergence base PR:** #39 — merged  
**QA/recovery/conflict-resolution PR:** #40 — **PACKAGE COMPLETE; CLOSURE/MERGE PENDING**  
**QA build:** `0.4.0-preqa.15` / `41500`  
**Verified behavior head for APK:** `008a73c20101a127edd82947af71c6024f894609`  
**Behavior-head Scaffold:** `35044956294` — **SUCCESS**  
**QA APK SHA-256:** `4615d1a9c8f9e87c2ebbc5ecb80e4a22f747384baa3091c1f05b6d90e4cac4f5`  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md` — **current Wave 4 completion checkpoint and exact closure/continuation**;
3. `docs/checkpoints/2026-09-15_HOSTED_PC_EXPLICIT_CONFLICT_RESOLUTION_READY_FOR_PHYSICAL_QA.md` — historical pre-gate implementation checkpoint;
4. `docs/checkpoints/2026-09-15_HOSTED_SYNC_QA_LOG_AND_LEGACY_BASELINE_RECOVERY_READY_FOR_PHYSICAL_QA.md` — permanent QA log + legacy-baseline recovery package;
5. `docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md` — PR #39 convergence design and original gate;
6. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
7. `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, `docs/ROADMAP.md`, `docs/ARCHITECTURE.md` and `docs/TESTING.md` as needed.

If older operational prose conflicts with this file or the current specific checkpoint, this newer completion checkpoint controls unless an even later approved decision/checkpoint supersedes it.

## Current Wave 4 state

```text
remembered Android Descope session/token             COMPLETE
owner-facing hosted account/campaign bootstrap       COMPLETE / OWNER-PHYSICAL PASS
campaign create + durable hosted delivery            COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery         COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync no-op confirmation                    COMPLETE / OWNER-PHYSICAL PASS
        |
        v
multi-client PC convergence safety                    COMPLETE / OWNER-PHYSICAL PASS
        |
        +-- concurrent divergence protection          PASS
        +-- explicit reviewed keep-local resolution   PASS
        +-- clean second-client convergence           PASS
        |
        v
PR #40 DOCUMENTATION/INTEGRATION CLOSURE              NOW
        |
        v
membership revoke + Player/DM authorization          NEXT SEPARATE PACKAGE
```

## Physical convergence evidence

The owner preserved two clients with intentionally different PC state. On the emulator, a local offline modification at revision `7` conflicted with independently newer hosted revision `8` while the durable baseline remained at revision `7`.

The QA report correctly produced `LOCAL_AND_HOSTED_CHANGED`, with local different from baseline, local different from hosted, and an empty outbox. The empty outbox was legitimate because the initial hosted pull discovered the conflict before snapshot queueing.

On `0.4.0-preqa.15`, the owner explicitly selected `Resolver conflicto: conservar PC local`. The result was:

`SUCCESS | PC=HBT PJ Test 2 B OFFLINE | reviewedHostedRevision=8 | resultingRevision=9`

The emulator then reported a clean synchronization with no PC conflicts, no queued/retryable/blocked mutations and an empty outbox.

The phone, which previously displayed the old/original PC value, subsequently displayed the emulator-modified value. Its QA diagnostic reported one unchanged hosted PC, no conflicts and an empty outbox, consistent with the normal app having already synchronized revision `9` before the explicit QA report ran.

Therefore the multi-client convergence gate is **OWNER-PHYSICAL PASS**. Do not rerun the whole scenario unless later code changes touch the relevant behavior.

## Permanent QA console

Debug builds expose `DnD Aid - QA DEV`. Hosted QA synchronization produces a copyable/shareable plain-text report with campaign eligibility, exact PC conflict reasons, revisions, baseline state, local-vs-baseline/current-hosted comparison, pull phase and outbox state.

The log excludes JWTs, refresh tokens, authorization headers, provider secrets and database credentials. Future physical gates should extend this structured diagnostic surface instead of creating unrelated one-off diagnostics.

The active campaign selector is local Player context only. Hosted synchronization reviews **all eligible hosted campaigns**.

## Current package closure

The tested behavior head is `008a73c20101a127edd82947af71c6024f894609`. The two commits that followed it before this completion record were documentation-only; no sync behavior changed after the physically tested APK.

PR #40 should remain focused on the completed convergence/QA/recovery/conflict-resolution package. Do not add membership revoke or Player/DM authorization work to it.

Exact closure sequence:

1. verify the documentation-only closure head passes CI;
2. merge PR #40 into `main`;
3. verify post-merge `main` according to normal workflow;
4. create a new focused package from current `main` for membership revoke + Player/DM authorization.

## Important corrections carried forward

- The repository is intentionally **public** under D-0075.
- External-service operating budget remains **USD $0** unless the owner explicitly changes it.
- Neon + Descope + Cloudflare DEV activation is complete; do not restart provider activation.
- Android remembered Descope session and `HostedAccessTokenProvider` are complete; do not create a second authentication/network abstraction.
- Ordinary Player hosted account/campaign bootstrap is complete.
- Campaign local-first creation + durable hosted delivery and PC push/pull are complete.
- The prior PC wire-envelope `VALIDATION_FAILED` defect was repaired; its blocked mutation was recovered and acknowledged.
- The unchanged-sync/no-op physical gate is PASS.
- Multi-client PC convergence, explicit reviewed keep-local resolution and clean second-client convergence are physically PASS.
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

These residuals do not reopen completed provider/session/bootstrap/campaign/PC/convergence packages.

## Historical Player evidence remains bounded

Current integrated CI and hosted physical proofs do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve historical QA evidence for exactly what it tested.
