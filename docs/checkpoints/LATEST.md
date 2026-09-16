# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified starting main for current package:** `40b29006052c9986e2d79227a6053f36241de1e6`  
**Starting Scaffold:** `35123470001` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-workbench-shell`  
**Current package:** Wave 5 — Desktop workbench shell + local campaign context  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_PACKAGE_OPEN.md`  
**Owner implementation authorization:** **GRANTED**  
**Current owner/manual action:** none

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_PACKAGE_OPEN.md` — current package boundary and verification plan;
3. this file — practical resume point;
4. `docs/BRANCH_STATUS.md` — branch lifecycle;
5. `docs/PROJECT_STATE.md` — global implementation state;
6. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md` — controlling Desktop/workbench product direction;
7. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md` — integrated-MVP sequencing/governance;
8. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md` — latest completed Wave 4 evidence;
9. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — `$0`, public-repository and secret-hygiene policy.

If older operational prose conflicts with this file or the current package checkpoint, the newer current record controls.

## Current implementation sequence

```text
Wave 4 Player <-> Server                           COMPLETE / INTEGRATED
membership revoke + Player/DM authorization       COMPLETE / OWNER-PHYSICAL PASS
        |
        v
Wave 5 Desktop shell + Campaign Administration    ACTIVE
        |
        v
desktop workbench shell + local campaign context CURRENT PACKAGE
```

## Current package intent

The Desktop application is currently only a placeholder window. Shared already owns Campaign identity/persistence semantics through `CampaignRepository`, and the Shared Desktop target already includes SQLite JDBC support.

This package therefore establishes the real Desktop workbench around those existing Shared seams instead of inventing Desktop-only campaign state. It will provide persistent local Desktop storage, the approved workbench frame, functioning Dashboard/Campaigns/Campaign Administration destinations, and active-campaign context.

Final invitation/Kick/Ban workflows, hosted Desktop authentication/sync, live combat authority, Managers, Media/Handouts, System Administration and backup/export remain outside this first slice.

## Completed Wave 4 baseline

PR #41 merged as `6f7165e6e5ae56a4b1985f037a656bf527b94d01`; post-merge Scaffold `35123027446` passed. The later documentation/device-inventory head `40b29006052c9986e2d79227a6053f36241de1e6` passed Scaffold `35123470001` and is the exact base of the current Wave 5 branch.

Do not reopen completed provider/session/campaign-PC/convergence/membership QA unless new behavior touches those contracts.

## Permanent safety rules

- Repository is intentionally public under D-0075.
- External-service operating budget remains **USD $0** unless explicitly changed.
- Never expose database credentials, provider tokens, private keys, authorization headers or session/refresh tokens.
- Do not reset local databases/app data, clear outboxes, delete PCs/campaigns, or reinstall merely to make QA pass.
- Membership, campaign role, PC ownership and current control remain distinct.
- DM authority does not imply PC ownership.
- Preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees.
- Live Combat belongs to Wave 6 and must not be pulled into the current Desktop shell package.

## Exact next action

Continue implementation on `wave5/desktop-workbench-shell` from the current package checkpoint. No owner input is needed until automated verification reaches a meaningful Desktop visual/manual QA gate or a genuine product-level ambiguity appears.
