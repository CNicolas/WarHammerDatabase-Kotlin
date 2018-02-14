package warhammer.database.tables.player.inventory

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.player.PlayerInventoryTable

object MoneyTable : IntIdTable() {
    val inventoryId = reference("items", PlayerInventoryTable).uniqueIndex()

    val brass = integer("brass")
    val silver = integer("silver")
    val gold = integer("gold")
}