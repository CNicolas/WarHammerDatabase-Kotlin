package warhammer.database.entities.mapping

import org.jetbrains.exposed.sql.ResultRow
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.other.Race
import warhammer.database.tables.PlayersTable

fun ResultRow?.mapToPlayer(): Player? = when (this) {
    null -> null
    else -> {
        Player(this[PlayersTable.name],
                Race.valueOf(this[PlayersTable.race]),
                this[PlayersTable.age],
                this[PlayersTable.size],
                id = this[PlayersTable.id].value)
    }
}