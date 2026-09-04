# Phase 4 Batch M4e — D06 rules/source semantic badges

**Date:** 2026-09-04  
**Status:** IMPLEMENTED — clean standard gate pending at checkpoint creation  
**Safety branch:** `tmp/phase4-m4e-rules-source-badges`  
**Authoritative M4d base:** `b1032aa23d896de702120d4a55e3f0c1abe2acb2`  
**Accepted product commit:** `6522ebc7f6cfcbcc44a2c559601ffb9ae4b4d80d`  
**Accepted product tree:** `f86f30d82e389e7572b6756a0a84a5673ec0f529`  
**Canonical `main`:** untouched  
**Historical L candidate:** untouched

## Purpose

Close M3 hole D06 with the smallest reusable semantic badge treatment for class/subclass rules generation and source/provenance. Color supports the presentation but never carries the meaning by itself.

## Shared textual semantics

Added `CharacterSemanticLabels.kt` with pure, UI-independent labels:

- `DND_5E` -> `5e`;
- `DND_5_5E` -> `5.5e`;
- `CUSTOM` -> `Custom`;
- `UNSPECIFIED` -> `Sin especificar`;
- nonblank source -> `Fuente · <source>`;
- null/blank source -> no source badge.

Focused `CharacterSemanticLabelsTest` proves all four rules-family mappings plus optional/trimmed source behavior.

Staging commits:

- `21bde8210e535dda036ccd82db635ed7db7635f8` — shared labels;
- `0d7593064e227a4798601bd4e0ccfb78a5e3e19e` — focused tests.

## Reusable Android badge primitive

Added `CharacterSemanticBadgesV4.kt`:

- `CharacterSemanticBadgeV4` renders explicit text inside a compact semantic surface;
- kinds: `RULES`, `SOURCE`, `STATE`, `NEUTRAL`;
- rules/source/state use different Material containers, but text remains authoritative;
- labels are single-line with ellipsis so long source names do not break phone width;
- `CharacterRulesSourceBadgesV4` stacks rules + optional source safely without an experimental layout dependency.

The `STATE` kind exists so M4f can reuse the same primitive instead of introducing a second badge grammar. M4e does not by itself declare D07 complete.

Staging commits:

- `1901fc200f2cf4dc627bcb9fbbb8ef37304e4589` — initial primitive;
- `329911b40111f827d1dced86f0d0f10d17fb31d5` — phone-safe source presentation.

## Reachable class/subclass integration

`CharacterClassIdentityV4` no longer joins rules/source as undifferentiated plain metadata on the active identity surface.

The reusable rules/source badges are reached in exactly three places:

1. each saved class/subclass identity row;
2. selected official class detail in the editor;
3. selected official subclass detail in the editor.

Official/manual/homebrew behavior and permissiveness are unchanged. D06 adds presentation semantics only; it does not add legality enforcement or a rules corpus.

Dropdown option labels remain compact textual selector labels; the active identity and selected-detail surfaces carry the distinct semantic badge grammar required by D06.

## Settings preview reuse

The M4d miniature settings sheet now reuses `CharacterSemanticBadgeV4` instead of its former local `SettingsPreviewBadge` duplicate:

- the `5.5e` preview badge uses the shared rules-family mapping and `RULES` kind;
- `Preparado` uses the same primitive's `STATE` kind as a representative state treatment.

This demonstrates the reusable grammar while leaving M4f responsible for applying state badges to real state-rich surfaces.

## Guarded integration gate

Temporary exact-match workflow:

- workflow `33907415642` — **SUCCESS**.

It verified before accepting the product commit:

- exactly three `CharacterRulesSourceBadgesV4` reaches on the real class/subclass identity surface;
- settings preview reuses the shared primitive;
- explicit textual source label is present;
- old local `SettingsPreviewBadge` duplicate is removed;
- focused shared mapping tests PASS;
- all shared/Kotlin desktop tests PASS;
- Android debug assemble PASS;
- Desktop build PASS;
- backend dependency install/type-check PASS;
- temporary patch/workflow helpers self-deleted in accepted product commit `6522ebc7f6cfcbcc44a2c559601ffb9ae4b4d80d`.

## Boundary

M4e closes D06 implementation completeness only. Final visual density/readability remains M6 owner QA.

At checkpoint creation, one ordinary `scaffold-check.yml` run on this helper-free product tree plus this checkpoint remains required before declaring M4e GREEN and promoting it to `implementation/phase4-preqa-consolidation`.

After clean promotion, the final M4 implementation slice is **M4f / D07 state-badge grammar closure**.
