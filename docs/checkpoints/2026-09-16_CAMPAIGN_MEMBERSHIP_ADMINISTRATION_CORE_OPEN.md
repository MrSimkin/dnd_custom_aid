# Campaign membership administration core — package open

**Date:** 2026-09-16 (Chile local time)  
**Wave:** 5 — Desktop shell + Campaign Administration  
**Branch:** `wave5/campaign-membership-administration-core`  
**Starting integrated `main`:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Predecessor:** PR #42 — Desktop workbench shell + local campaign context — merged / owner-QA PASS

## Purpose

Continue Wave 5 with the smallest dependency-correct hosted Campaign Administration core. The first Desktop package established the workbench and persistent local campaign context. This package establishes the server/shared contracts that a later authenticated Desktop Campaign Administration surface can consume.

## Bounded package scope

Implement and verify:

- authenticated DM-only hosted campaign member roster;
- member identity projection suitable for Campaign Administration: application user ID, display name, campaign role and lifecycle status;
- explicit Player membership moderation actions:
  - `KICK`: `ACTIVE -> KICKED`;
  - `BAN`: `ACTIVE|KICKED -> BANNED`;
  - `LIFT_BAN`: `BANNED -> KICKED`;
- idempotent repeated moderation actions;
- no membership-row deletion, PC deletion, ownership/control rewrite or local-cache deletion as a moderation side effect;
- campaign revision advances only when a moderation action actually changes lifecycle state;
- backend authorization/error behavior remains fail-closed and does not disclose roster data to non-DMs;
- provider-neutral Shared client contract for later Desktop consumption;
- automated backend + hosted PostgreSQL + Shared contract evidence.

`LIFT_BAN` deliberately returns the member to `KICKED`, not directly to `ACTIVE`: the approved product contract says a kicked user may rejoin through a valid invitation, while a banned user must first have the ban lifted. Invitation/rejoin is a later bounded package.

The initial moderation surface targets Player memberships. Co-DM moderation is not an MVP requirement and is not invented here.

## Explicit non-goals

This package does **not** yet implement:

- Desktop provider/session acquisition or login UI;
- Desktop hosted synchronization;
- Desktop Campaign Administration member UI;
- invitation create/revoke/regenerate/rejoin;
- campaign role editing/co-DM workflows;
- PC ownership/control assignment shortcuts;
- PC Manager/Audit;
- live Combat;
- generalized RBAC/ACL;
- destructive membership/account cleanup.

## Safety invariants carried forward

- membership, campaign role, PC ownership and PC control remain distinct;
- DM campaign authority remains distinct from PC ownership;
- moderation stops/restores hosted eligibility through lifecycle state; it does not silently destroy local or hosted character data;
- `BANNED` is stronger than `KICKED`; a Kick action must never implicitly lift an existing ban;
- public-repository secret hygiene and hard external-service budget `USD $0` remain controlling;
- no provider/database credentials are added to native clients or Git.

## Owner-approved Desktop settings follow-up

At the next genuine Desktop feature build, Application Settings must also receive the owner-requested presentation upgrade:

1. expand the Desktop font catalogue to the Android-equivalent choices where the Desktop platform can support them appropriately;
2. replace plain Font and Theme selection with Android-like preview cards/forms so the visual result can be previewed before selection.

This is a durable future Desktop-build requirement. It does **not** reorder the current Wave 5 dependency sequence and is not part of this backend/shared contract package.

## Continuity repair

The predecessor checkpoint correctly records owner QA completion, but `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md`, and `docs/BRANCH_STATUS.md` still describe PR #42 as open/manual-QA-pending. This branch must repair those current-state pointers before claiming the new package as the operative resume point.

## Current action

Implement the bounded hosted member-roster/moderation contracts, add proportionate automated tests, refresh operative-memory files, and run Scaffold on the exact branch head. No owner/manual action is currently required.
