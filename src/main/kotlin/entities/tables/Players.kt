package entities.tables

import org.jetbrains.exposed.dao.IntIdTable

object Players : IntIdTable() {
    val name = varchar("name", length = 50).uniqueIndex()
}