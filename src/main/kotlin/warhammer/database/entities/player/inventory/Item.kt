package warhammer.database.entities.player.inventory

import warhammer.database.entities.NamedEntity

interface Item : NamedEntity {
    val inventoryId: Int

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