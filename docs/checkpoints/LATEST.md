# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Post-merge Scaffold:** `35116690562` — **SUCCESS**  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current package:** membership revoke + Player/DM authorization  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. this file — exact practical resume point;
3. `docs/PROJECT_STATE.md` — current global implementation state;
4. `docs/BRANCH_STATUS.md` — current branch lifecycle;
5. `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md` — completed convergence evidence immediately preceding this package;
6. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
7. relevant architecture/testing/product/decision files as needed.

If older operational prose conflicts with this file or a newer specific checkpoint, the newer current-state/checkpoint evidence controls.

## Current Wave 4 state

```text
remembered Android Descope session/token             COMPLETE / OWNER-PHYSICAL PASS
owner-facing hosted account/campaign bootstrap       COMPLETE / OWNER-PHYSICAL PASS
campaign create + durable hosted delivery            COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery         COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync/no-op confirmation                    COMPLETE / OWNER-PHYSICAL PASS
multi-client PC convergence safety                    COMPLETE / OWNER-PHYSICAL PASS
        |
        v
PR #40 merged to main                                COMPLETE
post-merge main verification                          SUCCESS
        |
        v
membership revoke + Player/DM authorization          CURRENT SEPARATE PACKAGE
```

## Completed convergence package

PR #40 (`qa: add reusable sync log and safe legacy baseline recovery`) merged into `main` as:

`587a000dae7ff9b9f997dd138b0ebbaeca201256`

The exact post-merge `main` Scaffold run `35116690562` completed **SUCCESS**, including Kotlin/Android/Desktop/shared checks, backend type-check and hosted PostgreSQL contracts.

The physically tested convergence behavior remains bounded to QA build `0.4.0-preqa.15` / `41500`, behavior head `008a73c20101a127edd82947af71c6024f894609`, behavior-head Scaffold `35044956294` and APK SHA-256 `4615d1a9c8f9e87c2ebbc5ecb80e4a22f747384baa3091c1f05b6d90e4cac4f5`.

Physical evidence proved:

- concurrent local/hosted divergence produced `LOCAL_AND_HOSTED_CHANGED` rather than a silent winner;
- explicit reviewed keep-local resolution safely advanced hosted revision `8 -> 9`;
- the emulator returned to a clean conflict-free/empty-outbox state;
- the clean phone converged to the emulator-selected hosted PC value;
- no queued, retryable or blocked mutation remained.

Do not rerun the full convergence scenario unless later behavior changes touch the relevant sync/conflict-resolution code.

## Current package boundary

The active package is now:

> membership revoke + Player/DM authorization

This is intentionally separate from PR #40.

Existing approved semantics to preserve include:

- membership, campaign role, PC ownership and PC current control are distinct concepts;
- membership lifecycle includes `ACTIVE`, `KICKED` and `BANNED`;
- removal/revoke must stop future hosted access/synchronization without silently wiping local cached data;
- DM campaign authority must remain distinct from PC ownership;
- Player PC access must continue to respect owner/controller authority;
- stale revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees remain intact;
- no generalized RBAC/ACL framework should be introduced without a concrete need.

The immediate technical task is to inspect the already-integrated membership lifecycle, backend authorization, shared synchronization and regression coverage, then implement the smallest missing end-to-end enforcement/verification package. Routine technical work does not require a new owner decision.

Return to the owner only when a genuine product/security/cost/destructive-behavior decision or a manual/physical QA gate is reached.

## Permanent QA console

Debug builds expose `DnD Aid - QA DEV`. Hosted QA synchronization produces a copyable/shareable plain-text report with campaign eligibility, exact PC conflict reasons, revisions, baseline state, local-vs-baseline/current-hosted comparison, pull phase and outbox state.

The report must continue to exclude JWTs, refresh tokens, authorization headers, provider secrets and database credentials. Extend this reusable diagnostic surface for future physical gates where practical instead of creating disposable diagnostics.

The active campaign selector is local Player context only. Hosted synchronization reviews all eligible hosted campaigns.

## Important corrections carried forward

- The repository is intentionally **public** under D-0075.
- External-service operating budget remains **USD $0** unless the owner explicitly changes it.
- Neon + Descope + Cloudflare DEV activation is complete; do not restart provider activation.
- Android remembered Descope session and `HostedAccessTokenProvider` are complete; do not create a second authentication/network abstraction.
- Ordinary Player hosted account/campaign bootstrap, campaign delivery, PC push/pull, no-op sync and multi-client convergence are complete.
- Explicit keep-local resolution remains owner-controlled and revision-bound; do not convert it into automatic local-wins/server-wins behavior.
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
