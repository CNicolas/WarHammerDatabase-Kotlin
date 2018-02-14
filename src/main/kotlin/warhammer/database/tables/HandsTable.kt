package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object HandsTable : IntIdTable() {
    val name = varchar("name", length = 70).uniqueIndex()

    val characteristicDicesCount = integer("characteristicDicesCount")
    val expertiseDicesCount = integer("expertiseDicesCount")
    val fortuneDicesCount = integer("fortuneDicesCount")
    val conservativeDicesCount = integer("conservativeDicesCount")
    val recklessDicesCount = integer("recklessDicesCount")
    val challengeDicesCount = integer("challengeDicesCount")
    val misfortuneDicesCount = integer("misfortuneDicesCount")
}