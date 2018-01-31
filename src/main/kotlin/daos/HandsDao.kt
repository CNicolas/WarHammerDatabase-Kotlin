package daos

import entities.Hand
import entities.tables.Hands
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.sqlite.SQLiteException

class HandsDao : Dao<Hand> {
    override fun add(entity: Hand): Boolean {
        try {
            Hands.insert {
                it[name] = entity.name
                it[characteristicDicesCount] = entity.characteristicDicesCount
                it[expertiseDicesCount] = entity.expertiseDicesCount
                it[fortuneDicesCount] = entity.fortuneDicesCount
                it[conservativeDicesCount] = entity.conservativeDicesCount
                it[recklessDicesCount] = entity.recklessDicesCount
                it[challengeDicesCount] = entity.challengeDicesCount
                it[misfortuneDicesCount] = entity.misfortuneDicesCount
            }

            return true
        } catch (e: SQLiteException) {
            return false
        }
    }

    override fun findByName(name: String): Hand? {
        val result = Hands.select { Hands.name eq name }
                .firstOrNull()

        return when (result) {
            null -> null
            else -> Hand(result[Hands.name],
                    characteristicDicesCount = result[Hands.characteristicDicesCount],
                    expertiseDicesCount = result[Hands.expertiseDicesCount],
                    fortuneDicesCount = result[Hands.fortuneDicesCount],
                    conservativeDicesCount = result[Hands.conservativeDicesCount],
                    recklessDicesCount = result[Hands.recklessDicesCount],
                    challengeDicesCount = result[Hands.challengeDicesCount],
                    misfortuneDicesCount = result[Hands.misfortuneDicesCount])
        }
    }
}