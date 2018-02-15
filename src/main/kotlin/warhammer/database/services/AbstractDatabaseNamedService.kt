package warhammer.database.services

import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.NamedDao
import warhammer.database.entities.NamedEntity

abstract class AbstractDatabaseNamedService<E : NamedEntity>(databaseUrl: String, driver: String)
    : AbstractDatabaseService<E>(databaseUrl, driver), DatabaseNamedService<E> {

    abstract override val dao: NamedDao<E>

    override fun findByName(name: String): E? {
        connectToDatabase()

        return transaction { dao.findByName(name) }
    }
}