package warhammer.database.services

import warhammer.database.daos.PlayersDao
import warhammer.database.entities.Player
import warhammer.database.tables.Players

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<Player>(databaseUrl, driver) {
    override val table = Players
    override val dao = PlayersDao()

    init {
        initializeTable()
    }
}