package services

import daos.PlayersDao
import entities.PlayerEntity
import entities.tables.Players

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<PlayerEntity>(databaseUrl, driver) {
    override val table = Players
    override val dao = PlayersDao()

    init {
        initializeTable()
    }
}