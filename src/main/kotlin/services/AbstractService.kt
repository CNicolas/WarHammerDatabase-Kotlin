package services

import daos.Dao
import entities.WarHammerNamedEntity
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

abstract class AbstractService<E : WarHammerNamedEntity>(private val databaseUrl: String = "jdbc:sqlite:file:warhammer",
                                                         private val driver: String = "org.sqlite.JDBC") : Service<E> {
    abstract val table: IntIdTable
    protected abstract val dao: Dao<E>

    // region CREATE
    override fun add(entity: E): E? {
        connectToDatabase()

        return transaction {
            val addedId = dao.add(entity)
            dao.findById(addedId)
        }
    }

    override fun addAll(entities: List<E>): List<E?> {
        connectToDatabase()

        return transaction {
            dao.addAll(entities)
            dao.findAll()
        }
    }
    // endregion

    // region READ
    override fun findAll(): List<E?> {
        connectToDatabase()

        return transaction { dao.findAll() }
    }

    override fun findById(id: Int): E? {
        connectToDatabase()

        return transaction { dao.findById(id) }
    }

    override fun findByName(name: String): E? {
        connectToDatabase()

        return transaction { dao.findByName(name) }
    }

    override fun countAll(): Int = findAll().size
    // endregion

    // region UPDATE
    override fun update(entity: E): E? {
        connectToDatabase()

        return transaction {
            val updatedId = dao.update(entity)
            dao.findById(updatedId)
        }
    }

    override fun updateAll(entities: List<E>): List<E?> {
        connectToDatabase()

        return transaction {
            dao.updateAll(entities)
            dao.findAll()
        }
    }
    // endregion UPDATE

    // region DELETE
    override fun delete(entity: E): Boolean {
        connectToDatabase()

        return transaction { dao.delete(entity) == 1 }
    }

    override fun deleteAll() {
        connectToDatabase()

        return transaction { dao.deleteAll() }
    }
    // endregion

    protected fun initializeTable() {
        connectToDatabase()

        transaction {
            logger.addLogger(StdOutSqlLogger)

            SchemaUtils.create(table)
        }
    }

    private fun connectToDatabase() {
        Database.connect(url = databaseUrl, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}