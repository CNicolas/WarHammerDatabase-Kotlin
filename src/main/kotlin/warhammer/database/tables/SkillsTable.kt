package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object SkillsTable : IntIdTable() {
    val playerId = integer("playerId")

    val name = varchar("name", length = 50)
    val characteristic = varchar("characteristic", length = 20)
    val type = varchar("type", length = 10)
    val level = integer("level")
    val specializations = varchar("specializations", length = 300)

    init {
        uniqueIndex(name, playerId)
    }
}