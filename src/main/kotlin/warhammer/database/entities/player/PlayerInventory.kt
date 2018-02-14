package warhammer.database.entities.player

import warhammer.database.entities.WarHammerEntity
import warhammer.database.entities.player.inventory.Item
import warhammer.database.entities.player.inventory.Money

data class PlayerInventory(override val id: Int = -1,
                      val playerId: Int = -1,
                      val encumbrance: Int = 0,
                      val maxEncumbrance: Int = 0,
                      val money: Money = Money(),
                      val items: List<Item> = listOf()) : WarHammerEntity {
}