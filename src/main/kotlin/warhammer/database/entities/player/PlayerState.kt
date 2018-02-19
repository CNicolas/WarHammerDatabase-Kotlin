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
    val careerName get() = career.name
    val rank get() = career.rank
    val availableExperience get() = career.availableExperience
    val totalExperience get() = career.totalExperience

    val reckless get() = stance.reckless
    val maxReckless get() = stance.maxReckless
    val conservative get() = stance.conservative
    val maxConservative get() = stance.maxConservative
}