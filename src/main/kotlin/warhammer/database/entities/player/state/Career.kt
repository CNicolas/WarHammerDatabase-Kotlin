package warhammer.database.entities.player.state

import warhammer.database.entities.WarHammerEntity

data class Career(val stateId: Int = -1,
                  override val id: Int = -1,
                  val careerName: String? = "",
                  val rank: Int? = 1,
                  val availableExperience: Int? = 0,
                  val totalExperience: Int? = 0) : WarHammerEntity