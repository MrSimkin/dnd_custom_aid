# Checkpoint — Wave 6 reusable-content foundation integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`  
**PR:** #46 — `feat: add Wave 6 reusable content persistence foundation`  
**Final branch head:** `008b196ec1fc36cbd637cfbb8b2b4915109ddc8d`  
**Exact-head Scaffold:** `35219548535` — SUCCESS  
**PR Scaffold:** `35219863619` — SUCCESS  
**Post-merge Scaffold:** `35220099721` — SUCCESS

## Milestone status

The first bounded Wave 6 reusable-content persistence foundation is technically integrated into `main`.

Wave 6 itself is **not complete**. Normal development continues from this integrated foundation, but no later Wave 6 package is asserted by this checkpoint.

## Integrated scope

PR #46 added the local reusable-content persistence foundation on top of the existing Shared scope/provenance/revision spine:

- reusable-content family/catalog metadata for:
  - Creature;
  - NPC;
  - Homebrew/Rule;
  - Place;
  - Zone;
  - Encounter;
- Personal and Campaign scoped creation/listing;
- explicit Personal -> Campaign copy creating a new independent campaign object identity while retaining provenance;
- independent revision history after copy, with no automatic source-to-copy inheritance/update relationship;
- optimistic revision checks and stale-write rejection;
- tombstones and non-resurrection behavior;
- local SQLDelight persistence;
- migration `18.sqm`;
- Desktop migration support for the verified unversioned Wave-5 local schema;
- fail-closed refusal of unknown unversioned Desktop databases rather than destructive guessing;
- invariant, migration and Desktop reopen/migration tests.

## Verification evidence

The implementation branch head `008b196ec1fc36cbd637cfbb8b2b4915109ddc8d` passed Scaffold `35219548535`, including backend, hosted-database and Kotlin/build/test/APK jobs.

The PR-triggered Scaffold `35219863619` also passed before merge.

After PR #46 merged to `main` as `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`, post-merge Scaffold `35220099721` completed successfully.

Focused tests cover, among other recorded invariants:

- Personal -> Campaign copy uses a new identity and retains source provenance;
- later source rename/mutation does not update the independent Campaign copy;
- stale mutation is rejected;
- tombstoned content cannot be silently resurrected;
- family/scope listing works as intended;
- migration `18.sqm` preserves existing pre-Wave-6 campaign data;
- Desktop persistent data survives reopen;
- the recognized unversioned Wave-5 Desktop schema migrates safely;
- unknown unversioned Desktop DBs are refused fail-closed.

## Deliberately not implemented by this package

Do not infer any of the following from this checkpoint:

- large Manager UI;
- hosted reusable-content synchronization;
- object-storage/provider activation;
- a universal executable content payload model.

Those remain future work unless a later integrated checkpoint explicitly supersedes this state.

## Provider/cost state

No Cloudflare, Descope or Neon provider action was required for this local/shared persistence package. Do not redeploy Cloudflare because of this documentation milestone.

The hard external-service operating budget remains USD $0. The repository remains intentionally public; secrets must never be requested, pasted or committed.

## Continuation rule

Resume from current `main`.

Read `docs/checkpoints/LATEST.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md`, relevant D-0071/D-0072/D-0073/D-0075 decisions and `docs/ROADMAP.md`.

Then determine the next **bounded** Wave 6 package from current architecture and implementation seams. Do not rebuild the integrated persistence foundation and do not assume a later package already exists.