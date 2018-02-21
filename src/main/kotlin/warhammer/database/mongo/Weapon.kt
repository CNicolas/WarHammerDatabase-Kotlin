package warhammer.database.mongo

import warhammer.database.entities.player.inventory.item.enums.ItemType.WEAPON
import warhammer.database.entities.player.inventory.item.enums.Quality
import warhammer.database.entities.player.inventory.item.enums.Range

data class Weapon(override var name: String = "Weapon",
                  override var description: String? = null,
                  override var encumbrance: Int = 0,
                  override var quantity: Int = 1,
                  override var quality: Quality = Quality.NORMAL,

                  override var isEquipped: Boolean? = false,

                  override var damage: Int? = 0,
                  override var criticalLevel: Int? = 0,
                  override var range: Range? = Range.ENGAGED) : Item {

    override var type = WEAPON

    override var uses: Int? = null
    override var soak: Int? = null
    override var defense: Int? = null
}