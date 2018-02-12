package warhammer.database.services

import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.player.PlayerCharacteristicsDao
import warhammer.database.daos.PlayersDao
import warhammer.database.entities.mapping.mapToEntity
import warhammer.database.entities.mapping.mapToPlayerCharacteristics
import warhammer.database.entities.player.Player
import warhammer.database.tables.PlayerCharacteristicsTable
import warhammer.database.tables.PlayersTable

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseNamedService<Player>(databaseUrl, driver) {
    override val tables = listOf(PlayersTable, PlayerCharacteristicsTable)
    override val dao = PlayersDao()
    private val playerCharacteristicsDao = PlayerCharacteristicsDao()

    init {
        initializeTable()
    }

    // region CREATE
    override fun add(entity: Player): Player? {
        connectToDatabase()

        return transaction {
            addInsideTransaction(entity)
        }
    }

    override fun addAll(entities: List<Player>): List<Player?> {
        connectToDatabase()

        return transaction {
            entities.map { addInsideTransaction(it) }
        }
    }

    private fun addInsideTransaction(entity: Player): Player? {
        val playerId = dao.add(entity)
        playerCharacteristicsDao.add(entity.characteristics.mapToEntity(playerId))

        return findByIdInsideTransaction(playerId)
    }
    // endregion

    // region READ

    override fun findById(id: Int): Player? {
        connectToDatabase()

        return transaction { findByIdInsideTransaction(id) }
    }

    override fun findByName(name: String): Player? {
        connectToDatabase()

        return transaction {
            val player = dao.findByName(name)
            when {
                player != null -> {
                    val playerCharacteristicsEntity = playerCharacteristicsDao.findByPlayerId(player.id)

                    player.copy(characteristics = playerCharacteristicsEntity.mapToPlayerCharacteristics())
                }
                else -> null
            }
        }
    }

    override fun findAll(): List<Player?> {
        connectToDatabase()

        return transaction {
            val entities = dao.findAll()
            entities.map {
                findByIdInsideTransaction(it?.id!!)
            }
        }
    }

    override fun countAll(): Int = findAll().size

    private fun findByIdInsideTransaction(playerId: Int): Player? {
        val playerCharacteristicsEntity = playerCharacteristicsDao.findByPlayerId(playerId)
        val player = dao.findById(playerId)

        return player?.copy(characteristics = playerCharacteristicsEntity.mapToPlayerCharacteristics())
    }
    // endregion

    // region UPDATE
    override fun update(entity: Player): Player? {
        connectToDatabase()

        return transaction {
            val updatedPlayerId = dao.update(entity)
            playerCharacteristicsDao.update(entity.characteristics.mapToEntity(updatedPlayerId))

            findByIdInsideTransaction(updatedPlayerId)
        }
    }

    override fun updateAll(entities: List<Player>): List<Player?> {
        connectToDatabase()

        return transaction {
            val entitiesId = dao.updateAll(entities)
            entitiesId.map {
                findByIdInsideTransaction(it)
            }
        }
    }
    // endregion UPDATE

    // region DELETE
    override fun delete(entity: Player): Boolean {
        connectToDatabase()

        return transaction {
            val characteristicsDeleted = playerCharacteristicsDao.deleteByPlayerId(entity.id) == 1
            val playerDeleted = dao.delete(entity) == 1
            playerDeleted && characteristicsDeleted
        }
    }

    override fun deleteAll() {
        connectToDatabase()

        return transaction {
            playerCharacteristicsDao.deleteAll()
            dao.deleteAll()
        }
    }
    // endregion
}