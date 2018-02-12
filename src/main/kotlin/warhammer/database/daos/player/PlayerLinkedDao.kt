package warhammer.database.daos.player

import warhammer.database.daos.Dao
import warhammer.database.entities.WarHammerEntity

interface PlayerLinkedDao<E : WarHammerEntity> : Dao<E> {
    fun findByPlayerId(playerId: Int): E?
    fun deleteByPlayerId(playerId: Int): Int
}