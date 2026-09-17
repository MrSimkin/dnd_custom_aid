# Checkpoint — Wave 6 lightweight Homebrew/Rule payload persistence integrated

**Date:** 2026-09-17 (Chile local time)  
**Repository:** `MrSimkin/dnd_custom_aid`  
**Normal trunk:** `main`  
**Integrated merge:** `fc870fe8303b0f9925c479bdc21c388ef5cc8450`  
**PR:** #53 — `feat: add lightweight Homebrew Rule payload persistence`  
**Final branch head:** `0ef3b3466582482fc89f3025c1adf50a5fd40580`  
**Push Scaffold:** `35256210643` — SUCCESS  
**PR Scaffold:** `35256230033` — SUCCESS  
**Post-merge Scaffold:** `35256531651` — SUCCESS

## Milestone status

The bounded Wave 6 lightweight Homebrew/Rule reusable-content payload + local persistence core is integrated into `main` on top of the reusable-content foundation, Creature payload package and NPC payload package.

Wave 6 remains active. The next bounded package is **Place payload + local persistence core**.

## Integrated Homebrew/Rule semantics

PR #53 implements the lightweight rule/ruling/custom-system form approved by D-0072 without flattening structured game-content families into a generic document.

Integrated payload semantics include:

- reusable-content display name remains the rule title;
- summary and human-readable body text;
- optional category and rationale;
- examples;
- related references;
- tags;
- optional notes;
- lifecycle `DRAFT / ACTIVE / RETIRED`.

The package deliberately remains a domain-specific lightweight rule record. Structured races/sub-races, classes/subclasses, backgrounds, feats, spells, ordinary items and magic items still require family-appropriate models/editors and are not represented as arbitrary Homebrew/Rule JSON.

## Integrated persistence scope

PR #53 adds:

- `HomebrewRuleLifecycle`, `HomebrewRulePayload` and `HomebrewRuleContent` domain types;
- Personal and Campaign create/read flows;
- explicit Personal -> Campaign independent copy with retained provenance;
- source/copy independence after copy;
- payload mutation through existing optimistic revision/stale-write/tombstone semantics;
- SQLDelight `homebrew_rule_payload` persistence;
- explicit list serialization for examples, related references and tags;
- migration `21.sqm`, including metadata-only Homebrew/Rule backfill;
- database-reopen, migration, copy, revision and tombstone/non-resurrection coverage;
- the bounded Desktop legacy-fixture adjustment required to simulate the verified pre-Wave-6 schema.

No hosted API, Worker deployment, provider or object-storage change was required.

## Verification evidence

The implementation was committed as one bounded package at `0ef3b3466582482fc89f3025c1adf50a5fd40580`.

Validation evidence:

- push Scaffold `35256210643` — SUCCESS;
- pull-request Scaffold `35256230033` — SUCCESS;
- PR #53 merged as `fc870fe8303b0f9925c479bdc21c388ef5cc8450`;
- post-merge Scaffold `35256531651` — SUCCESS, including backend, hosted-database, Kotlin build/tests and Android debug APK upload.

## Preserved architecture boundaries

This package does **not** introduce:

- Homebrew & Rules Manager UI;
- structured race/class/subclass/background/feat/spell/item payloads;
- hosted reusable-content synchronization;
- import/export or AI helpers;
- Place/Zone/Encounter dependency graphs;
- object-storage/provider activation;
- a universal arbitrary/executable content payload abstraction.

## Next bounded package — Place payload core

Proceed with a self-contained **Place payload + local persistence core** using `ReusableContentFamily.PLACE`.

The first package should establish canonical reusable Places while preserving D-0072's rule that a Shop is a specialized Place rather than a separate top-level system. The initial payload may include human-facing presentation/retrieval data such as summary, area/geographic context, function, presentation text, services/interactives, hooks, player-safe text, DM-only notes, paper references and tags, with exact field decomposition delegated to implementation.

Keep this first Place package self-contained. Do **not** yet introduce generalized Place <-> NPC/Scene/Zone/Encounter dependency-copy graphs, clocks, media/object-storage references or automatic reveal/publication behavior. Those relationships should be introduced deliberately in a later bounded relationship package once the canonical Place record exists.

### Sequencing rationale

Place is the next dependency-safe domain because it can provide a useful canonical reusable record before Zone and Encounter force richer dependency/reference semantics. This preserves incremental architecture instead of smuggling a generalized graph model into the first location payload.

## Provider/cost state

No Cloudflare, Descope, Neon or object-storage action is required for the Place local-persistence package. The hard external-service operating budget remains USD $0.

## Continuation rule

After this documentation closure is integrated, resume from current `main` on a short-lived outcome branch for the Place payload + local persistence core. Preserve Personal/Campaign independent-copy semantics, stable identity, revisions, tombstones and provenance; defer cross-object graph semantics until a domain actually requires them.
