package warhammer.database.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

abstract class AbstractRepository {
    var databaseUrl: String = "jdbc:sqlite:file:warhammer"
    var driver: String = "org.sqlite.JDBC"

    protected fun connectToDatabase() {
        Database.connect(url = databaseUrl, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}