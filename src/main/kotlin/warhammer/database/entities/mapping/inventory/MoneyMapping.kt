package warhammer.database.entities.mapping.inventory

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.inventory.Money
import warhammer.database.tables.player.PlayerInventoryTable
import warhammer.database.tables.player.inventory.MoneyTable

internal fun ResultRow?.mapToMoney(): Money? = when (this) {
    null -> null
    else -> {
        Money(
                id = this[MoneyTable.id].value,
                inventoryId = this[MoneyTable.inventoryId].value,
                brass = this[MoneyTable.brass],
                silver = this[MoneyTable.silver],
                gold = this[MoneyTable.gold]
        )
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Money) {
    this[MoneyTable.inventoryId] = EntityID(entity.inventoryId, PlayerInventoryTable)

    this[MoneyTable.brass] = entity.brass
    this[MoneyTable.silver] = entity.silver
    this[MoneyTable.gold] = entity.gold
}