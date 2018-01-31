package daos

import entities.Player
import entities.tables.Players
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class PlayersDao {
    fun add(player: Player): Unit {
        Players.insert {
            it[name] = player.name
        }
    }

    fun findByName(name: String): Player? {
        val result = Players.select { Players.name eq name }
                .firstOrNull()

        return when (result) {
            null -> null
            else -> Player(result[Players.name])
        }
    }
}