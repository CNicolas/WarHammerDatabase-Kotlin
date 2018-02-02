package warhammer.database.services

import warhammer.database.daos.PlayersDao
import warhammer.database.entities.PlayerEntity
import warhammer.database.entities.tables.Players

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<PlayerEntity>(databaseUrl, driver) {
    override val table = Players
    override val dao = PlayersDao()

    init {
        initializeTable()
    }
}