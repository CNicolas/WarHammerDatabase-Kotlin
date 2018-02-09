package warhammer.database.entities.mapping

import org.jetbrains.exposed.sql.ResultRow
import warhammer.database.entities.Hand
import warhammer.database.tables.HandsTable

fun ResultRow?.mapToHand(): Hand? = when (this) {
    null -> null
    else -> {
        Hand(
                this[HandsTable.name],
                this[HandsTable.id].value,
                characteristicDicesCount = this[HandsTable.characteristicDicesCount],
                expertiseDicesCount = this[HandsTable.expertiseDicesCount],
                fortuneDicesCount = this[HandsTable.fortuneDicesCount],
                conservativeDicesCount = this[HandsTable.conservativeDicesCount],
                recklessDicesCount = this[HandsTable.recklessDicesCount],
                challengeDicesCount = this[HandsTable.challengeDicesCount],
                misfortuneDicesCount = this[HandsTable.misfortuneDicesCount]
        )
    }
}