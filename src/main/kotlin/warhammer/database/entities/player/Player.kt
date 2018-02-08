package warhammer.database.entities.player

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.characteristics.PlayerCharacteristics
import warhammer.database.entities.player.other.Race

data class Player(override val name: String,
                  val race: Race = Race.HUMAN,
                  val age: Int? = null,
                  val size: Int? = null,
                  val characteristics: PlayerCharacteristics = PlayerCharacteristics(),
                  override val id: Int = -1) : NamedEntity