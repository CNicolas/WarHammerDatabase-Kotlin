package warhammer.database.entities.player.inventory

import warhammer.database.entities.WarHammerEntity

interface Item : WarHammerEntity {
    val inventoryId: Int

    val name: String
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