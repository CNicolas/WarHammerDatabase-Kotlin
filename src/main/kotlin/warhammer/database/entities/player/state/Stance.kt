package warhammer.database.entities.player.state

import warhammer.database.entities.WarHammerEntity

data class Stance(val stateId: Int,
                  override val id: Int = -1,
                  val reckless: Int? = 0,
                  val maxReckless: Int? = 0,
                  val conservative: Int? = 0,
                  val maxConservative: Int? = 0) : WarHammerEntity {
}