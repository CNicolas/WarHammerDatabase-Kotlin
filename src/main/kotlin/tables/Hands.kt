package tables

import org.jetbrains.exposed.dao.IntIdTable

object Hands : IntIdTable() {
    val name = varchar("name", length = 70)

    val characteristicDicesCount = integer("characteristicDicesCount").nullable()
    val expertiseDicesCount = integer("expertiseDicesCount").nullable()
    val fortuneDicesCount = integer("fortuneDicesCount").nullable()
    val conservativeDicesCount = integer("conservativeDicesCount").nullable()
    val recklessDicesCount = integer("recklessDicesCount").nullable()
    val challengeDicesCount = integer("challengeDicesCount").nullable()
    val misfortuneDicesCount = integer("misfortuneDicesCount").nullable()
}
