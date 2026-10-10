# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent or other AI working in this repository.

## 1. Repository authority

Git is the project's durable source of truth and operative memory. Chat context is navigation only. Before substantial work, verify the current remote repository rather than trusting remembered state.

`main` is the sole normal integrated-MVP trunk. Use short-lived outcome-oriented branches. Branch existence alone never establishes authority.

Fresh-context authority/navigation order:

1. this `AGENTS.md` for mandatory rules;
2. `RESUME.md` for the stable fast-resume contract;
3. `docs/checkpoints/LATEST.md`;
4. the single **Canonical active checkpoint** named by `LATEST.md`;
5. `docs/PROJECT_STATE.md` and `docs/BRANCH_STATUS.md` when the active checkpoint/task needs broader state;
6. `MANIFEST.md`, decisions, conventions, product, roadmap, workflow, architecture and testing docs only as needed.

If a user says only **"see/resume repo dnd_custom_aid"**, do not walk historical waves/PRs first. Verify current remote `main`, then follow the route above.

**Comando de continuidad:** una vez identificado este repositorio, **«sigue» / «continúa»** significa ejecutar el siguiente paso seguro indicado por `RESUME.md → docs/checkpoints/LATEST.md → checkpoint canónico` y dejar evidencia de trabajo real en Git. No reiniciar diseño, observaciones QA o gate; no interpretar «sigue» como autorización para fusionar una PR de producto o saltar los controles de no-regresión.

Historical checkpoints remain evidence; do not follow old `next` instructions when newer authority supersedes them.

Route changes are incomplete unless the same coherent repository change updates the checkpoint and `docs/checkpoints/LATEST.md` canonical pointer. Keep `RESUME.md` stable; move the pointer, not the entry route.

## 2. Current stage

Integrated-MVP implementation is authorized and in progress.

The exact current stage/package/defect/manual boundary is intentionally not duplicated in this rules file. It is defined by:

`RESUME.md -> docs/checkpoints/LATEST.md -> Canonical active checkpoint`.

Do not infer current work from historical Wave text, old PRs, branch existence, or remembered chat context. Do not restart completed scope without new defect evidence.

## 3. Product/architecture invariants

Preserve:

- paper-first Player experience;
- `Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop`;
- local-first operation;
- Kotlin/Compose Android and Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence where appropriate;
- Ktor native HTTP;
- Cloudflare Worker API + Neon PostgreSQL + Descope identity proof;
- application-owned authorization; native clients do not connect directly to Neon;
- stable object IDs, optimistic revisions, stale-write rejection, mutation IDs/idempotency, durable outbox, tombstones/non-resurrection and explicit conflict handling;
- DM campaign authority distinct from PC ownership; PC owner distinct from current controller;
- provider-neutral Shared seams where useful.

For reusable DM content, approved semantics include Personal reusable material, explicit Personal -> Campaign copy, a new independent campaign object identity, retained provenance, and no automatic inheritance/update relationship after copy. The integrated Wave 6 foundation now persists and tests these core local semantics for its recorded scope.

Do not add generalized CRDT/sync engines, microservices, event buses, enterprise IAM/RBAC/ACL, exhaustive event sourcing, WebSockets/realtime, or enterprise observability without a concrete approved need.

## 4. Owner vs technical responsibility

The owner decides consequential product behavior, workflow/UX, game semantics, privacy/visibility, MVP scope, destructive behavior and meaningful cost/security/compatibility/lock-in tradeoffs.

Technical agents normally decide schema layout, class/type decomposition, endpoint/request shapes, migration mechanics, internal sync structures, serialization, tests and bounded branch/package granularity. Do not ask the owner to rubber-stamp routine engineering.

Explain meaningful work: what changed, why, practical approach, consequences/tradeoffs, verification, limitations and exact next action.

## 5. External-provider capability boundary

Cloudflare, Descope, Neon and future providers are explicit authenticated capability boundaries.

When a task reaches a provider:

1. determine whether the current environment actually has authenticated capability for the required action;
2. if yes, proceed only within approved scope/security/cost constraints;
3. if no, establish the limitation once and stop trying alternate plugins, MCPs, browser logins, API-token paths or dashboard workarounds;
4. finish all safe repository/code/test/CI work first;
5. stop at the first unavoidable authenticated owner action;
6. provide one bounded owner-action packet;
7. resume from returned non-secret evidence without repeating completed investigation.

Provider inability is a capability boundary, not a reason to fragment safe engineering into tiny tasks.

## 6. Owner-action packet standard

When provider/owner handoff is necessary, include:

- what must be done and why;
- exact working directory/page/provider location;
- ordered commands/clicks;
- expected successful result;
- secrets that must not be shared;
- exact non-secret evidence to return;
- explicit stop/error conditions;
- billing/security/destructive-change warnings.

Prefer one provider handoff per task.

## 6.1 Owner-local Android QA PDF destination and bulk pull rule

For owner-run Android emulator/device PDF review on Windows, when asking the owner to copy or pull QA outputs from the Android `Download` directory, use this local destination by default:

```text
D:\DnD_Aid\PDF Check
```

The owner requires the Android `Download` retrieval command to pull **everything present in that directory without discriminating by filename, extension or expected artifact**. Do not give a one-file `adb pull`, do not ask the owner to identify the generated PDF filename first, and do not filter to `*.pdf`. The standard PowerShell handoff is:

```powershell
New-Item -ItemType Directory -Force -Path "D:\DnD_Aid\PDF Check" | Out-Null
Set-Location "D:\DnD_Aid\PDF Check"
adb pull "/sdcard/Download/." "."
```

Use the bulk form by default because it is the owner's workable QA flow. Only narrow the pull if the owner explicitly asks to do so.

This is an owner-local QA convenience only; do not commit generated review files or make this local path an application/runtime dependency.

## 6.2 Owner-local Android QA APK naming and ADB boundary

For owner-facing Android QA APK downloads, rename the extracted APK before presenting or retaining it locally so the filename includes the application identity, version/pre-QA identity and build/versionCode. Use this pattern:

```text
dnd-custom-aid-<versionName>-build-<versionCode>.apk
```

Example:

```text
dnd-custom-aid-0.5.0-preqa.8-build-50800.apk
```

Do not leave an owner-facing QA APK named only `androidApp-debug.apk` when giving download/preparation instructions.

The owner does **not** use ADB to install QA APKs on either the physical phone or the emulator. Installation is owner-driven through normal device/emulator interaction (for example drag/drop on the emulator). Do not provide `adb install` as the normal QA installation step unless the owner explicitly asks for it.

ADB remains appropriate for bounded QA support such as:

- `adb push` to place fixture/import files in emulator/device-visible storage;
- `adb pull` to retrieve generated PDFs or similar artifacts for review;
- read-only inspection commands such as listing files or checking device connectivity when useful.

## 6.3 QA repair source-traceability, comprehension gate and owner-candidate identity

When implementation work is triggered by owner/QA observations, the **original observation source must remain the acceptance ledger for the entire fix**.

### Blocking pre-implementation QA comprehension gate

**No repair code may be changed until the real QA result that triggered the work has been reopened and understood.** Reading only a summarized issue list, a later checkpoint, a derived proof, a test name or a prior agent's claimed diagnosis is not sufficient.

Before changing code, the agent must:

1. reopen the exact owner/QA checkpoint, report, screenshots and/or generated artifact that triggered the fix;
2. inspect the real failing output itself whenever it exists (for PDF/layout work, inspect the actual generated PDF/pages, not only extracted text or a rewritten test fixture);
3. enumerate every original owner observation that is in scope;
4. restate each observation in concrete implementation-neutral terms: what is visibly/behaviorally wrong, what existing approved/native example establishes the intended behavior when one exists, and what evidence would demonstrate that the exact defect is gone;
5. distinguish original owner observations from later independent findings;
6. map each observation to the responsible implementation surface and planned regression evidence;
7. identify any ambiguity in the expected behavior and resolve only genuine owner-level ambiguity before implementation.

This gate is **blocking**. Do not begin coding merely because the likely implementation location is obvious. Do not treat reviewing the QA artifact as an administrative step after a fix has already been designed.

Where an approved/native component already demonstrates the required visual grammar, **reuse that grammar as the reference instead of inventing a new generic component**. A repair must not replace an existing proven source-native pattern with a bespoke approximation unless the owner has explicitly approved that design change.

A repair must **not** be declared closed merely because CI is green, a derived proof looks improved, page count decreases, or a rewritten test passes. Every source observation stays OPEN until it has explicit evidence against the exact candidate being promoted. Do not silently replace, summarize away or forget source observations while iterating.

Before owner handoff, re-run the acceptance ledger against the **actual candidate output**. For every original observation, record one of:

- **FIXED** — exact candidate evidence demonstrates the original defect is gone;
- **OPEN** — defect still exists or evidence is insufficient;
- **CHANGED/NEW** — the original symptom changed or a new regression appeared, which must be added rather than hiding the old observation.

A candidate with any blocking OPEN item or newly blocking regression is not ready for owner QA. The purpose of automation/proofs is to support this review, not replace it.

If a cross-family repair was motivated by one stress fixture, regression must also guard the product-level invariant on every affected family. Before owner handoff, generation smoke must cover all affected owner-facing families and must include known previously-working paths so the repair cannot introduce a new export blocker elsewhere.

Every materially different owner-facing QA binary must also have a **unique candidate identity**:

- bump versionName/versionCode (or equivalent build identity) for every materially changed owner candidate;
- never reuse an earlier owner-facing version/build number for a different binary;
- record the exact source commit, CI/run, artifact ID and SHA-256 when available;
- do not ask the owner to distinguish stale vs repaired binaries by filename alone.

If runtime output materially disagrees with repository proof (for example page counts, visible layout class or generation success), stop speculative fixing and establish exact binary/source provenance first.

## 7. Secret handling

Never request or commit passwords, OTPs, Cloudflare tokens, DB passwords, Neon connection strings, Descope/session/access/refresh JWTs, private keys, signing credentials, provider credential files or secret environment-variable values.

Use secure local/CI/provider secret stores and non-secret evidence.

## 8. Provider evidence discipline

Green repository CI does not prove a Worker is deployed, a route is live, provider configuration is accepted, or real authentication works. Provider-side behavior requires provider/owner evidence.

Conversely, do not repeat a completed provider action merely because documentation later changes.

For the verified Wave 5 state, the existing DEV Worker is `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`. Do not create a replacement Worker. Do not redeploy unless Worker code materially changes or newer evidence specifically requires it. Deployment must preserve configured secrets and fail safely when required secrets are absent.

## 9. Canonical DEV identity

Normal hosted DEV owner/DM testing uses the Outlook-backed application identity. Gmail is historical/inactive by default and may be deliberately reused only when a future test needs a secondary real user. Historical Gmail mutation/audit evidence remains historical truth and must not be rewritten.

## 10. Cost/security

External-service operating budget is **USD $0** unless the owner explicitly changes it. Do not enable paid plans, automatic overage, usage-based commitments, paid add-ons or paid AI services without explicit approval.

Before activating any new provider/resource, verify current free-tier/payment/quota/hard-cap/overage/region/lock-in terms. Object-storage provider selection remains deferred until Media/Handouts/assets actually require it.

The repository is intentionally public. Preserve fail-closed authorization, least privilege, object-level authorization, replay/idempotency safety, error/log hygiene, local recovery data, stale-write protection and deletion/tombstone guarantees.

Known residual: an owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; investigate exact packages/reachability/upgrade consequences when a relevant hardening package is taken up.

## 11. Development workflow

For substantial work:

1. verify remote branch/topology/current CI;
2. read current authority files;
3. identify already-approved semantics and implemented constraints;
4. surface only genuine owner-level ambiguities;
5. create a short-lived branch from current `main`;
6. implement the smallest coherent technical batch;
7. run focused checks and aggregate CI proportionate to risk;
8. update operative-memory documentation;
9. leave clean Git evidence and open/update PR as appropriate;
10. continue autonomously until a genuine owner/manual/provider boundary.

Do not stop simply because one safe subtask completed if the coherent package can continue, and do not expand speculatively beyond it.