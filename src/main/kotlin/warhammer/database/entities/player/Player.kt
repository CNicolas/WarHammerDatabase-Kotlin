package warhammer.database.entities.player

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.characteristics.PlayerCharacteristics
import warhammer.database.entities.player.other.Race

data class Player(override val id: Int = -1,
                  override var name: String,
                  var race: Race = Race.HUMAN,
                  var age: Int? = null,
                  var size: Int? = null,
                  val characteristics: PlayerCharacteristics = PlayerCharacteristics(),
                  val state: PlayerState = PlayerState(playerId = id),
                  val inventory: PlayerInventory = PlayerInventory(playerId = id)) : NamedEntity {
    val strength get() = characteristics.strength
    val toughness get() = characteristics.toughness
    val agility get() = characteristics.agility
    val intelligence get() = characteristics.intelligence
    val willpower get() = characteristics.willpower
    val fellowship get() = characteristics.fellowship

    val wounds get() = state.wounds
    val maxWounds get() = state.maxWounds
    val corruption get() = state.corruption
    val maxCorruption get() = state.maxCorruption
    val stress get() = state.stress
    val maxStress get() = state.maxStress
    val exhaustion get() = state.exhaustion
    val maxExhaustion get() = state.maxExhaustion

    val career get() = state.career
    val careerName get() = state.careerName
    val rank get() = state.rank
    val availableExperience get() = state.availableExperience
    val totalExperience get() = state.totalExperience

    val stance get() = state.stance
    val reckless get() = state.reckless
    val maxReckless get() = state.maxReckless
    val conservative get() = state.conservative
    val maxConservative get() = state.maxConservative

    val encumbrance get() = inventory.encumbrance
    val maxEncumbrance get() = inventory.maxEncumbrance
    val money get() = inventory.money
    val items get() = inventory.items
}