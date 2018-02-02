package warhammer.database.services

import warhammer.database.daos.HandsDao
import warhammer.database.entities.HandEntity
import warhammer.database.entities.tables.Hands

class HandsDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<HandEntity>(databaseUrl, driver) {
    override val table = Hands
    override val dao = HandsDao()

    init {
        initializeTable()
    }
}