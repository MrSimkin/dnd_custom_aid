package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassCatalog
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassCatalogEntry

/** Small lookup adapter kept in the Android successor layer; the shared catalog remains data-only. */
internal fun CharacterClassCatalog.findClass(key: String?): CharacterClassCatalogEntry? =
    key?.let { catalogKey -> classes.firstOrNull { it.key == catalogKey } }
