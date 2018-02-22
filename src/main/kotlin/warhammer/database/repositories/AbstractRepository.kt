package warhammer.database.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

abstract class AbstractRepository<out E>(private val databaseUrl: String, private val driver: String) : Repository<E> {
    override fun findById(id: Int): E? {
        connectToDatabase()

        return transaction { dao.findById(id) }
    }

    protected fun connectToDatabase() {
        Database.connect(url = databaseUrl, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}