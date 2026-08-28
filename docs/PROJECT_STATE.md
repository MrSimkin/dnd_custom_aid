# Project State

**Last verified:** 2026-08-28  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture initial product discovery picture without promoting it to requirements`  
**Phase:** Phase 1 — Product Discovery and Design  
**Status:** First product picture and first clarification round captured; further workflow design needed before MVP/stack decisions

## 1. Project in one paragraph

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D. The current design centers on a paper-preferred character-sheet workflow backed by shared digital character data, DM audit/correction, a player phone/tablet backup/reprint experience, a DM tablet live-session dashboard/combat tracker, and a desktop-friendly administration surface whose implementation form is still open. End-user product content is Spanish; technical project work remains English. The project may support other game systems in the future, but current work remains focused on the D&D use case. Git is the operative memory and product/interaction design precedes stack selection.

## 2. What exists now

There is still no application code or selected technology stack.

The repository now contains, on the active discovery branch:

- the approved governance/continuity foundation from PR #1;
- initial product brainstorming in `docs/discovery/2026-08-28_INITIAL_PRODUCT_PICTURE.md`;
- first confirmed clarification round in `docs/discovery/2026-08-28_CLARIFICATIONS_01.md`;
- updated approved product direction in `docs/PRODUCT.md`;
- major confirmed Phase 1 decisions through D-0019 in `docs/DECISIONS.md`;
- approved language/SRD terminology conventions in `docs/CONVENTIONS.md`;
- reserved PDF template location at `assets/character-sheets/templates/`;
- `assets/character-sheets/CHANGE_REQUESTS.md` for owner-side Adobe InDesign changes discovered later.

## 3. Major approved product direction

Current confirmed direction includes:

- Android phone/tablet support remains the main application target.
- User-facing product is Spanish; technical project/source material is English.
- Physical printed character sheets remain preferred at the table.
- Player app is primarily a digital backup/reference and reprint path, not a paper replacement.
- Character data is conceptually separate from the PDF output template.
- Player edits take effect without DM pre-approval; the DM can audit, correct and reverse them.
- One user identity can have different roles by campaign.
- D&D is the initial system; future other-system support should not be made unnecessarily impossible.
- Campaigns may mix SRD 5.1 (earlier/2014-era) and SRD 5.2.1 (revised/2024-era) rules plus house rules/homebrew.
- The app is not intended as a strict rules enforcer, D&D Beyond replacement or first-version character builder.
- The desired SRD outcome is quick rules clarification during play; AI is only a candidate implementation idea.
- DM live-session use is tablet-first, with PC group/current-PC quick views and initiative/combat support.
- Players see visible initiative/current turn but not DM-hidden creatures.
- Shared data should be hosted online and normally fit a no-cost hosted tier at the expected personal scale; backend selection remains deferred.
- Neon/Postgres is a plausible current candidate, not a decision.
- Desktop/laptop campaign/NPC/monster administration is a real workflow need; native Windows vs web/local-web is not decided.

See `docs/PRODUCT.md` and `docs/DECISIONS.md` for authoritative detail.

## 4. Current external feasibility notes

Fresh external checks performed during discovery:

- official Spanish SRD 5.1 and Spanish SRD 5.2.1 are available under CC-BY-4.0 from Wizards/D&D Beyond; any future use must follow the required attribution terms;
- Neon currently advertises a Free plan with 0.5 GB storage per project, confirming that the owner's expected storage scale is plausible for at least one current hosted Postgres candidate;
- these facts do not select an implementation or service.

## 5. Character sheet assets

Owner's custom character-sheet PDFs are produced from Adobe InDesign and are not fillable/editable PDFs.

Repository upload route:

`assets/character-sheets/templates/`

If a PDF layout needs an owner-side change, record it in:

`assets/character-sheets/CHANGE_REQUESTS.md`

The PDFs are inputs/output templates; no field mapping or PDF-generation implementation has been selected yet.

## 6. Active unresolved design questions

The next highest-priority questions are:

1. What is the real paper-to-digital update workflow so the digital backup stays current?
2. Should DM corrections/reversals preserve the original audit history through new reversal/correction records rather than deleting history?
3. Confirm multiplicity interpretation: one player may have multiple PCs; first version has one DM; underlying model should or should not leave future co-DM support possible.
4. How should campaign house rules be stored/consulted, especially when they override an official SRD answer?
5. What is the first practical NPC/monster entry workflow on the desktop-friendly administration surface?
6. Beyond initiative/current turn, what combat state should be persisted/synchronized in the first version?

## 7. Current technical status

No stack, framework, language, UI toolkit, database provider, authentication provider, AI provider/model, PDF library, persistence architecture, Windows/web implementation, or build system has been selected.

Do not begin stack selection yet.

## 8. Current review state

PR #2 is open and intentionally unmerged while discovery material continues to be refined.

The branch contains both:

- provisional historical discovery material; and
- confirmed conclusions promoted into authoritative product/decision/convention files.

Before merging PR #2, the owner should review whether the current product summary accurately represents the clarified intent.

## 9. Next action

Continue product discovery with the owner using the six active questions above, prioritizing the paper-to-digital character workflow and audit semantics first because they drive the core shared-character model.

## 10. Handoff note for the next agent

Read `docs/PRODUCT.md`, `docs/DECISIONS.md`, `docs/CONVENTIONS.md` and both current discovery notes before continuing. Do not interpret the existence of a possible AI rules assistant, Neon, native Windows, or any PDF implementation idea as a technology choice. Continue collaborative product/workflow design and persist conclusions in Git.
