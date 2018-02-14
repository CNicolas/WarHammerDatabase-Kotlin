package warhammer.database.entities.player.characteristics

import warhammer.database.entities.player.characteristics.Characteristic.*

data class PlayerCharacteristics(private val strength: CharacteristicValue = CharacteristicValue(0),
                                 private val toughness: CharacteristicValue = CharacteristicValue(0),
                                 private val agility: CharacteristicValue = CharacteristicValue(0),
                                 private val intelligence: CharacteristicValue = CharacteristicValue(0),
                                 private val willpower: CharacteristicValue = CharacteristicValue(0),
                                 private val fellowShip: CharacteristicValue = CharacteristicValue(0)) {

    private val characteristics: HashMap<Characteristic, CharacteristicValue> = hashMapOf(STRENGTH to strength,
            TOUGHNESS to toughness,
            AGILITY to agility,
            INTELLIGENCE to intelligence,
            WILLPOWER to willpower,
            FELLOWSHIP to fellowShip)

    operator fun set(characteristic: Characteristic, value: CharacteristicValue) {
        characteristics[characteristic] = value
    }

    operator fun get(characteristic: Characteristic): CharacteristicValue = characteristics[characteristic]!!
}