package warhammer.database.tables.player.state

import org.jetbrains.exposed.dao.IntIdTable
import warhammer.database.tables.player.PlayerStateTable

object StanceTable : IntIdTable() {
    val stateId = reference("state", PlayerStateTable).uniqueIndex()

    val reckless = integer("reckless").nullable()
    val maxReckless = integer("maxReckless").nullable()

    val conservative = integer("conservative").nullable()
    val maxConservative = integer("maxConservative").nullable()
}