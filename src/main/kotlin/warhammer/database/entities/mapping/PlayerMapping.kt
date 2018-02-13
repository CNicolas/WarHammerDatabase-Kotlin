package warhammer.database.entities.mapping

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.other.Race
import warhammer.database.tables.PlayersTable

fun ResultRow?.mapToPlayer(): Player? = when (this) {
    null -> null
    else -> {
        Player(this[PlayersTable.name],
                id = this[PlayersTable.id].value,
                race = Race.valueOf(this[PlayersTable.race]),
                age = this[PlayersTable.age],
                size = this[PlayersTable.size])
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Player) {
    this[PlayersTable.name] = entity.name
    this[PlayersTable.race] = entity.race.toString()
    this[PlayersTable.age] = entity.age
    this[PlayersTable.size] = entity.size
}