package warhammer.database.entities.player

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.characteristics.PlayerCharacteristicsMap
import warhammer.database.entities.player.other.Race

data class Player(override val name: String,
                  override val id: Int = -1,
                  val race: Race = Race.HUMAN,
                  val age: Int? = null,
                  val size: Int? = null,
                  val characteristics: PlayerCharacteristicsMap = PlayerCharacteristicsMap(),
                  val state: PlayerState = PlayerState(id)) : NamedEntity