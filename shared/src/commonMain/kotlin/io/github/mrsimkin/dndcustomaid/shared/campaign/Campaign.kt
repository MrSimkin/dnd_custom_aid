package io.github.mrsimkin.dndcustomaid.shared.campaign

import kotlin.uuid.Uuid

data class Campaign(
    val id: Uuid,
    val name: String,
)
