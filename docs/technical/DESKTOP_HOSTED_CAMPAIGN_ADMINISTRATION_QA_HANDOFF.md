# Owner Windows Desktop live-QA handoff — Wave 5 PR #44

**Purpose:** bounded owner action after verified DEV Worker deployment.

This handoff validates the real Desktop -> Descope -> Cloudflare Worker -> Neon path without exposing secrets and without inventing hosted test data.

## Preconditions already satisfied

- PR #44 repository implementation is CI-verified;
- DEV Worker code is deployed and healthy;
- Campaign Administration roster route is live through authentication enforcement;
- owner Windows Desktop QA workstation/toolchain is already recorded in `docs/TEST_DEVICES.md`;
- local Desktop data must not be deleted/reset merely to make QA pass.

## Owner action — preflight live Desktop integration

Open PowerShell / Windows Terminal.

### 1. Update the local PR #44 branch

```powershell
cd D:\DnD_Aid\repo\dnd_custom_aid
git status --short
git fetch origin
git switch wave5/desktop-hosted-campaign-administration
git pull --ff-only origin wave5/desktop-hosted-campaign-administration
git rev-parse HEAD
```

If `git status --short` shows unexpected local work you care about, stop before pulling or switching.

### 2. Launch Desktop using the already-proven QA toolchain

```powershell
$env:JAVA_HOME='D:\DnD_Aid\tools\desktop-qa\temurin17\jdk-17.0.20.1+1'
& 'D:\DnD_Aid\tools\desktop-qa\gradle-9.5.0\bin\gradle.bat' :desktopApp:run
```

Expected: the existing `D&D Custom Aid — Desktop` workbench opens normally. Existing local campaign data should still be present.

### 3. Open `Administración de campaña`

Verify before logging in:

- the active local campaign still exists;
- local campaign work is available while signed out;
- the hosted account card shows the signed-out state rather than blocking the local workbench.

Do not create/delete/reset local campaigns to force a hosted match.

### 4. Perform real DEV email-OTP login

In `Cuenta alojada`:

1. enter the existing DEV email account you normally use for this project;
2. click `Enviar código`;
3. retrieve the OTP from your email locally;
4. enter it into `Código de acceso`;
5. click `Verificar e iniciar sesión`.

Do **not** paste the OTP into chat or Git.

Expected after success:

- the UI changes to `Conectado`;
- an authenticated account is shown;
- hosted campaign bootstrap executes automatically;
- a summary such as `Campañas alojadas: ... · aplicadas: ... · conflictos: ...` appears;
- no local campaign/PC data disappears.

If authentication or bootstrap fails, stop and return only the visible error message plus the non-secret QA diagnostic text. Do not send session/refresh JWTs.

### 5. Select/confirm a real hosted campaign context

Open `Campañas` if needed and select the campaign that corresponds to the authenticated hosted DM membership, then return to `Administración de campaña`.

Expected hosted context for the campaign:

- `Rol alojado: DM`;
- `Estado de membresía: Activa` (or equivalent Spanish label);
- button `Actualizar miembros alojados` becomes available.

If the campaign appears only local with no hosted membership, do not create a fake link or paste IDs. Use `Actualizar campañas alojadas` once, then stop and report the visible campaign/bootstrap state if it still does not converge.

### 6. Retrieve the real hosted roster

Click:

`Actualizar miembros alojados`

Expected:

- `Miembros alojados · revisión <n>` appears;
- the DM row is visible;
- DM rows have no Player moderation buttons;
- any real Player membership rows show their role/status and only the actions valid for their current lifecycle state.

## Stop after roster retrieval for this handoff

This first QA handoff intentionally stops after obtaining the real roster.

Do **not** perform Kick/Ban/Lift Ban yet unless the technical agent explicitly advances to that next bounded mutation test after reviewing the roster evidence.

Reason: the repository does not currently prove that the DEV campaign contains a disposable/appropriate real Player membership. We must not invent data, edit Neon ad hoc, or moderate an unintended account simply to satisfy the test.

## Also make two quick visual observations

Before closing the app, open `Configuración` and confirm only:

- font choices are presented as visual previews rather than a plain selector;
- theme choices are presented as preview cards/forms.

Do not run the full settings persistence test yet; that can be combined with the later final QA pass after hosted moderation is resolved.

## Return to the technical agent

Return only:

1. `git rev-parse HEAD`;
2. whether Desktop launched normally and existing local campaign data remained present;
3. whether email OTP login succeeded (do not send the OTP);
4. the bootstrap summary shown by the app;
5. the selected campaign's hosted role/status;
6. the roster summary: campaign revision plus each visible member's display label, role and lifecycle status — **no JWTs or credentials**;
7. whether DM rows correctly lacked moderation controls;
8. whether a real Player row exists and which moderation buttons are visible for it;
9. whether font/theme previews were visibly present;
10. any visible error text or copied non-secret `QA / Diagnóstico` output if something failed.

## Stop conditions

Stop instead of improvising if:

- the app would require deleting/resetting local data;
- login asks you to expose an OTP/token outside the app;
- hosted bootstrap reports a conflict you do not understand;
- the active campaign does not converge to a hosted membership after one explicit refresh;
- roster retrieval returns an authorization/server error;
- the only Player row belongs to an account whose membership you do not want to mutate;
- any step suggests direct Neon edits or provider-resource changes.

After the returned roster evidence is reviewed, the technical agent will either:

- advance to a narrowly scoped real Kick/Ban/Lift-Ban QA sequence if a suitable Player row exists; or
- prepare a separate safe test-membership setup plan if no suitable Player exists, without expanding PR #44 into invitation/rejoin functionality.