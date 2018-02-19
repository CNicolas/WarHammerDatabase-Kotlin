package warhammer.database.entities.player

import warhammer.database.entities.WarHammerEntity
import warhammer.database.entities.player.inventory.Money
import warhammer.database.entities.player.inventory.item.Item

data class PlayerInventory(override val id: Int = -1,
                           val playerId: Int = -1,
                           var encumbrance: Int = 0,
                           var maxEncumbrance: Int = 0,
                           var money: Money = Money(inventoryId = id),
                           val items: MutableList<Item> = mutableListOf()) : WarHammerEntity