package warhammer.database.services

import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.PlayersDao
import warhammer.database.daos.player.PlayerCharacteristicsDao
import warhammer.database.daos.player.PlayerInventoryDao
import warhammer.database.daos.player.PlayerStateDao
import warhammer.database.daos.player.inventory.ItemsDao
import warhammer.database.daos.player.inventory.MoneyDao
import warhammer.database.daos.player.state.CareerDao
import warhammer.database.daos.player.state.StanceDao
import warhammer.database.entities.mapping.mapToEntity
import warhammer.database.entities.mapping.mapToPlayerCharacteristics
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerInventory
import warhammer.database.entities.player.PlayerState
import warhammer.database.entities.player.inventory.item.Armor
import warhammer.database.entities.player.inventory.item.Expandable
import warhammer.database.entities.player.inventory.item.GenericItem
import warhammer.database.entities.player.inventory.item.Weapon
import warhammer.database.entities.player.inventory.item.enums.ItemType.*
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerCharacteristicsTable
import warhammer.database.tables.player.PlayerInventoryTable
import warhammer.database.tables.player.PlayerStateTable
import warhammer.database.tables.player.inventory.ItemsTable
import warhammer.database.tables.player.inventory.MoneyTable
import warhammer.database.tables.player.state.CareerTable
import warhammer.database.tables.player.state.StanceTable

class PlayersDatabaseService(databaseUrl: String, driver: String) : AbstractDatabaseNamedService<Player>(databaseUrl, driver) {
    override val tables = listOf(
            PlayersTable,
            PlayerCharacteristicsTable,
            PlayerStateTable, CareerTable, StanceTable,
            PlayerInventoryTable, MoneyTable, ItemsTable)
    override val dao = PlayersDao()

    private val playerCharacteristicsDao = PlayerCharacteristicsDao()

    private val playerStateDao = PlayerStateDao()
    private val careerDao = CareerDao()
    private val stanceDao = StanceDao()

    private val playerInventoryDao = PlayerInventoryDao()
    private val moneyDao = MoneyDao()
    private val itemsDao = ItemsDao()

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

        val inventoryId = playerInventoryDao.add(entity.inventory.copy(playerId = playerId))
        moneyDao.add(entity.inventory.money.copy(inventoryId = inventoryId))
        entity.items.forEach {
            when (it.type) {
                ITEM -> itemsDao.add((it as GenericItem).copy(inventoryId = inventoryId))
                ARMOR -> itemsDao.add((it as Armor).copy(inventoryId = inventoryId))
                WEAPON -> itemsDao.add((it as Weapon).copy(inventoryId = inventoryId))
                EXPANDABLE -> itemsDao.add((it as Expandable).copy(inventoryId = inventoryId))
            }
        }

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
                    findByIdInsideTransaction(player.id)
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
        val filledState = when {
            playerState != null -> {
                val career = careerDao.findByStateId(playerState.id)
                val stance = stanceDao.findByStateId(playerState.id)
                playerState.copy(career = career!!, stance = stance!!)
            }
            else -> PlayerState(playerId = playerId)
        }

        val playerInventory = playerInventoryDao.findByPlayerId(playerId)
        val filledInventory = when {
            playerInventory != null -> {
                val money = moneyDao.findByInventoryId(playerInventory.id)
                val items = itemsDao.findAllByInventoryId(playerInventory.id)
                playerInventory.copy(money = money!!, items = items)
            }
            else -> PlayerInventory(playerId = playerId)
        }

        val player = dao.findById(playerId)

        return player?.copy(
                characteristics = playerCharacteristics.mapToPlayerCharacteristics(),
                state = filledState,
                inventory = filledInventory
        )
    }
    // endregion

    // region UPDATE
    override fun update(entity: Player): Player? {
        connectToDatabase()

        return transaction {
            val updatedPlayerId = dao.update(entity)

            if (updatedPlayerId != -1) {
                playerCharacteristicsDao.update(entity.characteristics.mapToEntity(updatedPlayerId))

                val stateId = playerStateDao.update(entity.state.copy(playerId = updatedPlayerId))
                careerDao.update(entity.state.career.copy(stateId = stateId))
                stanceDao.update(entity.state.stance.copy(stateId = stateId))

                val inventoryId = playerInventoryDao.update(entity.inventory.copy(playerId = updatedPlayerId))
                moneyDao.update(entity.money.copy(inventoryId = inventoryId))
                entity.items.forEach {
                    when (it.type) {
                        ITEM -> itemsDao.update((it as GenericItem).copy(inventoryId = inventoryId))
                        ARMOR -> itemsDao.update((it as Armor).copy(inventoryId = inventoryId))
                        WEAPON -> itemsDao.update((it as Weapon).copy(inventoryId = inventoryId))
                        EXPANDABLE -> itemsDao.update((it as Expandable).copy(inventoryId = inventoryId))
                    }
                }

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
            val characteristicsDeleted = playerCharacteristicsDao.deleteByPlayerId(entity.id) == 1

            val careerDeleted = careerDao.deleteByStateId(entity.state.id) == 1
            val stanceDeleted = stanceDao.deleteByStateId(entity.state.id) == 1
            val stateDeleted = playerStateDao.deleteByPlayerId(entity.id) == 1

            val moneyDeleted = moneyDao.deleteByInventoryId(entity.inventory.id) == 1
            val itemsDeleted = itemsDao.deleteByInventoryId(entity.inventory.id) >= 0
            val inventoryDeleted = playerInventoryDao.deleteByPlayerId(entity.id) == 1

            val playerDeleted = dao.delete(entity) == 1

            playerDeleted
                    && characteristicsDeleted
                    && careerDeleted && stanceDeleted && stateDeleted
                    && moneyDeleted && itemsDeleted && inventoryDeleted
        }
    }

    override fun deleteAll() {
        connectToDatabase()

        return transaction {
            playerCharacteristicsDao.deleteAll()

            careerDao.deleteAll()
            stanceDao.deleteAll()
            playerStateDao.deleteAll()

            moneyDao.deleteAll()
            itemsDao.deleteAll()
            playerInventoryDao.deleteAll()

            dao.deleteAll()
        }
    }
    // endregion
}