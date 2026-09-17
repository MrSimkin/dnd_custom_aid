# Desktop hosted Campaign Administration — live QA preflight: zero hosted campaigns

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave5/desktop-hosted-campaign-administration`  
**PR:** #44 — draft  
**Gate result:** AUTHENTICATION PASS / LOCAL-DATA PASS / HOSTED MEMBERSHIP DISCOVERY BLOCKED

## Observed owner QA evidence

The owner launched the current Windows Desktop workbench with the recorded portable JDK 17 + Gradle 9.5 QA toolchain.

Observed:

- Desktop launched normally;
- the existing local campaign `QA Wave 5 - 2026-09-16` remained present and active;
- local campaign UUID remained `30609c9d-89f7-42ef-85dd-a7a35df3c506`;
- real Descope email-OTP authentication succeeded;
- Desktop reported hosted session `AUTHENTICATED`;
- bootstrap reported `Campañas alojadas: 0 · aplicadas: 0 · conflictos: 0`;
- the local campaign remained available and was not silently reinterpreted as hosted;
- Campaign Administration correctly stated that the active local campaign has no hosted membership for the authenticated account;
- no roster/moderation controls were available because there was no hosted campaign membership to administer.

QA diagnostics confirmed the expected DEV hosted API URL, Windows 11 / Java 17.0.20.1 runtime, one local campaign, the existing local database/preferences locations and the successful hosted-auth/bootstrap events. No secret values were recorded.

## Interpretation

This is not a failure of OTP authentication, Worker route deployment or local-data preservation.

The backend resolves each validated Descope JWT `sub` to exactly one `app_user`, and campaign discovery returns active memberships for that application user. The current Desktop-authenticated application user currently receives zero active hosted campaigns.

Historical real-DEV Wave 4 evidence recorded an ACTIVE DM membership for a different application-user UUID. Because Android and Desktop use the same Descope project ID and same DEV Worker, the difference must be investigated before creating/editing memberships or claiming cross-platform identity convergence.

Do **not** infer a product defect yet: the owner may have authenticated a different Descope identity, or historical DEV test data may contain distinct application identities. Resolve this from read-only hosted data first.

## Next bounded gate — read-only Neon discovery

Use the existing DEV Neon database and perform a read-only query that answers:

1. whether the previously verified hosted DM campaign/membership still exists and remains ACTIVE;
2. whether the current Desktop-authenticated application user has any membership rows at all;
3. whether the historical DM membership and current Desktop account are distinct application users;
4. campaign name/status/revision for any matching memberships.

Do not UPDATE/INSERT/DELETE anything during this discovery step. Do not expose `DATABASE_URL`, Descope subjects, passwords or provider credentials in chat/Git.

After the read-only result is reviewed, choose the smallest correct next action. If the same intended human identity has unexpectedly split into two Descope subjects/application users, treat that as an identity/integration defect rather than papering over it with an arbitrary membership insert. If the owner intentionally authenticated a different DEV identity, use the established test identity or create only the minimum explicitly justified QA fixture.

## Moderation gate remains blocked

Do not run real `KICK`, `BAN` or `LIFT_BAN` yet. There is no suitable visible hosted Player row in the current Desktop session, and no provider/database mutation is authorized merely to manufacture one.
