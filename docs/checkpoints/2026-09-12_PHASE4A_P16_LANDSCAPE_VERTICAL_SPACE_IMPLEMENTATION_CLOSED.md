# Phase 4A — P16 landscape / vertical-space implementation closure

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Authority: `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md`

## Status

P16 IMPLEMENTATION CLOSED / AUTOMATION GREEN.

Owner/device acceptance is not claimed here. P17 remains the physical phone/tablet QA gate after the aggregate P1–P16 sweep.

## Final implementation evidence

Primary source repair:

`82fe1fd5f90d1844729ca01bec918a5981a33355` — `repair: implement P16 vertical-space policy`

Detailed audit: `docs/checkpoints/2026-09-12_PHASE4A_P16_VERTICAL_SPACE_REPAIR_AUDIT.md`.

The repair:

- derives Player vertical-pressure state from the editor's actual `BoxWithConstraints` viewport rather than relying only on static configuration height;
- preserves form-factor classification and phone/tablet navigation semantics separately from vertical pressure;
- propagates one measured layout context through `CharacterAdaptiveShellV4`;
- retains the existing reduced-height Combat HUD reflow;
- converts the seven audited collection/spell sticky headers into ordinary scrolling content under `REDUCED` / `CONSTRAINED` height while preserving all controls and actions;
- keeps centralized vertical-space thresholds in `CharacterLayoutContextV4.kt`;
- remains compatible with Android `adjustResize`, allowing IME-induced usable-height changes to feed the same policy.

## Final automated gate

Normal Scaffold run `34721112514` on commit `2544fece916883a074fd30b94d26aa24e9e1715c`, containing P16 source commit `82fe1fd5...` unchanged plus its audit checkpoint, completed GREEN:

- backend typecheck: success;
- exact Kotlin/shared test and build gate: success;
- Android assemble: success;
- Desktop build: success;
- Android debug APK upload: success.

## Closure conclusion

The demonstrated P16 implementation gaps are repaired and automation-green. Final behavioral acceptance across real phone/tablet portrait/landscape configurations remains intentionally deferred to P17.

No merge to `main` is authorized by this checkpoint. The current APK is still not the final Phase 4A QA candidate because P13 and the aggregate P1–P16 regression sweep remain outstanding.
