package services

import daos.HandsDao
import entities.HandEntity
import entities.tables.Hands

class HandsDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseService<HandEntity>(databaseUrl, driver) {
    override val table = Hands
    override val dao = HandsDao()

    init {
        initializeTable()
    }
}