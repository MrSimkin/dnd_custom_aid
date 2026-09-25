package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class PcSheetExportStateSelection {
    PERMANENT,
    CURRENT_SNAPSHOT,
}

enum class PcSheetVisualFamily {
    CLASSIC_DND_STYLE,
    CUSTOM_V1,
    CUSTOM_V2_PER_ATTRIBUTE,
    CUSTOM_V2_PER_ABILITY,
}

enum class PcSheetCustomStatisticsPresentation {
    EXTENDED_PAGE,
    APP_MODIFIED_SHEET,
    MODIFIED_SHEET_AND_COMPLETE_EXTENDED_PAGE,
}

enum class PcSheetPortraitFitMode {
    CROP_TO_FILL,
    FIT_ENTIRE_IMAGE,
}

enum class PcSheetBaseLayoutMode {
    FAITHFUL,
    APP_MODIFIED,
}

enum class PcSheetBasePageRole {
    MAIN,
    EQUIPMENT,
    NARRATIVE,
    EQUIPMENT_AND_NARRATIVE,
    SPELL_LIST,
    NOTES,
}

enum class PcSheetExtendedPageKind {
    CUSTOM_STATISTICS,
    TRAITS_AND_FEATURES,
    COMBAT_AND_ACTIONS,
    RESOURCES_AND_OPTIONS,
    INVENTORY_AND_EQUIPMENT,
    SPELLS,
    NOTES,
}

enum class PcSheetExportContentSection {
    CUSTOM_STATISTICS,
    TRAITS_AND_FEATURES,
    COMBAT_AND_ACTIONS,
    RESOURCES_AND_OPTIONS,
    INVENTORY_AND_EQUIPMENT,
    SPELLS,
    NOTES,
}

enum class PcSheetExportNoticeCode {
    CURRENT_SNAPSHOT_UNAVAILABLE,
    PORTRAIT_NOT_AVAILABLE_LOCALLY,
    SPELLBOOK_REQUESTED_WITHOUT_ATTACHED_SPELLS,
}

data class PcSheetExportNotice(
    val code: PcSheetExportNoticeCode,
    val message: String,
)

data class PcSheetPdfExportRequest(
    val visualFamily: PcSheetVisualFamily,
    val stateSelection: PcSheetExportStateSelection,
    val customStatisticsPresentation: PcSheetCustomStatisticsPresentation =
        PcSheetCustomStatisticsPresentation.EXTENDED_PAGE,
    val portraitFitMode: PcSheetPortraitFitMode = PcSheetPortraitFitMode.CROP_TO_FILL,
    val includeSpellDescriptions: Boolean = false,
)

data class PcSheetExportAggregate(
    val sheet: CharacterSheet,
    val closure: CharacterClosureState,
    val successor: CharacterSuccessorState,
) {
    init {
        require(sheet.id.toString().isNotBlank()) { "Character identity is required." }
    }
}

data class PcSheetExportSources(
    val permanent: PcSheetExportAggregate,
    val currentSnapshot: PcSheetExportAggregate? = null,
    val locallyAvailablePortraitRefs: Set<String> = emptySet(),
) {
    init {
        currentSnapshot?.let { current ->
            require(current.sheet.id == permanent.sheet.id) {
                "Current snapshot must belong to the same character as the permanent export source."
            }
            require(current.sheet.campaignId == permanent.sheet.campaignId) {
                "Current snapshot must belong to the same campaign as the permanent export source."
            }
        }
    }
}

data class PcSheetTemplatePage(
    val role: PcSheetBasePageRole,
    /**
     * Null means this page is application-designed rather than overlaid on an owner source PDF.
     */
    val sourceTemplatePath: String? = null,
    /**
     * 1-based source PDF page number when [sourceTemplatePath] is present.
     */
    val sourcePageNumber: Int? = null,
) {
    init {
        require((sourceTemplatePath == null) == (sourcePageNumber == null)) {
            "Template path and source page number must either both be present or both be absent."
        }
        sourcePageNumber?.let { require(it >= 1) { "Source PDF page numbers are 1-based." } }
    }
}

data class PcSheetOverflowRoute(
    val section: PcSheetExportContentSection,
    val extensionKind: PcSheetExtendedPageKind,
)

data class PcSheetCustomAttributeProjection(
    val attribute: CharacterCustomAttribute,
    val savingThrowTotal: Int?,
)

data class PcSheetCustomSkillProjection(
    val skill: CharacterCustomSkill,
    val ability: CharacterAbilityReference,
    val total: Int?,
)

data class PcSheetCustomStatisticsProjection(
    val attributes: List<PcSheetCustomAttributeProjection>,
    val skills: List<PcSheetCustomSkillProjection>,
) {
    val isEmpty: Boolean
        get() = attributes.isEmpty() && skills.isEmpty()
}

data class PcSheetPortraitPlan(
    val portraitRef: String?,
    val fitMode: PcSheetPortraitFitMode,
    val locallyAvailable: Boolean,
)

data class PcSheetSpellbookSourceProjection(
    val sourceId: Uuid,
    val sourceName: String?,
    val originKind: CharacterSpellcastingOriginKind?,
    val prepared: Boolean,
    val castingAbility: CharacterAbilityReference,
    val saveDc: Int?,
    val spellAttackModifier: Int?,
)

data class PcSheetSpellbookEntry(
    val spell: CharacterSpell,
    val sources: List<PcSheetSpellbookSourceProjection>,
)

data class PcSheetSpellbookPlan(
    val entries: List<PcSheetSpellbookEntry>,
    /**
     * Page numbers are assigned by the platform renderer after pagination, but an index is mandatory.
     */
    val indexRequired: Boolean = true,
) {
    val entriesByLevel: Map<Int, List<PcSheetSpellbookEntry>>
        get() = entries.groupBy { it.spell.level }
}

data class PcSheetExportSnapshot(
    val selectedState: PcSheetExportStateSelection,
    val aggregate: PcSheetExportAggregate,
    val customStatistics: PcSheetCustomStatisticsProjection,
    val portrait: PcSheetPortraitPlan,
    val spellbook: PcSheetSpellbookPlan?,
)

data class PcSheetPdfRenderPlan(
    val request: PcSheetPdfExportRequest,
    val snapshot: PcSheetExportSnapshot,
    val baseLayoutMode: PcSheetBaseLayoutMode,
    val basePages: List<PcSheetTemplatePage>,
    val mandatoryExtendedPages: List<PcSheetExtendedPageKind>,
    /**
     * Routes identify where a renderer must continue content after its readability floor is reached.
     * They deliberately do not decide typography, coordinates or physical pagination.
     */
    val overflowRoutes: List<PcSheetOverflowRoute>,
    val notices: List<PcSheetExportNotice>,
)

object PcSheetPdfExportPlanner {
    const val CUSTOM_V1_TEMPLATE_PATH: String =
        "assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"
    const val CUSTOM_V2_TEMPLATE_PATH: String =
        "assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf"

    fun plan(
        request: PcSheetPdfExportRequest,
        sources: PcSheetExportSources,
    ): PcSheetPdfRenderPlan {
        val notices = mutableListOf<PcSheetExportNotice>()
        val (selectedAggregate, effectiveState) = when (request.stateSelection) {
            PcSheetExportStateSelection.PERMANENT -> sources.permanent to PcSheetExportStateSelection.PERMANENT
            PcSheetExportStateSelection.CURRENT_SNAPSHOT -> {
                val current = sources.currentSnapshot
                if (current != null) {
                    current to PcSheetExportStateSelection.CURRENT_SNAPSHOT
                } else {
                    notices += PcSheetExportNotice(
                        code = PcSheetExportNoticeCode.CURRENT_SNAPSHOT_UNAVAILABLE,
                        message = "No hay un estado actual separado disponible; se exportará el estado permanente del personaje.",
                    )
                    sources.permanent to PcSheetExportStateSelection.PERMANENT
                }
            }
        }

        val portraitRef = selectedAggregate.closure.portraitRef?.trim()?.takeIf { it.isNotEmpty() }
        val portraitAvailable = portraitRef != null && portraitRef in sources.locallyAvailablePortraitRefs
        if (portraitRef != null && !portraitAvailable) {
            notices += PcSheetExportNotice(
                code = PcSheetExportNoticeCode.PORTRAIT_NOT_AVAILABLE_LOCALLY,
                message = "El retrato del personaje no está disponible localmente; la exportación continuará con el área de retrato en blanco.",
            )
        }

        val customStatistics = customStatisticsProjection(selectedAggregate)
        val hasCustomStatistics = !customStatistics.isEmpty
        val baseLayoutMode = when {
            !hasCustomStatistics -> PcSheetBaseLayoutMode.FAITHFUL
            request.customStatisticsPresentation == PcSheetCustomStatisticsPresentation.EXTENDED_PAGE ->
                PcSheetBaseLayoutMode.FAITHFUL
            else -> PcSheetBaseLayoutMode.APP_MODIFIED
        }
        val mandatoryExtendedPages = when {
            !hasCustomStatistics -> emptyList()
            request.customStatisticsPresentation == PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET ->
                emptyList()
            else -> listOf(PcSheetExtendedPageKind.CUSTOM_STATISTICS)
        }

        val spellbook = if (request.includeSpellDescriptions && selectedAggregate.sheet.spells.isNotEmpty()) {
            spellbookPlan(selectedAggregate)
        } else {
            if (request.includeSpellDescriptions && selectedAggregate.sheet.spells.isEmpty()) {
                notices += PcSheetExportNotice(
                    code = PcSheetExportNoticeCode.SPELLBOOK_REQUESTED_WITHOUT_ATTACHED_SPELLS,
                    message = "Se solicitaron descripciones de conjuros, pero el personaje no tiene conjuros asociados.",
                )
            }
            null
        }

        return PcSheetPdfRenderPlan(
            request = request,
            snapshot = PcSheetExportSnapshot(
                selectedState = effectiveState,
                aggregate = selectedAggregate,
                customStatistics = customStatistics,
                portrait = PcSheetPortraitPlan(
                    portraitRef = portraitRef,
                    fitMode = request.portraitFitMode,
                    locallyAvailable = portraitAvailable,
                ),
                spellbook = spellbook,
            ),
            baseLayoutMode = baseLayoutMode,
            basePages = activeBasePages(request.visualFamily, selectedAggregate),
            mandatoryExtendedPages = mandatoryExtendedPages,
            overflowRoutes = overflowRoutes(),
            notices = notices.toList(),
        )
    }

    private fun activeBasePages(
        family: PcSheetVisualFamily,
        aggregate: PcSheetExportAggregate,
    ): List<PcSheetTemplatePage> {
        val sheet = aggregate.sheet
        val hasSpellContent =
            sheet.spellcasterEnabled ||
                sheet.spells.isNotEmpty() ||
                sheet.spellSlots.isNotEmpty() ||
                sheet.spellcastingSources.isNotEmpty() ||
                aggregate.successor.spellcastingProfiles.isNotEmpty()

        val notesText = buildList {
            sheet.generalNotes.trim().takeIf { it.isNotEmpty() }?.let(::add)
            sheet.noteCards.sortedBy { it.sortOrder }.forEach { card ->
                val title = card.title.trim()
                val body = card.content.trim()
                when {
                    title.isNotEmpty() && body.isNotEmpty() -> add("$title: $body")
                    title.isNotEmpty() -> add(title)
                    body.isNotEmpty() -> add(body)
                }
            }
        }.joinToString("\n\n")

        val notesFitExistingNarrative = when (family) {
            PcSheetVisualFamily.CUSTOM_V1 -> notesText.length <= CUSTOM_V1_INLINE_NOTES_CHAR_BUDGET
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
            -> {
                val story = sheet.background.story.trim()
                story.length + notesText.length <= CUSTOM_V2_STORY_AND_NOTES_CHAR_BUDGET
            }
            PcSheetVisualFamily.CLASSIC_DND_STYLE -> false
        }

        return basePages(family).filter { page ->
            when (page.role) {
                PcSheetBasePageRole.SPELL_LIST -> hasSpellContent
                PcSheetBasePageRole.NOTES -> notesText.isNotBlank() && !notesFitExistingNarrative
                else -> true
            }
        }
    }

    fun basePages(family: PcSheetVisualFamily): List<PcSheetTemplatePage> = when (family) {
        PcSheetVisualFamily.CLASSIC_DND_STYLE -> listOf(
            // Owner-approved Classic Run 2 uses three application-designed normal pages:
            // main; character/history/equipment; spell list.
            PcSheetTemplatePage(PcSheetBasePageRole.MAIN),
            PcSheetTemplatePage(PcSheetBasePageRole.EQUIPMENT_AND_NARRATIVE),
            PcSheetTemplatePage(PcSheetBasePageRole.SPELL_LIST),
        )
        PcSheetVisualFamily.CUSTOM_V1 -> listOf(
            PcSheetTemplatePage(PcSheetBasePageRole.MAIN, CUSTOM_V1_TEMPLATE_PATH, 1),
            PcSheetTemplatePage(PcSheetBasePageRole.EQUIPMENT, CUSTOM_V1_TEMPLATE_PATH, 2),
            PcSheetTemplatePage(PcSheetBasePageRole.NARRATIVE, CUSTOM_V1_TEMPLATE_PATH, 3),
            PcSheetTemplatePage(PcSheetBasePageRole.SPELL_LIST, CUSTOM_V1_TEMPLATE_PATH, 4),
            PcSheetTemplatePage(PcSheetBasePageRole.NOTES, CUSTOM_V1_TEMPLATE_PATH, 5),
        )
        PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE -> listOf(
            PcSheetTemplatePage(PcSheetBasePageRole.MAIN, CUSTOM_V2_TEMPLATE_PATH, 1),
            PcSheetTemplatePage(PcSheetBasePageRole.EQUIPMENT_AND_NARRATIVE, CUSTOM_V2_TEMPLATE_PATH, 3),
            PcSheetTemplatePage(PcSheetBasePageRole.SPELL_LIST, CUSTOM_V2_TEMPLATE_PATH, 4),
            PcSheetTemplatePage(PcSheetBasePageRole.NOTES, CUSTOM_V2_TEMPLATE_PATH, 5),
        )
        PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY -> listOf(
            PcSheetTemplatePage(PcSheetBasePageRole.MAIN, CUSTOM_V2_TEMPLATE_PATH, 2),
            PcSheetTemplatePage(PcSheetBasePageRole.EQUIPMENT_AND_NARRATIVE, CUSTOM_V2_TEMPLATE_PATH, 3),
            PcSheetTemplatePage(PcSheetBasePageRole.SPELL_LIST, CUSTOM_V2_TEMPLATE_PATH, 4),
            PcSheetTemplatePage(PcSheetBasePageRole.NOTES, CUSTOM_V2_TEMPLATE_PATH, 5),
        )
    }

    private const val CUSTOM_V1_INLINE_NOTES_CHAR_BUDGET = 720
    private const val CUSTOM_V2_STORY_AND_NOTES_CHAR_BUDGET = 760

    private fun customStatisticsProjection(
        aggregate: PcSheetExportAggregate,
    ): PcSheetCustomStatisticsProjection {
        val attributes = aggregate.successor.customAttributes
            .sortedWith(compareBy<CharacterCustomAttribute> { it.sortOrder }.thenBy { it.name.lowercase() })
            .map { attribute ->
                PcSheetCustomAttributeProjection(
                    attribute = attribute,
                    savingThrowTotal = aggregate.sheet.customSavingThrowTotal(attribute),
                )
            }
        val skills = aggregate.closure.customSkills
            .sortedWith(compareBy<CharacterCustomSkill> { it.sortOrder }.thenBy { it.name.lowercase() })
            .map { skill ->
                val ability = skill.abilityReference(aggregate.successor)
                PcSheetCustomSkillProjection(
                    skill = skill,
                    ability = ability,
                    total = aggregate.sheet.customSkillTotal(skill, aggregate.successor),
                )
            }
        return PcSheetCustomStatisticsProjection(attributes = attributes, skills = skills)
    }

    private fun spellbookPlan(
        aggregate: PcSheetExportAggregate,
    ): PcSheetSpellbookPlan {
        val sourcesById = aggregate.sheet.spellcastingSources.associateBy { it.id }
        val profilesBySourceId = aggregate.successor.spellcastingProfiles.associateBy { it.sourceId }

        val entries = aggregate.sheet.spells
            .sortedWith(compareBy<CharacterSpell> { it.level }.thenBy { it.name.lowercase() }.thenBy { it.id.toString() })
            .map { spell ->
                val associations = spell.sourceAssociations
                    .sortedWith(
                        compareBy<CharacterSpellSourceAssociation> {
                            sourcesById[it.sourceId]?.sortOrder ?: Int.MAX_VALUE
                        }.thenBy {
                            sourcesById[it.sourceId]?.name?.lowercase().orEmpty()
                        }.thenBy { it.sourceId.toString() },
                    )
                    .map { association ->
                        val source = sourcesById[association.sourceId]
                        val profile = profilesBySourceId[association.sourceId]
                        PcSheetSpellbookSourceProjection(
                            sourceId = association.sourceId,
                            sourceName = source?.name,
                            originKind = source?.originKind,
                            prepared = association.prepared,
                            castingAbility = profile?.ability ?: CharacterAbilityReference.NONE,
                            saveDc = profile?.let { aggregate.sheet.spellSaveDc(it, aggregate.successor) },
                            spellAttackModifier = profile?.let {
                                aggregate.sheet.spellAttackModifier(it, aggregate.successor)
                            },
                        )
                    }
                PcSheetSpellbookEntry(spell = spell, sources = associations)
            }
        return PcSheetSpellbookPlan(entries = entries)
    }

    private fun overflowRoutes(): List<PcSheetOverflowRoute> = listOf(
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.CUSTOM_STATISTICS,
            extensionKind = PcSheetExtendedPageKind.CUSTOM_STATISTICS,
        ),
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.TRAITS_AND_FEATURES,
            extensionKind = PcSheetExtendedPageKind.TRAITS_AND_FEATURES,
        ),
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.COMBAT_AND_ACTIONS,
            extensionKind = PcSheetExtendedPageKind.COMBAT_AND_ACTIONS,
        ),
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.RESOURCES_AND_OPTIONS,
            extensionKind = PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS,
        ),
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.INVENTORY_AND_EQUIPMENT,
            extensionKind = PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT,
        ),
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.SPELLS,
            extensionKind = PcSheetExtendedPageKind.SPELLS,
        ),
        PcSheetOverflowRoute(
            section = PcSheetExportContentSection.NOTES,
            extensionKind = PcSheetExtendedPageKind.NOTES,
        ),
    )
}
