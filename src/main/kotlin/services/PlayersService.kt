package services

import daos.PlayersDao
import entities.PlayerEntity
import entities.tables.Players

class PlayersService(databaseUrl: String, driver: String) : AbstractService<PlayerEntity>(databaseUrl, driver) {
    override val table = Players
    override val dao = PlayersDao()

    init {
        initializeTable()
    }
}