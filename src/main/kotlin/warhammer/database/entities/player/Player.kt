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
    // region CHARACTERISTICS

    var strength
        get() = characteristics.strength
        set(value) {
            characteristics.strength = value
        }
    var toughness
        get() = characteristics.toughness
        set(value) {
            characteristics.toughness = value
        }
    var agility
        get() = characteristics.agility
        set(value) {
            characteristics.agility = value
        }
    var intelligence
        get() = characteristics.intelligence
        set(value) {
            characteristics.intelligence = value
        }
    var willpower
        get() = characteristics.willpower
        set(value) {
            characteristics.willpower = value
        }
    var fellowship
        get() = characteristics.fellowship
        set(value) {
            characteristics.fellowship = value
        }

    // endregion

    // region STATE

    var wounds
        get() = state.wounds
        set(value) {
            state.wounds = value
        }
    var maxWounds
        get() = state.maxWounds
        set(value) {
            state.maxWounds = value
        }
    var corruption
        get() = state.corruption
        set(value) {
            state.corruption = value
        }
    var maxCorruption
        get() = state.maxCorruption
        set(value) {
            state.maxCorruption = value
        }
    var stress
        get() = state.stress
        set(value) {
            state.stress = value
        }
    var maxStress
        get() = state.maxStress
        set(value) {
            state.maxStress = value
        }
    var exhaustion
        get() = state.exhaustion
        set(value) {
            state.exhaustion = value
        }
    var maxExhaustion
        get() = state.maxExhaustion
        set(value) {
            state.maxExhaustion = value
        }

    // endregion

    // region CAREER
    val career get() = state.career
    var careerName
        get() = state.careerName
        set(value) {
            state.careerName = value
        }
    var rank
        get() = state.rank
        set(value) {
            state.rank = value
        }
    var availableExperience
        get() = state.availableExperience
        set(value) {
            state.availableExperience = value
        }
    var totalExperience
        get() = state.totalExperience
        set(value) {
            state.totalExperience = value
        }

    // endregion

    // region STANCE

    val stance get() = state.stance
    var reckless
        get() = state.reckless
        set(value) {
            state.reckless = value
        }
    var maxReckless
        get() = state.maxReckless
        set(value) {
            state.maxReckless = value
        }
    var conservative
        get() = state.conservative
        set(value) {
            state.conservative = value
        }
    var maxConservative
        get() = state.maxConservative
        set(value) {
            state.maxConservative = value
        }

    // endregion

    // region INVENTORY

    var encumbrance
        get() = inventory.encumbrance
        set(value) {
            inventory.encumbrance = value
        }
    var maxEncumbrance
        get() = inventory.maxEncumbrance
        set(value) {
            inventory.maxEncumbrance = value
        }
    val money get() = inventory.money
    val items get() = inventory.items

    // endregion
}