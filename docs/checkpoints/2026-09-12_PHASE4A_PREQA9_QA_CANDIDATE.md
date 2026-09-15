# Phase 4A — preqa.9 repaired QA candidate

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** AUTOMATION GREEN / READY FOR PHYSICAL OWNER QA / NOT OWNER-ACCEPTED

## Candidate identity

- version: `0.4.0-preqa.9`
- versionCode/build: `40900`
- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`
- candidate commit message: `build: advance repaired QA candidate to preqa.9`

The version/build bump is intentionally monotonic from the owner-tested `0.4.0-preqa.8 / 40800`, so physical QA cannot be confused with the build that originally generated the repair backlog.

No new Player behavior was introduced by the candidate commit. The repaired P1–P16 product behavior comes from the already automation-qualified product boundary `d630270f2f3d8fab94f3c1290963c2da7afaf06d`; intervening work before the version bump is continuity documentation only.

## Normal automated gate

Scaffold checks run:

`34726572588`

Exact head SHA:

`cd0c203d337c062fa388010d300e875f2f54ced7`

Conclusion: **SUCCESS**.

Demonstrated by the run:

- backend dependency install and Worker type-check: success;
- stable CI debug keystore preparation: success;
- Kotlin/shared/Android/Desktop build-and-test surface: success;
- Android debug APK upload: success.

## Artifact

- artifact ID: `10307444450`
- artifact name: `dnd-custom-aid-debug-apk`
- artifact size: `13608921` bytes
- artifact digest: `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`
- workflow run: `34726572588`
- artifact source SHA: `cd0c203d337c062fa388010d300e875f2f54ced7`

The digest above is the GitHub Actions artifact digest. Do not relabel it as an independently computed APK-file SHA-256.

## What this closes

This candidate closes the technical packaging/automation step after the P1–P16 repair implementation:

- P13's formerly pending normal Scaffold condition is no longer pending;
- the aggregate repaired Player source is automation-qualified;
- the repaired source now has a unique successor QA identity;
- there is no additional authorized P17 code-repair item to implement before physical QA.

P17 is the already closed **tablet-QA gate policy**, not a new implementation package.

## What remains open

Physical owner/device acceptance remains open and cannot be inferred from CI.

### Targeted phone regression / acceptance

Use the `preqa.9 / 40900` artifact and concentrate on high-value repaired boundaries rather than replaying every historical screen mechanically:

- canonical HP across General/Combate, including damage/heal/temp HP;
- compact Combate HUD footprint and constrained-height behavior;
- representative P6 reorder interaction/persistence;
- representative P9 editor sizing and keyboard Save/Cancel reachability;
- Application Settings density/theme/help behavior;
- PC Settings information architecture;
- P14 Table Mode;
- P15 Supercompact;
- P16 phone landscape / vertical-space / sticky-region behavior;
- Conjuros sticky/source-context behavior;
- persistence/reopen and at least one cross-surface state sanity check.

### P17 physical tablet QA

Proceed to representative tablet portrait/landscape QA when the phone result does not expose a hard shared/systemic failure that would make tablet evidence meaningless.

Representative tablet coverage includes:

- portrait and landscape navigation/adaptive layout;
- Combate / P5 / P16;
- Conjuros sticky behavior;
- representative P9 editor/IME behavior;
- P6 reorder;
- PC Settings and Application Settings responsiveness;
- P15 Supercompact;
- P14 Table Mode;
- representative larger text/density;
- persistence/reopen;
- at least one canonical shared-state sanity check such as HP.

A bounded/local phone defect does not automatically block tablet QA. Actual tablet PASS/FAIL requires actual tablet evidence. A real-device failure may reopen the relevant P1–P16 repair point.

## Authorization boundary

The 2026-09-11 durable authorization covers the accepted Player P1–P17 repair/validation cycle. The owner's 2026-09-12 instruction additionally authorizes continuity correction on the appropriate branches and continuation under the real existing authorizations.

This candidate does **not** authorize or imply:

- self-awarded owner/device acceptance;
- unrelated Player feature expansion;
- destructive history rewriting;
- DM feature implementation before explicit Phase 4A owner closure.

## Exact continuation point

The repository is now at a real owner/device gate.

Do not manufacture another implementation item merely to keep coding. The next evidence required is physical QA of `0.4.0-preqa.9 / 40900`.

If QA finds a defect, reopen only the relevant accepted repair boundary and repair it on `implementation/phase4a-successor-cycle`. If the repaired baseline is owner-accepted, record explicit Phase 4A acceptance/closure before DM implementation begins.