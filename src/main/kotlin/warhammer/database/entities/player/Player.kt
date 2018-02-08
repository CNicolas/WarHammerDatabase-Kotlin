package warhammer.database.entities.player

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.characteristics.PlayerCharacteristics

data class Player(override val name: String,
                  val characteristics: PlayerCharacteristics = PlayerCharacteristics(),
                  override val id: Int = -1) : NamedEntity