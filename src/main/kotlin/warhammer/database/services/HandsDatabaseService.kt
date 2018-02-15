package warhammer.database.services

import warhammer.database.daos.HandsDao
import warhammer.database.entities.Hand
import warhammer.database.tables.HandsTable

class HandsDatabaseService(databaseUrl: String = "jdbc:sqlite:file:warhammer", driver: String = "org.sqlite.JDBC")
    : AbstractDatabaseNamedService<Hand>(databaseUrl, driver) {

    override val tables = listOf(HandsTable)
    override val dao = HandsDao()

    init {
        initializeTable()
    }
}