package warhammer.database.entities.hand

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.tables.HandsTable

fun ResultRow?.mapToHand(): Hand? = when (this) {
    null -> null
    else -> {
        Hand(
                id = this[HandsTable.id].value,
                name = this[HandsTable.name],
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

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Hand) {
    this[HandsTable.name] = entity.name

    this[HandsTable.characteristicDicesCount] = entity.characteristicDicesCount
    this[HandsTable.expertiseDicesCount] = entity.expertiseDicesCount
    this[HandsTable.fortuneDicesCount] = entity.fortuneDicesCount
    this[HandsTable.conservativeDicesCount] = entity.conservativeDicesCount
    this[HandsTable.recklessDicesCount] = entity.recklessDicesCount
    this[HandsTable.challengeDicesCount] = entity.challengeDicesCount
    this[HandsTable.misfortuneDicesCount] = entity.misfortuneDicesCount
}