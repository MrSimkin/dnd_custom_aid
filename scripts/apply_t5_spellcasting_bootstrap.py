#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
MARKER = "storedSpellcastingBootstrap"


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, observed {count}")
    return text.replace(old, new, 1)


def main() -> None:
    text = PATH.read_text(encoding="utf-8")
    if MARKER in text:
        print("T5 spellcasting bootstrap editor migration already applied; no changes required.")
        return

    text = replace_once(
        text,
        "import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalState\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterHitPoints\n",
        "import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalState\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.needsCharacterSpellcastingBootstrap\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.reconcileCharacterSpellcastingBootstrap\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterHitPoints\n",
        "shared bootstrap imports",
    )

    text = replace_once(
        text,
        "    val pcSettingsContext = LocalCharacterPcSettingsContextV4.current\n"
        "    val successorState = pcSettingsContext?.successorState ?: CharacterSuccessorState()\n"
        "    var draft by rememberSaveable(\n",
        "    val pcSettingsContext = LocalCharacterPcSettingsContextV4.current\n"
        "    val successorState = pcSettingsContext?.successorState ?: CharacterSuccessorState()\n"
        "    val storedSpellcastingBootstrap = remember(\n"
        "        stored.classes,\n"
        "        stored.spellcastingSources,\n"
        "        successorState.spellcastingProfiles,\n"
        "    ) {\n"
        "        reconcileCharacterSpellcastingBootstrap(\n"
        "            classes = stored.classes,\n"
        "            existingSources = stored.spellcastingSources,\n"
        "            existingProfiles = successorState.spellcastingProfiles,\n"
        "        )\n"
        "    }\n"
        "    var draft by rememberSaveable(\n",
        "stored bootstrap projection",
    )

    text = replace_once(
        text,
        "        mutableStateOf(characterSpellcastingProfilesToJsonV4(successorState.spellcastingProfiles))\n",
        "        mutableStateOf(characterSpellcastingProfilesToJsonV4(storedSpellcastingBootstrap.profiles))\n",
        "initial profile bootstrap",
    )

    text = replace_once(
        text,
        "                CharacterSpellcastingDraftV4(\n"
        "                    sources = stored.spellcastingSources,\n"
        "                    spells = stored.spells,\n"
        "                ),\n",
        "                CharacterSpellcastingDraftV4(\n"
        "                    sources = storedSpellcastingBootstrap.sources,\n"
        "                    spells = stored.spells,\n"
        "                ),\n",
        "initial source bootstrap",
    )

    text = replace_once(
        text,
        "    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored\n"
        "    val overviewProjectionSheet = settingsSheet.copy(\n",
        "    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored\n"
        "    LaunchedEffect(settingsSheet.classes) {\n"
        "        val reconciled = reconcileCharacterSpellcastingBootstrap(\n"
        "            classes = settingsSheet.classes,\n"
        "            existingSources = spellcastingDraft.sources,\n"
        "            existingProfiles = spellcastingProfiles,\n"
        "        )\n"
        "        if (reconciled.sources != spellcastingDraft.sources) {\n"
        "            spellcastingDraftJson = characterSpellcastingDraftToJsonV4(\n"
        "                spellcastingDraft.copy(sources = reconciled.sources),\n"
        "            )\n"
        "            savedMessage = null\n"
        "        }\n"
        "        if (reconciled.profiles != spellcastingProfiles) {\n"
        "            spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(reconciled.profiles)\n"
        "            savedMessage = null\n"
        "        }\n"
        "    }\n"
        "    val canonicalSpellcastingBootstrapNeeded = remember(settingsSheet.classes, stored.spellcastingSources) {\n"
        "        needsCharacterSpellcastingBootstrap(settingsSheet.classes, stored.spellcastingSources)\n"
        "    }\n"
        "    val effectiveSpellcasterEnabled = stored.spellcasterEnabled || canonicalSpellcastingBootstrapNeeded\n"
        "    val overviewProjectionSheet = settingsSheet.copy(\n",
        "class projection bootstrap and effective spellcaster availability",
    )

    text = replace_once(
        text,
        "    val storedSpellcastingProfilesDraftJson = remember(successorState.spellcastingProfiles) {\n"
        "        characterSpellcastingProfilesToJsonV4(successorState.spellcastingProfiles)\n"
        "    }\n",
        "    val storedSpellcastingProfilesDraftJson = remember(storedSpellcastingBootstrap.profiles) {\n"
        "        characterSpellcastingProfilesToJsonV4(storedSpellcastingBootstrap.profiles)\n"
        "    }\n",
        "stored profile comparison projection",
    )

    text = replace_once(
        text,
        "    val storedSpellcastingDraftJson = remember(stored) {\n"
        "        characterSpellcastingDraftToJsonV4(\n"
        "            CharacterSpellcastingDraftV4(\n"
        "                sources = stored.spellcastingSources,\n"
        "                spells = stored.spells,\n"
        "            ),\n"
        "        )\n"
        "    }\n",
        "    val storedSpellcastingDraftJson = remember(storedSpellcastingBootstrap.sources, stored.spells) {\n"
        "        characterSpellcastingDraftToJsonV4(\n"
        "            CharacterSpellcastingDraftV4(\n"
        "                sources = storedSpellcastingBootstrap.sources,\n"
        "                spells = stored.spells,\n"
        "            ),\n"
        "        )\n"
        "    }\n",
        "stored source comparison projection",
    )

    occurrences = text.count("spellcasterEnabled = stored.spellcasterEnabled,")
    if occurrences < 3:
        raise RuntimeError(f"effective spellcaster UI: expected at least three stored flag uses, observed {occurrences}")
    text = text.replace(
        "spellcasterEnabled = stored.spellcasterEnabled,",
        "spellcasterEnabled = effectiveSpellcasterEnabled,",
    )

    text = replace_once(
        text,
        "        val normalizedCandidate = setCharacterHitPoints(\n"
        "            sheet = candidate,\n"
        "            currentHp = candidate.currentHp,\n"
        "            maxHp = candidate.maxHp,\n"
        "        ).copy(tempHp = candidate.tempHp.coerceAtLeast(0))\n"
        "        val integrated = normalizedCandidate.copy(\n",
        "        val normalizedCandidate = setCharacterHitPoints(\n"
        "            sheet = candidate,\n"
        "            currentHp = candidate.currentHp,\n"
        "            maxHp = candidate.maxHp,\n"
        "        ).copy(tempHp = candidate.tempHp.coerceAtLeast(0))\n"
        "        val bootstrapNeededBeforePersist = needsCharacterSpellcastingBootstrap(\n"
        "            classes = normalizedCandidate.classes,\n"
        "            existingSources = stored.spellcastingSources,\n"
        "        )\n"
        "        val reconciledSpellcasting = reconcileCharacterSpellcastingBootstrap(\n"
        "            classes = normalizedCandidate.classes,\n"
        "            existingSources = spellcasting.sources,\n"
        "            existingProfiles = characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson),\n"
        "        )\n"
        "        val integrated = normalizedCandidate.copy(\n",
        "persist reconciliation setup",
    )

    text = replace_once(
        text,
        "            spellcastingSources = spellcasting.sources,\n"
        "            spells = spellcasting.spells,\n",
        "            spellcastingSources = reconciledSpellcasting.sources,\n"
        "            spells = spellcasting.spells,\n"
        "            spellcasterEnabled = normalizedCandidate.spellcasterEnabled || bootstrapNeededBeforePersist,\n",
        "persist reconciled sources and bootstrap visibility",
    )

    text = replace_once(
        text,
        "        val savedSpellcastingProfiles = characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson)\n"
        "            .filter { it.sourceId in liveSpellSourceIds }\n",
        "        val savedSpellcastingProfiles = reconciledSpellcasting.profiles\n"
        "            .filter { it.sourceId in liveSpellSourceIds }\n",
        "persist reconciled profiles",
    )

    PATH.write_text(text, encoding="utf-8")
    print(
        "T5 spellcasting bootstrap editor migration applied: canonical source/profile projection, "
        "class-change reconciliation from projected domain classes, one-time Conjuros enablement, "
        "and save-path persistence."
    )


if __name__ == "__main__":
    main()
