package warhammer.database.entities.player.extensions

import warhammer.database.entities.player.CharacteristicValue
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.entities.player.enums.Characteristic.*
import warhammer.database.entities.player.enums.Race

val Player.maxStress: Int
    get() = willpower.value * 2
val Player.maxExhaustion: Int
    get() = toughness.value * 2

val Player.maxEncumbrance: Int
    get() = strength.value * 5 + strength.fortuneValue + 5 + when (race) {
        Race.DWARF -> 5
        else -> 0
    }

operator fun Player.get(characteristic: Characteristic): CharacteristicValue = when (characteristic) {
    STRENGTH -> strength
    TOUGHNESS -> toughness
    AGILITY -> agility
    INTELLIGENCE -> intelligence
    WILLPOWER -> willpower
    FELLOWSHIP -> fellowship
}