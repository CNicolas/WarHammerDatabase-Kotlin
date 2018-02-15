package warhammer.database.tables.player.inventory

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.player.PlayerInventoryTable

object ItemsTable : IntIdTable() {
    val inventoryId = reference("inventory", PlayerInventoryTable)

    val name = varchar("name", length = 70)
    val description = varchar("description", length = 300).nullable()
    val encumbrance = integer("encumbrance")
    val quantity = integer("quantity")
    val quality = varchar("quality", 20)
    val type = varchar("type", length = 20)

    val uses = integer("uses").nullable()

    val isEquipped = bool("isEquipped").nullable()

    val soak = integer("soak").nullable()
    val defense = integer("defense").nullable()

    val damage = integer("damage").nullable()
    val criticalLevel = integer("criticalLevel").nullable()
    val range = varchar("range", length = 20)

    init {
        uniqueIndex(name, inventoryId)
    }
}