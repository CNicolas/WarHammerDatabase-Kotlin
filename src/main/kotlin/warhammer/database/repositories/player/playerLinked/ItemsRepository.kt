package warhammer.database.repositories.player.playerLinked

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.player.playerLinked.ItemsDao
import warhammer.database.entities.player.playerLinked.item.Item
import warhammer.database.repositories.player.AbstractPlayerLinkedRepository
import warhammer.database.tables.ItemsTable

class ItemsRepository(databaseUrl: String, driver: String) : AbstractPlayerLinkedRepository<Item>(databaseUrl, driver) {
    override val dao = ItemsDao()

    init {
        connectToDatabase()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(ItemsTable)
        }
    }
}