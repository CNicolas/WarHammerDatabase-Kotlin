package warhammer.database.entities.mapping

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.state.Career
import warhammer.database.tables.player.state.CareerTable
import warhammer.database.tables.player.PlayerStateTable

internal fun ResultRow?.mapToCareer(): Career? = when (this) {
    null -> null
    else -> {
        Career(
                stateId = this[CareerTable.stateId].value,
                id = this[CareerTable.id].value,
                careerName = this[CareerTable.careerName],
                rank = this[CareerTable.rank],
                availableExperience = this[CareerTable.availableExperience],
                totalExperience = this[CareerTable.totalExperience]
        )
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Career) {
    this[CareerTable.stateId] = EntityID(entity.stateId, PlayerStateTable)

    this[CareerTable.careerName] = entity.careerName
    this[CareerTable.rank] = entity.rank
    this[CareerTable.availableExperience] = entity.availableExperience
    this[CareerTable.totalExperience] = entity.totalExperience
}