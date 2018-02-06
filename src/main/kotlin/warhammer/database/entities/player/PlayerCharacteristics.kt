package warhammer.database.entities.player

import warhammer.database.entities.Hand
import warhammer.database.entities.player.Characteristic.*

data class PlayerCharacteristics(private val characteristics: HashMap<Characteristic, CharacteristicValue> =
                                 hashMapOf(STRENGTH to CharacteristicValue(0),
                                         TOUGHNESS to CharacteristicValue(0),
                                         AGILITY to CharacteristicValue(0),
                                         INTELLIGENCE to CharacteristicValue(0),
                                         WILLPOWER to CharacteristicValue(0),
                                         FELLOWSHIP to CharacteristicValue(0))) {

    constructor(strengthValue: CharacteristicValue = CharacteristicValue(0),
                toughnessValue: CharacteristicValue = CharacteristicValue(0),
                agilityValue: CharacteristicValue = CharacteristicValue(0),
                intelligenceValue: CharacteristicValue = CharacteristicValue(0),
                willpowerValue: CharacteristicValue = CharacteristicValue(0),
                fellowShipValue: CharacteristicValue = CharacteristicValue(0)) :
            this(hashMapOf(STRENGTH to strengthValue,
                    TOUGHNESS to toughnessValue,
                    AGILITY to agilityValue,
                    INTELLIGENCE to intelligenceValue,
                    WILLPOWER to willpowerValue,
                    FELLOWSHIP to fellowShipValue))

    fun getHand(name: String, characteristic: Characteristic): Hand {
        return characteristics[characteristic]!!.getHand(name)
    }

    operator fun set(characteristic: Characteristic, value: CharacteristicValue) {
        characteristics[characteristic] = value
    }

    operator fun get(characteristic: Characteristic): CharacteristicValue = characteristics[characteristic]!!
}