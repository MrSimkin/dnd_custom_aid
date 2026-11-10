package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * C443 (owner 2026-11-06, «Sí, a ATAQUES»; D-0076 3.8): the ATAQUES table summarises attacks,
 * weapons and spells. An entry with an attack bonus belongs to it whatever its action type;
 * an entry with no bonus and a type other than ATTACK stays in ACCIONES.
 */
object PcSheetV11AttackEntries {
    fun isAttackRow(entry: CharacterCombatEntry): Boolean =
        entry.type == CharacterCombatEntryType.ATTACK || entry.attackModifier != null
}
