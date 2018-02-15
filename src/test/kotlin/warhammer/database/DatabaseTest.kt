package warhammer.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.testng.annotations.Test
import java.sql.Connection

class DatabaseTest {
    @Test
    fun should_connect_sqlite() {
        Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}