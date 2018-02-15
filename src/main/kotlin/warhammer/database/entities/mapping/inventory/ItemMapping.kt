package warhammer.database.entities.mapping.inventory

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.inventory.*
import warhammer.database.entities.player.inventory.ItemType.*
import warhammer.database.tables.player.PlayerInventoryTable
import warhammer.database.tables.player.inventory.ItemsTable

internal fun ResultRow?.mapToItem(): Item? = when (this) {
    null -> null
    else -> {
        val type = valueOf(this[ItemsTable.type])

        when (type) {
            ITEM -> GenericItem(
                    id = this[ItemsTable.id].value,
                    inventoryId = this[ItemsTable.inventoryId].value,
                    name = this[ItemsTable.name],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality])
            )
            EXPANDABLE -> Expandable(
                    id = this[ItemsTable.id].value,
                    inventoryId = this[ItemsTable.inventoryId].value,
                    name = this[ItemsTable.name],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality]),
                    uses = this[ItemsTable.uses]!!
            )
            ARMOR -> Armor(
                    id = this[ItemsTable.id].value,
                    inventoryId = this[ItemsTable.inventoryId].value,
                    name = this[ItemsTable.name],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality]),
                    isEquipped = this[ItemsTable.isEquipped]!!,
                    soak = this[ItemsTable.soak],
                    defense = this[ItemsTable.defense]
            )
            WEAPON -> Weapon(
                    id = this[ItemsTable.id].value,
                    inventoryId = this[ItemsTable.inventoryId].value,
                    name = this[ItemsTable.name],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality]),
                    isEquipped = this[ItemsTable.isEquipped]!!,
                    damage = this[ItemsTable.damage]!!,
                    criticalLevel = this[ItemsTable.criticalLevel]!!,
                    range = Range.valueOf(this[ItemsTable.range])
            )
        }
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Item) {
    this[ItemsTable.inventoryId] = EntityID(entity.inventoryId, PlayerInventoryTable)

    this[ItemsTable.name] = entity.name
    this[ItemsTable.encumbrance] = entity.encumbrance
    this[ItemsTable.quantity] = entity.quantity
    this[ItemsTable.quality] = entity.quality.toString()
    this[ItemsTable.type] = entity.type.toString()

    this[ItemsTable.uses] = entity.uses

    this[ItemsTable.isEquipped] = entity.isEquipped

    this[ItemsTable.soak] = entity.soak
    this[ItemsTable.defense] = entity.defense

    this[ItemsTable.damage] = entity.damage
    this[ItemsTable.criticalLevel] = entity.criticalLevel
    this[ItemsTable.range] = entity.range.toString()
}