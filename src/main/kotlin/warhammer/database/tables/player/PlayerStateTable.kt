package warhammer.database.tables.player

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.PlayersTable

object PlayerStateTable : IntIdTable() {
    val playerId = reference("state", PlayersTable).uniqueIndex()

    val wounds = integer("wounds")
    val maxWounds = integer("maxWounds")

    val corruption = integer("corruption")
    val maxCorruption = integer("maxCorruption")

    val stress = integer("stress")
    val maxStress = integer("maxStress")

    val exhaustion = integer("exhaustion")
    val maxExhaustion = integer("maxExhaustion")
}