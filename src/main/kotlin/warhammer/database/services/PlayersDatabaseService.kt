package warhammer.database.services

import warhammer.database.daos.PlayersDao
import warhammer.database.entities.player.Player
import warhammer.database.tables.PlayerCharacteristicsTable
import warhammer.database.tables.PlayersTable

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseNamedService<Player>(databaseUrl, driver) {
    override val tables = listOf(PlayersTable, PlayerCharacteristicsTable)
    override val dao = PlayersDao()

    init {
        initializeTable()
    }
}