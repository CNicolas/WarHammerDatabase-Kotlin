package warhammer.database.entities.mapping

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.PlayerCharacteristicsEntity
import warhammer.database.entities.player.characteristics.CharacteristicValue
import warhammer.database.entities.player.characteristics.PlayerCharacteristics
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerCharacteristicsTable

internal fun ResultRow?.mapToPlayerCharacteristicsEntity(): PlayerCharacteristicsEntity? = when (this) {
    null -> null
    else -> {
        PlayerCharacteristicsEntity(
                id = this[PlayerCharacteristicsTable.id].value,
                playerId = this[PlayerCharacteristicsTable.playerId].value,
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

fun PlayerCharacteristicsEntity?.mapToPlayerCharacteristics(): PlayerCharacteristics = when (this) {
    null -> PlayerCharacteristics()
    else -> PlayerCharacteristics(
            strength = CharacteristicValue(strength, strengthFortune),
            toughness = CharacteristicValue(toughness, toughnessFortune),
            agility = CharacteristicValue(agility, agilityFortune),
            intelligence = CharacteristicValue(intelligence, intelligenceFortune),
            willpower = CharacteristicValue(willpower, willpowerFortune),
            fellowship = CharacteristicValue(fellowship, fellowshipFortune)
    )
}

fun PlayerCharacteristics.mapToEntity(playerId: Int): PlayerCharacteristicsEntity =
        PlayerCharacteristicsEntity(
                playerId = playerId,

                strength = strength.value,
                toughness = toughness.value,
                agility = agility.value,
                intelligence = intelligence.value,
                willpower = willpower.value,
                fellowship = fellowship.value,

                strengthFortune = strength.fortuneValue,
                toughnessFortune = toughness.fortuneValue,
                agilityFortune = agility.fortuneValue,
                intelligenceFortune = intelligence.fortuneValue,
                willpowerFortune = willpower.fortuneValue,
                fellowshipFortune = fellowship.fortuneValue
        )

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: PlayerCharacteristicsEntity) {
    this[PlayerCharacteristicsTable.playerId] = EntityID(entity.playerId, PlayersTable)

    this[PlayerCharacteristicsTable.strength] = entity.strength
    this[PlayerCharacteristicsTable.toughness] = entity.toughness
    this[PlayerCharacteristicsTable.agility] = entity.agility
    this[PlayerCharacteristicsTable.intelligence] = entity.intelligence
    this[PlayerCharacteristicsTable.willpower] = entity.willpower
    this[PlayerCharacteristicsTable.fellowship] = entity.fellowship

    this[PlayerCharacteristicsTable.strengthFortune] = entity.strengthFortune
    this[PlayerCharacteristicsTable.toughnessFortune] = entity.toughnessFortune
    this[PlayerCharacteristicsTable.agilityFortune] = entity.agilityFortune
    this[PlayerCharacteristicsTable.intelligenceFortune] = entity.intelligenceFortune
    this[PlayerCharacteristicsTable.willpowerFortune] = entity.willpowerFortune
    this[PlayerCharacteristicsTable.fellowshipFortune] = entity.fellowshipFortune
}