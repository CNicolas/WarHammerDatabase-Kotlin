package daos

import entities.Player
import entities.tables.Players
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.sqlite.SQLiteException

class PlayersDao : Dao<Player> {
    override fun add(entity: Player): Boolean {
        return try {
            Players.insert {
                it[name] = entity.name
            }

            true
        } catch (e: SQLiteException) {
            false
        }
    }

    override fun findByName(name: String): Player? {
        val result = Players.select { Players.name eq name }
                .firstOrNull()

        return when (result) {
            null -> null
            else -> Player(result[Players.name])
        }
    }
}