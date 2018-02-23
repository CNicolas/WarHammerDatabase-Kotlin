package warhammer.database.repositories.player

import warhammer.database.daos.player.playerLinked.PlayerLinkedDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.PlayerLinkedEntity
import warhammer.database.repositories.Repository

interface PlayerLinkedRepository<E : PlayerLinkedEntity> : Repository<E> {
    override val dao: PlayerLinkedDao<E>

    fun add(entity: E, player: Player): E?

    fun findAllByPlayer(player: Player): List<E>

    fun updateByPlayer(entity: E, player: Player): E?

    fun deleteByPlayer(entity: E, player: Player): Int
    fun deleteAllByPlayer(player: Player): Int
    fun deleteAll()
}