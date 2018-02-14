package warhammer.database.tables.player

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.PlayersTable

object PlayerCharacteristicsTable : IntIdTable() {
    val playerId = reference("characteristics", PlayersTable).uniqueIndex()

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
}