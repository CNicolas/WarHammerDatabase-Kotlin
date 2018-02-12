package warhammer.database.entities.player.state

import warhammer.database.entities.WarHammerEntity

data class PlayerState(val playerId: Int,
                       override val id: Int = -1,
                       val career: Career = Career(id),
                       val wounds: Int? = 0,
                       val maxWounds: Int? = 0,
                       val corruption: Int? = 0,
                       val maxCorruption: Int? = 0,
                       val stress: Int? = 0,
                       val maxStress: Int? = 0,
                       val exhaustion: Int? = 0,
                       val maxExhaustion: Int? = 0,
                       val stance: Stance = Stance(id)) : WarHammerEntity