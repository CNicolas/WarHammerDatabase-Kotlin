package warhammer.database.entities.mapping.state

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.state.Stance
import warhammer.database.tables.player.state.StanceTable
import warhammer.database.tables.player.PlayerStateTable

internal fun ResultRow?.mapToStance(): Stance? = when (this) {
    null -> null
    else -> {
        Stance(
                stateId = this[StanceTable.stateId].value,
                id = this[StanceTable.id].value,
                reckless = this[StanceTable.reckless],
                maxReckless = this[StanceTable.maxReckless],
                conservative = this[StanceTable.conservative],
                maxConservative = this[StanceTable.maxConservative]
        )
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Stance) {
    this[StanceTable.stateId] = EntityID(entity.stateId, PlayerStateTable)

    this[StanceTable.reckless] = entity.reckless
    this[StanceTable.maxReckless] = entity.maxReckless
    this[StanceTable.conservative] = entity.conservative
    this[StanceTable.maxConservative] = entity.maxConservative
}