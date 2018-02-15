package warhammer.database.entities.player.inventory

import warhammer.database.entities.player.inventory.ItemType.WEAPON

data class Weapon(override val id: Int = -1,
                  override val inventoryId: Int = -1,
                  override val name: String = "Weapon",
                  override val description: String? = null,
                  override val encumbrance: Int = 0,
                  override val quantity: Int = 1,
                  override val quality: Quality = Quality.NORMAL,

                  override val isEquipped: Boolean = false,

                  override val damage: Int? = 0,
                  override val criticalLevel: Int? = 0,
                  override val range: Range? = Range.ENGAGED) : Item {

    override val type = WEAPON

    override val uses: Int? = null
    override val soak: Int? = null
    override val defense: Int? = null
}