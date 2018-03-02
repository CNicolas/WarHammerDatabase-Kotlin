package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object ItemsTable : IntIdTable() {
    val playerId = integer("playerId")

    val name = varchar("name", length = 70)
    val description = varchar("description", length = 300).nullable()
    val encumbrance = integer("encumbrance")
    val quantity = integer("quantity")
    val quality = varchar("quality", 20)
    val type = varchar("type", length = 20)
    val subType = varchar("subType", length = 30)

    val uses = integer("uses").nullable()

    val isEquipped = bool("isEquipped").nullable()

    val soak = integer("soak").nullable()
    val defense = integer("defense").nullable()

    val damage = integer("damage").nullable()
    val criticalLevel = integer("criticalLevel").nullable()
    val range = varchar("range", length = 20)

    init {
        uniqueIndex(name, playerId)
    }
}