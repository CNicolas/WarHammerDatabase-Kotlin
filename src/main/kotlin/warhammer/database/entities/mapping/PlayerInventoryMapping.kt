package warhammer.database.entities.mapping

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.PlayerInventory
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerInventoryTable

internal fun ResultRow?.mapToPlayerInventoryEntity(): PlayerInventory? = when (this) {
    null -> null
    else -> {
        PlayerInventory(
                id = this[PlayerInventoryTable.id].value,
                playerId = this[PlayerInventoryTable.playerId].value,
                encumbrance = this[PlayerInventoryTable.encumbrance],
                maxEncumbrance = this[PlayerInventoryTable.maxEncumbrance]
        )
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: PlayerInventory) {
    this[PlayerInventoryTable.playerId] = EntityID(entity.playerId, PlayersTable)

    this[PlayerInventoryTable.encumbrance] = entity.encumbrance
    this[PlayerInventoryTable.maxEncumbrance] = entity.maxEncumbrance
}