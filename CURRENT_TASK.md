# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** close integrated lightweight Homebrew/Rule payload package, then continue with Place payload + local persistence core
- **Execution state:** Homebrew/Rule implementation is merged after green push and PR validation; post-merge Scaffold is running on the exact merge commit
- **Integrated `main`:** `fc870fe8303b0f9925c479bdc21c388ef5cc8450`
- **Homebrew implementation branch:** `wave6/homebrew-rule-payload-persistence`
- **Final Homebrew branch HEAD:** `0ef3b3466582482fc89f3025c1adf50a5fd40580`
- **Homebrew PR:** #53 — `feat: add lightweight Homebrew Rule payload persistence` — **MERGED** as `fc870fe8303b0f9925c479bdc21c388ef5cc8450`
- **Homebrew validation:** push Scaffold `35256210643` — **SUCCESS**; PR Scaffold `35256230033` — **SUCCESS**
- **Post-merge validation:** Scaffold `35256531651` for `fc870fe8303b0f9925c479bdc21c388ef5cc8450` — **IN PROGRESS** at latest inspection; backend and hosted-database are already **SUCCESS**, Kotlin is in `Build and test Kotlin surfaces`
- **Integrated Homebrew/Rule scope:** typed `DRAFT / ACTIVE / RETIRED` lifecycle; summary/body plus optional category/rationale, examples, related references, tags and notes; Personal/Campaign create/read/copy/update/tombstone behavior through existing reusable-content identity/provenance/revision seams; SQLDelight `homebrew_rule_payload`; migration `21.sqm`; metadata-only Homebrew backfill; legacy Desktop fixture adjustment; focused persistence/copy/revision/migration/reopen tests
- **Next bounded package after green closure:** Place payload + local persistence core using `ReusableContentFamily.PLACE`; begin with canonical/self-contained Place data and Shop-as-specialized-Place semantics, while deferring cross-object Place/NPC/Scene/Zone/Encounter dependency-copy graphs to a later explicit relationship package
- **Session override:** owner explicitly authorized polling/continuing past the normal async-CI stop rule for this session, until timeout/time-risk or unproductive waiting becomes reasonably high. This does not permanently change repository protocol.
- **Next action:** inspect post-merge Scaffold `35256531651`; if green, create and merge the Homebrew integration checkpoint/documentation closure naming Place payload persistence as the next bounded Wave 6 package. If the run fails, inspect only the exact attributable failure before changing anything. If it remains in the same long-running Kotlin step, stop cleanly rather than spending the session on repeated idle polling.
- **Do not:** restart completed Homebrew implementation, flatten structured races/classes/subclasses/backgrounds/feats/spells/items into the lightweight rule payload, start Manager UI, hosted reusable-content sync, import/export/AI helpers, object storage/provider work, or introduce a generalized dependency graph/universal payload abstraction.
