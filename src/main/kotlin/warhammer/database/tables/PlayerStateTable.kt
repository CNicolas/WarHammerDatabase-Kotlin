package warhammer.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object PlayerStateTable : IntIdTable() {
    val playerId = reference("state", PlayersTable).uniqueIndex()

    val wounds = integer("wounds").nullable()
    val maxWounds = integer("maxWounds").nullable()

    val corruption = integer("corruption").nullable()
    val maxCorruption = integer("maxCorruption").nullable()

    val stress = integer("stress").nullable()
    val maxStress = integer("maxStress").nullable()

    val exhaustion = integer("exhaustion").nullable()
    val maxExhaustion = integer("maxExhaustion").nullable()
}