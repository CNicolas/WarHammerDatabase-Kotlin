package warhammer.database.repositories.player.item

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.player.item.ItemDao
import warhammer.database.entities.player.item.Item
import warhammer.database.repositories.player.AbstractPlayerLinkedRepository
import warhammer.database.tables.ItemsTable

class ItemRepository : AbstractPlayerLinkedRepository<Item>() {
    override val dao = ItemDao()

    init {
        connectToDatabase()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(ItemsTable)
        }
    }
}