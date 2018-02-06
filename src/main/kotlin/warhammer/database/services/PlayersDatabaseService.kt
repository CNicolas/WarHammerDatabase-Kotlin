package warhammer.database.services

import warhammer.database.daos.PlayersDao
import warhammer.database.entities.Player
import warhammer.database.tables.PlayersTable

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<Player>(databaseUrl, driver) {
    override val table = PlayersTable
    override val dao = PlayersDao()

    init {
        initializeTable()
    }
}