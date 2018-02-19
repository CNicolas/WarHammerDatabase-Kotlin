package warhammer.database.entities.player.inventory.item

import warhammer.database.entities.player.inventory.item.enums.ItemType
import warhammer.database.entities.player.inventory.item.enums.Quality
import warhammer.database.entities.player.inventory.item.enums.Range

data class Expandable(override val id: Int = -1,
                      override val inventoryId: Int = -1,
                      override var name: String = "Expandable",
                      override var description: String? = null,
                      override var encumbrance: Int = 0,
                      override var quantity: Int = 1,
                      override var quality: Quality = Quality.NORMAL,
                      override var uses: Int? = 0) : Item {
    override var type = ItemType.EXPANDABLE

    override var isEquipped: Boolean? = null
    override var soak: Int? = null
    override var defense: Int? = null
    override var damage: Int? = null
    override var criticalLevel: Int? = null
    override var range: Range? = null
}