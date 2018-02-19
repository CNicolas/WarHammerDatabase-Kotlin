package warhammer.database.entities.player.characteristics

import warhammer.database.entities.player.characteristics.Characteristic.*

data class PlayerCharacteristics(val strength: CharacteristicValue = CharacteristicValue(0),
                                 val toughness: CharacteristicValue = CharacteristicValue(0),
                                 val agility: CharacteristicValue = CharacteristicValue(0),
                                 val intelligence: CharacteristicValue = CharacteristicValue(0),
                                 val willpower: CharacteristicValue = CharacteristicValue(0),
                                 val fellowship: CharacteristicValue = CharacteristicValue(0)) {

    private val characteristics: HashMap<Characteristic, CharacteristicValue> = hashMapOf(STRENGTH to strength,
            TOUGHNESS to toughness,
            AGILITY to agility,
            INTELLIGENCE to intelligence,
            WILLPOWER to willpower,
            FELLOWSHIP to fellowship)

    operator fun set(characteristic: Characteristic, value: CharacteristicValue) {
        characteristics[characteristic] = value
    }

    operator fun get(characteristic: Characteristic): CharacteristicValue = characteristics[characteristic]!!
}