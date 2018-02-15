package warhammer.database.entities.player.inventory

import warhammer.database.entities.player.inventory.ItemType.ITEM

data class GenericItem(override val id: Int = -1,
                       override val inventoryId: Int = -1,
                       override val name: String = "Item",
                       override val description: String? = null,
                       override val encumbrance: Int = 0,
                       override val quantity: Int = 1,
                       override val quality: Quality = Quality.NORMAL) : Item {
    override val type = ITEM

    override val isEquipped: Boolean? = null
    override val uses: Int? = null
    override val soak: Int? = null
    override val defense: Int? = null
    override val damage: Int? = null
    override val criticalLevel: Int? = null
    override val range: Range? = null
}