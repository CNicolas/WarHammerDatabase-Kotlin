package warhammer.database.services

import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.PlayersDao
import warhammer.database.daos.player.PlayerCharacteristicsDao
import warhammer.database.daos.player.PlayerStateDao
import warhammer.database.daos.player.state.CareerDao
import warhammer.database.daos.player.state.StanceDao
import warhammer.database.entities.mapping.mapToEntity
import warhammer.database.entities.mapping.mapToPlayerCharacteristics
import warhammer.database.entities.player.Player
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerCharacteristicsTable
import warhammer.database.tables.player.PlayerStateTable
import warhammer.database.tables.player.state.CareerTable
import warhammer.database.tables.player.state.StanceTable

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseNamedService<Player>(databaseUrl, driver) {
    override val tables = listOf(PlayersTable, PlayerCharacteristicsTable, PlayerStateTable, CareerTable, StanceTable)
    override val dao = PlayersDao()
    private val playerCharacteristicsDao = PlayerCharacteristicsDao()
    private val playerStateDao = PlayerStateDao()
    private val careerDao = CareerDao()
    private val stanceDao = StanceDao()

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
            entities.map {
                addInsideTransaction(it)
            }
        }
    }

    private fun addInsideTransaction(entity: Player): Player? {
        val playerId = dao.add(entity)

        val stateId = playerStateDao.add(entity.state.copy(playerId = playerId))
        careerDao.add(entity.state.career.copy(stateId = stateId))
        stanceDao.add(entity.state.stance.copy(stateId = stateId))

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
                    val playerCharacteristics = playerCharacteristicsDao.findByPlayerId(player.id)

                    val playerState = playerStateDao.findByPlayerId(player.id)
                    val career = careerDao.findByStateId(playerState?.id!!)
                    val stance = stanceDao.findByStateId(playerState.id)

                    player.copy(
                            characteristics = playerCharacteristics.mapToPlayerCharacteristics(),
                            state = playerState.copy(career = career!!, stance = stance!!)
                    )
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
        val playerCharacteristics = playerCharacteristicsDao.findByPlayerId(playerId)

        val playerState = playerStateDao.findByPlayerId(playerId)

        return when {
            playerState != null -> {
                val career = careerDao.findByStateId(playerState.id)
                val stance = stanceDao.findByStateId(playerState.id)

                val player = dao.findById(playerId)

                player?.copy(
                        characteristics = playerCharacteristics.mapToPlayerCharacteristics(),
                        state = playerState.copy(career = career!!, stance = stance!!)
                )
            }
            else -> null
        }
    }
    // endregion

    // region UPDATE
    override fun update(entity: Player): Player? {
        connectToDatabase()

        return transaction {
            val updatedPlayerId = dao.update(entity)

            if (updatedPlayerId != -1) {
                val stateId = playerStateDao.update(entity.state.copy(playerId = updatedPlayerId))

                careerDao.update(entity.state.career.copy(stateId = stateId))
                stanceDao.update(entity.state.stance.copy(stateId = stateId))

                playerCharacteristicsDao.update(entity.characteristics.mapToEntity(updatedPlayerId))

                findByIdInsideTransaction(updatedPlayerId)
            } else {
                null
            }
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
            val careerDeleted = careerDao.deleteByStateId(entity.state.id) == 1
            val stanceDeleted = stanceDao.deleteByStateId(entity.state.id) == 1
            val stateDeleted = playerStateDao.deleteByPlayerId(entity.id) == 1

            val characteristicsDeleted = playerCharacteristicsDao.deleteByPlayerId(entity.id) == 1

            val playerDeleted = dao.delete(entity) == 1

            playerDeleted && characteristicsDeleted && careerDeleted && stanceDeleted && stateDeleted
        }
    }

    override fun deleteAll() {
        connectToDatabase()

        return transaction {
            careerDao.deleteAll()
            stanceDao.deleteAll()
            playerStateDao.deleteAll()

            playerCharacteristicsDao.deleteAll()

            dao.deleteAll()
        }
    }
    // endregion
}