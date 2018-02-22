package warhammer.database.repositories

import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.entities.NamedEntity

abstract class AbstractNameKeyRepository<E : NamedEntity>(databaseUrl: String = "jdbc:sqlite:file:warhammer",
                                                          driver: String = "org.sqlite.JDBC")
    : AbstractRepository<E>(databaseUrl, driver), NameKeyRepository<E> {
    override fun add(entity: E): E? {
        connectToDatabase()

        return transaction { dao.add(entity) }
    }

    override fun findByName(name: String): E? {
        connectToDatabase()

        return transaction { dao.findByName(name) }
    }

    override fun findAll(): List<E> {
        connectToDatabase()

        return transaction { dao.findAll() }
    }


    override fun update(entity: E): E? {
        connectToDatabase()

        return transaction { dao.update(entity) }
    }

    override fun delete(entity: E): Boolean {
        connectToDatabase()

        return transaction { dao.delete(entity) == 1 }
    }

    override fun deleteByName(name: String): Int {
        connectToDatabase()

        return transaction { dao.deleteByName(name) }
    }

    override fun deleteAll() {
        connectToDatabase()

        return transaction { dao.deleteAll() }
    }
}