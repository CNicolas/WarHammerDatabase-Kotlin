package warhammer.database.entities.player.inventory.item

import warhammer.database.entities.player.inventory.item.enums.ItemType.ARMOR
import warhammer.database.entities.player.inventory.item.enums.Quality
import warhammer.database.entities.player.inventory.item.enums.Range

data class Armor(override val id: Int = -1,
                 override val inventoryId: Int = -1,
                 override val name: String = "Armor",
                 override val description: String? = null,
                 override val encumbrance: Int = 0,
                 override val quantity: Int = 1,
                 override val quality: Quality = Quality.NORMAL,

                 override val isEquipped: Boolean = false,

                 override val soak: Int? = 0,
                 override val defense: Int? = 0) : Item {

    override val type = ARMOR

    override val uses: Int? = null
    override val damage: Int? = null
    override val criticalLevel: Int? = null
    override val range: Range? = null
}