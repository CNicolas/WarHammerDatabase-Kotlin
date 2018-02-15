package warhammer.database.entities.player.inventory.item

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.inventory.item.enums.ItemType
import warhammer.database.entities.player.inventory.item.enums.Quality
import warhammer.database.entities.player.inventory.item.enums.Range

interface Item : NamedEntity {
    val inventoryId: Int

    val description: String?
    val encumbrance: Int
    val quantity: Int
    val quality: Quality
    val type: ItemType

    val uses: Int?

    val isEquipped: Boolean?

    val soak: Int?
    val defense: Int?

    val damage: Int?
    val criticalLevel: Int?
    val range: Range?
}