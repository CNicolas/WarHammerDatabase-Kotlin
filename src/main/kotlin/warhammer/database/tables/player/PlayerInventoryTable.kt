package warhammer.database.tables.player

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.PlayersTable

object PlayerInventoryTable : IntIdTable() {
    val playerId = reference("inventory", PlayersTable).uniqueIndex()

    val encumbrance = integer("encumbrance")
    val maxEncumbrance = integer("maxEncumbrance")
}