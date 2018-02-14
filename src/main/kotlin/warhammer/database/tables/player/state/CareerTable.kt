package warhammer.database.tables.player.state

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.player.PlayerStateTable

object CareerTable : IntIdTable() {
    val stateId = reference("state", PlayerStateTable).uniqueIndex()

    val careerName = varchar("careerName", length = 70)
    val rank = integer("rank")
    val availableExperience = integer("availableExperience")
    val totalExperience = integer("totalExperience")
}