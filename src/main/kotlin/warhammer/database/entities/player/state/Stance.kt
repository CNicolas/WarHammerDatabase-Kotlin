package warhammer.database.entities.player.state

import warhammer.database.entities.WarHammerEntity

data class Stance(override val id: Int = -1,
                  val stateId: Int = -1,
                  var reckless: Int = 0,
                  var maxReckless: Int = 0,
                  var conservative: Int = 0,
                  var maxConservative: Int = 0) : WarHammerEntity