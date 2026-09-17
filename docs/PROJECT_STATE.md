# Project State — global repository navigation

**Last reconstructed:** 2026-09-17 (Chile local time)  
**Owner integrated-MVP implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Last verified runtime/integration merge:** `0b73d79e46edb7022ace2a2efd161149d9aefc73` (PR #57)  
**Post-merge Scaffold:** `35262999144` — SUCCESS  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 reusable-content persistence foundation:** INTEGRATED  
**Wave 6 Creature payload persistence:** INTEGRATED  
**Wave 6 NPC payload persistence:** INTEGRATED  
**Wave 6 lightweight Homebrew/Rule payload persistence:** INTEGRATED  
**Wave 6 Place payload persistence:** INTEGRATED  
**Wave 6 Zone payload persistence:** INTEGRATED  
**Current normal wave:** Wave 6 — reusable/persistent content architecture continues  
**Next bounded package:** TO BE SELECTED after Zone documentation closure

## 1. Current topology

`main` is the sole normal integrated-MVP trunk. New implementation work uses short-lived outcome-oriented branches from current `main`.

PR #46 `feat: add Wave 6 reusable content persistence foundation` merged as `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` succeeded.

PR #48 `feat: add Wave 6 Creature payload persistence` merged as `5762058645ba8af8fa470dcf185bd2b418af65e9`; replacement Scaffold `35252554882` and post-merge Scaffold `35253188344` succeeded.

PR #51 `feat: add Wave 6 NPC payload persistence` merged as `1aebc6d6b769d0da59b4dfd6e13a1ce52ccbb99a`; push Scaffold `35254216489`, PR Scaffold `35254260799` and post-merge Scaffold `35254527744` all succeeded.

PR #53 `feat: add lightweight Homebrew Rule payload persistence` merged as `fc870fe8303b0f9925c479bdc21c388ef5cc8450`; push Scaffold `35256210643`, PR Scaffold `35256230033` and post-merge Scaffold `35256531651` all succeeded.

PR #55 `feat: add Wave 6 Place payload persistence` merged as `d978a4191054227a03b32ecca3e7ceadc5d6e869`; push Scaffold `35258393882`, PR Scaffold `35258423034` and post-merge Scaffold `35259027937` all succeeded.

PR #57 `feat: add Wave 6 Zone payload persistence` merged as `0b73d79e46edb7022ace2a2efd161149d9aefc73`; push Scaffold `35262051406`, PR Scaffold `35262563057` and post-merge Scaffold `35262999144` all succeeded.

No completed Wave 5 work or integrated Wave 6 foundation/Creature/NPC/Homebrew/Rule/Place/Zone package should be repeated without new defect evidence.

## 2. Wave 5 acceptance baseline

Wave 5 verified real behavior includes Desktop local campaign/workbench persistence, real Descope email-OTP authentication, hosted campaign bootstrap/convergence, authoritative member roster/moderation, campaign revisions, canonical Outlook DEV owner/DM migration, Desktop hosted bootstrap, persisted device-local settings, explicit hosted-session lifecycle and sign-out semantics.

Historical Gmail mutation/audit evidence remains truthful and unchanged.

## 3. Hosted DEV architecture

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Existing DEV Worker: `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

Wave 5 deployment was already performed and verified. Documentation-only and local/shared persistence changes do not require redeployment. Deploy again only if Worker code materially changes or newer evidence specifically requires it.

## 4. Durable provider rules

Provider work requires an explicit capability check. If the worker lacks authenticated provider capability, establish that once, stop alternate connection probing, finish safe repo/code/test/CI work, hand the owner one exact bounded action packet, and resume from non-secret evidence.

Never request/paste/commit passwords, OTPs, provider tokens, DB credentials/connection strings, JWTs, private keys/signing credentials or secret environment values.

Green CI does not prove deployment/auth/provider behavior. Completed real-provider evidence should not be repeated merely because docs changed.

Normal hosted DEV owner/DM identity is Outlook-backed. Gmail is historical/inactive by default and may be deliberately reused as a secondary identity only when a future multi-user test needs it.

## 5. Wave 6 approved semantic baseline and integrated foundation

Existing Shared spine provides `ContentScope`, provenance, stable scoped identity, optimistic revisions/stale-write checks, sync metadata/tombstones and local integrated-spine persistence.

PR #46 integrated family/catalog metadata for Creature, NPC, Homebrew/Rule, Place, Zone and Encounter; Personal/Campaign creation/listing; explicit independent Personal -> Campaign copies; optimistic revisions/tombstones/non-resurrection; SQLDelight migration `18.sqm`; safe recognized Wave-5 Desktop migration and fail-closed unknown-unversioned handling; and focused tests.

Approved product semantics continue to require reusable Personal DM material, explicit independent Campaign copies, provenance visibility and no automatic inheritance/update after copy.

## 6. Integrated Creature payload package

PR #48 adds a human-complete, selectively structured Creature payload, local SQLDelight payload persistence, Personal/Campaign create/read/copy/update/tombstone behavior, migration `19.sqm` with metadata-only Creature backfill and focused invariant/reopen/migration tests.

The original PR failure was only a synthetic legacy-fixture mismatch; repair commit `8a9ec6425c697472a6c57982acbe8e4001b4fc5d` corrected the fixture without changing production migration semantics.

## 7. Integrated NPC payload package

PR #51 adds a human-complete NPC payload while preserving incomplete/Quick NPC validity.

Integrated semantics:

- Quick and Developed NPCs are both valid;
- combat mechanics are optional;
- no completion score exists;
- richer narrative/dossier fields can accumulate incrementally;
- optional full combat mechanics reuse the existing `CreaturePayload` model;
- the Creature mechanics are stored inside the NPC payload rather than represented by a hidden duplicate reusable Creature object;
- Personal -> Campaign copy is independent with retained provenance;
- later source changes do not mutate campaign copies;
- stale writes are rejected and tombstoned NPCs cannot be resurrected by stale mutation.

Persistence scope includes SQLDelight `npc_payload`, migration `20.sqm` with default metadata-only NPC backfill, database-reopen tests, migration-preservation coverage and the bounded synthetic Wave-5 Desktop fixture update.

## 8. Integrated lightweight Homebrew/Rule payload package

PR #53 adds the self-contained lightweight rule/ruling/custom-system payload approved by D-0072.

Integrated semantics include:

- reusable-content display name as title;
- summary and human-readable body text;
- optional category and rationale;
- examples, related references and tags;
- optional notes;
- lifecycle `DRAFT / ACTIVE / RETIRED`;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- independent campaign copies with retained provenance;
- stale-write rejection and tombstone/non-resurrection semantics.

Persistence scope includes SQLDelight `homebrew_rule_payload`, migration `21.sqm` with metadata-only Homebrew/Rule backfill, explicit list serialization, database-reopen/migration/copy/revision coverage and the bounded synthetic legacy Desktop fixture adjustment.

Structured races/sub-races, classes/subclasses, backgrounds, feats, spells, ordinary items and magic items remain outside this lightweight record and require family-appropriate models/editors. No universal arbitrary JSON/executable homebrew payload was introduced.

## 9. Integrated Place payload package

PR #55 establishes canonical reusable Place data while preserving D-0072's rule that Shops are specialized Places rather than a separate top-level content family.

Integrated semantics include:

- `PlaceKind.PLACE` and `PlaceKind.SHOP`;
- reusable-content display name as the canonical Place name;
- summary;
- area/geographic context;
- function/purpose;
- presentation/atmosphere text;
- services and interactives;
- hooks;
- player-safe text;
- DM-only notes;
- paper references and tags;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- independent campaign copies with retained provenance;
- stale-write rejection and tombstone/non-resurrection semantics.

Persistence scope includes SQLDelight `place_payload`, migration `22.sqm` with metadata-only Place backfill defaulting to ordinary `PLACE`, explicit list serialization, database-reopen/migration/copy/revision coverage and the bounded synthetic legacy Desktop fixture adjustment.

The package deliberately does not introduce generalized Place <-> NPC/Scene/Zone/Encounter relationship/dependency-copy graphs, clocks, media/object-storage references, automatic reveal/publication behavior or Stage/Place Manager UI.

## 10. Integrated Zone payload package

PR #57 establishes canonical reusable prepared Zone / Zone Brief data while preserving D-0072's distinction between authored Area/Zone Brief preparation and live Dungeon Turn movement-zone state.

Integrated semantics include:

- reusable-content display name as canonical Zone name;
- summary and area/context;
- presentation/atmosphere;
- space/layout description;
- exploration guidance;
- interactives;
- clues;
- checks;
- consequences;
- encounter brief/orientation;
- DM guidance;
- player-safe text;
- paper references and tags;
- Personal/Campaign create/read/copy/update/tombstone behavior;
- independent campaign copies with retained provenance;
- stale-write rejection and tombstone/non-resurrection semantics.

Persistence scope includes SQLDelight `zone_payload`, migration `23.sqm` with metadata-only Zone backfill, explicit list serialization, database-reopen/migration/copy/revision coverage and the bounded synthetic legacy Desktop fixture adjustment.

The package deliberately does not introduce generalized Place/NPC/Zone/Encounter relationship/dependency graphs, Encounter dependency-copy machinery, live Dungeon Turn movement-zone state, clocks/triggers, media/object-storage references, hosted reusable-content sync or Dungeon/Zone Manager UI.

## 11. Wave 6 continuation selection

The next bounded Wave 6 implementation package is intentionally **not selected by this documentation closure itself**.

After this closure is merged, re-read current D-0072/D-0073 authority and select one dependency-safe package from the integrated foundation + Creature + NPC + Homebrew/Rule + Place + Zone state. Encounter is now the remaining reserved rich reusable family, but its first package should remain bounded and must not prematurely force the entire future relationship/dependency graph.

Under the coherent-task continuation rule, routine safe green boundaries after this closure do not require separate owner confirmation.

## 12. Security/cost residuals

Hard external-service operating budget remains USD $0. Repository is intentionally public. Object-storage provider selection remains deferred.

Known residual: owner-local backend install reported 3 high-severity npm vulnerabilities. Do not run `npm audit fix --force` blindly; inspect packages/reachability/fixed versions when a relevant hardening package is scheduled.

## 13. Resume rule

Read `docs/checkpoints/LATEST.md`, its referenced checkpoint, `docs/BRANCH_STATUS.md`, relevant D-0071/D-0072/D-0073/D-0075 records and `docs/ROADMAP.md`.

Finish the Zone documentation closure. Then resume from current `main`, select the next bounded dependency-safe Wave 6 continuation package from current authority and proceed autonomously through routine safe gates. Do not rebuild prior Wave 6 packages, introduce generalized relationship graphs prematurely, generalize all content into one universal abstraction, or redeploy Cloudflare for documentation/local-persistence work.
