# Phase 4A — P7 canonical provenance implementation CLOSED

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Implementation evidence head before this checkpoint:** `3362e52ea0bd7bdb1e87582bddb1e695798fafe5`  
**Status:** **IMPLEMENTATION CLOSED / READY FOR OWNER QA IN THE REPAIRED BUILD**

## Authority and scope

This checkpoint closes implementation of P7 — **Canonical provenance / character-owned origin references / catalog readiness** — under:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`;
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

P6 remains independently implementation-closed in:

`docs/checkpoints/2026-09-11_PHASE4A_P6_REORDER_IMPLEMENTATION_CLOSED.md`.

No P6 reorder behavior was redesigned or reopened by P7. No DM implementation, `main` synchronization, destructive history rewrite, or P8 implementation is authorized or performed by this checkpoint.

## Implemented P7 model

P7 now uses character-owned canonical gameplay-origin entities and one Trait-instance-to-one-provenance relationship.

Structured provenance kinds are:

- Clase;
- Subclase;
- Raza;
- Subraza;
- Trasfondo.

`Dote`, `Don / bendición` and `Otro` retain the accepted custom/free-text path. Structured provenance does not use arbitrary free text as a fallback.

The canonical provenance domain and persistence foundation lives in:

- `shared/.../CharacterProvenance.kt`;
- `shared/.../CharacterProvenanceRepository.kt`;
- the P7 SQLDelight persistence/migration/query layer;
- `CharacterSuccessorState` provenance fields.

Character acquisition and catalog/definition presence remain separate concepts. Eligible structured provenance options are derived only from origins this character owns or has registered; catalog metadata does not itself create character ownership.

Custom definitions participate in the same owned-identity schemas through `definitionKey`/rules metadata rather than a parallel second-class provenance model.

## Canonical hierarchy and identity behavior

### Clase / Subclase

Subclase remains structurally attached to the parent class through stable character-owned identity. Subclass provenance resolves and disambiguates using that parent, e.g. conceptually `Evocación (Mago)`.

### Raza / Subraza

The normal Player sheet now exposes owner-facing **`Subraza`** terminology.

`CharacterOwnedSubraceIdentity` is structurally attached to the current canonical Raza through `parentSpeciesId`. Repository validation rejects a Subraza without the character's current Raza or with a mismatched parent identity.

The model remains source-neutral internally so a rules source may conceptually call the child lineage/ancestry/etc.; the ordinary owner-facing Player vocabulary remains `Subraza`.

### Rename / deletion

Canonical renames preserve stable identity and therefore propagate to resolved Trait provenance.

When a structured owned source disappears, dependent Traits are not deleted and are not silently remapped. They remain unresolved provenance relationships.

Before a still-resolved structured relationship is saved, the current canonical display label is refreshed into its historical fallback label. Therefore a later deletion preserves the latest useful canonical label rather than reverting to an obsolete pre-migration spelling.

## Player editor integration

The real Player successor-state lifecycle is wired to `CharacterProvenanceRepository`; opening/loading a character reconciles provenance and normal successor saves preserve it.

Relevant provider integration commit:

- `abcc0eb1341f74cfab1d885510652f3bbfd746ae`.

The live Rasgos editor uses provenance relationships rather than treating copied `CharacterTrait.source` as authoritative.

Relevant editor integration commit:

- `cbcc9af325759a5b703fb79ad48a890c0f673f1f`.

The final P7 Player integration adds canonical owner-facing Subraza and hardens unresolved history:

- `ab630e4dbbd4971ef78aaa7c405fcf8e1c65c79e` — `feat: complete P7 subrace and unresolved provenance flow`.

Raza, Subraza and Trasfondo canonical origins now participate in the normal Player unsaved editing transaction. A Subraza-only edit contributes to dirty-state detection and uses the same Guardar / discard boundary as the rest of the character. P7 does not persist a half-edited Subraza immediately while the character remains an unsaved draft.

Rasgos see the projected canonical-origin draft while editing, so provenance choices remain coherent with unsaved Raza/Subraza/Trasfondo edits.

## Unresolved provenance behavior

Historical structured provenance is preservable when automatic resolution is unsafe or impossible.

In particular, a valid unresolved legacy relation may be represented as:

- structured `kind`;
- `targetId == null`;
- nonblank `legacyText`.

The Player helper now treats that relation as valid unresolved history. Editing an unrelated field on the Rasgo does not force an arbitrary new source choice. The editor may display the unavailable historical label and allows the owner either to preserve it unresolved or explicitly choose a currently owned source.

Ambiguous legacy matching remains conservative: if multiple owned targets match the copied legacy text, P7 preserves the text unresolved instead of guessing.

## Backup / restore lifecycle

P7 provenance participates in character backup/export and restore-as-copy.

Restore-as-copy remaps:

- class/subclass provenance identities;
- Raza identity;
- Subraza identity and its parent Raza identity;
- Trasfondo identity;
- Trait instance IDs and structured provenance target IDs.

The copied character therefore does not retain identity references to the source character. Unresolved targets remain unresolved rather than being silently mapped to unrelated owned entities.

## Regression evidence

Focused tests now cover the required P7 boundary.

### Canonical identity, unresolved history, schemas and instance semantics

`CharacterProvenanceRepositoryTest.kt` proves, among other P7 behavior:

- canonical Raza and Trasfondo rename while retaining identity;
- later deletion leaves dependent Traits unresolved;
- unresolved fallback preserves the latest canonical labels;
- ambiguous legacy class text remains unresolved instead of being guessed;
- structural Raza -> Subraza parent binding;
- custom Subraza definition metadata uses the same canonical owned-identity schema;
- structured provenance rejects arbitrary `freeText`;
- two distinct Character Trait instances with the same trait name may preserve two distinct provenance sources;
- Subclase choices remain attached to and disambiguated by their parent classes.

### Sole-source auto-selection and catalog != ownership

`CharacterProvenanceContractTest.kt` was added in:

- `3362e52ea0bd7bdb1e87582bddb1e695798fafe5` — `test: close remaining P7 provenance contract gaps`.

It proves:

- exactly one owned structured source is eligible for automatic selection, while zero eligible sources produce no guess;
- the global class catalog may contain `wizard` / `Mago`, but a character that does not own that class receives no Clase provenance option and legacy `Mago` remains unresolved.

### Backup graph

`CharacterProvenanceBackupTest.kt` proves export + restore-as-copy for the P7 graph, including:

- Clase;
- Subclase;
- Raza;
- Subraza;
- Trasfondo;
- Trait provenance relationships;
- fresh imported identities;
- remapped Subraza -> Raza parent identity;
- resolved imported relationships after remapping.

### Player UI / transaction integration evidence

Compiled Player integration proves:

- the normal Player-facing label is `Subraza`;
- structured Rasgo origin selection has no arbitrary free-text field;
- sole eligible structured origin auto-selection remains active;
- unresolved `targetId == null + legacyText` history is preservable/editable;
- Raza/Subraza/Trasfondo canonical drafts participate in dirty-state, save and discard behavior;
- Rasgos use the projected canonical draft during the same editing transaction.

## Validation

Normal Scaffold-equivalent validation was used, including:

`gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace`

and backend:

`npm run check`

Validation evidence:

- workflow `34667081145` on `ab630e4dbbd4971ef78aaa7c405fcf8e1c65c79e`: backend **SUCCESS**, Kotlin build/tests **SUCCESS**, Android debug APK upload **SUCCESS**;
- workflow `34668109261` on `3362e52ea0bd7bdb1e87582bddb1e695798fafe5`: backend **SUCCESS**, Kotlin build/tests **SUCCESS**, Android debug APK upload **SUCCESS**.

The final test-only delta from `ab630e...` to `3362e52...` adds only `CharacterProvenanceContractTest.kt`; no production or P6 file changed in that coverage commit.

## P7 implementation conclusion

The accepted P7 regression boundary is now evidenced:

- no structured free-text fallback for Clase/Subclase/Raza/Subraza/Trasfondo;
- sole-source auto-selection;
- subclass disambiguation by parent class;
- canonical rename propagation;
- unresolved preservation after source deletion;
- ambiguous legacy text preserved rather than guessed;
- conservative legacy matching restricted to entities this PC owns;
- structural Raza -> Subraza relationship;
- owner-facing `Subraza`;
- catalog/definition presence does not imply character ownership;
- custom definitions use the same owned-identity schemas;
- separate Character Trait instances preserve exactly one provenance source each when the same trait is acquired from distinct sources;
- backup/export/import-as-copy preserves and remaps the P7 graph.

P7 is therefore **IMPLEMENTATION CLOSED**.

## Acceptance boundary and next step

This is implementation/automated-validation closure only. It is **not owner/device acceptance** and does not claim that physical phone/tablet QA has passed the repaired P7 interaction.

P8 has not been started by this closure.

The next authorized repair item may begin only from the normal Phase 4A repair sequence, preserving this P7 boundary and the already-closed P6 behavior.
