package warhammer.database.entities.mapping

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.PlayerCharacteristicsEntity
import warhammer.database.entities.player.characteristics.Characteristic.*
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
            strength = CharacteristicValue(this.strength, this.strengthFortune),
            toughness = CharacteristicValue(this.toughness, this.toughnessFortune),
            agility = CharacteristicValue(this.agility, this.agilityFortune),
            intelligence = CharacteristicValue(this.intelligence, this.intelligenceFortune),
            willpower = CharacteristicValue(this.willpower, this.willpowerFortune),
            fellowShip = CharacteristicValue(this.fellowship, this.fellowshipFortune)
    )
}

fun PlayerCharacteristics.mapToEntity(playerId: Int): PlayerCharacteristicsEntity =
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