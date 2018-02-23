package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object SkillsTable : IntIdTable() {
    val playerId = integer("playerId")

    val name = varchar("name", length = 50)
    val characteristic = varchar("characteristic", length = 20)
    val type = varchar("type", length = 10)
    val level = integer("level")

    init {
        uniqueIndex(name, playerId)
    }
}