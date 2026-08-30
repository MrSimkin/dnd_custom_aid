# Product Design Tension Resolutions

**Date:** 2026-08-29  
**Status:** Confirmed owner decisions in progress  
**Phase:** Phase 1 — final pre-merge clarification

## Purpose

This note records explicit owner resolutions of soft design tensions identified during the final PR #2 audit. These are not speculative questions: each numbered section is added only after the owner confirms the intended product behavior.

Once this pass is complete, confirmed conclusions must be promoted into the authoritative product/decision/state documents before PR #2 is merged.

---

## 1. Android primary/live surface vs desktop/laptop administration

**Confirmed resolution:** Android and desktop are intentionally asymmetric product surfaces for the foreseeable scope.

1. Desktop is **not required to duplicate the whole Android application yet**. Full or broader desktop parity may be considered as a possible much-later feature, but it is not near-term scope.
2. No player desktop application is required in the MVP.
3. Feature parity between Android and desktop is a **very-future possible feature**, not a current or near-term implementation goal.
4. The desktop implementation form—web, native Windows, local web, or another practical desktop-friendly approach—remains a Phase 2 technology/architecture decision.
5. Android and desktop operate on the **same campaign/domain data** rather than becoming two independent products/data silos.
6. Android remains the primary **at-the-table/live-use** surface.
7. Desktop remains primarily a **preparation/administration** surface.
8. The desktop surface does **not** require the combat tracker in the MVP.

### Consequences

- Desktop administration is a separately approved companion surface to the Android-first product; this does not supersede Android as the primary live-use target.
- MVP architecture should support the required desktop administration workflows without imposing Android/desktop feature parity.
- A future decision may deliberately broaden desktop capabilities, but current architecture must not treat such parity as an MVP or near-term requirement.
- Shared domain/campaign data should be accessible to both surfaces according to their approved workflows and permissions.
- If the eventual architecture makes some additional Android functionality naturally available on desktop at negligible cost, that does not turn feature parity into a requirement.

---

## 2. MVP campaign scope

**Confirmed scope change:** the previous MVP restriction to one active campaign is withdrawn. The MVP is now **multicampaign**.

The owner prefers implementing multicampaign behavior in the MVP because it is closer to the intended real product and avoids building a temporary single-campaign product restriction that would later need to be removed.

### Consequences

- Multiple campaigns may exist and be active concurrently in the MVP.
- A user account may participate in multiple campaigns concurrently, with campaign-scoped roles and permissions as already approved.
- The DM/owner may manage multiple campaigns rather than having to archive or replace one before using another.
- Characters, NPCs, encounters, campaign membership, permissions, audit history and other campaign-scoped data must retain explicit campaign association.
- The MVP requires sufficient campaign selection/switching UX to use the multicampaign capability coherently.
- The old wording in D-0010, `docs/PRODUCT.md`, `docs/PROJECT_STATE.md`, PR #2 and related documentation that describes one active campaign as an MVP restriction is now stale and must be superseded/promoted before merge.
- This changes product scope but does not itself select any architecture, data technology or UI implementation approach.

---

## 3. Mixed campaign rules vs SRD-only MVP rules clarification

**Confirmed resolution:** campaigns may use mixed official generations and homebrew from day one, while the MVP rules assistant remains limited to supported official SRD sources and does not enforce rules.

1. A campaign may freely use mixed D&D 5e / SRD 5.1, D&D 5.5e / SRD 5.2.1 and homebrew rules from day one.
2. Character, NPC and monster data must not reject content merely because it is not "legal SRD".
3. The application is not a rules enforcer.
4. The MVP rules assistant may answer from both supported official SRDs and must clearly identify whether relevant information comes from D&D 5e / SRD 5.1 or D&D 5.5e / SRD 5.2.1.
5. The MVP rules assistant does **not** automatically know or apply campaign house rules.
6. If a campaign rule differs from an official SRD rule, the DM/player is responsible for applying the campaign rule manually in the MVP.
7. House-rule-aware rules answers are a later feature.

### Consequences

- Campaign data/content flexibility and rules-assistant knowledge scope are separate concerns.
- The rules assistant must not present campaign homebrew as if it were official SRD content.
- Mixed/homebrew campaign records must remain usable even when the rules assistant cannot interpret or validate those rules.
- Later house-rule-aware clarification may combine official provenance with campaign-specific overrides, but that capability is explicitly outside the MVP.

---

## 4. Complete monster stat blocks vs selective structured mechanics

**Confirmed resolution:** monster records must be complete for human use while the MVP structures only the mechanics that provide practical software value; future enrichment must remain possible without architectural obstruction.

1. A monster record must be capable of representing and displaying the **entire stat block**, with nothing important omitted.
2. Core stable fields should be structured where useful, including name, CR, type, size, alignment, AC, HP, speeds, ability scores, saves, skills, senses, languages, resistances/immunities and similar stable stat-block data.
3. Traits, actions, bonus actions, reactions, legendary actions and similar elements are **individual structured records** with ordering/category.
4. Their complete mechanical wording may remain formatted text in the MVP.
5. The application does **not** need to understand that formatted text as executable rules.
6. Combat tracking may use selected structured values—such as HP, AC, conditions and initiative—without requiring every action or trait to be machine-interpretable.
7. Later versions may progressively add structured fields such as attack bonus, reach, save DC, damage components, recharge and targets where those fields provide actual product value.
8. The current architecture must not impede that future enrichment.

### Consequences

- "Complete stat block" is a presentation/data-completeness requirement, not a requirement for a full rules engine.
- The MVP should avoid both extremes: one undifferentiated giant stat-block blob and premature atomic normalization of every game-mechanics clause.
- Stable structured fields and first-class action/trait records should support search, display, encounter use and combat reference now.
- Deeper structured mechanics should be additive through ordinary evolution/migrations rather than requiring replacement of monster, encounter or combat models.
- Human-readable completeness takes priority over speculative machine interpretation in the MVP.

---

## 5. Paper-first live authority vs complete digital character state

**Confirmed resolution:** live-session authority follows the surface actually being used for play, while the latest intentionally saved/reconciled digital record is the durable backup baseline.

1. During normal paper-first play, **paper is the authoritative live-session state**.
2. Digital represents the **latest digitally recorded/reconciled state**, not necessarily the current physical-table state.
3. The application should show a **last updated / freshness indication** so users do not mistake an old digital backup for current live truth.
4. There is no automatic assumption that digital values supersede newer paper notes.
5. When the player performs the normal end-of-session reconciliation, the resulting saved digital state becomes the **new durable baseline/backup**.
6. If the player is actively using the application instead of paper for a session, the digital sheet may temporarily be the live authoritative working state.
7. Returning later to paper should use the latest reconciled/exported state as the starting point.
8. The application does **not** attempt automatic conflict merging between paper and digital because it cannot observe changes written only on paper.

### Consequences

- "Authoritative" has two contexts: live-session authority and durable stored authority.
- Paper-first play does not make digital data disposable; digital remains the durable reconstruction/reference baseline after reconciliation.
- Digital storage must not silently overwrite unobserved paper changes merely because its timestamp or sync state is newer.
- Freshness information is a meaningful UX requirement, not only decorative metadata.
- No paper/digital conflict-resolution engine is required for the MVP.
