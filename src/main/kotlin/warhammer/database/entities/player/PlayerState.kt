package warhammer.database.entities.player

import warhammer.database.entities.WarHammerEntity
import warhammer.database.entities.player.state.Career
import warhammer.database.entities.player.state.Stance

data class PlayerState(override val id: Int = -1,
                       val playerId: Int = -1,
                       val career: Career = Career(id),
                       val wounds: Int = 0,
                       val maxWounds: Int = 0,
                       val corruption: Int = 0,
                       val maxCorruption: Int = 0,
                       val stress: Int = 0,
                       val maxStress: Int = 0,
                       val exhaustion: Int = 0,
                       val maxExhaustion: Int = 0,
                       val stance: Stance = Stance(id)) : WarHammerEntity {
    val careerName = career.careerName
    val rank = career.rank
    val availableExperience = career.availableExperience
    val totalExperience = career.totalExperience

    val reckless = stance.reckless
    val maxReckless = stance.maxReckless
    val conservative = stance.conservative
    val maxConservative = stance.maxConservative
}