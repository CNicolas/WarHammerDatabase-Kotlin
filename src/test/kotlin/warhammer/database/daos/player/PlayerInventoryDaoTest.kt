package warhammer.database.daos.player

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.daos.PlayersDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerInventory
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerInventoryTable
import java.sql.Connection

class PlayerInventoryDaoTest {
    private val playerInventoryDao = PlayerInventoryDao()

    @BeforeMethod
    fun createPlayers() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable, PlayerInventoryTable)

            playersDao.add(Player(id = 1, name = "PlayerName1"))
            playersDao.add(Player(id = 2, name = "PlayerName2"))
            playersDao.add(Player(id = 3, name = "PlayerName3"))

            PlayerInventoryTable.deleteAll()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_playerInventory() {
        transaction {
            playerInventoryDao.add(PlayerInventory(playerId = 1))

            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_playerInventory() {
        val playerInventoryToAdd = listOf(
                PlayerInventory(playerId = 1),
                PlayerInventory(playerId = 2),
                PlayerInventory(playerId = 3))

        transaction {
            playerInventoryDao.addAll(playerInventoryToAdd)

            assertThat(playerInventoryDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_playerInventory_then_fail_to_add_it_again() {
        transaction {
            var resOfInsert = playerInventoryDao.add(PlayerInventory(1))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)

            resOfInsert = playerInventoryDao.add(PlayerInventory(1))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_playerInventory() {
        transaction {
            playerInventoryDao.add(PlayerInventory(1, maxEncumbrance = 1))

            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)

            val playerInventory = playerInventoryDao.findById(1)

            assertThat(playerInventory).isNotNull()
            assertThat(playerInventory?.maxEncumbrance).isEqualTo(1)
        }
    }

    @Test
    fun should_read_a_playerInventory_from_playerId() {
        transaction {
            playerInventoryDao.add(PlayerInventory(playerId = 2, encumbrance = 1))

            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)

            val playerInventory = playerInventoryDao.findByPlayerId(2)

            assertThat(playerInventory).isNotNull()
            assertThat(playerInventory?.encumbrance).isEqualTo(1)
        }
    }

    @Test
    fun should_read_all_playerInventory() {
        val playerInventoryToAdd = listOf(
                PlayerInventory(playerId = 1, maxEncumbrance = 3),
                PlayerInventory(playerId = 2, maxEncumbrance = 2),
                PlayerInventory(playerId = 3, encumbrance = 4))

        transaction {
            playerInventoryDao.addAll(playerInventoryToAdd)

            val allInsertedPlayerInventory = playerInventoryDao.findAll()
            assertThat(allInsertedPlayerInventory.size).isEqualTo(3)

            assertThat(allInsertedPlayerInventory[0]?.playerId).isEqualTo(1)
            assertThat(allInsertedPlayerInventory[0]?.maxEncumbrance).isEqualTo(3)

            assertThat(allInsertedPlayerInventory[1]?.playerId).isEqualTo(2)
            assertThat(allInsertedPlayerInventory[1]?.maxEncumbrance).isEqualTo(2)

            assertThat(allInsertedPlayerInventory[2]?.playerId).isEqualTo(3)
            assertThat(allInsertedPlayerInventory[2]?.encumbrance).isEqualTo(4)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_playerInventory() {
        transaction {
            // ADD
            val id = playerInventoryDao.add(PlayerInventory(playerId = 1))
            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)

            // FIND
            val playerInventory = playerInventoryDao.findById(id)
            assertThat(playerInventory).isNotNull()
            assertThat(playerInventory?.playerId).isEqualTo(1)
            assertThat(playerInventory?.maxEncumbrance).isEqualTo(0)

            // UPDATE
            val playerInventoryToUpdate = playerInventory?.copy(maxEncumbrance = 1)
            playerInventoryDao.update(playerInventoryToUpdate!!)
            assertThat(playerInventoryDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newPlayerInventory = playerInventoryDao.findById(id)
            assertThat(newPlayerInventory).isNotNull()
            assertThat(newPlayerInventory?.playerId).isEqualTo(1)
            assertThat(newPlayerInventory?.maxEncumbrance).isEqualTo(1)
        }
    }

    @Test
    fun should_update_all_playerInventory() {
        transaction {
            // ADD
            val id1 = playerInventoryDao.add(PlayerInventory(playerId = 1))
            val id2 = playerInventoryDao.add(PlayerInventory(playerId = 2))
            assertThat(playerInventoryDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = playerInventoryDao.updateAll(
                    listOf(PlayerInventory(1, id1, encumbrance = 2),
                            PlayerInventory(2, id2, maxEncumbrance = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedPlayerInventory = playerInventoryDao.findAll()
            assertThat(allInsertedPlayerInventory.size).isEqualTo(2)
            assertThat(allInsertedPlayerInventory.map { it?.id }).containsExactly(id1, id2)

            assertThat(allInsertedPlayerInventory[0]?.playerId).isEqualTo(1)
            assertThat(allInsertedPlayerInventory[0]?.encumbrance).isEqualTo(2)

            assertThat(allInsertedPlayerInventory[1]?.playerId).isEqualTo(2)
            assertThat(allInsertedPlayerInventory[1]?.maxEncumbrance).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_playerInventory() {
        transaction {
            assertThat(playerInventoryDao.findAll().size).isEqualTo(0)

            val res = playerInventoryDao.update(PlayerInventory(playerId = 1))
            assertThat(res).isEqualTo(-1)
            assertThat(playerInventoryDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        transaction {
            drop(PlayerInventoryTable)

            val res = playerInventoryDao.update(PlayerInventory(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_playerInventory() {
        val playerInventory1 = PlayerInventory(playerId = 1)
        val playerInventory2 = PlayerInventory(playerId = 2)
        val playerInventory3 = PlayerInventory(playerId = 3)

        transaction {
            val addAllResult = playerInventoryDao.addAll(listOf(playerInventory1, playerInventory2, playerInventory3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = playerInventoryDao.delete(playerInventory2)
            assertThat(res).isEqualTo(1)
            assertThat(playerInventoryDao.findAll().size).isEqualTo(2)
            assertThat(playerInventoryDao.findByPlayerId(1)).isNotNull()
            assertThat(playerInventoryDao.findByPlayerId(2)).isNull()
            assertThat(playerInventoryDao.findByPlayerId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_playerInventory_from_playerId() {
        val playerInventory = PlayerInventory(playerId = 2)

        transaction {
            val addedInventorys = playerInventoryDao.add(playerInventory)
            assertThat(addedInventorys).isEqualTo(1)
            assertThat(playerInventoryDao.findByPlayerId(2)).isNotNull()

            val res = playerInventoryDao.deleteByPlayerId(2)
            assertThat(res).isEqualTo(1)
            assertThat(playerInventoryDao.findByPlayerId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_playerInventory() {
        val playerInventory1 = PlayerInventory(playerId = 1)
        val playerInventory2 = PlayerInventory(playerId = 2)

        transaction {
            playerInventoryDao.add(playerInventory1)
            playerInventoryDao.add(playerInventory2)
            assertThat(playerInventoryDao.findAll().size).isEqualTo(2)

            playerInventoryDao.deleteAll()
            assertThat(playerInventoryDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_playerInventory() {
        transaction {
            assertThat(playerInventoryDao.findAll().size).isEqualTo(0)

            val res = playerInventoryDao.delete(PlayerInventory(1, 6))
            assertThat(res).isEqualTo(0)
            assertThat(playerInventoryDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        transaction {
            drop(PlayerInventoryTable)

            val res = playerInventoryDao.delete(PlayerInventory(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_player_id_but_inexistant_table() {
        transaction {
            drop(PlayerInventoryTable)

            val res = playerInventoryDao.deleteByPlayerId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}