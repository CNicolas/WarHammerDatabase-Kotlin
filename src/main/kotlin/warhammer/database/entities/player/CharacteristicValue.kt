package warhammer.database.entities.player

import warhammer.database.entities.hand.DifficultyLevel
import warhammer.database.entities.hand.DifficultyLevel.NONE
import warhammer.database.entities.hand.Hand

data class CharacteristicValue(val value: Int = 0, val fortuneValue: Int = 0) : Comparable<CharacteristicValue> {
    fun getHand(name: String, difficultyLevel: DifficultyLevel = NONE) = Hand(
            name = name,
            characteristicDicesCount = value,
            fortuneDicesCount = fortuneValue,
            challengeDicesCount = difficultyLevel.challengeDicesCount
    )

    override fun compareTo(other: CharacteristicValue): Int {
        val valueDiff = value - other.value
        val fortuneDiff = fortuneValue - other.fortuneValue

        return when (valueDiff) {
            0 -> fortuneDiff
            else -> valueDiff
        }
    }
}