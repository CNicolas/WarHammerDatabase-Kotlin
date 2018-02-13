package warhammer.database.entities.player.characteristics

import warhammer.database.entities.player.characteristics.Characteristic.*

data class PlayerCharacteristicsMap(private val strengthValue: CharacteristicValue = CharacteristicValue(0),
                                    private val toughnessValue: CharacteristicValue = CharacteristicValue(0),
                                    private val agilityValue: CharacteristicValue = CharacteristicValue(0),
                                    private val intelligenceValue: CharacteristicValue = CharacteristicValue(0),
                                    private val willpowerValue: CharacteristicValue = CharacteristicValue(0),
                                    private val fellowShipValue: CharacteristicValue = CharacteristicValue(0)) {

    private val characteristics: HashMap<Characteristic, CharacteristicValue> = hashMapOf(STRENGTH to strengthValue,
            TOUGHNESS to toughnessValue,
            AGILITY to agilityValue,
            INTELLIGENCE to intelligenceValue,
            WILLPOWER to willpowerValue,
            FELLOWSHIP to fellowShipValue)

    operator fun set(characteristic: Characteristic, value: CharacteristicValue) {
        characteristics[characteristic] = value
    }

    operator fun get(characteristic: Characteristic): CharacteristicValue = characteristics[characteristic]!!
}