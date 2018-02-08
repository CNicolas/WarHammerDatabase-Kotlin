package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object PlayersTable : IntIdTable() {
    val name = varchar("name", length = 50).uniqueIndex()

    val race = varchar("race", length = 20)
    val age = integer("age").nullable()
    val size = integer("size").nullable()
}