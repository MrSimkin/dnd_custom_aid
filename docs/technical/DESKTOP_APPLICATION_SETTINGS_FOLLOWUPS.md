# Desktop Application Settings — approved follow-ups

**Status:** Owner-approved future Desktop UX requirement  
**Recorded:** 2026-09-16 (Chile local time)

This record preserves owner-approved Desktop settings work that must be included the next time genuine Desktop feature development produces a new owner-testable Desktop build.

## Required follow-up

### Font catalogue

Expand the Desktop font catalogue so it offers the Android-equivalent application font choices wherever those fonts can be supported appropriately on Desktop.

The intent is product consistency, not platform-forced byte-for-byte implementation identity. Do not introduce Java-source implementations or unrelated font infrastructure merely to mimic Android internals.

### Theme and font selection previews

Replace the current plain Theme and Font selectors with Android-like preview cards/forms.

The selection surface should let the owner see representative text/presentation for each choice before committing the selection, rather than choosing only from an abstract label.

The Desktop implementation should reuse the same product concepts and visual intent as Android while remaining appropriate for keyboard/mouse/wide-screen Compose Desktop UX.

## Timing / sequencing

This work belongs to the **next genuine Desktop feature build**. It does not reorder Wave 5 dependencies and is intentionally not pulled into backend/shared-only packages merely because Desktop is compiled by CI.

When that Desktop build begins, read the actual Android settings implementation and catalogue first; do not recreate the choices from memory or invent a separate Desktop settings philosophy.

## Verification expectation

The next affected Desktop manual QA should verify:

- the expanded font choices are present as intended;
- theme previews are visually informative;
- font previews visibly demonstrate the selected font;
- selecting a preview applies the setting;
- persisted settings survive normal close/relaunch;
- existing spacing/text-size/workspace-density behavior does not regress.
