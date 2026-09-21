package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProgressMode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBaseLayoutMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.spellAttackModifier
import io.github.mrsimkin.dndcustomaid.shared.character.spellSaveDc
import java.awt.Color
import java.io.OutputStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle

/**
 * Production promotion of the owner-approved/frozen Classic Run-2 normal three-page family.
 *
 * This first Classic production pass intentionally renders only the three normal pages. Matching
 * Extended roles remain isolated follow-up passes; until then this renderer rejects plans/data that
 * would require an unpromoted Classic continuation instead of silently discarding canonical data.
 */
internal class DesktopClassicRenderer {
    private val overflowDiagnostics = mutableListOf<String>()

    fun renderBase(
        plan: PcSheetPdfRenderPlan,
        output: OutputStream,
    ) {
        require(plan.request.visualFamily == PcSheetVisualFamily.CLASSIC_DND_STYLE) {
            "DesktopClassicRenderer only supports the Classic D&D-style family."
        }
        require(plan.baseLayoutMode == PcSheetBaseLayoutMode.FAITHFUL) {
            "Classic production pass 1 currently supports the faithful base layout only."
        }
        require(plan.mandatoryExtendedPages.isEmpty()) {
            "Classic production pass 1 cannot yet render mandatory Extended pages."
        }

        overflowDiagnostics.clear()
        PDDocument().use { doc ->
            val fonts = DesktopPdfFontRegistry(doc, CLASSIC_THEME)
            val p = DesktopPdfRenderingPrimitives(fonts)

            drawMain(doc, p, plan)
            drawCharacterAndEquipment(doc, p, plan)
            drawSpells(doc, p, plan)

            check(overflowDiagnostics.isEmpty()) {
                "Classic production base requires a matching Extended continuation:\n" +
                    overflowDiagnostics.joinToString("\n")
            }
            doc.save(output)
        }
    }

    private fun drawMain(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val closure = aggregate.closure
        val successor = aggregate.successor
        val classes = sheet.classes.sortedBy { it.sortOrder }
        val backgroundName = successor.backgroundIdentity?.name
            ?.trim()?.takeIf { it.isNotEmpty() }
            ?: sheet.background.name.trim()
        val speciesName = successor.subraceIdentity?.name
            ?.trim()?.takeIf { it.isNotEmpty() }
            ?: successor.speciesIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
            ?: sheet.background.race.trim()
        val classSummary = classes.joinToString(" / ") { "${it.name} ${it.level}" }
        val subclassSummary = classes.mapNotNull { classLevel ->
            classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }
        }.joinToString(" / ")
        val xp = if (closure.progressMode == CharacterProgressMode.EXPERIENCE) {
            formatInteger(closure.experiencePoints)
        } else {
            ""
        }

        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            identityHeader(
                s = s,
                p = p,
                name = sheet.name,
                details = listOf(
                    "TRASFONDO" to backgroundName,
                    "CLASE" to classSummary,
                    "ESPECIE" to speciesName,
                    "SUBCLASE" to subclassSummary,
                ),
                level = sheet.totalLevel.toString(),
                xp = xp,
            )

            val leftX = 24f
            val leftW = 232f
            decorativeStat(
                s, p, leftX, 108f, leftW, 43f,
                "BONIFICADOR POR COMPETENCIA", signed(sheet.finalProficiencyBonus),
            )

            val panelW = 112f
            val gap = 8f
            val placements = listOf(
                Triple(CharacterAbility.STRENGTH, leftX, 160f),
                Triple(CharacterAbility.INTELLIGENCE, leftX + panelW + gap, 160f),
                Triple(CharacterAbility.DEXTERITY, leftX, 344f),
                Triple(CharacterAbility.WISDOM, leftX + panelW + gap, 344f),
                Triple(CharacterAbility.CONSTITUTION, leftX, 528f),
                Triple(CharacterAbility.CHARISMA, leftX + panelW + gap, 528f),
            )
            placements.forEach { (ability, x, top) ->
                val save = sheet.savingThrow(ability)
                val skills = SkillKey.entries
                    .filter { it.ability == ability }
                    .map { key ->
                        val state = sheet.skill(key)
                        SkillRow(
                            name = skillLabel(key),
                            total = signed(sheet.skillTotal(key)),
                            training = training(state.training),
                        )
                    }
                attributePanel(
                    s = s,
                    p = p,
                    x = x,
                    top = top,
                    width = panelW,
                    title = abilityLabel(ability),
                    abbreviation = abilityAbbreviation(ability),
                    score = sheet.abilityScore(ability).toString(),
                    modifier = signed(sheet.abilityModifier(ability)),
                    saveTotal = signed(sheet.savingThrowTotal(ability)),
                    saveTraining = if (save.proficient) Training.PROFICIENT else Training.NONE,
                    skills = skills,
                )
            }

            val rightX = 270f
            val rightW = 318f
            combatOverview(
                s = s,
                p = p,
                x = rightX,
                top = 108f,
                width = rightW,
                armorClass = sheet.armorClass,
                currentHp = sheet.currentHp,
                maxHp = sheet.maxHp,
                hitDice = classes.joinToString(" / ") { "${it.hitDiceRemaining}d${it.hitDieSides}" },
                deathSaveSuccesses = sheet.deathSaveSuccesses,
                deathSaveFailures = sheet.deathSaveFailures,
            )
            quickReferenceRow(
                s = s,
                p = p,
                x = rightX,
                top = 194f,
                width = rightW,
                initiative = signed(sheet.initiativeModifier),
                speed = sheet.speed.toString(),
                passivePerception = sheet.passivePerception.toString(),
                inspiration = sheet.inspiration,
            )

            titledFrame(s, p, rightX, 248f, rightW, 150f, "ARMAS Y ACCIONES")
            tableHeader(
                s, p, rightX + 10f, 278f,
                listOf(148f to "Nombre", 50f to "Bonif.", 86f to "Daño / notas"),
            )
            val combatEntries = sheet.combatEntries.sortedBy { it.sortOrder }
            if (combatEntries.size > BASE_COMBAT_CAPACITY) {
                overflowDiagnostics += "combat:${combatEntries.size - BASE_COMBAT_CAPACITY} entrada(s)"
            }
            combatEntries.take(BASE_COMBAT_CAPACITY).forEachIndexed { index, entry ->
                val top = 300f + index * 23f
                text(s, p, rightX + 10f, top, 148f, 18f, entry.name, PdfTypographyRole.BODY, 8.5f, 7.2f)
                text(
                    s, p, rightX + 162f, top, 45f, 18f,
                    entry.attackModifier?.let(::signed).orEmpty(),
                    PdfTypographyRole.NUMERIC_COMPACT, 9f, 8f,
                    align = PdfHorizontalAlignment.CENTER,
                )
                val detail = listOfNotNull(
                    entry.damageEffect.takeIf { it.isNotBlank() },
                    entry.rangeText?.takeIf { it.isNotBlank() },
                    entry.notes?.takeIf { it.isNotBlank() },
                ).joinToString(" · ")
                text(s, p, rightX + 211f, top, 97f, 18f, detail, PdfTypographyRole.BODY, 8.2f, 7f)
                hairline(s, rightX + 10f, top + 20f, rightX + rightW - 10f, top + 20f)
            }

            val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
            val classTraits = orderedTraits.filter { it.type == CharacterTraitType.CLASS }
            val speciesTraits = orderedTraits.filter { it.type == CharacterTraitType.SPECIES_RACE }
            val feats = orderedTraits.filter { it.type == CharacterTraitType.FEAT }

            titledFrame(s, p, rightX, 410f, rightW, 184f, "RASGOS DE CLASE")
            ruledTextArea(
                s, p, rightX + 10f, 442f, rightW - 20f, 140f,
                classTraits.take(BASE_CLASS_TRAIT_CAPACITY).map(::traitSummary),
                8.4f,
            )

            titledFrame(s, p, rightX, 606f, 154f, 112f, "ATRIBUTOS DE ESPECIE")
            speciesTraits.take(BASE_SPECIES_TRAIT_CAPACITY).forEachIndexed { index, trait ->
                val rowTop = 636f + index * 22f
                text(
                    s, p, rightX + 10f, rowTop, 134f, 17f,
                    trait.name, PdfTypographyRole.BODY, 7.8f, 6.8f,
                )
                hairline(s, rightX + 10f, rowTop + 21f, rightX + 144f, rowTop + 21f)
            }

            titledFrame(s, p, rightX + 164f, 606f, 154f, 112f, "DOTES")
            ruledTextArea(
                s, p, rightX + 173f, 638f, 136f, 70f,
                feats.take(BASE_FEAT_CAPACITY).map(::traitSummary),
                8.2f,
            )

            footer(s, p, 1, "RESUMEN / COMBATE / ATRIBUTOS Y HABILIDADES")
        }
    }

    private fun drawCharacterAndEquipment(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val background = sheet.background
        val usageByItem = aggregate.closure.inventoryUsage.associateBy { it.itemId }
        val inventory = sheet.inventoryItems.sortedBy { it.sortOrder }

        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            secondaryHeader(s, p, sheet.name, "PERSONAJE / HISTORIA / EQUIPO")

            titledFrame(s, p, 24f, 104f, 226f, 196f, "ASPECTO")
            // D-0074: when no real locally-resolved portrait bytes are supplied, leave the
            // portrait area blank. The border/frame remains part of the approved Classic grammar.
            fantasyFrame(s, 36f, 136f, 202f, 150f, 0.65f)

            titledFrame(s, p, 24f, 314f, 226f, 108f, "DESCRIPCIÓN")
            ruledBackground(s, 34f, 346f, 206f, 66f, firstRuleOffset = 28f, lineGap = 22f)

            titledFrame(s, p, 24f, 436f, 226f, 282f, "HISTORIA Y PERSONALIDAD")
            ruledBackground(s, 34f, 468f, 206f, 238f, firstRuleOffset = 32f, lineGap = 22f)
            text(
                s, p, 36f, 468f, 202f, 54f,
                listOf(background.name, background.summary, background.story)
                    .filter { it.isNotBlank() }.joinToString(" · "),
                PdfTypographyRole.NOTE_TEXT, 7.7f, 7f, wrap = true, maxLines = 3,
                vertical = PdfVerticalAlignment.TOP,
            )
            text(
                s, p, 36f, 544f, 202f, 30f,
                background.personalityTraits.takeIf { it.isNotBlank() }?.let { "Rasgo: $it" }.orEmpty(),
                PdfTypographyRole.NOTE_TEXT, 8.1f, 7f, wrap = true, maxLines = 2,
                vertical = PdfVerticalAlignment.TOP,
            )
            text(
                s, p, 36f, 588f, 202f, 25f,
                background.ideals.takeIf { it.isNotBlank() }?.let { "Ideal: $it" }.orEmpty(),
                PdfTypographyRole.NOTE_TEXT, 8.1f, 7f, wrap = true, maxLines = 2,
                vertical = PdfVerticalAlignment.TOP,
            )
            text(
                s, p, 36f, 632f, 202f, 30f,
                background.bonds.takeIf { it.isNotBlank() }?.let { "Vínculo: $it" }.orEmpty(),
                PdfTypographyRole.NOTE_TEXT, 8.1f, 7f, wrap = true, maxLines = 2,
                vertical = PdfVerticalAlignment.TOP,
            )
            text(
                s, p, 36f, 676f, 202f, 28f,
                background.flaws.takeIf { it.isNotBlank() }?.let { "Defecto: $it" }.orEmpty(),
                PdfTypographyRole.NOTE_TEXT, 8.1f, 7f, wrap = true, maxLines = 2,
                vertical = PdfVerticalAlignment.TOP,
            )

            titledFrame(s, p, 264f, 104f, 324f, 316f, "EQUIPO")
            coinStrip(s, p, 276f, 136f, sheet.currencies.associateBy { it.key.lowercase() })
            tableHeader(
                s, p, 276f, 180f,
                listOf(36f to "Cant.", 146f to "Objeto", 112f to "Notas"),
            )
            if (inventory.size > BASE_EQUIPMENT_CAPACITY) {
                overflowDiagnostics += "inventory:${inventory.size - BASE_EQUIPMENT_CAPACITY} objeto(s)"
            }
            inventory.take(BASE_EQUIPMENT_CAPACITY).forEachIndexed { index, item ->
                val top = 202f + index * 27f
                text(
                    s, p, 278f, top, 32f, 19f,
                    item.quantity.toString(), PdfTypographyRole.NUMERIC_COMPACT, 8.6f, 7.8f,
                    align = PdfHorizontalAlignment.CENTER,
                )
                text(s, p, 316f, top, 142f, 19f, item.name, PdfTypographyRole.BODY, 8.5f, 7.2f)
                text(
                    s, p, 464f, top, 112f, 19f,
                    inventoryBaseNote(item, usageByItem[item.id]),
                    PdfTypographyRole.BODY, 8f, 7f,
                )
                hairline(s, 276f, top + 21f, 576f, top + 21f)
            }
            val baseEquipmentCount = inventory.size.coerceAtMost(BASE_EQUIPMENT_CAPACITY)
            repeat((BASE_EQUIPMENT_CAPACITY - baseEquipmentCount).coerceAtLeast(0)) { index ->
                val row = baseEquipmentCount + index
                val y = 202f + row * 27f + 21f
                hairline(s, 276f, y, 576f, y)
            }
            repeat(2) { index ->
                val y = 391f + index * 18f
                hairline(s, 276f, y, 576f, y)
            }

            val orderedTraits = sheet.traits.sortedBy { it.sortOrder }
            val usedTraitIds = buildSet {
                orderedTraits.filter { it.type == CharacterTraitType.CLASS }
                    .take(BASE_CLASS_TRAIT_CAPACITY).forEach { add(it.id) }
                orderedTraits.filter { it.type == CharacterTraitType.SPECIES_RACE }
                    .take(BASE_SPECIES_TRAIT_CAPACITY).forEach { add(it.id) }
                orderedTraits.filter { it.type == CharacterTraitType.FEAT }
                    .take(BASE_FEAT_CAPACITY).forEach { add(it.id) }
            }
            val additionalTraits = orderedTraits.filter { it.id !in usedTraitIds }
            titledFrame(s, p, 264f, 434f, 324f, 132f, "RASGOS ADICIONALES")
            if (additionalTraits.size > BASE_ADDITIONAL_TRAIT_CAPACITY) {
                overflowDiagnostics += "additional-traits:${additionalTraits.size - BASE_ADDITIONAL_TRAIT_CAPACITY} rasgo(s)"
            }
            ruledTextArea(
                s, p, 276f, 466f, 300f, 88f,
                additionalTraits.take(BASE_ADDITIONAL_TRAIT_CAPACITY).map(::traitSummary),
                8.4f,
            )

            val languages = sheet.proficiencies
                .filter { it.type == CharacterProficiencyType.LANGUAGE }
                .sortedBy { it.sortOrder }
            titledFrame(s, p, 264f, 580f, 156f, 138f, "IDIOMAS")
            if (languages.size > BASE_LANGUAGE_CAPACITY) {
                overflowDiagnostics += "languages:${languages.size - BASE_LANGUAGE_CAPACITY} idioma(s)"
            }
            ruledTextArea(
                s, p, 276f, 612f, 132f, 92f,
                languages.take(BASE_LANGUAGE_CAPACITY).map { it.name },
                8.8f,
            )

            val alliesAndTreasure = buildList {
                sheet.companions.sortedBy { it.sortOrder }.take(1).forEach { companion ->
                    add(
                        companion.name +
                            companion.kind.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty(),
                    )
                }
                aggregate.successor.preferences.valuablesText
                    .split(';').map { it.trim() }.filter { it.isNotEmpty() }
                    .take(1).forEach(::add)
            }
            titledFrame(s, p, 432f, 580f, 156f, 138f, "ALIADOS Y TESORO")
            ruledTextArea(s, p, 444f, 612f, 132f, 92f, alliesAndTreasure, 8.1f)

            footer(s, p, 2, "PERSONAJE / EQUIPO / HISTORIA")
        }
    }

    private fun drawSpells(
        doc: PDDocument,
        p: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val aggregate = plan.snapshot.aggregate
        val sheet = aggregate.sheet
        val sourceOrder = sheet.spellcastingSources.associate { it.id to it.sortOrder }
        val profile = aggregate.successor.spellcastingProfiles
            .minByOrNull { sourceOrder[it.sourceId] ?: Int.MAX_VALUE }
        val profileAbility = profile?.ability
        val abilityName = when {
            profileAbility?.builtIn != null -> abilityLabel(requireNotNull(profileAbility.builtIn))
            profileAbility?.customAttributeId != null -> aggregate.successor.customAttributes
                .firstOrNull { it.id == profileAbility.customAttributeId }?.name.orEmpty()
            sheet.spellcastingAbility != SpellcastingAbility.NONE -> spellcastingAbilityLabel(sheet.spellcastingAbility)
            else -> ""
        }
        val saveDc = profile?.let { sheet.spellSaveDc(it, aggregate.successor) } ?: sheet.spellSaveDc
        val attack = profile?.let { sheet.spellAttackModifier(it, aggregate.successor) } ?: sheet.spellAttackModifier
        val slots = sheet.spellSlots.associateBy { it.level }
        val spells = sheet.spells
            .sortedWith(compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell> { it.sortOrder }
                .thenBy { it.name.lowercase() })
            .groupBy { it.level }

        val page = addPage(doc)
        PDPageContentStream(doc, page).use { s ->
            spellHeader(
                s = s,
                p = p,
                name = sheet.name,
                ability = abilityName,
                modifier = when {
                    profileAbility?.builtIn != null ->
                        signed(sheet.abilityModifier(requireNotNull(profileAbility.builtIn)))
                    profileAbility?.customAttributeId != null -> aggregate.successor.customAttributes
                        .firstOrNull { it.id == profileAbility.customAttributeId }
                        ?.modifier?.let(::signed).orEmpty()
                    else -> legacySpellcastingModifier(sheet.spellcastingAbility, sheet)
                },
                saveDc = saveDc?.toString().orEmpty(),
                attack = attack?.let(::signed).orEmpty(),
            )
            spellSlotBand(s, p, 24f, 112f, 564f, slots.mapValues { it.value.totalSlots })

            val colW = 176f
            val gap = 12f
            val x1 = 24f
            val x2 = x1 + colW + gap
            val x3 = x2 + colW + gap

            spellLevelBlock(s, p, x1, 190f, colW, 160f, "TRUCOS", "",
                classicSpellRows(spells[0].orEmpty()))
            spellLevelBlock(s, p, x1, 362f, colW, 356f, "NIVEL 1", slots[1]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[1].orEmpty()))

            spellLevelBlock(s, p, x2, 190f, colW, 252f, "NIVEL 2", slots[2]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[2].orEmpty()))
            spellLevelBlock(s, p, x2, 454f, colW, 264f, "NIVEL 3", slots[3]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[3].orEmpty()))

            spellLevelBlock(s, p, x3, 190f, colW, 168f, "NIVEL 4", slots[4]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[4].orEmpty()))
            spellLevelBlock(s, p, x3, 370f, colW, 168f, "NIVEL 5", slots[5]?.totalSlots?.toString().orEmpty(),
                classicSpellRows(spells[5].orEmpty()))
            val highLevel = (6..9).flatMap { level ->
                spells[level].orEmpty().map { spell ->
                    ClassicSpellRow("N$level ${spell.name}", spell.sourceAssociations.any { it.prepared })
                }
            }
            spellLevelBlock(s, p, x3, 550f, colW, 168f, "NIVEL 6+", "", highLevel)

            footer(s, p, 3, "CONJUROS / ESPACIOS / PREPARACIÓN")
        }
    }

    private fun combatOverview(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        armorClass: Int,
        currentHp: Int,
        maxHp: Int,
        hitDice: String,
        deathSaveSuccesses: Int,
        deathSaveFailures: Int,
    ) {
        fantasyFrame(s, x, top, width, 76f, 0.9f)
        shieldStat(s, p, x + 10f, top + 10f, 56f, 55f, "CA", armorClass.toString())
        miniRunicStat(s, p, x + 74f, top + 10f, 88f, 55f, "PUNTOS DE GOLPE", "$currentHp / $maxHp")
        miniRunicStat(s, p, x + 170f, top + 10f, 60f, 55f, "DADOS DE GOLPE", hitDice)
        text(s, p, x + 238f, top + 10f, 68f, 12f, "SALV. MUERTE", PdfTypographyRole.OPTIONAL_DECORATIVE, 6.3f, 5.4f,
            align = PdfHorizontalAlignment.CENTER)
        repeat(3) { index ->
            marker(
                s, p, x + 250f + index * 17f, top + 36f, 9f,
                if (index < deathSaveSuccesses.coerceIn(0, 3)) PdfMarkerKind.DIAMOND_FILLED else PdfMarkerKind.DIAMOND_OUTLINE,
            )
        }
        repeat(3) { index ->
            marker(
                s, p, x + 250f + index * 17f, top + 55f, 9f,
                if (index < deathSaveFailures.coerceIn(0, 3)) PdfMarkerKind.DIAMOND_FILLED else PdfMarkerKind.DIAMOND_OUTLINE,
            )
        }
        text(s, p, x + 298f, top + 29f, 8f, 10f, "E", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.4f, 4.8f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 298f, top + 48f, 8f, 10f, "F", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.4f, 4.8f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun quickReferenceRow(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        initiative: String,
        speed: String,
        passivePerception: String,
        inspiration: Boolean,
    ) {
        val labels = listOf(
            Triple("INICIATIVA", initiative, 58f),
            Triple("VELOCIDAD", speed, 58f),
            Triple("TAMAÑO", "", 60f),
            Triple("PERCEPCIÓN PASIVA", passivePerception, 82f),
        )
        var cursor = x
        labels.forEach { (label, value, w) ->
            miniRunicStat(s, p, cursor, top, w, 42f, label, value)
            cursor += w + 4f
        }
        val inspirationWidth = 44f
        fantasyFrame(s, cursor, top, inspirationWidth, 42f, 0.65f)
        text(s, p, cursor + 2f, top + 4f, inspirationWidth - 4f, 11f, "INSPIRACIÓN",
            PdfTypographyRole.OPTIONAL_DECORATIVE, 5.7f, 4.9f, align = PdfHorizontalAlignment.CENTER)
        marker(
            s, p, cursor + inspirationWidth / 2f, top + 27f, 11f,
            if (inspiration) PdfMarkerKind.STAR_FILLED else PdfMarkerKind.STAR_OUTLINE,
        )
    }

    private fun coinStrip(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        currencies: Map<String, io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency>,
    ) {
        val coins = listOf(
            "PC" to "pc",
            "PP" to "pp",
            "PE" to "pe",
            "PO" to "po",
            "PPT" to "pt",
        )
        coins.forEachIndexed { index, (label, key) ->
            miniRunicStat(
                s, p, x + index * 58f, top, 52f, 34f,
                label, currencies[key]?.amount?.toString().orEmpty(),
            )
        }
    }

    private fun spellHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        ability: String,
        modifier: String,
        saveDc: String,
        attack: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 72f, 1.05f, fill = PAPER_TINT)
        text(s, p, 36f, 31f, 195f, 31f, name, PdfTypographyRole.CHARACTER_NAME, 18f, 15f)
        text(s, p, 36f, 66f, 195f, 12f, "APTITUD MÁGICA", PdfTypographyRole.OPTIONAL_DECORATIVE, 6.8f, 6f)
        text(s, p, 239f, 27f, 112f, 15f, ability, PdfTypographyRole.OPTIONAL_DECORATIVE, 9f, 7.5f,
            align = PdfHorizontalAlignment.CENTER)
        miniRunicStat(s, p, 239f, 45f, 112f, 38f, "MODIFICADOR", modifier)
        miniRunicStat(s, p, 363f, 30f, 101f, 52f, "CD DE SALVACIÓN", saveDc)
        miniRunicStat(s, p, 476f, 30f, 100f, 52f, "ATAQUE", attack)
    }

    private fun spellSlotBand(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        totals: Map<Int, Int>,
    ) {
        titledFrame(s, p, x, top, width, 66f, "ESPACIOS DE CONJURO")
        (1..9).forEachIndexed { index, level ->
            val total = totals[level] ?: 0
            val cellX = x + 10f + index * 60.2f
            text(s, p, cellX, top + 31f, 18f, 16f, level.toString(), PdfTypographyRole.NUMERIC_COMPACT, 8f, 7f,
                align = PdfHorizontalAlignment.CENTER)
            if (total > 4) {
                overflowDiagnostics += "spell-slots:N$level tiene $total espacios; la banda base muestra hasta 4"
            }
            repeat(total.coerceAtMost(4)) { markerIndex ->
                marker(s, p, cellX + 26f + markerIndex * 8.6f, top + 40f, 6.5f, PdfMarkerKind.DIAMOND_OUTLINE)
            }
        }
    }

    private fun spellLevelBlock(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        title: String,
        slots: String,
        spells: List<ClassicSpellRow>,
    ) {
        fantasyFrame(s, x, top, width, height, 0.75f)
        text(s, p, x + 10f, top + 8f, width - 58f, 18f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 9f, 7.8f)
        if (slots.isNotBlank()) {
            fantasyFrame(s, x + width - 47f, top + 6f, 37f, 24f, 0.55f)
            text(s, p, x + width - 44f, top + 9f, 31f, 16f, slots, PdfTypographyRole.NUMERIC_COMPACT, 9f, 8f,
                align = PdfHorizontalAlignment.CENTER)
        }
        val usableTop = top + 36f
        val rowH = 23f
        val maxRows = ((height - 44f) / rowH).toInt().coerceAtLeast(1)
        if (spells.size > maxRows) {
            overflowDiagnostics += "$title:${spells.size - maxRows} conjuro(s)"
        }
        repeat(maxRows) { index ->
            val rowTop = usableTop + index * rowH
            val spell = spells.getOrNull(index)
            marker(
                s, p, x + 11f, rowTop + 8f, 7f,
                if (spell?.prepared == true) PdfMarkerKind.CIRCLE_FILLED else PdfMarkerKind.CIRCLE_OUTLINE,
            )
            text(
                s, p, x + 21f, rowTop, width - 31f, 18f,
                spell?.name.orEmpty(), PdfTypographyRole.SPELL_NAME, 8.1f, 7f,
            )
            hairline(s, x + 21f, rowTop + 20f, x + width - 10f, rowTop + 20f)
        }
    }

    private fun classicSpellRows(
        spells: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell>,
    ): List<ClassicSpellRow> = spells.map { spell ->
        ClassicSpellRow(
            name = spell.name,
            prepared = spell.sourceAssociations.any { it.prepared },
        )
    }

    private fun inventoryBaseNote(
        item: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem,
        usage: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage?,
    ): String = buildList {
        when {
            item.attuned -> add("Sintonizado")
            item.equipped -> add("Equipado")
        }
        when (usage?.kind) {
            CharacterConsumableKind.CONSUMABLE -> add("Consumible")
            CharacterConsumableKind.AMMUNITION -> add("Munición")
            CharacterConsumableKind.NONE, null -> Unit
        }
        if (usage?.quickUseAmount != null && usage.quickUseAmount != 1) add("Uso rápido ${usage.quickUseAmount}")
        if (usage?.carryState == CharacterInventoryCarryState.STORED) add("Almacenado")
        item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
    }.joinToString(" · ")

    private fun traitSummary(
        trait: io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
    ): String = if (trait.description.isBlank()) trait.name else "${trait.name}: ${trait.description}"

    private fun abilityLabel(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> "FUERZA"
        CharacterAbility.DEXTERITY -> "DESTREZA"
        CharacterAbility.CONSTITUTION -> "CONSTITUCIÓN"
        CharacterAbility.INTELLIGENCE -> "INTELIGENCIA"
        CharacterAbility.WISDOM -> "SABIDURÍA"
        CharacterAbility.CHARISMA -> "CARISMA"
    }

    private fun abilityAbbreviation(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> "FUE"
        CharacterAbility.DEXTERITY -> "DES"
        CharacterAbility.CONSTITUTION -> "CON"
        CharacterAbility.INTELLIGENCE -> "INT"
        CharacterAbility.WISDOM -> "SAB"
        CharacterAbility.CHARISMA -> "CAR"
    }

    private fun legacySpellcastingModifier(
        ability: SpellcastingAbility,
        sheet: io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet,
    ): String = when (ability) {
        SpellcastingAbility.STRENGTH -> signed(sheet.abilityModifier(CharacterAbility.STRENGTH))
        SpellcastingAbility.DEXTERITY -> signed(sheet.abilityModifier(CharacterAbility.DEXTERITY))
        SpellcastingAbility.CONSTITUTION -> signed(sheet.abilityModifier(CharacterAbility.CONSTITUTION))
        SpellcastingAbility.INTELLIGENCE -> signed(sheet.abilityModifier(CharacterAbility.INTELLIGENCE))
        SpellcastingAbility.WISDOM -> signed(sheet.abilityModifier(CharacterAbility.WISDOM))
        SpellcastingAbility.CHARISMA -> signed(sheet.abilityModifier(CharacterAbility.CHARISMA))
        SpellcastingAbility.OTHER,
        SpellcastingAbility.NONE,
        -> ""
    }

    private fun spellcastingAbilityLabel(ability: SpellcastingAbility): String = when (ability) {
        SpellcastingAbility.STRENGTH -> "FUERZA"
        SpellcastingAbility.DEXTERITY -> "DESTREZA"
        SpellcastingAbility.CONSTITUTION -> "CONSTITUCIÓN"
        SpellcastingAbility.INTELLIGENCE -> "INTELIGENCIA"
        SpellcastingAbility.WISDOM -> "SABIDURÍA"
        SpellcastingAbility.CHARISMA -> "CARISMA"
        SpellcastingAbility.OTHER -> "OTRA"
        SpellcastingAbility.NONE -> ""
    }

    private fun skillLabel(key: SkillKey): String = when (key) {
        SkillKey.ACROBATICS -> "Acrobacias"
        SkillKey.ANIMAL_HANDLING -> "Trato con animales"
        SkillKey.ARCANA -> "Conocimiento arcano"
        SkillKey.ATHLETICS -> "Atletismo"
        SkillKey.DECEPTION -> "Engaño"
        SkillKey.HISTORY -> "Historia"
        SkillKey.INSIGHT -> "Perspicacia"
        SkillKey.INTIMIDATION -> "Intimidación"
        SkillKey.INVESTIGATION -> "Investigación"
        SkillKey.MEDICINE -> "Medicina"
        SkillKey.NATURE -> "Naturaleza"
        SkillKey.PERCEPTION -> "Percepción"
        SkillKey.PERFORMANCE -> "Interpretación"
        SkillKey.PERSUASION -> "Persuasión"
        SkillKey.RELIGION -> "Religión"
        SkillKey.SLEIGHT_OF_HAND -> "Juego de manos"
        SkillKey.STEALTH -> "Sigilo"
        SkillKey.SURVIVAL -> "Supervivencia"
    }

    private fun training(value: SkillTraining): Training = when (value) {
        SkillTraining.NONE -> Training.NONE
        SkillTraining.PROFICIENT -> Training.PROFICIENT
        SkillTraining.EXPERTISE -> Training.EXPERTISE
    }

    private fun signed(value: Int): String = if (value >= 0) "+$value" else value.toString()

    private fun formatInteger(value: Int): String =
        value.toString().reversed().chunked(3).joinToString(".").reversed()

    private fun identityHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        details: List<Pair<String, String>>,
        level: String,
        xp: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 72f, 1.15f, fill = PAPER_TINT)
        text(s, p, 36f, 31f, 214f, 37f, name, PdfTypographyRole.CHARACTER_NAME, 20f, 16f)
        hairline(s, 36f, 68f, 250f, 68f)
        text(s, p, 36f, 70f, 214f, 15f, "NOMBRE DEL PERSONAJE", PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.5f)

        val detailX = 264f
        val detailW = 226f
        details.forEachIndexed { i, (label, value) ->
            val col = i % 2
            val row = i / 2
            val x = detailX + col * 113f
            val top = 31f + row * 27f
            text(s, p, x, top, 106f, 14f, value, PdfTypographyRole.BODY, 8.5f, 7.2f)
            hairline(s, x, top + 14f, x + 106f, top + 14f)
            text(s, p, x, top + 15f, 106f, 9f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 5.8f, 5.2f)
        }

        fantasyFrame(s, 500f, 31f, 76f, 52f, 0.8f)
        text(s, p, 505f, 34f, 31f, 25f, level, PdfTypographyRole.PRIMARY_VALUE, 17f, 14f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, 505f, 60f, 31f, 10f, "NIVEL", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.8f, 5f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, 540f, 34f, 31f, 25f, xp, PdfTypographyRole.NUMERIC_COMPACT, 9f, 7f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, 540f, 60f, 31f, 10f, "PX", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.8f, 5f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun secondaryHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        name: String,
        title: String,
    ) {
        fantasyFrame(s, 24f, 24f, 564f, 64f, 1.05f, fill = PAPER_TINT)
        text(s, p, 36f, 30f, 230f, 33f, name, PdfTypographyRole.CHARACTER_NAME, 18f, 15f)
        hairline(s, 36f, 66f, 266f, 66f)
        text(s, p, 278f, 32f, 298f, 22f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 12f, 10f,
            align = PdfHorizontalAlignment.RIGHT)
        text(s, p, 278f, 57f, 298f, 14f, "HOJA DE PERSONAJE", PdfTypographyRole.BODY, 7.3f, 6.3f,
            align = PdfHorizontalAlignment.RIGHT)
    }

    private fun attributePanel(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        title: String,
        abbreviation: String,
        score: String,
        modifier: String,
        saveTotal: String,
        saveTraining: Training,
        skills: List<SkillRow>,
    ) {
        fantasyFrame(s, x, top, width, 174f, 0.85f)
        text(s, p, x + 6f, top + 6f, width - 12f, 14f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 8.6f, 7.2f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 8f, top + 22f, 56f, 40f, modifier, PdfTypographyRole.PRIMARY_VALUE, 23f, 19f,
            align = PdfHorizontalAlignment.CENTER)
        circleOutline(s, x + 36f, top + 42f, 25f)
        text(s, p, x + 68f, top + 23f, width - 76f, 18f, score, PdfTypographyRole.SECONDARY_VALUE, 13f, 11f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 68f, top + 42f, width - 76f, 10f, "PUNT.", PdfTypographyRole.OPTIONAL_DECORATIVE, 5.6f, 5f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 68f, top + 53f, width - 76f, 10f, abbreviation, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.2f, 5.5f,
            align = PdfHorizontalAlignment.CENTER)

        val saveTop = top + 69f
        trainingMarker(s, p, x + 10f, saveTop + 8f, saveTraining)
        text(s, p, x + 21f, saveTop, width - 49f, 16f, "Tirada de salvación", PdfTypographyRole.BODY, 7.2f, 6.2f)
        text(s, p, x + width - 26f, saveTop, 20f, 16f, saveTotal, PdfTypographyRole.NUMERIC_COMPACT, 8f, 7f,
            align = PdfHorizontalAlignment.RIGHT)
        hairline(s, x + 8f, saveTop + 18f, x + width - 8f, saveTop + 18f)

        skills.forEachIndexed { i, row ->
            val rowTop = saveTop + 20f + i * 16.2f
            trainingMarker(s, p, x + 10f, rowTop + 7f, row.training)
            text(s, p, x + 21f, rowTop, width - 49f, 14f, row.name, PdfTypographyRole.BODY, 6.9f, 6.0f)
            text(s, p, x + width - 26f, rowTop, 20f, 14f, row.total, PdfTypographyRole.NUMERIC_COMPACT, 7.6f, 6.6f,
                align = PdfHorizontalAlignment.RIGHT)
            if (i < skills.lastIndex) hairline(s, x + 21f, rowTop + 15f, x + width - 8f, rowTop + 15f)
        }
    }

    private fun shieldStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        val cut = 8f
        s.saveGraphicsState()
        s.setStrokingColor(INk)
        s.setLineWidth(0.85f)
        val yTop = H - top
        val yBottom = H - top - height
        s.moveTo(x + cut, yTop)
        s.lineTo(x + width - cut, yTop)
        s.lineTo(x + width, yTop - cut)
        s.lineTo(x + width - 6f, yBottom + 11f)
        s.lineTo(x + width / 2f, yBottom)
        s.lineTo(x + 6f, yBottom + 11f)
        s.lineTo(x, yTop - cut)
        s.closePath()
        s.stroke()
        s.restoreGraphicsState()
        text(s, p, x + 5f, top + 6f, width - 10f, 12f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7f, 6f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 6f, top + 20f, width - 12f, 28f, value, PdfTypographyRole.PRIMARY_VALUE, 18f, 15f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun miniRunicStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.65f)
        text(s, p, x + 4f, top + 4f, width - 8f, 11f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.5f, 5.4f,
            align = PdfHorizontalAlignment.CENTER)
        text(s, p, x + 5f, top + 16f, width - 10f, height - 20f, value, PdfTypographyRole.PRIMARY_VALUE, 13.5f, 9.5f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun decorativeStat(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.8f)
        text(s, p, x + 8f, top + 7f, width - 58f, height - 14f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7.8f, 6.5f)
        circleOutline(s, x + width - 28f, top + height / 2f, 16f)
        text(s, p, x + width - 44f, top + 6f, 32f, height - 12f, value, PdfTypographyRole.PRIMARY_VALUE, 13f, 11f,
            align = PdfHorizontalAlignment.CENTER)
    }

    private fun titledFrame(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        title: String,
    ) {
        fantasyFrame(s, x, top, width, height, 0.8f)
        val labelWidth = (title.length * 5.3f + 26f).coerceIn(88f, width - 24f)
        fillRect(s, x + (width - labelWidth) / 2f, top - 1f, labelWidth, 18f, Color.WHITE)
        text(
            s, p, x + (width - labelWidth) / 2f + 6f, top + 1f,
            labelWidth - 12f, 14f, title, PdfTypographyRole.OPTIONAL_DECORATIVE, 8.6f, 7.2f,
            align = PdfHorizontalAlignment.CENTER,
        )
    }

    private fun tableHeader(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        columns: List<Pair<Float, String>>,
    ) {
        var cursor = x
        columns.forEach { (width, label) ->
            text(s, p, cursor + 2f, top, width - 4f, 16f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 6.8f, 5.8f,
                align = PdfHorizontalAlignment.CENTER)
            cursor += width
        }
        hairline(s, x, top + 17f, cursor, top + 17f)
    }

    private fun ruledTextArea(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        content: List<String>,
        fontSize: Float,
    ) {
        val lineGap = 20f
        val lines = (height / lineGap).toInt().coerceAtLeast(1)
        repeat(lines) { i ->
            val ruleTop = top + (i + 1) * lineGap
            if (ruleTop <= top + height) {
                hairline(s, x, ruleTop, x + width, ruleTop)
            }
        }
        // Text sits inside the ruled rows rather than on their baselines.
        var cursorTop = top - 2f
        content.forEach { paragraph ->
            val boxHeight = 39f
            text(s, p, x + 2f, cursorTop, width - 4f, boxHeight, paragraph, PdfTypographyRole.NOTE_TEXT,
                fontSize, (fontSize - 1.1f).coerceAtLeast(6.2f), wrap = true, maxLines = 2,
                vertical = PdfVerticalAlignment.TOP)
            cursorTop += 40f
        }
    }

    private fun ruledBackground(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        firstRuleOffset: Float = 23f,
        lineGap: Float = 20f,
    ) {
        var ruleTop = top + firstRuleOffset
        while (ruleTop <= top + height) {
            hairline(s, x, ruleTop, x + width, ruleTop)
            ruleTop += lineGap
        }
    }

    private fun trainingMarker(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        centerX: Float,
        centerTop: Float,
        training: Training,
    ) {
        val kind = when (training) {
            Training.NONE -> PdfMarkerKind.CIRCLE_OUTLINE
            Training.PROFICIENT -> PdfMarkerKind.CIRCLE_FILLED
            Training.EXPERTISE -> PdfMarkerKind.DOUBLE_CIRCLE
        }
        marker(s, p, centerX, centerTop, 8f, kind)
    }

    private fun marker(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        centerX: Float,
        centerTop: Float,
        size: Float,
        kind: PdfMarkerKind,
    ) {
        p.drawMarker(
            s,
            centerX = centerX,
            centerY = H - centerTop,
            sizePt = size,
            kind = kind,
            family = PdfSymbolFamily.V1_DERIVED,
        )
    }

    private fun fantasyFrame(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        lineWidth: Float,
        fill: Color? = null,
    ) {
        val c = 8f.coerceAtMost(width / 5f).coerceAtMost(height / 5f)
        val yTop = H - top
        val yBottom = H - top - height

        s.saveGraphicsState()
        fill?.let { s.setNonStrokingColor(it) }
        s.setStrokingColor(INk)
        s.setLineWidth(lineWidth)
        s.moveTo(x + c, yTop)
        s.lineTo(x + width - c, yTop)
        s.lineTo(x + width, yTop - c)
        s.lineTo(x + width, yBottom + c)
        s.lineTo(x + width - c, yBottom)
        s.lineTo(x + c, yBottom)
        s.lineTo(x, yBottom + c)
        s.lineTo(x, yTop - c)
        s.closePath()
        if (fill != null) s.fillAndStroke() else s.stroke()

        s.setLineWidth(0.35f)
        val d = 4f
        s.moveTo(x + c + d, yTop - 3f)
        s.lineTo(x + width - c - d, yTop - 3f)
        s.moveTo(x + c + d, yBottom + 3f)
        s.lineTo(x + width - c - d, yBottom + 3f)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun circleOutline(s: PDPageContentStream, centerX: Float, centerTop: Float, radius: Float) {
        s.saveGraphicsState()
        s.setStrokingColor(Color(125, 125, 125))
        s.setLineWidth(0.75f)
        circlePath(s, centerX, H - centerTop, radius)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun circlePath(s: PDPageContentStream, cx: Float, cy: Float, r: Float) {
        val c = r * 0.552284749831f
        s.moveTo(cx + r, cy)
        s.curveTo(cx + r, cy + c, cx + c, cy + r, cx, cy + r)
        s.curveTo(cx - c, cy + r, cx - r, cy + c, cx - r, cy)
        s.curveTo(cx - r, cy - c, cx - c, cy - r, cx, cy - r)
        s.curveTo(cx + c, cy - r, cx + r, cy - c, cx + r, cy)
        s.closePath()
    }

    private fun fillRect(
        s: PDPageContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        color: Color,
    ) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x, H - top - height, width, height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun hairline(
        s: PDPageContentStream,
        x1: Float,
        top1: Float,
        x2: Float,
        top2: Float,
    ) {
        s.saveGraphicsState()
        s.setStrokingColor(LINE_COLOR)
        s.setLineWidth(0.34f)
        s.moveTo(x1, H - top1)
        s.lineTo(x2, H - top2)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun text(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        role: PdfTypographyRole,
        preferred: Float,
        minimum: Float,
        wrap: Boolean = false,
        maxLines: Int = 1,
        align: PdfHorizontalAlignment = PdfHorizontalAlignment.LEFT,
        vertical: PdfVerticalAlignment = PdfVerticalAlignment.CENTER,
    ) {
        val result = p.drawTextBox(
            s,
            PdfTextBoxSpec(
                rect = rect(x, top, width, height),
                text = value,
                role = role,
                preferredSizePt = preferred,
                minimumSizePt = minimum,
                horizontalAlignment = align,
                verticalAlignment = vertical,
                wrapPolicy = if (wrap) PdfWrapPolicy.WORD_WRAP else PdfWrapPolicy.SINGLE_LINE,
                maximumLines = maxLines,
                horizontalPaddingPt = 0.6f,
                verticalPaddingPt = 0.35f,
            ),
        )
        if (result.hasOverflow) {
            overflowDiagnostics += "value='$value' overflow='${result.overflowText}'"
        }
    }

    private fun footer(
        s: PDPageContentStream,
        p: DesktopPdfRenderingPrimitives,
        pageNumber: Int,
        label: String,
    ) {
        hairline(s, 24f, 744f, 588f, 744f)
        text(s, p, 24f, 750f, 430f, 14f, label, PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.2f)
        text(s, p, 506f, 750f, 82f, 14f, "PÁGINA $pageNumber", PdfTypographyRole.OPTIONAL_DECORATIVE, 7.2f, 6.2f,
            align = PdfHorizontalAlignment.RIGHT)
    }

    private fun rect(x: Float, top: Float, width: Float, height: Float) =
        PdfRect(x, H - top - height, width, height)

    private fun addPage(doc: PDDocument): PDPage =
        PDPage(PDRectangle(W, H)).also(doc::addPage)

    private data class SkillRow(
        val name: String,
        val total: String,
        val training: Training,
    )

    private data class ClassicSpellRow(
        val name: String,
        val prepared: Boolean,
    )

    private enum class Training {
        NONE,
        PROFICIENT,
        EXPERTISE,
    }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val BASE_COMBAT_CAPACITY = 4
        const val BASE_CLASS_TRAIT_CAPACITY = 4
        const val BASE_SPECIES_TRAIT_CAPACITY = 3
        const val BASE_FEAT_CAPACITY = 1
        const val BASE_EQUIPMENT_CAPACITY = 7
        const val BASE_ADDITIONAL_TRAIT_CAPACITY = 2
        const val BASE_LANGUAGE_CAPACITY = 4

        val INk = Color(42, 42, 42)
        val PAPER_TINT = Color(248, 247, 243)
        val LINE_COLOR = Color(146, 146, 142)

        val CLASSIC_THEME = PdfTypographyTheme(
            id = "classic-dnd-style-run2-production",
            resourcesByRole = mapOf(
                PdfTypographyRole.CHARACTER_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
                PdfTypographyRole.HANDWRITTEN_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
                PdfTypographyRole.PRIMARY_VALUE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
                PdfTypographyRole.SECONDARY_VALUE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
                PdfTypographyRole.BODY to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.COMPACT_TABLE to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.NUMERIC_COMPACT to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.NOTE_TEXT to "fonts/pdf/text/FiraSans-Regular.ttf",
                PdfTypographyRole.SPELL_NAME to "fonts/pdf/text/FiraSans-SemiBold.ttf",
                PdfTypographyRole.OPTIONAL_DECORATIVE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
            ),
        )
    }
}
