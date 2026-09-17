# Desktop hosted Campaign Administration — owner QA handoff

**Current package:** Wave 5 / PR #44  
**Current state:** DEV Worker deployment verified; Windows Desktop auth/local-data preflight passed; hosted bootstrap returned zero campaigns  
**Current owner action:** read-only hosted membership/identity discovery before any moderation mutation

## What has already passed

The owner has verified on Windows:

- current Desktop workbench launches normally;
- the existing local campaign remains present and active;
- real Descope email-OTP authentication succeeds;
- Desktop reports hosted session `AUTHENTICATED`;
- the live DEV Worker responds normally;
- local campaign data is not deleted or silently converted to hosted state.

Observed bootstrap result:

`Campañas alojadas: 0 · aplicadas: 0 · conflictos: 0`

Therefore the authenticated Desktop application user currently has no active hosted campaign membership visible through the API.

Historical Wave 4 physical QA recorded an ACTIVE DM membership under a different application-user UUID. Android and Desktop use the same Descope project and same Worker. This must be understood from read-only hosted data before creating/editing memberships or diagnosing a cross-platform identity defect.

## Current owner action — read-only Neon discovery only

Open the existing DEV Neon project's SQL Editor and run the query provided by the technical assistant for this gate.

The query must be SELECT-only and should compare:

- the current Desktop authenticated `app_user` UUID shown by Desktop;
- the previously recorded real-DEV DM `app_user` UUID;
- the previously recorded hosted campaign UUID;
- any membership rows for either application user;
- campaign name, revision and deletion state.

Do **not** include `descope_subject` in the output because the UUID/membership comparison is sufficient for this gate.

Do not INSERT/UPDATE/DELETE anything yet.

## Why this gate exists

The backend resolves a validated Descope JWT subject to one stable `app_user`. Hosted campaign discovery then returns active memberships for that application user.

Zero hosted campaigns after successful Desktop login can therefore mean either:

1. the owner authenticated an intentionally different DEV identity;
2. historical DEV data belongs to another identity;
3. a cross-platform Descope identity mismatch exists and needs repair.

Do not choose among those explanations without read-only evidence.

## What to return

Return only the SELECT result rows requested by the technical assistant. Database credentials, connection strings, Descope subjects, passwords and provider tokens must not be pasted into chat or Git.

## Stop conditions

Stop and return to the technical assistant if:

- Neon asks for billing/payment or a new resource;
- the intended DEV database is unclear;
- the SQL editor is pointed at a different project/environment;
- any proposed command contains INSERT, UPDATE, DELETE, DROP, TRUNCATE or ALTER;
- the read-only result contradicts the expected recorded IDs/campaign.

## Moderation remains blocked

Do not click real `Kick`, `Ban` or `Lift Ban` yet. No suitable hosted Player row is currently visible in Desktop, and no database mutation is authorized merely to create one for testing.

After the read-only result is reviewed, the technical assistant should select the smallest safe next step and provide a new bounded owner-action packet only if external-provider action is still required.
