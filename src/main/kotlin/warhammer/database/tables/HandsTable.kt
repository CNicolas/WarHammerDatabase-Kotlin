package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object HandsTable : IntIdTable() {
    val name = varchar("name", length = 70).uniqueIndex()

    val characteristicDicesCount = integer("characteristicDicesCount").nullable()
    val expertiseDicesCount = integer("expertiseDicesCount").nullable()
    val fortuneDicesCount = integer("fortuneDicesCount").nullable()
    val conservativeDicesCount = integer("conservativeDicesCount").nullable()
    val recklessDicesCount = integer("recklessDicesCount").nullable()
    val challengeDicesCount = integer("challengeDicesCount").nullable()
    val misfortuneDicesCount = integer("misfortuneDicesCount").nullable()
}