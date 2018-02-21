package warhammer.database.repositories.hand

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.hand.HandDao
import warhammer.database.entities.hand.Hand
import warhammer.database.repositories.AbstractNameKeyRepository
import warhammer.database.tables.HandsTable

class HandRepository : AbstractNameKeyRepository<Hand>() {
    override val dao = HandDao()

    init {
        connectToDatabase()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(HandsTable)
        }
    }
}