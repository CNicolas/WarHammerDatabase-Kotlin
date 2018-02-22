package warhammer.database.repositories.player

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.player.PlayerDao
import warhammer.database.entities.player.Player
import warhammer.database.repositories.AbstractNameKeyRepository
import warhammer.database.tables.PlayersTable

class PlayerRepository(databaseUrl: String, driver: String) : AbstractNameKeyRepository<Player>(databaseUrl, driver) {
    override val dao = PlayerDao()

    init {
        connectToDatabase()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable)
        }
    }
}