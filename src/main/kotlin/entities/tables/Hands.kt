package entities.tables

import org.jetbrains.exposed.sql.Table

object Hands : Table() {
    val name = varchar("name", length = 70).primaryKey()

    val characteristicDicesCount = integer("characteristicDicesCount").nullable()
    val expertiseDicesCount = integer("expertiseDicesCount").nullable()
    val fortuneDicesCount = integer("fortuneDicesCount").nullable()
    val conservativeDicesCount = integer("conservativeDicesCount").nullable()
    val recklessDicesCount = integer("recklessDicesCount").nullable()
    val challengeDicesCount = integer("challengeDicesCount").nullable()
    val misfortuneDicesCount = integer("misfortuneDicesCount").nullable()
}