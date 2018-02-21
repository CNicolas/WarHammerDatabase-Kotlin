package warhammer.database.entities.mapping

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.PlayerEntity
import warhammer.database.entities.player.other.Race
import warhammer.database.tables.PlayersTable

fun ResultRow?.mapToPlayer(): PlayerEntity? = when (this) {
    null -> null
    else -> {
        PlayerEntity(id = this[PlayersTable.id].value,
                name = this[PlayersTable.name],
                race = Race.valueOf(this[PlayersTable.race]),
                age = this[PlayersTable.age],
                size = this[PlayersTable.size])
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: PlayerEntity) {
    this[PlayersTable.name] = entity.name
    this[PlayersTable.race] = entity.race.toString()
    this[PlayersTable.age] = entity.age
    this[PlayersTable.size] = entity.size
}