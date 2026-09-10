# D-0070 — Shared player/DM rules-question capability and provisional naming

**Status:** Approved  
**Date:** 2026-09-10  
**Decision owner:** Project owner  
**Implementation status:** product/design clarification only; current implementation gates remain unchanged

## Purpose

This decision preserves two owner clarifications that must not be lost while the broader DM Desk design is being developed:

1. the AI-assisted natural-language rules-question capability is available on **both the Player side and the DM side** of the application;
2. **`Quick Rules Question` is only a working/discovery label, not the definitive product/UI name**.

This clarifies D-0069 and is consistent with the already-approved product direction in `docs/PRODUCT.md`, where rules clarification is an MVP capability for both players and DM.

---

## 1. Shared capability, not a DM-only Desk feature

The rules-question capability is a **shared application capability**.

### Player side

Players must be able to ask natural-language rules questions from the player-facing application without needing DM access or a DM Desk.

The player use case is rapid clarification/remembrance during play: answer a rule question without opening books or navigating a large rules reference.

### DM side

The same conceptual capability is available to the DM. **DM Screen** is its natural neutral entry point, and later specialized Desks may expose contextual shortcuts if that reduces navigation without creating duplicate implementations or separate rule assistants.

The existence of a DM Screen entry point must never be interpreted as making the feature DM-only.

### One conceptual capability

Do not create unrelated `Player Rules Assistant` and `DM Rules Assistant` products unless a later requirement genuinely justifies divergent behavior.

Role/context may affect available campaign-specific material or presentation, but the underlying purpose remains the same: fast grounded rules clarification/retrieval.

---

## 2. Working name only

`Quick Rules Question` is **not an approved final user-facing name**.

It is a temporary design label used to keep the capability visible during discovery and planning.

Final Spanish product naming, iconography and exact placement are **Pending** and should be decided when the feature approaches UX implementation rather than being frozen prematurely.

Future documentation should preserve the capability even if the working label changes.

---

## 3. Current MVP rules boundary remains unchanged

This clarification does not expand current MVP corpus scope.

D-0041 and `docs/PRODUCT.md` remain controlling:

- supported official corpus: **SRD 5.1** and **SRD 5.2.1**;
- provenance/version must be preserved;
- answers are grounded in retrieved supported SRD material rather than ungrounded model memory;
- user-facing answers are in Spanish and identify the relevant D&D 5e / D&D 5.5e source/version;
- the application is not a rules engine, legality enforcer or D&D Beyond replacement.

The project may ingest/store the supported open SRD corpus for retrieval.

---

## 4. Broader future campaign-rule direction

The broader post-MVP direction recorded in D-0069 remains protected:

- DM-authored house rules;
- custom rules;
- homebrew rules/content used for clarification;
- adopted variant rules or campaign overrides;
- clear distinction between official SRD baseline and the campaign rule actually in force.

Non-SRD/custom campaign rule material is prepared and maintained through the **Desktop DM Manager**, not as live-table bookkeeping.

Player access to campaign-specific rules clarification should consume the campaign material the DM has made applicable/available; players do not gain DM authoring/admin authority merely because the shared clarification capability exists.

The exact mixed-corpus retrieval architecture and permissions are Pending until implementation approaches.

---

## 5. Continuity rule

Future agents must preserve these facts even if terminology changes:

> The natural-language rules clarification feature belongs to both Player and DM experiences. `Quick Rules Question` is a provisional working label only.

Nothing in this decision changes the Phase 4A closure gate or authorizes DM-feature implementation before that gate is satisfied.
