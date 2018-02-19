package warhammer.database.entities.player.characteristics

import warhammer.database.entities.player.characteristics.Characteristic.*

data class PlayerCharacteristics(var strength: CharacteristicValue = CharacteristicValue(0),
                                 var toughness: CharacteristicValue = CharacteristicValue(0),
                                 var agility: CharacteristicValue = CharacteristicValue(0),
                                 var intelligence: CharacteristicValue = CharacteristicValue(0),
                                 var willpower: CharacteristicValue = CharacteristicValue(0),
                                 var fellowship: CharacteristicValue = CharacteristicValue(0)) {

    operator fun get(characteristic: Characteristic): CharacteristicValue = when (characteristic) {
        STRENGTH -> strength
        TOUGHNESS -> toughness
        AGILITY -> agility
        INTELLIGENCE -> intelligence
        WILLPOWER -> willpower
        FELLOWSHIP -> fellowship
    }
}