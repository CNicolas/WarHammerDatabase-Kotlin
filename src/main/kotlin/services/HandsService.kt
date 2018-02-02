package services

import daos.HandsDao
import entities.HandEntity
import entities.tables.Hands

class HandsService(databaseUrl: String, driver: String) : AbstractService<HandEntity>(databaseUrl, driver) {
    override val table = Hands
    override val dao = HandsDao()

    init {
        initializeTable()
    }
}