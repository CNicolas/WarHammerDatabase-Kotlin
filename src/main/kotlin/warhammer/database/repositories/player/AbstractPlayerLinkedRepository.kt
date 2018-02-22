package warhammer.database.repositories.player

import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerLinkedEntity
import warhammer.database.repositories.AbstractRepository

abstract class AbstractPlayerLinkedRepository<E : PlayerLinkedEntity>(databaseUrl: String, driver: String)
    : AbstractRepository<E>(databaseUrl, driver), PlayerLinkedRepository<E> {
    override fun add(entity: E, player: Player): E? {
        connectToDatabase()

        return transaction { dao.add(entity, player) }
    }

    override fun findAllByPlayer(player: Player): List<E> {
        connectToDatabase()

        return transaction { dao.findAllByPlayer(player) }
    }

    override fun updateByPlayer(entity: E, player: Player): E? {
        connectToDatabase()

        return transaction { dao.updateByPlayer(entity, player) }
    }

    override fun deleteByPlayer(entity: E, player: Player): Int {
        connectToDatabase()

        return transaction { dao.deleteByPlayer(entity, player) }
    }

    override fun deleteAllByPlayer(player: Player): Int {
        connectToDatabase()

        return transaction { dao.deleteAllByPlayer(player) }
    }

    override fun deleteAll() {
        connectToDatabase()

        return transaction { dao.deleteAll() }
    }
}