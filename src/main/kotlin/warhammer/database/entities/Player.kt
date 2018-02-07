package warhammer.database.entities

import warhammer.database.entities.characteristics.PlayerCharacteristics

data class Player(override val name: String,
                  val characteristics: PlayerCharacteristics = PlayerCharacteristics(),
                  override val id: Int = -1) : NamedEntity