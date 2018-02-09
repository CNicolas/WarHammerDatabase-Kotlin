package warhammer.database.entities.mapping

import org.jetbrains.exposed.sql.ResultRow
import warhammer.database.entities.player.PlayerCharacteristicsEntity
import warhammer.database.entities.player.characteristics.Characteristic.*
import warhammer.database.entities.player.characteristics.CharacteristicValue
import warhammer.database.entities.player.characteristics.PlayerCharacteristics
import warhammer.database.tables.PlayerCharacteristicsTable

internal fun ResultRow?.mapToPlayerCharacteristicsEntity(): PlayerCharacteristicsEntity? = when (this) {
    null -> null
    else -> {
        PlayerCharacteristicsEntity(
                playerId = this[PlayerCharacteristicsTable.playerId].value,
                id = this[PlayerCharacteristicsTable.id].value,
                strength = this[PlayerCharacteristicsTable.strength],
                toughness = this[PlayerCharacteristicsTable.toughness],
                agility = this[PlayerCharacteristicsTable.agility],
                intelligence = this[PlayerCharacteristicsTable.intelligence],
                willpower = this[PlayerCharacteristicsTable.willpower],
                fellowship = this[PlayerCharacteristicsTable.fellowship],
                strengthFortune = this[PlayerCharacteristicsTable.strengthFortune],
                toughnessFortune = this[PlayerCharacteristicsTable.toughnessFortune],
                agilityFortune = this[PlayerCharacteristicsTable.agilityFortune],
                intelligenceFortune = this[PlayerCharacteristicsTable.intelligenceFortune],
                willpowerFortune = this[PlayerCharacteristicsTable.willpowerFortune],
                fellowshipFortune = this[PlayerCharacteristicsTable.fellowshipFortune]
        )
    }
}

fun PlayerCharacteristicsEntity?.mapToPlayerCharacteristics(): PlayerCharacteristics {
    return when (this) {
        null -> PlayerCharacteristics()
        else -> PlayerCharacteristics(
                strengthValue = CharacteristicValue(this.strength ?: 0, this.strengthFortune ?: 0),
                toughnessValue = CharacteristicValue(this.toughness ?: 0, this.toughnessFortune ?: 0),
                agilityValue = CharacteristicValue(this.agility ?: 0, this.agilityFortune ?: 0),
                intelligenceValue = CharacteristicValue(this.intelligence ?: 0, this.intelligenceFortune ?: 0),
                willpowerValue = CharacteristicValue(this.willpower ?: 0, this.willpowerFortune ?: 0),
                fellowShipValue = CharacteristicValue(this.fellowship ?: 0, this.fellowshipFortune ?: 0)
        )
    }
}

fun PlayerCharacteristics.mapToEntity(playerId:Int):PlayerCharacteristicsEntity=
        PlayerCharacteristicsEntity(
                playerId = playerId,

                strength = this[STRENGTH].value,
                toughness = this[TOUGHNESS].value,
                agility = this[AGILITY].value,
                intelligence = this[INTELLIGENCE].value,
                willpower = this[WILLPOWER].value,
                fellowship = this[FELLOWSHIP].value,

                strengthFortune = this[STRENGTH].fortuneValue,
                toughnessFortune = this[TOUGHNESS].fortuneValue,
                agilityFortune = this[AGILITY].fortuneValue,
                intelligenceFortune = this[INTELLIGENCE].fortuneValue,
                willpowerFortune = this[WILLPOWER].fortuneValue,
                fellowshipFortune = this[FELLOWSHIP].fortuneValue
        )
