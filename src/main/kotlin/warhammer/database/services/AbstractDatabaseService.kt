package warhammer.database.services

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.Dao
import warhammer.database.entities.NamedEntity
import java.sql.Connection

abstract class AbstractDatabaseService<E : NamedEntity>(private val databaseUrl: String,
                                                        private val driver: String) : DatabaseService<E> {
    abstract val tables: List<IntIdTable>
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

            tables.forEach {
                create(it)
            }
        }
    }

    protected fun connectToDatabase() {
        Database.connect(url = databaseUrl, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}