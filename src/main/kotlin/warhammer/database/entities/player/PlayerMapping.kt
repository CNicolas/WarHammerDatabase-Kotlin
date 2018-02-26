package warhammer.database.entities.player

import com.beust.klaxon.Klaxon
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.enums.Race
import warhammer.database.tables.PlayersTable

fun ResultRow?.mapToPlayer(): Player? =
        when (this) {
            null -> null
            else -> {
                Player(name = this[PlayersTable.name],
                        race = Race.valueOf(this[PlayersTable.race]),
                        age = this[PlayersTable.age],
                        size = this[PlayersTable.size],
                        strength = CharacteristicValue(this[PlayersTable.strength], this[PlayersTable.strengthFortune]),
                        toughness = CharacteristicValue(this[PlayersTable.toughness], this[PlayersTable.toughnessFortune]),
                        agility = CharacteristicValue(this[PlayersTable.agility], this[PlayersTable.agilityFortune]),
                        intelligence = CharacteristicValue(this[PlayersTable.intelligence], this[PlayersTable.intelligenceFortune]),
                        willpower = CharacteristicValue(this[PlayersTable.willpower], this[PlayersTable.willpowerFortune]),
                        fellowship = CharacteristicValue(this[PlayersTable.fellowship], this[PlayersTable.fellowshipFortune]),
                        careerName = this[PlayersTable.careerName],
                        rank = this[PlayersTable.rank],
                        availableExperience = this[PlayersTable.availableExperience],
                        totalExperience = this[PlayersTable.totalExperience],
                        reckless = this[PlayersTable.reckless],
                        maxReckless = this[PlayersTable.maxReckless],
                        conservative = this[PlayersTable.conservative],
                        maxConservative = this[PlayersTable.maxConservative],
                        wounds = this[PlayersTable.wounds],
                        maxWounds = this[PlayersTable.maxWounds],
                        corruption = this[PlayersTable.corruption],
                        maxCorruption = this[PlayersTable.maxCorruption],
                        stress = this[PlayersTable.stress],
                        exhaustion = this[PlayersTable.exhaustion],
                        brass = this[PlayersTable.brass],
                        silver = this[PlayersTable.silver],
                        gold = this[PlayersTable.gold],
                        skills = Klaxon().parseArray(this[PlayersTable.skills])!!,
                        id = this[PlayersTable.id].value
                )
            }
        }

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Player) {
    this[PlayersTable.name] = entity.name
    this[PlayersTable.race] = entity.race.toString()
    this[PlayersTable.age] = entity.age
    this[PlayersTable.size] = entity.size
    // region CHARACTERISTICS
    this[PlayersTable.strength] = entity.strength.value
    this[PlayersTable.toughness] = entity.toughness.value
    this[PlayersTable.agility] = entity.agility.value
    this[PlayersTable.intelligence] = entity.intelligence.value
    this[PlayersTable.willpower] = entity.willpower.value
    this[PlayersTable.fellowship] = entity.fellowship.value

    this[PlayersTable.strengthFortune] = entity.strength.fortuneValue
    this[PlayersTable.toughnessFortune] = entity.toughness.fortuneValue
    this[PlayersTable.agilityFortune] = entity.agility.fortuneValue
    this[PlayersTable.intelligenceFortune] = entity.intelligence.fortuneValue
    this[PlayersTable.willpowerFortune] = entity.willpower.fortuneValue
    this[PlayersTable.fellowshipFortune] = entity.fellowship.fortuneValue
    // endregion
    // region STATE
    this[PlayersTable.wounds] = entity.wounds
    this[PlayersTable.maxWounds] = entity.maxWounds
    this[PlayersTable.corruption] = entity.corruption
    this[PlayersTable.maxCorruption] = entity.maxCorruption
    this[PlayersTable.stress] = entity.stress
    this[PlayersTable.exhaustion] = entity.exhaustion

    this[PlayersTable.careerName] = entity.careerName
    this[PlayersTable.rank] = entity.rank
    this[PlayersTable.availableExperience] = entity.availableExperience
    this[PlayersTable.totalExperience] = entity.totalExperience

    this[PlayersTable.reckless] = entity.reckless
    this[PlayersTable.maxReckless] = entity.maxReckless
    this[PlayersTable.conservative] = entity.conservative
    this[PlayersTable.maxConservative] = entity.maxConservative
    // endregion
    // region INVENTORY
    this[PlayersTable.brass] = entity.brass
    this[PlayersTable.silver] = entity.silver
    this[PlayersTable.gold] = entity.gold
    // endregion
    this[PlayersTable.skills] = Klaxon().toJsonString(entity.skills)
}