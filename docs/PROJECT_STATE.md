# Project State

**Last verified:** 2026-08-29  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture initial product discovery picture without promoting it to requirements`  
**Phase:** Phase 1 — Product Discovery and Design  
**Status:** Two clarification rounds captured; core character/audit/combat/content direction is substantially clearer, but several data/workflow questions and MVP boundaries remain open

## 1. Project in one paragraph

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D. The product centers on a paper-first player workflow backed by a full digital character copy, a DM tablet live-session/combat surface, and a desktop-friendly NPC/monster administration surface. It is explicitly not intended to replace Foundry/VTTs, D&D Beyond or normal tabletop play. Character audit/correction is practical and bounded rather than anti-cheat-focused. Live combat state is kept conceptually separate from durable character-sheet data. User-facing product content is Spanish; technical project material is English. Git is the operative memory and product/interaction design still precedes stack selection.

## 2. What exists now

There is still no application code or selected technology stack.

The active discovery branch now contains:

- the approved governance/continuity foundation from PR #1;
- initial product brainstorming in `docs/discovery/2026-08-28_INITIAL_PRODUCT_PICTURE.md`;
- first clarification round in `docs/discovery/2026-08-28_CLARIFICATIONS_01.md`;
- second clarification round in `docs/discovery/2026-08-29_CLARIFICATIONS_02.md`;
- consolidated approved product direction in `docs/PRODUCT.md`;
- major confirmed Phase 1 decisions through D-0026 in `docs/DECISIONS.md`;
- approved language and SRD/user-facing D&D terminology conventions in `docs/CONVENTIONS.md`;
- reserved PDF template location at `assets/character-sheets/templates/`;
- `assets/character-sheets/CHANGE_REQUESTS.md` for owner-side Adobe InDesign changes discovered later.

Three owner-provided DOCX examples were also reviewed during Round 2 and their relevant design characteristics were captured in the discovery/product documents:

- Quick NPC example;
- Developed NPC example;
- custom monster example.

The binary DOCX files themselves are conversation attachments, not repository assets at this time.

## 3. Major approved product direction

Current confirmed direction includes:

- Android phone/tablet remains the main application target.
- Desktop/laptop-friendly administration is also a real workflow need; native Windows vs web/local-web remains open.
- The product is an assistant, not a VTT/D&D Beyond replacement.
- Physical printed character sheets remain the preferred normal play surface.
- The digital character is a backup/reference copy capable of holding full end-of-session sheet state, including transient values such as current HP/resources where useful.
- A phone/tablet may temporarily become the player's active sheet if paper is unavailable.
- Character updates are flexible; end-of-session is normal, but during/between-session updates are allowed. No mandatory "confirm no changes" ritual.
- PDF output should support both baseline/permanent-data and full-latest-sheet-state intentions; exact save/export atomicity remains open.
- Player edits take effect without DM pre-approval.
- Character audit uses grouped mechanical change sets and compensating correction/reversal history.
- Audit is DM-only in v1; correction reasons are optional; history retention/bloat strategy remains open.
- One user identity can have different roles by campaign.
- One player may have multiple PCs in a campaign.
- Character ownership is distinct from temporary control; explicit permanent transfer is possible.
- Inactive/dead/retired PCs remain preserved.
- First version has one active DM per campaign, but the model should not unnecessarily block future co-DMs.
- D&D is the initial system; future other-system support should not be made unnecessarily impossible.
- Campaigns may mix SRD 5.1 / D&D 5e and SRD 5.2.1 / D&D 5.5e plus house rules.
- Technical provenance uses SRD document versions; user-facing answers use D&D 5e / D&D 5.5e labels.
- House rules start as notes, not a rule engine; DM edits them; player need is quick rules clarification rather than a browsable rule library.
- Full guided character creation remains outside current first-version intent.
- NPC administration uses Quick and Developed NPC concepts rather than a bare minimal-NPC model.
- Monster/creature records must be capable of complete D&D 5.5e Monster Manual-style stat blocks.
- A reusable personal NPC/creature library, duplicate/modify workflow, official SRD starting templates where possible and a desktop/web monster creator assistant are approved directions.
- Useful creature/NPC filters include name, CR, type, alignment and environment.
- DM live-session use is tablet-first.
- Player combat view shows visible initiative/current turn and visible conditions, never DM-hidden participants.
- DM combat board may track monster/NPC current HP, temporary HP, conditions and concentration; PC HP tracking is optional.
- Same-group creatures normally share initiative while keeping individual HP/status and may be split individually when needed.
- The DM may manually override monster HP.
- Combat state must survive app close/restart/network loss/session pause; DM local state remains authoritative during Internet loss.
- Combat does not automatically mutate persistent character sheets.
- Death saves, player spell slots/resources, automatic combat/rules resolution and combat-history analytics are outside current first scope.
- Shared data should be hosted online and normally fit a no-cost tier at personal scale; no provider is selected.

See `docs/PRODUCT.md`, `docs/DECISIONS.md` and `docs/discovery/2026-08-29_CLARIFICATIONS_02.md` for detail.

## 4. Current external/technical status

No stack, framework, language, UI toolkit, database provider, authentication provider, AI provider/model, PDF library, persistence architecture, desktop implementation approach or build system has been selected.

Neon/Postgres remains only a candidate mentioned during discovery.

Do **not** begin stack selection yet.

No application tests exist because there is no application code. This session changed documentation/product decisions only; verification consisted of reading the current branch/project-control files, reviewing the supplied NPC/monster examples and checking that the new decisions were recorded consistently across discovery, decision, product, convention and state documents.

## 5. Active unresolved design questions

Highest-value next discussions are now:

1. **PDF/export save semantics** — explain and choose whether exports can see in-progress edits or only completed atomic character updates/change sets.
2. **Audit retention/bloat** — estimate realistic volume and choose a bounded retention/summarization/archive approach.
3. **Unassigned PC records** — decide whether a PC-style character can exist in a campaign without a current player account.
4. **Stat-block internal granularity** — decide structured action/trait objects vs complete rich-text action/trait blocks within an otherwise structured stat block.
5. **Prepared encounter workflow** — explore saved encounter compositions that can populate the live tracker while preserving improvisation and live modification.
6. **Account/invitation/recovery details** — exact personal-use flows remain open.
7. **MVP boundary** — decide which approved capabilities belong in the first usable release versus later increments.

## 6. Character sheet assets

Owner's custom character-sheet PDFs are produced from Adobe InDesign and are not fillable/editable PDFs.

Repository upload route:

`assets/character-sheets/templates/`

The current preferred upload target while PR #2 is open is branch:

`discovery/initial-product-picture`

If a PDF layout needs an owner-side change, record it in:

`assets/character-sheets/CHANGE_REQUESTS.md`

The PDFs are presentation/output templates; no field mapping or PDF-generation implementation has been selected yet.

## 7. Current review state

PR #2 remains open and intentionally unmerged while the first discovery cycle continues.

The branch contains both historical discovery material and confirmed conclusions promoted into authoritative product/decision/convention files.

Do not merge merely because Round 2 was recorded; owner review/approval of the branch/PR remains the gate for canonical `main` under D-0007.

## 8. Next action

Continue product discovery using the seven unresolved questions above. A sensible immediate order is:

1. explain the four questions the owner explicitly did not understand (PDF state, unassigned PCs, stat-block granularity, prepared encounter launch);
2. separately work through audit retention/bloat with concrete size estimates;
3. then define the MVP boundary before technology selection.

## 9. Handoff note for the next agent

Read `docs/PRODUCT.md`, `docs/DECISIONS.md`, `docs/CONVENTIONS.md` and all three current discovery notes before continuing. Do not infer a technology choice from Neon, AI rules assistance, native Windows/web ideas or any PDF implementation concept. Keep the assistant-not-replacement scope boundary visible and do not turn future extensibility into present scope creep.
