package warhammer.database.entities.player

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.characteristics.Characteristic.*
import warhammer.database.entities.player.characteristics.PlayerCharacteristics
import warhammer.database.entities.player.other.Race

data class Player(override val id: Int = -1,
                  override val name: String,
                  val race: Race = Race.HUMAN,
                  val age: Int? = null,
                  val size: Int? = null,
                  val characteristics: PlayerCharacteristics = PlayerCharacteristics(),
                  val state: PlayerState = PlayerState(playerId = id),
                  val inventory: PlayerInventory = PlayerInventory(playerId = id)) : NamedEntity {
    val strength = characteristics[STRENGTH]
    val toughness = characteristics[TOUGHNESS]
    val agility = characteristics[AGILITY]
    val intelligence = characteristics[INTELLIGENCE]
    val willpower = characteristics[WILLPOWER]
    val fellowship = characteristics[FELLOWSHIP]

    val wounds = state.wounds
    val maxWounds = state.maxWounds
    val corruption = state.corruption
    val maxCorruption = state.maxCorruption
    val stress = state.stress
    val maxStress = state.maxStress
    val exhaustion = state.exhaustion
    val maxExhaustion = state.maxExhaustion

    val careerName = state.careerName
    val rank = state.rank
    val availableExperience = state.availableExperience
    val totalExperience = state.totalExperience

    val reckless = state.reckless
    val maxReckless = state.maxReckless
    val conservative = state.conservative
    val maxConservative = state.maxConservative

    val encumbrance = inventory.encumbrance
    val maxEncumbrance = inventory.maxEncumbrance
    val money = inventory.money
    val items = inventory.items
}