package services

import daos.HandsDao
import entities.HandEntity
import entities.tables.Hands
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class HandsService(private val databaseUrl: String = "jdbc:sqlite:file:warhammer",
                   private val driver: String = "org.sqlite.JDBC") {
    private val handsDao: HandsDao = HandsDao()

    init {
        connectToDatabase()

        transaction { create(Hands) }
    }

    fun add(hand: HandEntity): Int {
        connectToDatabase()

        return transaction {
            handsDao.add(hand)
        }
    }

    fun findByName(name: String): HandEntity? {
        connectToDatabase()

        return transaction {
            handsDao.findByName(name)
        }
    }

    private fun connectToDatabase() {
        Database.connect(url = databaseUrl, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}