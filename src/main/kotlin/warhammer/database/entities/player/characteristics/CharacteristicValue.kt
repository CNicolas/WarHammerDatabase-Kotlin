package warhammer.database.entities.player.characteristics

import warhammer.database.entities.Hand

data class CharacteristicValue(val value: Int, val fortuneValue: Int = 0) {
    fun getHand(name: String) = Hand(name = name, characteristicDicesCount = value, fortuneDicesCount = fortuneValue)
}