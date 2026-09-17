# CURRENT TASK

This file contains **only the current execution state**. Replace it when the active task changes; do not append historical narrative here.

Last verified: 2026-09-17

- **Wave:** 6 — reusable/persistent content architecture
- **Current task:** NPC reusable-content payload + local persistence core
- **Execution state:** Creature package and its documentation closure are integrated/green; NPC implementation is complete in one atomic branch commit, PR #51 is open, and Scaffold validation is running
- **Integrated `main`:** `bea3695f1c6ab21548183447422f35554bec0c45`
- **Creature package:** PR #48 merged as `5762058645ba8af8fa470dcf185bd2b418af65e9`; post-merge Scaffold `35253188344` — **SUCCESS**
- **Creature documentation closure:** PR #50 merged as `bea3695f1c6ab21548183447422f35554bec0c45`; post-merge Scaffold `35253993095` — **SUCCESS**
- **Active branch:** `wave6/npc-payload-persistence`
- **Branch HEAD:** `ce2f956324eedefaf4c4f34d4f4c462b49387eae`
- **PR:** #51 — `feat: add Wave 6 NPC payload persistence` — **OPEN**
- **NPC scope:** Quick/Developed NPC payloads remain valid without combat mechanics; optional combat mechanics reuse serialized `CreaturePayload`; Personal/Campaign create/read/copy; revision/stale-write/tombstone behavior; SQLDelight `npc_payload`; migration `20.sqm`; legacy Desktop fixture update; focused migration/reopen/invariant tests
- **Push CI:** Scaffold run `35254216489` for SHA `ce2f956324eedefaf4c4f34d4f4c462b49387eae` — **IN PROGRESS** at the latest inspection; backend and hosted-database jobs already succeeded, Kotlin job still running
- **Session override:** owner explicitly authorized polling/continuing past the normal async-CI stop rule for this session, until timeout risk becomes reasonably high. This does not permanently change the repository protocol.
- **Next action:** continue observing PR #51 validation; if a bounded NPC-package failure appears, repair only that failure; if exact-head/PR CI is green and the PR remains mergeable, merge #51, validate post-merge `main`, then record the NPC integration checkpoint before selecting another Wave 6 package.
- **Do not:** widen this package into NPC Manager UI, import/export, AI helpers, hosted reusable-content sync, object storage/provider work, dependency graphs, or a universal payload abstraction.
