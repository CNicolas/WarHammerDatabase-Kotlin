package warhammer.database.mongo

import warhammer.database.entities.player.inventory.item.enums.ItemType.ARMOR
import warhammer.database.entities.player.inventory.item.enums.Quality
import warhammer.database.entities.player.inventory.item.enums.Range

data class Armor(override var name: String = "Armor",
                 override var description: String? = null,
                 override var encumbrance: Int = 0,
                 override var quantity: Int = 1,
                 override var quality: Quality = Quality.NORMAL,

                 override var isEquipped: Boolean? = false,

                 override var soak: Int? = 0,
                 override var defense: Int? = 0) : Item {

    override var type = ARMOR

    override var uses: Int? = null
    override var damage: Int? = null
    override var criticalLevel: Int? = null
    override var range: Range? = null
}