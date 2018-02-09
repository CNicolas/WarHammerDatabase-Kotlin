package warhammer.database.entities.player

import org.jetbrains.exposed.sql.ResultRow
import warhammer.database.entities.player.other.Race
import warhammer.database.tables.PlayersTable

object PlayerMapper {
    fun mapResultRowToEntity(result: ResultRow?): Player? = when (result) {
        null -> null
        else -> {
            Player(result[PlayersTable.name],
                    Race.valueOf(result[PlayersTable.race]),
                    result[PlayersTable.age],
                    result[PlayersTable.size],
                    id = result[PlayersTable.id].value)
        }
    }
}