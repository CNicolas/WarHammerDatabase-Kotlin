package warhammer.database.daos.player.inventory

import warhammer.database.daos.Dao
import warhammer.database.entities.WarHammerEntity

interface PlayerInventoryLinkedDao<E : WarHammerEntity> : Dao<E> {
    fun deleteByInventoryId(inventoryId: Int): Int
}