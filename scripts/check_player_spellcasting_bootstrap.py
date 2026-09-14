#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")
SHARED = Path("shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character")
HELPER = SHARED / "CharacterSpellcastingBootstrap.kt"
EDITOR = ROOT / "CharacterEditorV4.kt"

helper = HELPER.read_text(encoding="utf-8")
editor = EDITOR.read_text(encoding="utf-8")

errors: list[str] = []

def require(text: str, marker: str, label: str) -> None:
    if marker not in text:
        errors.append(f"missing {label}: {marker}")

for key, ability in {
    "artificer-2025": "INTELLIGENCE",
    "artificer-5e": "INTELLIGENCE",
    "bard-2024": "CHARISMA",
    "cleric-2024": "WISDOM",
    "druid-2024": "WISDOM",
    "paladin-2024": "CHARISMA",
    "ranger-2024": "WISDOM",
    "sorcerer-2024": "CHARISMA",
    "warlock-2024": "CHARISMA",
    "wizard-2024": "INTELLIGENCE",
}.items():
    require(helper, f'"{key}" to CharacterClassSpellcastingMetadata(CharacterAbility.{ability})', f"catalog metadata {key}")

require(helper, "fun needsCharacterSpellcastingBootstrap(", "bootstrap-need helper")
require(helper, "fun reconcileCharacterSpellcastingBootstrap(", "reconciliation helper")
require(helper, "source.originKind == CharacterSpellcastingOriginKind.CLASS && source.linkedClassId == classLevel.id", "canonical source identity reuse")
require(helper, "if (!current.ability.configured)", "configured profile preservation")
require(helper, "profiles[profileIndex] = current.copy(ability = defaultAbility)", "unconfigured profile ability fill")

require(editor, "val storedSpellcastingBootstrap = remember(", "stored bootstrap projection")
require(editor, "LaunchedEffect(draft.classes)", "class-change bootstrap")
require(editor, "needsCharacterSpellcastingBootstrap(settingsSheet.classes, stored.spellcastingSources)", "one-time UI availability")
require(editor, "val effectiveSpellcasterEnabled = stored.spellcasterEnabled || canonicalSpellcastingBootstrapNeeded", "effective spellcaster visibility")
require(editor, "val reconciledSpellcasting = reconcileCharacterSpellcastingBootstrap(", "save-path reconciliation")
require(editor, "spellcastingSources = reconciledSpellcasting.sources", "reconciled source persistence")
require(editor, "spellcasterEnabled = normalizedCandidate.spellcasterEnabled || bootstrapNeededBeforePersist", "bootstrap enablement persistence")
require(editor, "val savedSpellcastingProfiles = reconciledSpellcasting.profiles", "reconciled profile persistence")

if errors:
    raise SystemExit("Player spellcasting bootstrap guard FAIL:\n- " + "\n- ".join(errors))

print(
    "Player spellcasting bootstrap guard PASS: canonical base-caster metadata present; "
    "linked source identities reused; configured profiles preserved; Mago/class bootstrap wired "
    "through draft visibility and save persistence."
)
