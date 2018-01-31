package daos

import entities.Hand
import entities.tables.Hands
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.sqlite.SQLiteException

class HandsDao {
    fun add(hand: Hand): Boolean {
        try {
            Hands.insert {
                it[name] = hand.name
                it[characteristicDicesCount] = hand.characteristicDicesCount
                it[expertiseDicesCount] = hand.expertiseDicesCount
                it[fortuneDicesCount] = hand.fortuneDicesCount
                it[conservativeDicesCount] = hand.conservativeDicesCount
                it[recklessDicesCount] = hand.recklessDicesCount
                it[challengeDicesCount] = hand.challengeDicesCount
                it[misfortuneDicesCount] = hand.misfortuneDicesCount
            }

            return true
        } catch (e: SQLiteException) {
            return false
        }
    }

    fun findByName(name: String): Hand? {
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