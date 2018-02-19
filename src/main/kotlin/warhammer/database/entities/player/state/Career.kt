package warhammer.database.entities.player.state

import warhammer.database.entities.WarHammerEntity

data class Career(override val id: Int = -1,
                  val stateId: Int = -1,
                  var name: String = "Unemployed",
                  var rank: Int = 1,
                  var availableExperience: Int = 0,
                  var totalExperience: Int = 0) : WarHammerEntity