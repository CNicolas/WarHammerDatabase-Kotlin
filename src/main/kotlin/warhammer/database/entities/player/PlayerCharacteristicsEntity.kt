package warhammer.database.entities.player

import warhammer.database.entities.WarHammerEntity

data class PlayerCharacteristicsEntity(val playerId: Int,
                                  override val id: Int = -1,
                                  val strength: Int? = 0,
                                  val toughness: Int? = 0,
                                  val agility: Int? = 0,
                                  val intelligence: Int? = 0,
                                  val willpower: Int? = 0,
                                  val fellowship: Int? = 0,
                                  val strengthFortune: Int? = 0,
                                  val toughnessFortune: Int? = 0,
                                  val agilityFortune: Int? = 0,
                                  val intelligenceFortune: Int? = 0,
                                  val willpowerFortune: Int? = 0,
                                  val fellowshipFortune: Int? = 0) : WarHammerEntity {

}