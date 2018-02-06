package warhammer.database.services

import warhammer.database.daos.HandsDao
import warhammer.database.entities.Hand
import warhammer.database.tables.Hands

class HandsDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<Hand>(databaseUrl, driver) {
    override val table = Hands
    override val dao = HandsDao()

    init {
        initializeTable()
    }
}