package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object PlayersTable : IntIdTable() {
    val name = varchar("name", length = 50).uniqueIndex()
}