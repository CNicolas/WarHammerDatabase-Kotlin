package warhammer.database.entities.player

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.enums.Race
import warhammer.database.entities.player.playerLinked.item.Item
import warhammer.database.entities.player.playerLinked.skill.Skill

data class Player(override var name: String,

                  var race: Race = Race.HUMAN,
                  var age: Int? = null,
                  var size: Int? = null,

                  var strength: CharacteristicValue = CharacteristicValue(0),
                  var toughness: CharacteristicValue = CharacteristicValue(0),
                  var agility: CharacteristicValue = CharacteristicValue(0),
                  var intelligence: CharacteristicValue = CharacteristicValue(0),
                  var willpower: CharacteristicValue = CharacteristicValue(0),
                  var fellowship: CharacteristicValue = CharacteristicValue(0),

                  var careerName: String? = null,
                  var rank: Int = 0,
                  var availableExperience: Int = 0,
                  var totalExperience: Int = 0,

                  var reckless: Int = 0,
                  var maxReckless: Int = 0,
                  var conservative: Int = 0,
                  var maxConservative: Int = 0,

                  var wounds: Int = 0,
                  var maxWounds: Int = 0,
                  var corruption: Int = 0,
                  var maxCorruption: Int = 0,
                  var stress: Int = 0,
                  var exhaustion: Int = 0,

                  var encumbrance: Int = 0,

                  var brass: Int = 0,
                  var silver: Int = 0,
                  var gold: Int = 0,
                  var items: List<Item> = listOf(),
                  var skills: List<Skill> = listOf(),

                  override val id: Int = -1) : NamedEntity