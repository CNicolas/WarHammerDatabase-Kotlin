package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object PlayerCharacteristicsTable : IntIdTable() {
    val playerId = reference("player", PlayersTable).uniqueIndex()

    val strength = integer("strength").nullable()
    val toughness = integer("toughness").nullable()
    val agility = integer("agility").nullable()
    val intelligence = integer("intelligence").nullable()
    val willpower = integer("willpower").nullable()
    val fellowship = integer("fellowship").nullable()

    val strengthFortune = integer("strengthFortune").nullable()
    val toughnessFortune = integer("toughnessFortune").nullable()
    val agilityFortune = integer("agilityFortune").nullable()
    val intelligenceFortune = integer("intelligenceFortune").nullable()
    val willpowerFortune = integer("willpowerFortune").nullable()
    val fellowshipFortune = integer("fellowshipFortune").nullable()
}