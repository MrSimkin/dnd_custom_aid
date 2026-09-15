# Phase 4A pre-QA8 — P16 landscape / vertical-space policy CLOSED

Date: 2026-09-11
Branch: `implementation/phase4a-successor-cycle`
Status: DESIGN CLOSED — no implementation authorized
Classification: FULL APP AUDIT REQUIRED

## Context

Owner-phone 40800 QA showed that phone landscape preserved the desired phone interaction/navigation model, but fixed/sticky UI consumed too much vertical space, especially in Combate and Conjuros. Repository inspection confirmed two systemic contributors:

- layout context primarily classifies PHONE/TABLET + PORTRAIT/LANDSCAPE instead of exposing a usable-height policy;
- Conjuros uses unconditional sticky spell-level headers;
- Combate keeps a multi-row operational quick-reference card permanently above the scrollable collection.

P16 therefore defines a shared adaptive vertical-space policy rather than isolated landscape exceptions.

## Accepted decisions

### 1. Use actual usable vertical height

Persistent UI must react to the actual usable viewport after system bars/navigation and, when relevant, the IME/keyboard. Orientation/device category alone is insufficient. A short split-screen portrait window may require constrained behavior; a tall landscape tablet need not inherit phone-like compromises.

### 2. Automatic progressive compaction

Persistent regions automatically adapt through sensible presentation states driven by available space, conceptually:

- normal usable height -> normal compact presentation;
- reduced usable height -> tighter/reflowed presentation;
- severely constrained height -> lower-priority persistent content loses persistence or moves into scrollable content.

This is separate from the P10 user-controlled density/whitespace preference and must not require the owner to adjust density to compensate for a bad viewport.

### 3. Combate uses width to save height

P5 remains authoritative. In shallow/wide layouts the combat HUD should reflow horizontally as much as practical while keeping the important data permanently visible. Conceptual constrained form:

`CA 18 | Inic. +4 | Vel. 30 ft (9 m) | PV 37/52 | Temp. 8`

`Daño | cantidad | Curar`

Do not preserve portrait-style multi-row geometry merely because it works in portrait. Death saves remain normal scrollable content under the HUD, per P5.

### 4. Conjuros sticky level headers become conditional

Spell-level headers (`Trucos`, `Nivel 1`, etc.) may remain sticky when enough usable vertical room exists. When usable height is too constrained, they become ordinary scrollable section headers.

Rule:

- enough vertical room -> sticky;
- too little vertical room -> scroll normally.

### 5. Audit combined persistent footprint

Header, character navigation, contextual/collection toolbar, HUD, sticky subsection headers and any other persistent layer must be evaluated as one total vertical-space budget. A set of individually modest persistent components is still unacceptable if their combined footprint leaves the primary content region impractically small.

### 6. Preserve phone interaction/navigation model in phone landscape

Do not solve shallow phone landscape by switching it into the tablet side-rail interaction model or otherwise changing the accepted phone navigation grammar. Use available width intelligently while retaining phone interaction semantics.

### 7. Priority order under vertical constraint

Persistent elements yield in this order, from highest protection to lowest:

1. main character navigation;
2. essential live-play HUD for that screen;
3. active user task/control;
4. secondary contextual/collection toolbar;
5. sticky subsection headers;
6. explanatory/decorative persistent content.

Thus lower-priority persistence must compact/scroll before higher-priority navigation or essential live state is sacrificed.

### 8. Collection toolbars participate in the budget

Search/filter/sort/add controls cannot assume a permanently large strip. In constrained height they may compact or partially collapse while preserving access to all functions. For example, full search/action controls may become compact icon/short-control variants when appropriate.

### 9. Titles/explanatory headers are not privileged

Screen/section titles or explanatory subtitles may scroll away, merge into a compact toolbar, or otherwise reduce their footprint if keeping them fixed harms primary content. Repeating the current section name is lower priority than navigation and meaningful live-play data.

### 10. Dynamic response to keyboard/IME

When the keyboard materially reduces usable height, the vertical-space policy recomputes dynamically:

- unnecessary stickiness may turn off;
- persistent regions use constrained forms;
- search/results or active editing/task area remains usable.

When the keyboard closes, the appropriate less-constrained presentation may return. This complements P9 editor/IME rules.

### 11. Do not drive compaction from scroll position

Normal/reduced/severely-constrained presentation is principally driven by available dimensions, not small scroll movements. Avoid jumpy expand/collapse behavior and moving touch targets caused merely by scrolling. Normal sticky-header mechanics are still allowed when the viewport has enough room.

### 12. No arbitrary design-time dp threshold mandate

The design specification intentionally does not freeze arbitrary exact dp cutoffs. Implementation should determine sensible thresholds from the repaired components and validate them against representative viewport heights.

Behavioral acceptance criterion:

> Persistent UI must never leave the primary usable content region impractically small when a lower-priority persistent element can compact or scroll instead.

## Full-app audit requirement

P16 is `FULL APP AUDIT REQUIRED` because it defines a shared presentation/layout contract. Repair completion must systematically inspect all Player surfaces that contain fixed/sticky/persistent UI or collection toolbars and either:

- make them conform to this usable-height / combined-footprint policy; or
- document a legitimate exception.

At minimum the audit must include Combate, Conjuros, shared character navigation/header shells, collection toolbars, and other screens with sticky/fixed status or contextual regions.

## Cross references

- P5: compact fixed combat HUD and global speed presentation.
- P9: adaptive editors and IME-safe sizing.
- P10: global density/whitespace preference; not a substitute for viewport adaptation.
- P15: Supercompact responsive layout also follows available-dimensions reasoning.

## Non-goals

- No code implementation in this decision pass.
- No switch from phone landscape to tablet navigation semantics.
- No arbitrary single threshold such as `height < N dp` imposed by product design.
- No change to the P10 density preference semantics.

P16 is CLOSED.