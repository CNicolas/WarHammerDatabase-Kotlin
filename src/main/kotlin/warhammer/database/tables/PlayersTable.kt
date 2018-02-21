package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object PlayersTable : IntIdTable() {
    val name = varchar("name", length = 50).primaryKey()

    val race = varchar("race", length = 20)
    val age = integer("age").nullable()
    val size = integer("size").nullable()

    // region CHARACTERISTICS

    val strength = integer("strength")
    val toughness = integer("toughness")
    val agility = integer("agility")
    val intelligence = integer("intelligence")
    val willpower = integer("willpower")
    val fellowship = integer("fellowship")

    val strengthFortune = integer("strengthFortune")
    val toughnessFortune = integer("toughnessFortune")
    val agilityFortune = integer("agilityFortune")
    val intelligenceFortune = integer("intelligenceFortune")
    val willpowerFortune = integer("willpowerFortune")
    val fellowshipFortune = integer("fellowshipFortune")

    // endregion

    // region STATE

    val wounds = integer("wounds")
    val maxWounds = integer("maxWounds")
    val corruption = integer("corruption")
    val maxCorruption = integer("maxCorruption")
    val stress = integer("stress")
    val exhaustion = integer("exhaustion")

    val careerName = varchar("name", length = 70).nullable()
    val rank = integer("rank")
    val availableExperience = integer("availableExperience")
    val totalExperience = integer("totalExperience")

    val reckless = integer("reckless")
    val maxReckless = integer("maxReckless")
    val conservative = integer("conservative")
    val maxConservative = integer("maxConservative")

    // endregion

    // region INVENTORY

    val encumbrance = integer("encumbrance")
    val brass = integer("brass")
    val silver = integer("silver")
    val gold = integer("gold")

    // endregion
}