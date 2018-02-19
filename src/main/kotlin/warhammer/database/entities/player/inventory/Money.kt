package warhammer.database.entities.player.inventory

import warhammer.database.entities.WarHammerEntity

data class Money(override val id: Int = -1,
                 val inventoryId: Int = -1,
                 var brass: Int = 0,
                 var silver: Int = 0,
                 var gold: Int = 0) : WarHammerEntity {

    constructor(brass: Int = 0, silver: Int = 0, gold: Int = 0) : this(-1, -1, brass, silver, gold)
}