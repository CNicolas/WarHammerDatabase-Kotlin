package warhammer.database.entities

import warhammer.database.entities.player.PlayerCharacteristics

data class Player(override val name: String,
                  override val id: Int = -1,
                  val characteristics: PlayerCharacteristics = PlayerCharacteristics()) : NamedEntity