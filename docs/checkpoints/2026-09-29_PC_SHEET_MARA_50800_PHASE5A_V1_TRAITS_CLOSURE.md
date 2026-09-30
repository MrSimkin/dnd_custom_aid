# Mara 50800 — Phase 5A Custom-v1 Traits reclaim closure

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Validated head:** `4255e38b92cec8f9e04257ef157ddc2e66a1df10`  
**Workflow:** 4318 / `36649417194` — SUCCESS  
**Exact proof artifact:** `pc-sheet-populated-template-proofs` / `11069468492`  
**Artifact digest:** `sha256:e1e34f6e1e82866a23095fc65ecbaeab11606305b9d8ded22676741122b8def3`  
**Status:** INTERNAL EXACT-ARTIFACT PROGRESSION PASS / MASTER MATRIX REMAINS OPEN

## Scope

This bounded Phase-5 repair addressed the exact Custom-v1 M50800-27 failure discovered during cross-family inspection of run 4309:

- exhausted native Traits sibling streams continued reserving most of each page;
- logical trait records were flattened into physical lines and could begin a page as an anonymous tail.

No Resources/Options repair is claimed here.

## Exact artifact result

Real Mara output observations at the exact 4318 head:

- Fantasy: 37 pages;
- Custom v1: 27 pages;
- Custom v2 per Attribute: 16 pages;
- Custom v2 per Ability: 16 pages.

Counts remain observations only.

Custom-v1 evidence:

- pages 9–11 retain the complete source-native Traits scaffold while left-side class/race/feat/proficiency/language/other streams still contain content;
- pages 12–18 switch to the reclaimed `Detalles de Rasgos` continuation once those sibling streams are exhausted;
- reclaimed pages do not carry the rejected empty class/race/feat/proficiency/language scaffold;
- reclaimed pages contain no hidden source-form underlay from a clipped whole-page logo form;
- logical trait detail groups remain whole; pages 12–17 begin with owning `Rasgo extenso N` identities;
- the final reclaimed page begins with explicit operational/reference identities such as `Resistencia`, `Vulnerabilidad`, `Movimiento`, `Sentido` and `Efecto temporal`, not an anonymous tail;
- all Mara trait identities remain present under the focused regression;
- Current Snapshot semantic coverage remains protected by its existing content assertions while using the reclaimed detail surface when sibling streams are exhausted.

Post-Phase-4 Notes evidence is unchanged and still positive:

- pages 26–27 reuse native Notes grammar;
- both columns are consumed;
- continuation identities remain visible;
- background/religion/subclass records remain preserved.

## QA history for this bounded repair

- 4311: expected Android sync guard failure after Desktop-only implementation;
- 4313: renderer/test run reached 91 tests; Mara reclaim regression exposed hidden source-form text and a historical Current Snapshot layer expectation;
- source-form header crop was removed from the reclaimed page to prevent hidden underlay leakage;
- the historical Current Snapshot test was first corrected at the wrong identical assertion and was then restored/retargeted precisely;
- 4317: Mara reclaim regression passed; only the still-misdirected historical assertion remained;
- **4318: SUCCESS**, all Kotlin/rendering tests + backend + hosted database + artifact uploads passed.

No acceptance criterion was weakened to obtain green CI.

## Disposition

- M50800-27 — **Custom-v1 Traits portion: positive exact-artifact internal PASS candidate**;
- M50800-29 — semantic-preservation regression remains positive for this scope;
- both remain formally OPEN until final exact promoted-candidate acceptance.

## Next bounded work

Continue Phase 5 with the already-mapped Custom-v1 Resources/Options defect from exact artifact review:

- M50800-14 / M50800-27;
- preserve whole Resource/Option logical records;
- reclaim the Options area after Options are exhausted;
- reject any page beginning with an anonymous detail tail;
- inspect the exact generated artifact before moving on.
