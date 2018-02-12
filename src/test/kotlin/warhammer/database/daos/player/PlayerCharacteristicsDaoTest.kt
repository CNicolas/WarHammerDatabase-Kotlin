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
import warhammer.database.entities.player.PlayerCharacteristics
import warhammer.database.tables.player.PlayerCharacteristicsTable
import warhammer.database.tables.PlayersTable
import java.sql.Connection

class PlayerCharacteristicsDaoTest {
    private val playerCharacteristicsDao = PlayerCharacteristicsDao()

    @BeforeMethod
    fun createPlayers() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable, PlayerCharacteristicsTable)

            playersDao.add(Player("PlayerName1", id = 1))
            playersDao.add(Player("PlayerName2", id = 2))
            playersDao.add(Player("PlayerName3", id = 3))

            PlayerCharacteristicsTable.deleteAll()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_playerCharacteristic() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            playerCharacteristicsDao.add(PlayerCharacteristics(1))

            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_playerCharacteristics() {
        val playerCharacteristicsToAdd = listOf(
                PlayerCharacteristics(1),
                PlayerCharacteristics(2),
                PlayerCharacteristics(3))

        transaction {
            logger.addLogger(StdOutSqlLogger)

            playerCharacteristicsDao.addAll(playerCharacteristicsToAdd)

            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_playerCharacteristic_then_fail_to_add_it_again() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            var resOfInsert = playerCharacteristicsDao.add(PlayerCharacteristics(1))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)

            resOfInsert = playerCharacteristicsDao.add(PlayerCharacteristics(1))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_playerCharacteristic() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            playerCharacteristicsDao.add(PlayerCharacteristics(1, agility = 1))

            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)

            val playerCharacteristic = playerCharacteristicsDao.findById(1)

            assertThat(playerCharacteristic).isNotNull()
            assertThat(playerCharacteristic?.agility).isEqualTo(1)
        }
    }

    @Test
    fun should_read_a_playerCharacteristic_from_playerId() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            playerCharacteristicsDao.add(PlayerCharacteristics(2, agility = 1))

            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)

            val playerCharacteristic = playerCharacteristicsDao.findByPlayerId(2)

            assertThat(playerCharacteristic).isNotNull()
            assertThat(playerCharacteristic?.agility).isEqualTo(1)
        }
    }

    @Test
    fun should_read_all_playerCharacteristics() {
        val playerCharacteristicsToAdd = listOf(
                PlayerCharacteristics(1, intelligence = 3),
                PlayerCharacteristics(2, toughnessFortune = 2),
                PlayerCharacteristics(3, willpower = 4))

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(PlayerCharacteristicsTable)

            playerCharacteristicsDao.addAll(playerCharacteristicsToAdd)

            val allInsertedPlayerCharacteristics = playerCharacteristicsDao.findAll()
            assertThat(allInsertedPlayerCharacteristics.size).isEqualTo(3)

            assertThat(allInsertedPlayerCharacteristics[0]?.playerId).isEqualTo(1)
            assertThat(allInsertedPlayerCharacteristics[0]?.intelligence).isEqualTo(3)

            assertThat(allInsertedPlayerCharacteristics[1]?.playerId).isEqualTo(2)
            assertThat(allInsertedPlayerCharacteristics[1]?.toughnessFortune).isEqualTo(2)

            assertThat(allInsertedPlayerCharacteristics[2]?.playerId).isEqualTo(3)
            assertThat(allInsertedPlayerCharacteristics[2]?.willpower).isEqualTo(4)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_playerCharacteristic() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            // ADD
            val id = playerCharacteristicsDao.add(PlayerCharacteristics(1))
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)

            // FIND
            val playerCharacteristic = playerCharacteristicsDao.findById(id)
            assertThat(playerCharacteristic).isNotNull()
            assertThat(playerCharacteristic?.playerId).isEqualTo(1)
            assertThat(playerCharacteristic?.fellowship).isEqualTo(0)

            // UPDATE
            val playerCharacteristicToUpdate = playerCharacteristic?.copy(fellowship = 1)
            playerCharacteristicsDao.update(playerCharacteristicToUpdate!!)
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newPlayerCharacteristic = playerCharacteristicsDao.findById(id)
            assertThat(newPlayerCharacteristic).isNotNull()
            assertThat(newPlayerCharacteristic?.playerId).isEqualTo(1)
            assertThat(newPlayerCharacteristic?.fellowship).isEqualTo(1)
        }
    }

    @Test
    fun should_update_all_playerCharacteristics() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayerCharacteristicsTable)

            // ADD
            val id1 = playerCharacteristicsDao.add(PlayerCharacteristics(1))
            val id2 = playerCharacteristicsDao.add(PlayerCharacteristics(2))
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = playerCharacteristicsDao.updateAll(
                    listOf(PlayerCharacteristics(1, id1, strength = 2),
                            PlayerCharacteristics(2, id2, strengthFortune = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedPlayerCharacteristics = playerCharacteristicsDao.findAll()
            assertThat(allInsertedPlayerCharacteristics.size).isEqualTo(2)
            assertThat(allInsertedPlayerCharacteristics.map { it?.id }).containsExactly(id1, id2)

            assertThat(allInsertedPlayerCharacteristics[0]?.playerId).isEqualTo(1)
            assertThat(allInsertedPlayerCharacteristics[0]?.strength).isEqualTo(2)

            assertThat(allInsertedPlayerCharacteristics[1]?.playerId).isEqualTo(2)
            assertThat(allInsertedPlayerCharacteristics[1]?.strengthFortune).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_playerCharacteristic() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayerCharacteristicsTable)

            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(0)

            val res = playerCharacteristicsDao.update(PlayerCharacteristics(1))
            assertThat(res).isEqualTo(-1)
            assertThat(playerCharacteristicsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(PlayerCharacteristicsTable)

            val res = playerCharacteristicsDao.update(PlayerCharacteristics(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_playerCharacteristic() {
        val playerCharacteristic1 = PlayerCharacteristics(1)
        val playerCharacteristic2 = PlayerCharacteristics(2)
        val playerCharacteristic3 = PlayerCharacteristics(3)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayerCharacteristicsTable)

            val addAllResult = playerCharacteristicsDao.addAll(listOf(playerCharacteristic1, playerCharacteristic2, playerCharacteristic3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = playerCharacteristicsDao.delete(playerCharacteristic2)
            assertThat(res).isEqualTo(1)
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(2)
            assertThat(playerCharacteristicsDao.findByPlayerId(1)).isNotNull()
            assertThat(playerCharacteristicsDao.findByPlayerId(2)).isNull()
            assertThat(playerCharacteristicsDao.findByPlayerId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_playerCharacteristic_from_playerId() {
        val playerCharacteristic = PlayerCharacteristics(2)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayerCharacteristicsTable)

            val addedCharacteristics = playerCharacteristicsDao.add(playerCharacteristic)
            assertThat(addedCharacteristics).isEqualTo(1)
            assertThat(playerCharacteristicsDao.findByPlayerId(2)).isNotNull()

            val res = playerCharacteristicsDao.deleteByPlayerId(2)
            assertThat(res).isEqualTo(1)
            assertThat(playerCharacteristicsDao.findByPlayerId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_playerCharacteristics() {
        val playerCharacteristic1 = PlayerCharacteristics(1)
        val playerCharacteristic2 = PlayerCharacteristics(2)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayerCharacteristicsTable)

            playerCharacteristicsDao.add(playerCharacteristic1)
            playerCharacteristicsDao.add(playerCharacteristic2)
            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(2)

            playerCharacteristicsDao.deleteAll()
            assertThat(playerCharacteristicsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_playerCharacteristic() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayerCharacteristicsTable)

            assertThat(playerCharacteristicsDao.findAll().size).isEqualTo(0)

            val res = playerCharacteristicsDao.delete(PlayerCharacteristics(1, 6))
            assertThat(res).isEqualTo(0)
            assertThat(playerCharacteristicsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(PlayerCharacteristicsTable)

            val res = playerCharacteristicsDao.delete(PlayerCharacteristics(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_player_id_but_inexistant_table() {
        transaction {
            drop(PlayerCharacteristicsTable)

            val res = playerCharacteristicsDao.deleteByPlayerId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}