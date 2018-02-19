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
    var careerName
        get() = career.name
        set(value) {
            career.name = value
        }
    var rank
        get() = career.rank
        set(value) {
            career.rank = value
        }
    var availableExperience
        get() = career.availableExperience
        set(value) {
            career.availableExperience = value
        }
    var totalExperience
        get() = career.totalExperience
        set(value) {
            career.totalExperience = value
        }

    var reckless get() = stance.reckless
        set(value) {
            stance.reckless = value
        }
    var maxReckless get() = stance.maxReckless
        set(value) {
            stance.maxReckless = value
        }
    var conservative get() = stance.conservative
        set(value) {
            stance.conservative = value
        }
    var maxConservative get() = stance.maxConservative
        set(value) {
            stance.maxConservative = value
        }
}