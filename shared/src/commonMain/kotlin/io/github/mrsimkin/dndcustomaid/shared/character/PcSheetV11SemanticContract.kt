package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * V11 is a separate opt-in export product. Neither this request nor its A/B choice is added
 * to the four frozen [PcSheetVisualFamily] renderers.
 */
enum class PcSheetV11Variant { A, B }

data class PcSheetV11ExportRequest(
    val variant: PcSheetV11Variant,
    val stateSelection: PcSheetExportStateSelection,
    val portraitFitMode: PcSheetPortraitFitMode = PcSheetPortraitFitMode.CROP_TO_FILL,
    val includeSpellDescriptions: Boolean = false,
)

/** A built-in ability has no synthetic UUID; a custom ability retains its persisted ID. */
data class PcSheetV11Attribute(
    val reference: CharacterAbilityReference,
    val name: String,
    val abbreviation: String,
    val score: Int,
    val modifier: Int,
    val savingThrowTotal: Int?,
)

data class PcSheetV11Skill(
    val builtInKey: SkillKey?,
    val customSkillId: Uuid?,
    val name: String,
    val ability: CharacterAbilityReference,
    /** Null means the configured reference cannot be resolved; never print a fabricated value. */
    val total: Int?,
    val training: SkillTraining,
)

data class PcSheetV11Equipment(
    val sourceId: Uuid,
    val name: String,
    val quantity: Int,
    val location: String?,
    val description: String?,
    val notes: String?,
    val special: Boolean,
    /**
     * Paper ACTIVO is not equipped, attuned, carried/stored, or an active temporary effect.
     * No source field establishes ACTIVO today; null means unverified/leave the paper mark blank.
     */
    val active: Boolean? = null,
)

data class PcSheetV11DigitalNote(
    /** General notes have no separate source ID. Titled cards do. */
    val sourceId: Uuid?,
    val title: String?,
    val content: String,
)

/**
 * G1 semantic input to a future shared V11 compositor; not a PDF layout or a product export UI.
 * [source] remains available losslessly so later G1 fields do not need to reconstruct domain data
 * from display labels. All derived lists are projections and never persist to the repository.
 */
data class PcSheetV11SemanticData(
    val request: PcSheetV11ExportRequest,
    val selectedState: PcSheetExportStateSelection,
    val notices: List<PcSheetExportNotice>,
    val source: PcSheetExportAggregate,
    val portrait: PcSheetPortraitPlan,
    val attributes: List<PcSheetV11Attribute>,
    val skills: List<PcSheetV11Skill>,
    val combatEntries: List<CharacterCombatEntry>,
    val traits: List<CharacterTrait>,
    val ordinaryEquipment: List<PcSheetV11Equipment>,
    val specialEquipment: List<PcSheetV11Equipment>,
    val currencies: List<CharacterCurrency>,
    val background: CharacterBackground,
    val digitalNotes: List<PcSheetV11DigitalNote>,
    val spellSlots: List<CharacterSpellSlot>,
    val spellcastingSources: List<CharacterSpellcastingSource>,
    val spells: List<PcSheetSpellbookEntry>,
    /** Optional appendix: null unless explicitly requested and there are attached spells. */
    val spellbookEntries: List<PcSheetSpellbookEntry>?,
) {
    val characterId: Uuid get() = source.sheet.id
    val campaignId: Uuid get() = source.sheet.campaignId

    /** P1 reserves the spellcasting area regardless; P4 needs at least one real spell. */
    val hasQuickSpellList: Boolean get() = spells.isNotEmpty()
}

/**
 * G1-only bridge to the already-proven shared source selection, read-only text projection,
 * notices and portrait semantics. The existing Classic plan is discarded; its family is NEVER
 * returned as V11 nor used as the V11 visual/layout plan.
 *
 * G3 will attach the independent V11 UI/renderer to this contract, not to a legacy renderer.
 */
object PcSheetV11SemanticMapper {
    fun map(request: PcSheetV11ExportRequest, sources: PcSheetExportSources): PcSheetV11SemanticData {
        val sharedPlan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = request.stateSelection,
                portraitFitMode = request.portraitFitMode,
                includeSpellDescriptions = request.includeSpellDescriptions,
            ),
            sources = sources,
        )
        val snapshot = sharedPlan.snapshot
        val aggregate = snapshot.aggregate
        val sheet = aggregate.sheet

        val builtInAttributes = CharacterAbility.entries.map { ability ->
            PcSheetV11Attribute(
                reference = CharacterAbilityReference.builtIn(ability),
                name = ability.name,
                abbreviation = ability.name.take(3),
                score = sheet.abilityScore(ability),
                modifier = sheet.abilityModifier(ability),
                savingThrowTotal = sheet.savingThrowTotal(ability),
            )
        }
        val customAttributes = aggregate.successor.customAttributes
            .sortedWith(compareBy<CharacterCustomAttribute> { it.sortOrder }.thenBy { it.id.toString() })
            .map { attribute ->
                PcSheetV11Attribute(
                    reference = CharacterAbilityReference.custom(attribute.id),
                    name = attribute.name,
                    abbreviation = attribute.abbreviation,
                    score = attribute.score,
                    modifier = attribute.modifier,
                    savingThrowTotal = sheet.customSavingThrowTotal(attribute),
                )
            }
        val builtInSkills = SkillKey.entries.map { key ->
            PcSheetV11Skill(
                builtInKey = key,
                customSkillId = null,
                name = key.name,
                ability = CharacterAbilityReference.builtIn(key.ability),
                total = sheet.skillTotal(key),
                training = sheet.skill(key).training,
            )
        }
        val customSkills = aggregate.closure.customSkills
            .sortedWith(compareBy<CharacterCustomSkill> { it.sortOrder }.thenBy { it.id.toString() })
            .map { skill ->
                PcSheetV11Skill(
                    builtInKey = null,
                    customSkillId = skill.id,
                    name = skill.name,
                    ability = skill.abilityReference(aggregate.successor),
                    total = sheet.customSkillTotal(skill, aggregate.successor),
                    training = skill.training,
                )
            }
        val equipment = sheet.inventoryItems
            .sortedWith(compareBy<CharacterInventoryItem> { it.sortOrder }.thenBy { it.id.toString() })
            .map { item ->
                PcSheetV11Equipment(
                    sourceId = item.id,
                    name = item.name,
                    quantity = item.quantity,
                    location = item.location,
                    description = item.description,
                    notes = item.notes,
                    special = item.special,
                    active = null,
                )
            }
        val digitalNotes = buildList {
            if (sheet.generalNotes.isNotBlank()) {
                add(PcSheetV11DigitalNote(sourceId = null, title = null, content = sheet.generalNotes))
            }
            sheet.noteCards.sortedWith(compareBy<CharacterNote> { it.sortOrder }.thenBy { it.id.toString() })
                .filter { it.title.isNotBlank() || it.content.isNotBlank() }
                .forEach { note ->
                    add(PcSheetV11DigitalNote(note.id, note.title, note.content))
                }
        }
        val spells = if (sheet.spells.isEmpty()) {
            emptyList()
        } else {
            // The shared source/association implementation preserves prepared per source ID.
            PcSheetPdfExportPlanner.spellbookPlan(aggregate).entries
        }
        return PcSheetV11SemanticData(
            request = request,
            selectedState = snapshot.selectedState,
            notices = sharedPlan.notices,
            source = aggregate,
            portrait = snapshot.portrait,
            attributes = builtInAttributes + customAttributes,
            skills = builtInSkills + customSkills,
            combatEntries = sheet.combatEntries.sortedWith(
                compareBy<CharacterCombatEntry> { it.sortOrder }.thenBy { it.id.toString() },
            ),
            traits = sheet.traits.sortedWith(
                compareBy<CharacterTrait> { it.sortOrder }.thenBy { it.id.toString() },
            ),
            ordinaryEquipment = equipment.filterNot { it.special },
            specialEquipment = equipment.filter { it.special },
            currencies = sheet.currencies.sortedWith(
                compareBy<CharacterCurrency> { it.sortOrder }.thenBy { it.key },
            ),
            background = sheet.background,
            digitalNotes = digitalNotes,
            spellSlots = sheet.spellSlots.sortedBy { it.level },
            spellcastingSources = sheet.spellcastingSources.sortedWith(
                compareBy<CharacterSpellcastingSource> { it.sortOrder }.thenBy { it.id.toString() },
            ),
            spells = spells,
            spellbookEntries = if (request.includeSpellDescriptions && spells.isNotEmpty()) spells else null,
        )
    }
}
