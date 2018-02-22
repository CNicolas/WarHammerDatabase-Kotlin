package warhammer.database.daos.player

import warhammer.database.daos.Dao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerLinkedEntity

interface PlayerLinkedDao<E : PlayerLinkedEntity> : Dao<E> {
    fun add(entity: E, player: Player): E?

    fun findByNameAndPlayer(name: String, player: Player): E?
    fun findAllByPlayer(player: Player): List<E>

    fun updateByPlayer(entity: E, player: Player): E?

    fun deleteByPlayer(entity: E, player: Player): Int
    fun deleteByNameAndPlayer(name: String, player: Player): Int
    fun deleteAllByPlayer(player: Player): Int
    fun deleteAll()
}