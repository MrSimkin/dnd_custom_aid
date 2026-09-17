# Wave 5 integrated — Wave 6 ready

**Date:** 2026-09-17 (Chile local time)  
**Milestone:** Wave 5 final post-merge continuity closure  
**Result:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED / WAVE 6 READY

## Verified Git state

At reconstruction time:

- `main` = `306377df1a453f531af4b670d2b231c88a3c9419`;
- PR #44 `feat: add Desktop hosted campaign administration` = MERGED;
- final PR head = `20f62b110df80759b5e90d083253b3b87716ff31`;
- exact-head pre-merge Scaffold `35168771704` = SUCCESS;
- post-merge Scaffold `35168920031` = SUCCESS;
- backend job = SUCCESS;
- hosted-database job = SUCCESS;
- Kotlin/build/test/APK job = SUCCESS.

The runtime/provider/owner-QA evidence preceding the merge remains historical evidence and is not recreated by this documentation closure.

## Wave 5 closure

Wave 5 is complete and integrated. Verified owner/provider behavior includes real Desktop OTP authentication, hosted campaign bootstrap/convergence, authoritative roster, DM moderation guard, Player moderation `ACTIVE -> KICKED -> BANNED -> KICKED`, campaign revision progression through `4`, canonical Outlook owner/DM migration, settings persistence, memory-only hosted session across shutdown, reauthentication and explicit sign-out preserving local data/settings.

Do not restart Wave 5 packages or Cloudflare deployment absent new defect/code evidence.

## Canonical DEV identity

Normal hosted DEV owner/DM testing uses the Outlook-backed application identity. Gmail has no current campaign membership and is historical/inactive by default. It may be deliberately reused as a secondary identity in a future multi-user test. Historical Gmail mutation/audit evidence remains untouched.

## Provider operating rules

Cloudflare/Descope/Neon are authenticated capability boundaries. A worker without actual authenticated capability must stop alternate connection probing after establishing the limitation, finish all safe repo/CI work, provide one exact owner-action packet, and resume from non-secret returned evidence.

Never request/paste/commit secrets. Green CI and real provider evidence are distinct. Do not repeat completed provider actions merely because documentation changes.

The existing DEV Worker is `dnd-custom-aid-api`. Wave 5 deployment is already verified and must not be repeated for documentation-only changes.

## Branch/PR housekeeping observations

Two accidental remote refs were present during reconstruction:

- `__noop_should_not_create__` — 0 commits ahead of `main`, 2 behind, no PR;
- `__should_not_create__` — 0 commits ahead of `main`, 59 behind, no PR.

They contain no unique project work and are non-authoritative. The connected GitHub tool did not expose remote branch deletion, so deletion is optional owner housekeeping rather than a blocker.

Open PRs #36 and #37 are older Wave 4-era work items and are not current continuation authority. Current `main`/LATEST govern continuation.

## Wave 6 discovery result

Approved semantics already exist in D-0071/D-0072/D-0073 and partly in code:

- reusable Personal content is creator-owned/private by default;
- explicit use in a Campaign creates a new independent campaign object identity;
- provenance remains visible;
- later Personal edits do not silently update Campaign copies and vice versa;
- Official content remains canonical/read-only; customization creates an independent copy;
- stable IDs, revisions, stale-write rejection, tombstones/non-resurrection and explicit conflict principles continue to apply;
- saved/live content distinctions remain explicit;
- Encounter dependency copies may be created/reconnected when a Personal encounter is used in a Campaign.

Existing `shared.spine` already implements `ContentScope`, `CopyProvenance`, `ScopedObjectIdentity.independentCampaignCopy()`, `Revision`, `SyncMetadata` and tombstone/stale-write primitives.

## First Wave 6 package

Recommended/authorized routine engineering package:

**Reusable-content local persistence foundation**

Scope:

- a bounded reusable-content envelope/catalog using the existing spine scope/provenance primitives;
- content-family discriminator suitable for Monsters/Creatures, NPCs, Homebrew/Rules, Places/Zones, Encounters and related families without forcing their payloads into one universal domain model;
- SQLDelight local persistence + migration;
- create/read/list/update/tombstone operations with optimistic revisions;
- explicit Personal -> Campaign independent-copy operation retaining provenance;
- invariant/migration tests;
- no large Manager UI;
- no hosted/provider activation;
- no object storage.

There is no unresolved owner-level product choice blocking this package and no immediate external-provider handoff.