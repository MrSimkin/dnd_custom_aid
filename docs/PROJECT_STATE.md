# Project State — global repository navigation

**Last verified:** 2026-09-15 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Provider-neutral implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign bootstrap:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Campaign + PC hosted delivery/recovery:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Current exact handoff:** `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md`

`docs/checkpoints/LATEST.md` and the current exact handoff control the operational resume point if older prose conflicts.

## 1. Current authority/topology

`main` is the single normal integrated-MVP development trunk. Historical Player/convergence branches remain evidence only. New work uses short-lived outcome-oriented branches from current `main` and reintegrates early.

The approved product remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

The project remains paper-first and intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise-infrastructure exercise.

## 2. Completed shared/provider-neutral foundation

The integrated baseline includes:

- Account/global identity;
- Campaign + Membership + campaign role;
- PC owner distinct from current controller;
- stable IDs;
- monotonic optimistic revisions and stale-write semantics;
- tombstones/non-resurrection;
- Personal/Campaign/System-or-Official scopes where valid;
- independent-copy provenance;
- local sync metadata/invariants;
- `/v1` hosted API/auth/domain foundation;
- explicit PostgreSQL migrations/contracts + CI validation;
- shared Android/Desktop Ktor transport;
- provider-neutral access-token seam;
- durable SQLDelight hosted outbox;
- local-first campaign create + idempotent hosted delivery;
- authenticated account/campaign bootstrap;
- campaign membership lifecycle reconciliation;
- hosted PC current-state snapshots;
- server-side application authorization;
- PC optimistic revisions/idempotency/conflict/tombstone rules;
- same-identity hosted reconciliation distinct from user-facing restore-as-copy;
- non-destructive local recovery when hosted state is deleted.

Provider-neutral checkpoint: `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`; Actions `34985799585` — **SUCCESS**.

## 3. Hosted DEV environment — complete

Verified active DEV architecture:

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Current public identifiers:

- Neon project `dnd-custom-aid-dev`, project ID `holy-meadow-19010740`, São Paulo region, database `dnd-custom-aid-dev`;
- Descope project `dnd-custom-aid-dev`, project ID `P3JNKAUazZAxRXF4uM7nKzaAiy7Y`, DEV base URL `https://api.descope.com`;
- Cloudflare Worker `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

The Worker runtime contract uses `DATABASE_URL` + `DESCOPE_PROJECT_ID`; `DESCOPE_BASE_URL` is optional.

Verified evidence includes real Neon migration/contracts, real Descope OTP, deployed Worker health, unauthenticated 401, authenticated `/v1/me`, application-user persistence through Neon, and representative Workers Free CPU/runtime proof for the tested path.

Do not repeat provider activation.

## 4. Android hosted session — complete

PR #30 integrated Descope Android session handling into the existing `HostedAccessTokenProvider` seam.

The owner physically verified OTP login, authenticated Worker access, remembered session across full restart, remembered-session reuse without another login, logout, and no remembered session after restart.

The separate debug-only `DnD Aid - Hosted DEV Auth` launcher remains verification infrastructure, not final product login UX.

## 5. Ordinary Player campaign bootstrap — complete

PR #32 wired the normal Player `Campañas` screen to the remembered hosted session and shared campaign-bootstrap reconciliation.

The owner physically verified the normal Player could read/reconcile hosted campaign state without losing existing local campaign data.

## 6. Campaign + PC hosted synchronization — integrated through PR #34

PR #34 final head:

`ff7d96d6d5806fcf9969490d288c0d25b00d62fe`

Merged commit:

`75d5acf354b41185255ff7d1a5eb4a689f300721`

Validation:

- exact-head Actions `35027987125` / #1939 — **SUCCESS**;
- post-merge Actions `35028893643` / #1940 — **SUCCESS**.

Integrated behavior:

- local-first campaign creation + durable outbox mutation;
- idempotent hosted campaign delivery/read-back;
- PC snapshot pull;
- new/changed PC snapshot push;
- authoritative PC revision acknowledgement;
- durable retry/block semantics;
- no-op suppression logic;
- debug-only outbox diagnostics/recovery tooling.

### Real physical defect found and repaired

The owner found a real `PC_SNAPSHOT_PUT` remaining in the outbox. Diagnostic state was:

```text
PC_SNAPSHOT_PUT | BLOCKED | attempts=1 | expectedRevision=0 | error=VALIDATION_FAILED
```

Root cause: default-valued `CharacterBackupDocument.format` and `.version` could be omitted from the hosted Ktor JSON wire payload, while the Worker correctly required those fields.

The repair keeps Worker validation strict, enables hosted JSON default-value encoding on Android/Desktop, adds regression coverage, preserves the original blocked mutation, and safely returns only that known compatible validation-blocked PC mutation to `READY` through debug tooling.

The owner retried the same preserved mutation and then confirmed:

```text
Outbox local: vacío.
No hay cambios hospedados pendientes ni bloqueados.
```

Campaign/PC delivery plus blocked-mutation recovery are therefore physically proven.

One exact manual observation is carried forward: the owner did not separately report an unchanged repeat Player sync followed by another empty-outbox diagnostic. Include that observation in the next consolidated physical gate; do not falsely mark it as already observed.

## 7. Current implementation boundary

Wave 4 — Player <-> Server end-to-end — remains active.

```text
remembered Android session/token                     COMPLETE
HostedAccessTokenProvider                            COMPLETE
owner-facing hosted campaign bootstrap               COMPLETE
campaign create + durable hosted delivery            COMPLETE
PC snapshot push/pull + blocked retry recovery       COMPLETE
unchanged-repeat no-op owner observation             CARRY FORWARD
        |
        v
multi-client PC convergence safety                   NEXT IMPLEMENTATION BATCH
        |
        v
membership revoke + Player/DM authorization          FOLLOWING BOUNDARY
```

The next implementation batch must harden multi-client convergence before the next owner physical test.

Key rule:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

Add enough last-synchronized baseline knowledge to distinguish:

- clean old local copy -> safe apply of newer hosted state;
- locally modified old copy + newer hosted state -> explicit conflict preserving local data;
- offline local edit + unchanged server -> reconnect and deliver normally;
- fresh second client -> pull the same stable hosted identity.

The owner explicitly prefers batched development/testing. Accumulate closely related implementation behind automated CI and stop at the next meaningful physical boundary rather than demanding an APK install after every small step.

## 8. Protected integrated-MVP direction

Do not silently demote these protected directions to stretch goals:

- Player hosted/shared integration, remembered auth and campaign switching;
- project-specific revisions/idempotency/outbox/tombstone/conflict sync;
- object storage and Media/Handouts when their implementation wave arrives;
- complete DM live Workspace on Android/tablet and Desktop;
- explicit DM combat authority resume/handoff;
- Monster + Creature Creator Assistant;
- NPC Manager/helper;
- Homebrew & Rules Manager;
- Stage/Place/Scene and Dungeon/Zone/Encounter readiness foundations;
- Encounter Manager;
- PC Manager/Audit/correction;
- PC Sheet PDF export across Player/DM surfaces;
- Campaign Manager + System Administration;
- meaningful audit/history/recovery;
- full verifiable server backup/export;
- official SRD storage/retrieval/grounded clarification.

## 9. External-service/security state

D-0075 remains controlling:

- hard external-service operating budget = **USD $0** unless owner changes it;
- repository visibility is intentionally public;
- never commit secrets;
- paid plans/overage/billing commitments require explicit owner approval.

Security residuals remain visible follow-up work:

- JWT/fail-closed verification robustness;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log secret leakage;
- replay/idempotency authorization;
- investigate the locally reported **3 high severity npm vulnerabilities** before remediation; never run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role;
- reassess Descope production region/settings before release.

Object storage remains deferred until Media/Handouts/assets need it. Workers AI remains later/conditional under the `$0` policy.

## 10. Owner/local notes

Owner project root: `D:\DnD_Aid`  
Local clone: `D:\DnD_Aid\repo\dnd_custom_aid`  
Owner credential file: `D:\DnD_Aid\dnd_custom_aid_dev_credentials.md`

The credential file is intentionally plaintext and outside Git by explicit owner choice. Do not read/copy/commit it and do not impose a vault/password-manager migration unless asked.

Known Windows caveat: PowerShell `Invoke-RestMethod` and Windows `curl.exe` had SChannel TLS trouble with workers.dev while Node `fetch()` and Vivaldi worked. Treat Node/browser as the known-good endpoint-test path unless separately investigating Windows TLS.

## 11. Release/acceptance status

The project remains development/debug and is not release-ready.

Current integrated/hosted physical proofs do not retroactively convert historical Player physical QA into a PASS. Historical frozen Player candidate remains `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`; preserve that evidence for exactly what it tested.

## 12. Resume rule

For exact continuation, read:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md`;
3. `docs/recovery/PROJECT_RECOVERY_PROMPT.md`.

Older state documents describing provider activation, Android session acquisition, campaign bootstrap, or first campaign/PC delivery as pending are superseded for those operational points.
