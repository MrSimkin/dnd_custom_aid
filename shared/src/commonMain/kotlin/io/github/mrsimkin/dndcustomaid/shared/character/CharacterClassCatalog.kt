package io.github.mrsimkin.dndcustomaid.shared.character

enum class CharacterModuleKind {
    ARTIFICER,
    FORMS,
    TECHNIQUES,
    METAMAGIC,
    PACTS,
    COMPANIONS,
}

data class CharacterSubclassCatalogEntry(
    val key: String,
    val name: String,
    val source: String,
    val rulesFamily: CharacterRulesFamily,
    val modules: Set<CharacterModuleKind> = emptySet(),
    val availabilityNote: String? = null,
)

data class CharacterClassCatalogEntry(
    val key: String,
    val nameEs: String,
    val source: String,
    val rulesFamily: CharacterRulesFamily,
    val modules: Set<CharacterModuleKind> = emptySet(),
    val subclasses: List<CharacterSubclassCatalogEntry> = emptyList(),
)

/**
 * Convenience metadata only. This is deliberately not a legality engine or a rules corpus.
 * Manual/custom class and subclass names remain valid everywhere in the character model.
 */
object CharacterClassCatalog {
    const val CUSTOM_KEY = "custom"

    val classes: List<CharacterClassCatalogEntry> = listOf(
        CharacterClassCatalogEntry(
            key = "artificer-2025",
            nameEs = "Artífice",
            source = "Eberron: Forge of the Artificer",
            rulesFamily = CharacterRulesFamily.DND_5_5E,
            modules = setOf(CharacterModuleKind.ARTIFICER),
            subclasses = listOf(
                sc("artificer-alchemist-2025", "Alchemist", "Eberron: Forge of the Artificer", modules = setOf(CharacterModuleKind.ARTIFICER)),
                sc("artificer-armorer-2025", "Armorer", "Eberron: Forge of the Artificer", modules = setOf(CharacterModuleKind.ARTIFICER)),
                sc("artificer-artillerist-2025", "Artillerist", "Eberron: Forge of the Artificer", modules = setOf(CharacterModuleKind.ARTIFICER, CharacterModuleKind.COMPANIONS)),
                sc("artificer-battle-smith-2025", "Battle Smith", "Eberron: Forge of the Artificer", modules = setOf(CharacterModuleKind.ARTIFICER, CharacterModuleKind.COMPANIONS)),
                sc("artificer-cartographer-2025", "Cartographer", "Eberron: Forge of the Artificer", modules = setOf(CharacterModuleKind.ARTIFICER)),
                sc("artificer-reanimator-2026", "Reanimator", "Ravenloft: The Horrors Within", modules = setOf(CharacterModuleKind.ARTIFICER, CharacterModuleKind.COMPANIONS)),
            ),
        ),
        CharacterClassCatalogEntry(
            key = "artificer-5e",
            nameEs = "Artífice",
            source = "Tasha's Cauldron of Everything",
            rulesFamily = CharacterRulesFamily.DND_5E,
            modules = setOf(CharacterModuleKind.ARTIFICER),
            subclasses = listOf(
                sc5e("artificer-alchemist-5e", "Alchemist", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.ARTIFICER)),
                sc5e("artificer-armorer-5e", "Armorer", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.ARTIFICER)),
                sc5e("artificer-artillerist-5e", "Artillerist", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.ARTIFICER, CharacterModuleKind.COMPANIONS)),
                sc5e("artificer-battle-smith-5e", "Battle Smith", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.ARTIFICER, CharacterModuleKind.COMPANIONS)),
            ),
        ),
        core("barbarian", "Bárbaro", listOf(
            phb24("barbarian-berserker-2024", "Berserker"),
            phb24("barbarian-wild-heart-2024", "Wild Heart"),
            phb24("barbarian-world-tree-2024", "World Tree"),
            phb24("barbarian-zealot-2024", "Zealot"),
            sc5e("barbarian-berserker-2014", "Berserker", "Player's Handbook (2014)"),
            sc5e("barbarian-totem-warrior", "Totem Warrior", "Player's Handbook (2014)"),
            sc5e("barbarian-battlerager", "Battlerager", "Sword Coast Adventurer's Guide"),
            sc5e("barbarian-ancestral-guardian", "Ancestral Guardian", "Xanathar's Guide to Everything"),
            sc5e("barbarian-storm-herald", "Storm Herald", "Xanathar's Guide to Everything"),
            sc5e("barbarian-zealot-5e", "Zealot", "Xanathar's Guide to Everything"),
            sc5e("barbarian-beast", "Beast", "Tasha's Cauldron of Everything"),
            sc5e("barbarian-wild-magic", "Wild Magic", "Tasha's Cauldron of Everything"),
            sc5e("barbarian-giant", "Giant", "Bigby Presents: Glory of the Giants"),
        )),
        core("bard", "Bardo", listOf(
            phb24("bard-dance-2024", "Dance"), phb24("bard-glamour-2024", "Glamour"),
            phb24("bard-lore-2024", "Lore"), phb24("bard-valor-2024", "Valor"),
            sc5e("bard-lore-2014", "Lore", "Player's Handbook (2014)"), sc5e("bard-valor-2014", "Valor", "Player's Handbook (2014)"),
            sc5e("bard-glamour-5e", "Glamour", "Xanathar's Guide to Everything"), sc5e("bard-swords", "Swords", "Xanathar's Guide to Everything"),
            sc5e("bard-whispers", "Whispers", "Xanathar's Guide to Everything"), sc5e("bard-creation", "Creation", "Tasha's Cauldron of Everything"),
            sc5e("bard-eloquence", "Eloquence", "Tasha's Cauldron of Everything"), sc5e("bard-spirits-5e", "Spirits", "Van Richten's Guide to Ravenloft"),
            sc("bard-moon-2025", "Moon", "Forgotten Realms: Heroes of Faerûn"), sc("bard-spirits-2026", "Spirits", "Ravenloft: The Horrors Within"),
        )),
        core("cleric", "Clérigo", listOf(
            phb24("cleric-life-2024", "Life"), phb24("cleric-light-2024", "Light"), phb24("cleric-trickery-2024", "Trickery"), phb24("cleric-war-2024", "War"),
            sc5e("cleric-knowledge", "Knowledge", "Player's Handbook (2014)"), sc5e("cleric-life-2014", "Life", "Player's Handbook (2014)"),
            sc5e("cleric-light-2014", "Light", "Player's Handbook (2014)"), sc5e("cleric-nature", "Nature", "Player's Handbook (2014)"),
            sc5e("cleric-tempest", "Tempest", "Player's Handbook (2014)"), sc5e("cleric-trickery-2014", "Trickery", "Player's Handbook (2014)"),
            sc5e("cleric-war-2014", "War", "Player's Handbook (2014)"), sc5e("cleric-death", "Death", "Dungeon Master's Guide (2014)"),
            sc5e("cleric-arcana", "Arcana", "Sword Coast Adventurer's Guide"), sc5e("cleric-forge", "Forge", "Xanathar's Guide to Everything"),
            sc5e("cleric-grave-5e", "Grave", "Xanathar's Guide to Everything"), sc5e("cleric-order", "Order", "Guildmasters' Guide to Ravnica"),
            sc5e("cleric-peace", "Peace", "Tasha's Cauldron of Everything"), sc5e("cleric-twilight", "Twilight", "Tasha's Cauldron of Everything"),
            sc("cleric-knowledge-2025", "Knowledge", "Forgotten Realms: Heroes of Faerûn"), sc("cleric-grave-2026", "Grave", "Ravenloft: The Horrors Within"),
            current("cleric-arcana-2026", "Arcana", "Arcana Unleashed"),
        )),
        core("druid", "Druida", listOf(
            phb24("druid-land-2024", "Land", setOf(CharacterModuleKind.FORMS)), phb24("druid-moon-2024", "Moon", setOf(CharacterModuleKind.FORMS)),
            phb24("druid-sea-2024", "Sea", setOf(CharacterModuleKind.FORMS)), phb24("druid-stars-2024", "Stars", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-land-2014", "Land", "Player's Handbook (2014)", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-moon-2014", "Moon", "Player's Handbook (2014)", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-dreams", "Dreams", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-shepherd", "Shepherd", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-spores", "Spores", "Guildmasters' Guide to Ravnica", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-stars-5e", "Stars", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.FORMS)),
            sc5e("druid-wildfire", "Wildfire", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.FORMS, CharacterModuleKind.COMPANIONS)),
        ), modules = setOf(CharacterModuleKind.FORMS)),
        core("fighter", "Guerrero", listOf(
            phb24("fighter-battle-master-2024", "Battle Master", setOf(CharacterModuleKind.TECHNIQUES)), phb24("fighter-champion-2024", "Champion"),
            phb24("fighter-eldritch-knight-2024", "Eldritch Knight"), phb24("fighter-psi-warrior-2024", "Psi Warrior"),
            sc5e("fighter-battle-master-5e", "Battle Master", "Player's Handbook (2014)", setOf(CharacterModuleKind.TECHNIQUES)),
            sc5e("fighter-champion-5e", "Champion", "Player's Handbook (2014)"), sc5e("fighter-eldritch-knight-5e", "Eldritch Knight", "Player's Handbook (2014)"),
            sc5e("fighter-arcane-archer-5e", "Arcane Archer", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.TECHNIQUES)),
            sc5e("fighter-cavalier", "Cavalier", "Xanathar's Guide to Everything"), sc5e("fighter-samurai", "Samurai", "Xanathar's Guide to Everything"),
            sc5e("fighter-rune-knight", "Rune Knight", "Tasha's Cauldron of Everything"), sc5e("fighter-echo-knight", "Echo Knight", "Explorer's Guide to Wildemount"),
            sc5e("fighter-purple-dragon-knight", "Purple Dragon Knight", "Sword Coast Adventurer's Guide"),
            sc("fighter-banneret-2025", "Banneret", "Forgotten Realms: Heroes of Faerûn"), current("fighter-arcane-archer-2026", "Arcane Archer", "Arcana Unleashed", setOf(CharacterModuleKind.TECHNIQUES)),
        )),
        core("monk", "Monje", listOf(
            phb24("monk-mercy-2024", "Mercy"), phb24("monk-shadow-2024", "Shadow"), phb24("monk-elements-2024", "Elements"), phb24("monk-open-hand-2024", "Open Hand"),
            sc5e("monk-open-hand-5e", "Open Hand", "Player's Handbook (2014)"), sc5e("monk-four-elements", "Four Elements", "Player's Handbook (2014)"),
            sc5e("monk-shadow-5e", "Shadow", "Player's Handbook (2014)"), sc5e("monk-long-death", "Long Death", "Sword Coast Adventurer's Guide"),
            sc5e("monk-sun-soul", "Sun Soul", "Sword Coast Adventurer's Guide"), sc5e("monk-drunken-master", "Drunken Master", "Xanathar's Guide to Everything"),
            sc5e("monk-kensei", "Kensei", "Xanathar's Guide to Everything"), sc5e("monk-astral-self", "Astral Self", "Tasha's Cauldron of Everything"),
            sc5e("monk-mercy-5e", "Mercy", "Tasha's Cauldron of Everything"), sc5e("monk-ascendant-dragon", "Ascendant Dragon", "Fizban's Treasury of Dragons"),
            current("monk-mystic-arts-2026", "Warrior of the Mystic Arts", "Arcana Unleashed", setOf(CharacterModuleKind.TECHNIQUES)),
        )),
        core("paladin", "Paladín", listOf(
            phb24("paladin-devotion-2024", "Devotion"), phb24("paladin-glory-2024", "Glory"), phb24("paladin-ancients-2024", "Ancients"), phb24("paladin-vengeance-2024", "Vengeance"),
            sc5e("paladin-devotion-5e", "Devotion", "Player's Handbook (2014)"), sc5e("paladin-ancients-5e", "Ancients", "Player's Handbook (2014)"),
            sc5e("paladin-vengeance-5e", "Vengeance", "Player's Handbook (2014)"), sc5e("paladin-oathbreaker", "Oathbreaker", "Dungeon Master's Guide (2014)"),
            sc5e("paladin-crown", "Crown", "Sword Coast Adventurer's Guide"), sc5e("paladin-conquest", "Conquest", "Xanathar's Guide to Everything"),
            sc5e("paladin-redemption", "Redemption", "Xanathar's Guide to Everything"), sc5e("paladin-glory-5e", "Glory", "Mythic Odysseys of Theros"),
            sc5e("paladin-watchers", "Watchers", "Tasha's Cauldron of Everything"), sc("paladin-noble-genies", "Noble Genies", "Forgotten Realms: Heroes of Faerûn"),
        )),
        core("ranger", "Explorador", listOf(
            phb24("ranger-beast-master-2024", "Beast Master", setOf(CharacterModuleKind.COMPANIONS)), phb24("ranger-fey-wanderer-2024", "Fey Wanderer"),
            phb24("ranger-gloom-stalker-2024", "Gloom Stalker"), phb24("ranger-hunter-2024", "Hunter"),
            sc5e("ranger-beast-master-5e", "Beast Master", "Player's Handbook (2014)", setOf(CharacterModuleKind.COMPANIONS)), sc5e("ranger-hunter-5e", "Hunter", "Player's Handbook (2014)"),
            sc5e("ranger-gloom-stalker-5e", "Gloom Stalker", "Xanathar's Guide to Everything"), sc5e("ranger-horizon-walker", "Horizon Walker", "Xanathar's Guide to Everything"),
            sc5e("ranger-monster-slayer", "Monster Slayer", "Xanathar's Guide to Everything"), sc5e("ranger-fey-wanderer-5e", "Fey Wanderer", "Tasha's Cauldron of Everything"),
            sc5e("ranger-swarmkeeper", "Swarmkeeper", "Tasha's Cauldron of Everything"), sc5e("ranger-drakewarden", "Drakewarden", "Fizban's Treasury of Dragons", setOf(CharacterModuleKind.COMPANIONS)),
            sc("ranger-winter-walker", "Winter Walker", "Forgotten Realms: Heroes of Faerûn"), sc("ranger-hollow-warden", "Hollow Warden", "Ravenloft: The Horrors Within"),
        )),
        core("rogue", "Pícaro", listOf(
            phb24("rogue-arcane-trickster-2024", "Arcane Trickster"), phb24("rogue-assassin-2024", "Assassin"), phb24("rogue-soulknife-2024", "Soulknife"), phb24("rogue-thief-2024", "Thief"),
            sc5e("rogue-arcane-trickster-5e", "Arcane Trickster", "Player's Handbook (2014)"), sc5e("rogue-assassin-5e", "Assassin", "Player's Handbook (2014)"),
            sc5e("rogue-thief-5e", "Thief", "Player's Handbook (2014)"), sc5e("rogue-mastermind", "Mastermind", "Sword Coast Adventurer's Guide"),
            sc5e("rogue-swashbuckler", "Swashbuckler", "Sword Coast Adventurer's Guide"), sc5e("rogue-inquisitive", "Inquisitive", "Xanathar's Guide to Everything"),
            sc5e("rogue-scout", "Scout", "Xanathar's Guide to Everything"), sc5e("rogue-phantom-5e", "Phantom", "Tasha's Cauldron of Everything"),
            sc5e("rogue-soulknife-5e", "Soulknife", "Tasha's Cauldron of Everything"), sc("rogue-scion-three", "Scion of the Three", "Forgotten Realms: Heroes of Faerûn"),
            sc("rogue-phantom-2026", "Phantom", "Ravenloft: The Horrors Within"),
        )),
        core("sorcerer", "Hechicero", listOf(
            phb24("sorcerer-aberrant-2024", "Aberrant Sorcery", setOf(CharacterModuleKind.METAMAGIC)), phb24("sorcerer-clockwork-2024", "Clockwork Sorcery", setOf(CharacterModuleKind.METAMAGIC)),
            phb24("sorcerer-draconic-2024", "Draconic Sorcery", setOf(CharacterModuleKind.METAMAGIC)), phb24("sorcerer-wild-magic-2024", "Wild Magic Sorcery", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-draconic-5e", "Draconic Bloodline", "Player's Handbook (2014)", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-wild-magic-5e", "Wild Magic", "Player's Handbook (2014)", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-storm", "Storm Sorcery", "Sword Coast Adventurer's Guide", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-divine-soul", "Divine Soul", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-shadow-5e", "Shadow Magic", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-aberrant-mind-5e", "Aberrant Mind", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-clockwork-soul-5e", "Clockwork Soul", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.METAMAGIC)),
            sc5e("sorcerer-lunar", "Lunar Sorcery", "Dragonlance: Shadow of the Dragon Queen", setOf(CharacterModuleKind.METAMAGIC)),
            sc("sorcerer-spellfire", "Spellfire Sorcery", "Forgotten Realms: Heroes of Faerûn", modules = setOf(CharacterModuleKind.METAMAGIC)),
            sc("sorcerer-shadow-2026", "Shadow Sorcery", "Ravenloft: The Horrors Within", modules = setOf(CharacterModuleKind.METAMAGIC)),
        ), modules = setOf(CharacterModuleKind.METAMAGIC)),
        core("warlock", "Brujo", listOf(
            phb24("warlock-archfey-2024", "Archfey", setOf(CharacterModuleKind.PACTS)), phb24("warlock-celestial-2024", "Celestial", setOf(CharacterModuleKind.PACTS)),
            phb24("warlock-fiend-2024", "Fiend", setOf(CharacterModuleKind.PACTS)), phb24("warlock-great-old-one-2024", "Great Old One", setOf(CharacterModuleKind.PACTS)),
            sc5e("warlock-archfey-5e", "Archfey", "Player's Handbook (2014)", setOf(CharacterModuleKind.PACTS)), sc5e("warlock-fiend-5e", "Fiend", "Player's Handbook (2014)", setOf(CharacterModuleKind.PACTS)),
            sc5e("warlock-great-old-one-5e", "Great Old One", "Player's Handbook (2014)", setOf(CharacterModuleKind.PACTS)), sc5e("warlock-undying", "Undying", "Sword Coast Adventurer's Guide", setOf(CharacterModuleKind.PACTS)),
            sc5e("warlock-celestial-5e", "Celestial", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.PACTS)), sc5e("warlock-hexblade", "Hexblade", "Xanathar's Guide to Everything", setOf(CharacterModuleKind.PACTS)),
            sc5e("warlock-fathomless", "Fathomless", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.PACTS)), sc5e("warlock-genie", "Genie", "Tasha's Cauldron of Everything", setOf(CharacterModuleKind.PACTS)),
            sc5e("warlock-undead-5e", "Undead", "Van Richten's Guide to Ravenloft", setOf(CharacterModuleKind.PACTS)),
            sc("warlock-undead-2026", "Undead", "Ravenloft: The Horrors Within", modules = setOf(CharacterModuleKind.PACTS)),
            current("warlock-vestige-2026", "Vestige Patron", "Arcana Unleashed", setOf(CharacterModuleKind.PACTS, CharacterModuleKind.COMPANIONS)),
        ), modules = setOf(CharacterModuleKind.PACTS)),
        core("wizard", "Mago", listOf(
            phb24("wizard-abjurer-2024", "Abjurer"), phb24("wizard-diviner-2024", "Diviner"), phb24("wizard-evoker-2024", "Evoker"), phb24("wizard-illusionist-2024", "Illusionist"),
            sc5e("wizard-abjuration-5e", "Abjuration", "Player's Handbook (2014)"), sc5e("wizard-conjuration-5e", "Conjuration", "Player's Handbook (2014)"),
            sc5e("wizard-divination-5e", "Divination", "Player's Handbook (2014)"), sc5e("wizard-enchantment-5e", "Enchantment", "Player's Handbook (2014)"),
            sc5e("wizard-evocation-5e", "Evocation", "Player's Handbook (2014)"), sc5e("wizard-illusion-5e", "Illusion", "Player's Handbook (2014)"),
            sc5e("wizard-necromancy-5e", "Necromancy", "Player's Handbook (2014)"), sc5e("wizard-transmutation-5e", "Transmutation", "Player's Handbook (2014)"),
            sc5e("wizard-bladesinging-5e", "Bladesinging", "Sword Coast Adventurer's Guide"), sc5e("wizard-war-magic", "War Magic", "Xanathar's Guide to Everything"),
            sc5e("wizard-scribes", "Order of Scribes", "Tasha's Cauldron of Everything"), sc5e("wizard-chronurgy", "Chronurgy", "Explorer's Guide to Wildemount"),
            sc5e("wizard-graviturgy", "Graviturgy", "Explorer's Guide to Wildemount"), sc("wizard-bladesinger-2025", "Bladesinger", "Forgotten Realms: Heroes of Faerûn"),
            current("wizard-conjurer-2026", "Conjurer", "Arcana Unleashed"), current("wizard-enchanter-2026", "Enchanter", "Arcana Unleashed"),
            current("wizard-necromancer-2026", "Necromancer", "Arcana Unleashed", setOf(CharacterModuleKind.COMPANIONS)), current("wizard-transmuter-2026", "Transmuter", "Arcana Unleashed"),
        )),
    )

    fun byKey(key: String?): CharacterClassCatalogEntry? = classes.firstOrNull { it.key == key }

    fun subclassByKey(key: String?): CharacterSubclassCatalogEntry? =
        classes.asSequence().flatMap { it.subclasses.asSequence() }.firstOrNull { it.key == key }

    fun modulesFor(classLevel: CharacterClassLevel): Set<CharacterModuleKind> {
        val classModules = byKey(classLevel.catalogKey)?.modules.orEmpty()
        val subclassModules = subclassByKey(classLevel.subclassCatalogKey)?.modules.orEmpty()
        return classModules + subclassModules
    }

    fun modulesFor(classes: List<CharacterClassLevel>): Set<CharacterModuleKind> =
        classes.flatMapTo(mutableSetOf()) { modulesFor(it) }

    private fun core(
        key: String,
        nameEs: String,
        subclasses: List<CharacterSubclassCatalogEntry>,
        modules: Set<CharacterModuleKind> = emptySet(),
    ) = CharacterClassCatalogEntry(
        key = "$key-2024",
        nameEs = nameEs,
        source = "Player's Handbook (2024)",
        rulesFamily = CharacterRulesFamily.DND_5_5E,
        modules = modules,
        subclasses = subclasses,
    )

    private fun phb24(
        key: String,
        name: String,
        modules: Set<CharacterModuleKind> = emptySet(),
    ) = CharacterSubclassCatalogEntry(key, name, "Player's Handbook (2024)", CharacterRulesFamily.DND_5_5E, modules)

    private fun sc(
        key: String,
        name: String,
        source: String,
        modules: Set<CharacterModuleKind> = emptySet(),
    ) = CharacterSubclassCatalogEntry(key, name, source, CharacterRulesFamily.DND_5_5E, modules)

    private fun sc5e(
        key: String,
        name: String,
        source: String,
        modules: Set<CharacterModuleKind> = emptySet(),
    ) = CharacterSubclassCatalogEntry(key, name, source, CharacterRulesFamily.DND_5E, modules)

    private fun current(
        key: String,
        name: String,
        source: String,
        modules: Set<CharacterModuleKind> = emptySet(),
    ) = CharacterSubclassCatalogEntry(
        key = key,
        name = name,
        source = source,
        rulesFamily = CharacterRulesFamily.DND_5_5E,
        modules = modules,
        availabilityNote = null,
    )
}
