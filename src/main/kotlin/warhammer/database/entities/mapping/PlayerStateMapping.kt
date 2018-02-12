package warhammer.database.entities.mapping

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.PlayerState
import warhammer.database.tables.player.PlayerStateTable
import warhammer.database.tables.PlayersTable

internal fun ResultRow?.mapToPlayerStateEntity(): PlayerState? = when (this) {
    null -> null
    else -> {
        PlayerState(
                playerId = this[PlayerStateTable.playerId].value,
                id = this[PlayerStateTable.id].value,
                wounds = this[PlayerStateTable.wounds],
                maxWounds = this[PlayerStateTable.maxWounds],
                corruption = this[PlayerStateTable.corruption],
                maxCorruption = this[PlayerStateTable.maxCorruption],
                stress = this[PlayerStateTable.stress],
                maxStress = this[PlayerStateTable.maxStress],
                exhaustion = this[PlayerStateTable.exhaustion],
                maxExhaustion = this[PlayerStateTable.maxExhaustion]
        )
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: PlayerState) {
    this[PlayerStateTable.playerId] = EntityID(entity.playerId, PlayersTable)

    this[PlayerStateTable.wounds] = entity.wounds
    this[PlayerStateTable.maxWounds] = entity.maxWounds

    this[PlayerStateTable.corruption] = entity.corruption
    this[PlayerStateTable.maxCorruption] = entity.maxCorruption

    this[PlayerStateTable.stress] = entity.stress
    this[PlayerStateTable.maxStress] = entity.maxStress

    this[PlayerStateTable.exhaustion] = entity.exhaustion
    this[PlayerStateTable.maxExhaustion] = entity.maxExhaustion
}