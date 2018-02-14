package warhammer.database.tables.player.state

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.player.PlayerStateTable

object StanceTable : IntIdTable() {
    val stateId = reference("state", PlayerStateTable).uniqueIndex()

    val reckless = integer("reckless")
    val maxReckless = integer("maxReckless")
    val conservative = integer("conservative")
    val maxConservative = integer("maxConservative")
}