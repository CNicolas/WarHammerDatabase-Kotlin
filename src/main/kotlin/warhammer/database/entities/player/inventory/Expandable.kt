package warhammer.database.entities.player.inventory

data class Expandable(override val id: Int = -1,
                      override val inventoryId: Int = -1,
                      override val name: String = "Expandable",
                      override val encumbrance: Int = 0,
                      override val quantity: Int = 0,
                      override val quality: Quality = Quality.NORMAL,
                      override val uses: Int = 0) : Item {
    override val type = ItemType.EXPANDABLE

    override val isEquipped: Boolean? = null
    override val soak: Int? = null
    override val defense: Int? = null
    override val damage: Int? = null
    override val criticalLevel: Int? = null
    override val range: Range? = null
}