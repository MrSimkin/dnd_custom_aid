# Checkpoint — Aldren preqa.4 cross-family manual QA review

**Date:** 2026-09-25 (Chile local time)  
**Branch:** `fix/pc-sheet-cross-family-runtime-repair`  
**QA build:** `0.5.0-preqa.4` / versionCode `50400`  
**Green implementation build:** `17d93d32561a6cc46d228b2bd6b0aa8dca466ea8` / Scaffold `36163990848` SUCCESS  
**Manual Android QA result:** **FAIL / REPAIR REQUIRED**  
**Scope:** Aldren Vale / Permanente / all four PDF families

This checkpoint supersedes the “manual QA ready” state in
`2026-09-25_PC_SHEET_CROSS_FAMILY_RUNTIME_REPAIR_ACTIVE.md`.
The implementation build is mechanically green, but the owner-reviewed runtime PDFs are not accepted.

## Runtime output obtained

Save/generation/open succeeded for all four families.

Produced PDFs:
- Fantasy Sheet — **14 pages**
- Custom v1 — **11 pages**
- Custom v2 per Attribute — **7 pages**
- Custom v2 per Ability — **7 pages**

Custom-v2 pages 2–7 are pixel-identical between per-Attribute and per-Ability exports, so shared defects below apply to both variants.

## Repairs that visibly passed

1. **Unicode/mojibake repair**
   - `Común`, `Élfico`, `Acólito`, `acción`, `versátil`, `2–5` and punctuation render correctly.
   - No recurrence of `ComÃºn`, `Ã‰lfico`, `2â€“5`, etc.

2. **Empty spell-page suppression**
   - Aldren no longer receives a separate empty spell page.

3. **Currency-domain routing improved**
   - Custom v1 standard coins remain in `Monedas`.
   - Custom v2 standard coins remain in `Tesoro`, with Electrum using adjacent `Otros`.
   - No observed currency-as-normal-equipment regression in this fixture.

4. **Save/open stability**
   - all four PDFs are generated and usable; the earlier bounded-routing crash did not recur.

## Owner-observed defects — confirmed

### Fantasy Sheet

1. **Base boxes underuse their available width/capacity**
   - `Rasgos de clase`, `Rasgos adicionales`, and combat/reference text wrap early or leave large compatible space unused.

2. **Race name is incorrectly used as a racial-trait entry**
   - `Humano` is already the race; the racial-traits area should contain the actual racial traits/attributes, not merely repeat the race name.

3. **Traits/reference continuation is badly under-packed**
   - `Rasgos y características` / `Rasgos de raza / trasfondo / otros` spread across four pages while large portions remain blank.
   - page 6 exists only for `Fe / religión: Culto solar local`.

4. **Resource use markers are detached from their resource rows**
   - the one-use circles appear at the bottom of the resource box instead of aligned with Second Wind / Action Surge.

5. **Equipment detail is routed into `Tesoro / Detalles / Notas`**
   - normal/special equipment descriptions continue under a treasure/details heading.

### Custom v1

6. **Traits are duplicated between semantic destinations**
   - the same traits/features appear in `Detalles de Rasgos` and again in `Notas`.

7. **Trait/reference output is split across too many mostly-empty pages**
   - pages 5–8 repeatedly instantiate the same large fixed layout;
   - page 8 contains only `Subclase: Guerrero - Champion`.

### Custom v2 — both variants

8. **Normal Equipment is placed in the page-1 Treasure/Objects region**
   - `Ballesta ligera`, `Virotes`, `Mochila de explorador`, and `Libro de oraciones` occupy `OBJETOS` even though the normal `EQUIPO` page has substantial unused capacity.

9. **Base `Equipo Especial` location typography is overlaid rather than cleanly replaced**
   - the original large decorative location labels remain visible while smaller replacement labels are drawn on populated rows.

10. **Normal equipment metadata is promoted into `Notas`**
    - page 3 is filled with ordinary-item locations/descriptions, contrary to the desired compact Equipment semantics.

11. **An almost-empty inventory continuation page is created despite free native capacity**
    - page 7 contains only `Virotes — Estado: Munición` while the normal Equipment area on page 2 has many empty rows.

12. **One-use resources use `Disponible` instead of an explicit trackable one-use representation**
    - desired direction from owner: `___ / 1` or a clearly aligned one-use circle, not the word `Disponible`.

13. **Custom-v2 extended special-equipment labels contain spelling errors**
    - `Mano izuierda` -> `Mano izquierda`
    - `Mano dereca` -> `Mano derecha`
    - `Brazo dereco` -> `Brazo derecho`
    - `Peco` -> `Pecho`

### Custom v1 and Custom v2

14. **Combat/action continuation does not use the normal attack/action schema**
    - generic prose lines such as `Ataque — ... · Efecto / daño ...` should use the same structured divisions/columns as the normal attack/action surface (name/action, range, bonus, effect/damage as applicable).

## Additional defects found during assistant cross-check

15. **Custom v1 special equipment is mapped to the wrong body-location rows**
    - page 2 visually places:
      - Cota de malla on the `Cabeza` row,
      - Escudo on `Rostro`,
      - Espada larga on `Cuello`,
      - Símbolo sagrado on `Mano Izquierda`.
    - each description simultaneously states its real location (`Cuerpo`, `Mano izquierda`, `Mano derecha`, `Cuello`), so the page contradicts itself.
    - this is a renderer slot-mapping bug, not content ambiguity.

16. **Fantasy inventory-detail pagination splits one logical item across pages**
    - page 11 ends Cota de malla with `Equipo inicial`; page 12 begins `de Guerrero.`
    - page 13 ends Símbolo sagrado with `Trasfondo`; page 14 begins `Acólito.`
    - a logical equipment/detail record must remain atomic across page boundaries.

17. **Fantasy inventory continuation wastes several nearly-empty pages**
    - pages 10–14 mostly contain only a few detail lines while the large inventory table above is empty.
    - the continuation planner is paginating by fixed slots/lines rather than packing whole detail records into available compatible space.

18. **Trait continuation still routes non-trait metadata as “traits”**
    - Fantasy routes language (`Enano`), armor proficiency, weapon proficiency, history/reference and faith/reference through `Rasgos de raza / trasfondo / otros`.
    - this both duplicates data already represented elsewhere and causes page amplification.

19. **Action/resource semantics are duplicated across too many surfaces**
    - Second Wind / Action Surge can appear as base traits, trait details, combat/actions, and resources.
    - the repair must keep a concise identity where useful but choose one authoritative detailed destination per semantic role instead of replaying the full record.

20. **Custom v1 creates a one-line inventory continuation solely for ammunition state**
    - page 11 contains only `Virotes — Estado: Munición`.
    - this is the same root class as Custom-v2 page 7: state metadata alone must not allocate a full Equipment continuation when native capacity is available.

21. **Dedicated ammunition capacity is not being used coherently in Custom v2**
    - the page-1 `MUNICIONES` tracker remains empty while Virotes are rendered as normal Equipment and later create an Equipment continuation because they are ammunition.
    - exact visual treatment can follow the sheet contract, but the current routing is internally inconsistent.

22. **Custom v1 resource recovery text is duplicated**
    - each one-use row repeats both `Descanso corto o largo` and `Descanso corto/largo`, then adds `A máximo`.
    - the circle presentation itself remains acceptable; the redundant recovery prose is not.

23. **Custom v1 AC decomposition is misleading**
    - page 1 displays `+2 MOD. DESTREZA` while the fixture’s AC 18 is explicitly chain mail 16 + shield 2; the Armor and Shield decomposition fields remain blank.
    - the detailed AC decomposition should reflect the equipped armor/shield model rather than the character’s raw Dex modifier.

## Consolidated repair principles

These principles are owner-approved or directly implied by the manual findings and supersede temporary branch behavior that conflicts with them:

1. **Use native capacity before continuation.**
   - Fill the normal Equipment/Traits/Story surfaces before adding extension pages.
   - A status/location/description alone must not manufacture a nearly-empty page.

2. **Normal Equipment remains compact.**
   - normal row: quantity + item + weight;
   - ordinary location/description/notes must not be auto-promoted into Equipment or Notes merely for preservation;
   - special equipment may use its dedicated location/state/detail fields.

3. **No semantic replay.**
   - detailed combat/action data belongs in a structured combat/action destination;
   - use-limited resource state belongs in Resources;
   - passive trait detail belongs in Traits;
   - languages/proficiencies/background/reference metadata belong in their own appropriate sections;
   - do not replay the same full record across Traits + Notes + Combat + Resources.

4. **Continuation packing is content-aware and record-aware.**
   - exploit the full usable box width/height;
   - pack compatible content before allocating a new page;
   - never split a logical record mid-sentence across pages.

5. **Actions/attacks use structured columns.**
   - Custom v1/v2 continuation must visually follow the normal attacks/actions schema rather than generic prose rows.

6. **One-use resources must be visibly trackable.**
   - Custom v2: `___ / 1` or a properly aligned one-use marker, not `Disponible`;
   - Fantasy markers must align with their resource rows;
   - Custom v1 circle presentation remains acceptable, with recovery text deduplicated.

7. **Special-equipment slots must be location-faithful.**
   - Custom v1 must map items to the actual location row;
   - Custom v2 must remove/replace source location typography cleanly, without visible overlay;
   - extended labels must be correctly spelled.

## Acceptance before manual QA can expand

A repaired Aldren/Permanente build must be rerun in all four families and demonstrate:

- no Unicode corruption;
- Save/open PASS;
- no unnecessary spell page;
- no equipment in treasure/art-object regions;
- no ordinary equipment metadata dumped into Notes;
- no continuation page caused only by Virotes/ammunition state when normal capacity exists;
- correct Custom-v1 special-equipment slot mapping;
- correct Custom-v2 special-equipment typography/spelling;
- structured action/attack continuation;
- one-use resource presentation accepted by owner;
- no logical record split across pages;
- materially improved content packing with no one-entry/sparse continuation page caused by fixed-section allocation;
- no full semantic duplication across Traits/Notes/Combat/Resources.

Do not resume Share, Ilyra, Mara, Current Snapshot or final physical-device QA until this repaired Aldren cross-family rerun passes.
