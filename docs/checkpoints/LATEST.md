# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Product boundary:** physical `preqa.9` QA reopened P1/P2 and transversal presentation consistency  
**Failed QA candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Acceptance boundary:** bounded repair + revalidation before P17 tablet QA resumes  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

Read first:

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — current physical evidence, exact failures and reopened repair boundary;
2. `docs/PROJECT_STATE.md` — live Player state and repair gate;
3. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — accepted P1/P2 behavior;
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md` — exact failed candidate and prior automated evidence;
5. `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` — cross-branch truth and authorization boundary;
6. `docs/BRANCH_STATUS.md` — branch roles and historical refs;
7. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable P1–P17 repair authorization;
8. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — P17 tablet-QA policy.

Historical checkpoints remain evidence. Any older live instruction saying to continue broad `preqa.9` phone/tablet acceptance testing is superseded by the physical failure recorded here.

## Current physical QA evidence

### PASS preserved

`preqa.9 / 40900` physically passed update-in-place and persistence sanity:

- install over previous QA build;
- launch;
- campaign and character preservation;
- representative persisted data across major Player sections;
- full close/reopen.

### Blocking shared failures found

Physical P1/P2 testing found:

- General HP edits do not live-propagate to Combate without explicit `Guardar`, contrary to the owner-approved no-extra-commit interaction;
- max-HP reduction does not enforce `current HP <= max HP`; the owner physically observed Combate displaying `20/10` after the tested reduction/save path;
- the agreed subtle changed-HP glow/pulse is absent;
- Combat `Establecer PV` does not change current HP, although the analogous temporary-HP exact correction works;
- `Daño — Cantidad — Curar` is visibly out of proportion with surrounding Combate controls, reopening the required transversal size/margin/padding consistency audit rather than just one local widget.

Physical passes inside the same test are also preserved: max-HP increase did not silently heal, damage arithmetic/temp-HP semantics worked, healing capped at max, amount clearing worked, and Combat operation results projected back to General.

Detailed evidence is in `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`.

## Current interpretation

- `preqa.9 / 40900`: **failed physical acceptance candidate**; retain as evidence, do not present it as current acceptable baseline;
- P1: **REOPENED by physical evidence**;
- P2: **REOPENED by physical evidence**;
- transversal presentation consistency: **REOPENED for exact existing-boundary classification and repair**;
- tablet P17 physical QA: **PAUSED** because the current defects affect shared/systemic Player behavior;
- Phase 4A owner acceptance: pending;
- DM implementation: blocked until explicit Phase 4A closure.

No P18 is created. Real physical QA evidence reopens the relevant existing repair boundaries under the durable authorization.

## Exact next action

Inspect and repair the successor implementation against the already-approved P1/P2 behavior and the existing presentation-consistency audit. Add regression coverage for the concrete physical failures, run focused plus aggregate validation, and package a new monotonic physical-QA candidate if the implementation changes materially.

After a new candidate is green, physically recheck the reopened phone boundary first. Resume representative P17 tablet QA only after the hard shared defects no longer make tablet acceptance evidence misleading.

## Historical automated proof for failed `preqa.9`

Candidate commit `cd0c203d337c062fa388010d300e875f2f54ced7` had normal Scaffold run `34726572588` — **SUCCESS** — and artifact ID `10307444450`, digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

That CI evidence remains valid for what it tested; physical QA demonstrated missing behavioral/interaction coverage and therefore overrides any implication of acceptance.

## Cross-branch reminder

`main` is intentionally divergent and carries later global/Phase 5A/DM discovery decisions absent here. This branch carries current Player runtime and reopened repair work absent from `main`.

Do not force one branch over the other. Future integration must preserve both valid lines.
