# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Product boundary:** physical `preqa.9` QA reopened P1/P2 and transversal presentation consistency; canonical HP + General navigation/save repairs implemented  
**Failed QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Acceptance boundary:** complete bounded repair + revalidation before P17 tablet QA resumes  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

Read first:

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — current physical evidence and live repair progress;
2. `docs/PROJECT_STATE.md` — live Player state and repair gate;
3. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — accepted P1/P2 behavior;
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md` — exact failed candidate and prior automated evidence;
5. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` — cross-branch truth and authorization boundary;
6. `docs/BRANCH_STATUS.md` — branch roles and historical refs;
7. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable P1–P17 repair authorization;
8. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — P17 tablet-QA policy.

Historical checkpoints remain evidence. Any older live instruction saying to continue broad `preqa.9` phone/tablet acceptance testing is superseded by the physical failure and repair progress recorded here.

## Current physical QA evidence

`preqa.9 / 40900` physically passed update-in-place/persistence sanity, then failed the shared P1/P2 acceptance boundary. Blocking findings were:

- General HP edits required explicit `Guardar` before Combat saw them;
- lowering maximum HP could persist/project invalid `current > max`; owner observed `20/10`;
- subtle changed-HP feedback was absent;
- Combat `Establecer PV` current-HP correction was ineffective while temp-HP correction worked;
- `Daño — Cantidad — Curar` was visibly out of proportion with surrounding Combate controls.

Preserved passes include max-HP increase without silent healing, damage/temp-HP arithmetic, healing cap, amount clearing and Combat-operation-to-General projection.

## Repair progress

### R1 — canonical HP boundary — IMPLEMENTED + REGRESSION-LOCKED / AUTOMATION PENDING

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical exact current/max HP normalization.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational persistence now carries proposed max HP and normalizes current against that max.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` — direct HP normalization regression tests.
- `49833bb64857376c4931e91c5af684bd287b2aba` — operational merge regression tests lock `20/10 → 10/10`, proposed max persistence, no silent healing and temp-HP preservation.

### R2 — General HP navigation/save propagation — IMPLEMENTED / AUTOMATION PENDING

`da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — `fix: persist General HP across navigation and save`:

- normal `Guardar` canonicalizes current/max HP and non-negative temp HP;
- operational sync now refreshes max/current/temp HP together;
- leaving General for another tab canonically flushes valid HP draft values, so General→Combate no longer depends on global `Guardar`;
- transient/unparseable numeric drafts are not force-persisted;
- the shared R1 HP operation is reused rather than duplicated.

The R2 product diff was inspected and contains only the intended `CharacterEditorV4.kt` changes. Temporary guarded patch machinery used to overcome the connector's lack of line-patch writes was removed after the product commit.

R1/R2 are not yet declared CI-green or physically accepted.

## Current interpretation

- `preqa.9 / 40900`: failed physical acceptance candidate; historical evidence only;
- P1 HP state/persistence defects: source repair implemented through R1/R2, validation pending;
- P2 feedback defect: still open;
- transversal presentation consistency: still open and pending historical boundary classification;
- P17 tablet QA: paused;
- Phase 4A owner acceptance: pending;
- DM implementation: blocked until explicit Phase 4A closure.

No P18 is created.

## Exact next action

Recover and repair the accepted P2 subtle changed-state feedback contract, then classify/repair the transversal size/margin/padding inconsistency including `Daño — Cantidad — Curar`. After that, run focused + aggregate validation and package a new monotonic physical-QA candidate only after green automation.

## Historical automated proof for failed `preqa.9`

Candidate `cd0c203d337c062fa388010d300e875f2f54ced7` had normal Scaffold run `34726572588` — SUCCESS — and artifact ID `10307444450`, digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

CI evidence remains valid for its scope; physical QA demonstrated missing behavioral/interaction coverage.

## Cross-branch reminder

`main` is intentionally divergent and carries later global/Phase 5A/DM discovery decisions absent here. This branch carries current Player runtime and reopened repair work absent from `main`.

Do not force one branch over the other. Future integration must preserve both valid lines.
