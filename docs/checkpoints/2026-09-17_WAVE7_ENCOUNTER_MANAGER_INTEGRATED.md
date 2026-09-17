# Checkpoint — Wave 7 Desktop Encounter Manager integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `7000535b78df2b2a7149b019796ff3d5903fdb3d`  
**PR:** #75 — `feat: add Desktop Encounter Manager core`  
**Initial implementation head:** `91d744a66e3ff18ee9190c41d4dbb3970ca412fe`  
**Final implementation head:** `a68f62897d6178f1da1c19deb2721ea04abc837a`  
**Initial push Scaffold:** `35286844850` — FAILED (Kotlin visibility mismatch)  
**Corrected push Scaffold:** `35287257508` — SUCCESS  
**PR Scaffold:** `35287507268` — SUCCESS  
**Post-merge Scaffold:** `35287713130` — SUCCESS

## Milestone status

The bounded **Desktop Encounter Manager / Encounter Creator — local authoring core** is integrated into `main`.

The implementation reuses the existing Wave 6 Encounter persistence and its domain-specific Creature/NPC dependency copy/remap behavior. No new schema or generalized dependency framework was required.

The next bounded Wave 7 package is **PC Manager / Audit — Desktop inspection/audit core**.

## Integrated Encounter Manager behavior

The Desktop Managers hub now includes a dedicated `Encuentros` surface with:

- Personal and active-Campaign Encounter browse/search/create/open/edit;
- full-text and tag filtering;
- explicit Personal -> Campaign independent copy with retained provenance;
- atomic display-name + Encounter payload save under one optimistic revision;
- stale-write rejection and tombstone/non-resurrection semantics inherited from the reusable-content spine.

Encounter authoring exposes the current persisted model: summary, environment, context, DM guidance, tags, notes and participants/groups.

Each participant can be authored from a same-scope Creature/NPC choice or as a freeform label-only group, with quantity, `EXPECTED` / `RESERVE` / `CONDITIONAL` readiness, condition text, encounter-specific overrides and notes. Encounter-specific overrides do not mutate the source Creature/NPC record.

## Dependency and copy semantics

The package preserves the already-integrated Encounter repository rules:

- referenced dependencies must be active Creature/NPC reusable content;
- referenced dependencies must match the Encounter scope;
- Personal -> Campaign Encounter copy creates independent Campaign dependency copies where needed;
- repeated references to one source dependency are deduplicated during the copy/remap;
- the resulting Campaign Encounter points to the Campaign dependency IDs;
- later edits to Personal sources do not automatically alter the Campaign copies.

This remains domain-specific Encounter behavior. No generalized dependency graph was introduced.

## Atomic update behavior

`EncounterContentRepository` now exposes the same atomic display-name + payload update pattern used by the other visible Managers. It validates the Encounter family, persisted payload and new participant dependencies, then changes reusable-content display name and Encounter payload inside one revision-controlled mutation.

## Validation evidence

Focused coverage verifies conservative unique local DM resolution, Personal Encounter creation, same-scope participant-source retrieval, freeform participants, full payload editing, query/tag filtering, atomic update, stale-write rejection, explicit Personal -> Campaign dependency remapping/copy independence and tombstone/non-resurrection.

The first implementation push exposed one compiler-only visibility defect: a public controller method returned an internal Desktop participant-source type. Commit `a68f62897d6178f1da1c19deb2721ea04abc837a` aligned that method visibility without changing domain behavior or package scope.

Exact evidence:

- initial implementation head `91d744a66e3ff18ee9190c41d4dbb3970ca412fe`;
- initial push Scaffold `35286844850` — FAILED on the visibility mismatch;
- final implementation head `a68f62897d6178f1da1c19deb2721ea04abc837a`;
- corrected push Scaffold `35287257508` — SUCCESS;
- PR Scaffold `35287507268` — SUCCESS;
- PR #75 merged as `7000535b78df2b2a7149b019796ff3d5903fdb3d`;
- post-merge Scaffold `35287713130` — SUCCESS, including backend, hosted database, Kotlin/shared/Desktop tests, Android debug assembly and APK upload.

## Preserved boundaries

This Encounter Manager slice does **not** add a new Encounter schema/migration, generalized dependency graph infrastructure, live initiative/combat working state, automatic encounter balance authority/simulation, automatic mutation of source Creature/NPC records, hosted reusable-content synchronization or provider/deployment changes.

## Next package — PC Manager / Audit

The next bounded package is **PC Manager / Audit — Desktop inspection/audit core**.

D-0072 requires the Desktop DM surface to operate over the same canonical PC records used by the Player App. It must not become a second character-builder and explicit DM correction must not silently impersonate the Player.

The next implementation should first inspect the existing PC data, ownership/controller authority, synchronization and history architecture before adding persistence.

Expected bounded direction:

- campaign PC overview and retrieval;
- complete DM inspection of canonical PC records;
- clear distinction between available PC data freshness and synchronization freshness;
- meaningful grouped audit/history using existing records where available;
- explicit DM correction/edit entry points that preserve history and authority semantics;
- keep campaign membership, PC ownership and PC control distinct;
- focused controller/domain tests around inspection, authority and correction boundaries.

Approved later PC Manager responsibilities include freeze/unfreeze, lifecycle administration, ownership/controller administration, duplication and PC Sheet PDF export. Their exact first implementation ordering should follow evidence from the existing architecture rather than creating parallel models.

## Provider/cost state

No Cloudflare, Descope, Neon deployment or object-storage action was required. Hard external-service budget remains USD $0.
