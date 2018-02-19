package warhammer.database.entities.player

import warhammer.database.entities.WarHammerEntity

data class PlayerCharacteristicsEntity(override val id: Int = -1,
                                       val playerId: Int,
                                       var strength: Int = 0,
                                       var toughness: Int = 0,
                                       var agility: Int = 0,
                                       var intelligence: Int = 0,
                                       var willpower: Int = 0,
                                       var fellowship: Int = 0,
                                       var strengthFortune: Int = 0,
                                       var toughnessFortune: Int = 0,
                                       var agilityFortune: Int = 0,
                                       var intelligenceFortune: Int = 0,
                                       var willpowerFortune: Int = 0,
                                       var fellowshipFortune: Int = 0) : WarHammerEntity