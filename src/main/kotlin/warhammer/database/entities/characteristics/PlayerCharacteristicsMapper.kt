package warhammer.database.entities.characteristics

import org.jetbrains.exposed.sql.ResultRow
import warhammer.database.entities.PlayerCharacteristicsEntity
import warhammer.database.entities.characteristics.Characteristic.*
import warhammer.database.tables.PlayerCharacteristicsTable

object PlayerCharacteristicsMapper {
    fun mapResultRowToEntity(result: ResultRow?): PlayerCharacteristicsEntity? = when (result) {
        null -> null
        else -> {
            PlayerCharacteristicsEntity(
                    playerId = result[PlayerCharacteristicsTable.playerId].value,
                    id = result[PlayerCharacteristicsTable.id].value,
                    strength = result[PlayerCharacteristicsTable.strength],
                    toughness = result[PlayerCharacteristicsTable.toughness],
                    agility = result[PlayerCharacteristicsTable.agility],
                    intelligence = result[PlayerCharacteristicsTable.intelligence],
                    willpower = result[PlayerCharacteristicsTable.willpower],
                    fellowship = result[PlayerCharacteristicsTable.fellowship],
                    strengthFortune = result[PlayerCharacteristicsTable.strengthFortune],
                    toughnessFortune = result[PlayerCharacteristicsTable.toughnessFortune],
                    agilityFortune = result[PlayerCharacteristicsTable.agilityFortune],
                    intelligenceFortune = result[PlayerCharacteristicsTable.intelligenceFortune],
                    willpowerFortune = result[PlayerCharacteristicsTable.willpowerFortune],
                    fellowshipFortune = result[PlayerCharacteristicsTable.fellowshipFortune]
            )
        }
    }

    fun mapEntityToPlayerCharacteristics(entity: PlayerCharacteristicsEntity?): PlayerCharacteristics {
        return when (entity) {
            null -> PlayerCharacteristics()
            else -> PlayerCharacteristics(
                    strengthValue = CharacteristicValue(entity.strength ?: 0, entity.strengthFortune ?: 0),
                    toughnessValue = CharacteristicValue(entity.toughness ?: 0, entity.toughnessFortune ?: 0),
                    agilityValue = CharacteristicValue(entity.agility ?: 0, entity.agilityFortune ?: 0),
                    intelligenceValue = CharacteristicValue(entity.intelligence ?: 0, entity.intelligenceFortune ?: 0),
                    willpowerValue = CharacteristicValue(entity.willpower ?: 0, entity.willpowerFortune ?: 0),
                    fellowShipValue = CharacteristicValue(entity.fellowship ?: 0, entity.fellowshipFortune ?: 0)
            )
        }
    }

    fun mapPlayerCharacteristicsToEntity(playerCharacteristics: PlayerCharacteristics, playerId: Int): PlayerCharacteristicsEntity =
            PlayerCharacteristicsEntity(
                    playerId = playerId,

                    strength = playerCharacteristics[STRENGTH].value,
                    toughness = playerCharacteristics[TOUGHNESS].value,
                    agility = playerCharacteristics[AGILITY].value,
                    intelligence = playerCharacteristics[INTELLIGENCE].value,
                    willpower = playerCharacteristics[WILLPOWER].value,
                    fellowship = playerCharacteristics[FELLOWSHIP].value,

                    strengthFortune = playerCharacteristics[STRENGTH].fortuneValue,
                    toughnessFortune = playerCharacteristics[TOUGHNESS].fortuneValue,
                    agilityFortune = playerCharacteristics[AGILITY].fortuneValue,
                    intelligenceFortune = playerCharacteristics[INTELLIGENCE].fortuneValue,
                    willpowerFortune = playerCharacteristics[WILLPOWER].fortuneValue,
                    fellowshipFortune = playerCharacteristics[FELLOWSHIP].fortuneValue
            )

    fun mapPlayerCharacteristicsToEntityKnowingId(playerCharacteristics: PlayerCharacteristics,
                                         playerId: Int,
                                         id: Int): PlayerCharacteristicsEntity =
            PlayerCharacteristicsEntity(
                    id = id,
                    playerId = playerId,

                    strength = playerCharacteristics[STRENGTH].value,
                    toughness = playerCharacteristics[TOUGHNESS].value,
                    agility = playerCharacteristics[AGILITY].value,
                    intelligence = playerCharacteristics[INTELLIGENCE].value,
                    willpower = playerCharacteristics[WILLPOWER].value,
                    fellowship = playerCharacteristics[FELLOWSHIP].value,

                    strengthFortune = playerCharacteristics[STRENGTH].fortuneValue,
                    toughnessFortune = playerCharacteristics[TOUGHNESS].fortuneValue,
                    agilityFortune = playerCharacteristics[AGILITY].fortuneValue,
                    intelligenceFortune = playerCharacteristics[INTELLIGENCE].fortuneValue,
                    willpowerFortune = playerCharacteristics[WILLPOWER].fortuneValue,
                    fellowshipFortune = playerCharacteristics[FELLOWSHIP].fortuneValue
            )
}