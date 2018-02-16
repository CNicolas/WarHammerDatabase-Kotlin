package warhammer.database.entities.player.state

import warhammer.database.entities.WarHammerEntity

data class Career(override val id: Int = -1,
                  val stateId: Int = -1,
                  val name: String = "Unemployed",
                  val rank: Int = 1,
                  val availableExperience: Int = 0,
                  val totalExperience: Int = 0) : WarHammerEntity