package warhammer.database.entities.player.playerLinked.item

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.item.enums.ArmorType
import warhammer.database.entities.player.playerLinked.item.enums.ItemType.*
import warhammer.database.entities.player.playerLinked.item.enums.Quality
import warhammer.database.entities.player.playerLinked.item.enums.Range
import warhammer.database.entities.player.playerLinked.item.enums.WeaponType
import warhammer.database.tables.ItemsTable

internal fun ResultRow?.mapToItem(): Item? = when (this) {
    null -> null
    else -> {
        val type = valueOf(this[ItemsTable.type])

        when (type) {
            GENERIC_ITEM -> GenericItem(
                    id = this[ItemsTable.id].value,
                    name = this[ItemsTable.name],
                    description = this[ItemsTable.description],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality])
            )
            EXPANDABLE -> Expandable(
                    id = this[ItemsTable.id].value,
                    name = this[ItemsTable.name],
                    description = this[ItemsTable.description],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality]),
                    uses = this[ItemsTable.uses]!!
            )
            ARMOR -> Armor(
                    id = this[ItemsTable.id].value,
                    name = this[ItemsTable.name],
                    description = this[ItemsTable.description],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality]),
                    subType = ArmorType.valueOf(this[ItemsTable.subType]),
                    isEquipped = this[ItemsTable.isEquipped]!!,
                    soak = this[ItemsTable.soak],
                    defense = this[ItemsTable.defense]
            )
            WEAPON -> Weapon(
                    id = this[ItemsTable.id].value,
                    name = this[ItemsTable.name],
                    description = this[ItemsTable.description],
                    encumbrance = this[ItemsTable.encumbrance],
                    quantity = this[ItemsTable.quantity],
                    quality = Quality.valueOf(this[ItemsTable.quality]),
                    subType = WeaponType.valueOf(this[ItemsTable.subType]),
                    isEquipped = this[ItemsTable.isEquipped]!!,
                    damage = this[ItemsTable.damage]!!,
                    criticalLevel = this[ItemsTable.criticalLevel]!!,
                    range = Range.valueOf(this[ItemsTable.range])
            )
        }
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Item, player: Player) {
    this[ItemsTable.playerId] = player.id

    this[ItemsTable.name] = entity.name
    this[ItemsTable.description] = entity.description
    this[ItemsTable.encumbrance] = entity.encumbrance
    this[ItemsTable.quantity] = entity.quantity
    this[ItemsTable.quality] = entity.quality.toString()
    this[ItemsTable.type] = entity.type.toString()
    this[ItemsTable.subType] = entity.subType.toString()

    this[ItemsTable.uses] = entity.uses

    this[ItemsTable.isEquipped] = entity.isEquipped

    this[ItemsTable.soak] = entity.soak
    this[ItemsTable.defense] = entity.defense

    this[ItemsTable.damage] = entity.damage
    this[ItemsTable.criticalLevel] = entity.criticalLevel
    this[ItemsTable.range] = entity.range.toString()
}