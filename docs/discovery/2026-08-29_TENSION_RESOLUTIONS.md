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
