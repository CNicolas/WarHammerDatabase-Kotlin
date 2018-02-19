package warhammer.database.entities.player

import warhammer.database.entities.WarHammerEntity
import warhammer.database.entities.player.state.Career
import warhammer.database.entities.player.state.Stance

data class PlayerState(override val id: Int = -1,
                       val playerId: Int = -1,
                       val career: Career = Career(stateId = id),
                       var wounds: Int = 0,
                       var maxWounds: Int = 0,
                       var corruption: Int = 0,
                       var maxCorruption: Int = 0,
                       var stress: Int = 0,
                       var maxStress: Int = 0,
                       var exhaustion: Int = 0,
                       var maxExhaustion: Int = 0,
                       val stance: Stance = Stance(stateId = id)) : WarHammerEntity {
    val careerName = career.name
    val rank = career.rank
    val availableExperience = career.availableExperience
    val totalExperience = career.totalExperience

    val reckless = stance.reckless
    val maxReckless = stance.maxReckless
    val conservative = stance.conservative
    val maxConservative = stance.maxConservative
}