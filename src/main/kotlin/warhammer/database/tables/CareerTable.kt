package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object CareerTable : IntIdTable() {
    val stateId = reference("state", PlayerStateTable).uniqueIndex()

    val careerName = varchar("careerName", length = 70).nullable()
    val rank = integer("rank").nullable()
    val availableExperience = integer("availableExperience").nullable()
    val totalExperience = integer("totalExperience").nullable()
}