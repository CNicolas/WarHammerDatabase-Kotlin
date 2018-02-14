package warhammer.database.entities.player.inventory

import warhammer.database.entities.WarHammerEntity

data class Money(override val id: Int = -1,
                 val inventoryId: Int = -1,
                 val brass: Int = 0,
                 val silver: Int = 0,
                 val gold: Int = 0) : WarHammerEntity