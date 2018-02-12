package warhammer.database.daos.player.state

import warhammer.database.daos.Dao
import warhammer.database.entities.WarHammerEntity

interface PlayerStateLinkedDao<E : WarHammerEntity> : Dao<E> {
    fun findByStateId(stateId: Int): E?
    fun deleteByStateId(stateId: Int): Int
}