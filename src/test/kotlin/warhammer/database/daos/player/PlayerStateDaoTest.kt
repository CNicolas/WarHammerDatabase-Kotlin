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
import warhammer.database.entities.player.PlayerState
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerStateTable
import java.sql.Connection

class PlayerStateDaoTest {
    private val playerStateDao = PlayerStateDao()

    @BeforeMethod
    fun createPlayers() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(PlayersTable, PlayerStateTable)

            playersDao.deleteAll()
            PlayerStateTable.deleteAll()

            playersDao.add(Player(id = 1, name = "PlayerName1"))
            playersDao.add(Player(id = 2, name = "PlayerName2"))
            playersDao.add(Player(id = 3, name = "PlayerName3"))
        }
    }

    // region CREATE
    @Test
    fun should_add_a_playerState() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            playerStateDao.add(PlayerState(playerId = 1))

            assertThat(playerStateDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_playerState() {
        val playerStateToAdd = listOf(
                PlayerState(playerId = 1),
                PlayerState(playerId = 2),
                PlayerState(playerId = 3))

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            playerStateDao.addAll(playerStateToAdd)

            assertThat(playerStateDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_playerState_then_fail_to_add_it_again() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            var resOfInsert = playerStateDao.add(PlayerState(1))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(playerStateDao.findAll().size).isEqualTo(1)

            resOfInsert = playerStateDao.add(PlayerState(1))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(playerStateDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_playerState() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            playerStateDao.add(PlayerState(1, stress = 1))

            assertThat(playerStateDao.findAll().size).isEqualTo(1)

            val playerState = playerStateDao.findById(1)

            assertThat(playerState).isNotNull()
            assertThat(playerState?.stress).isEqualTo(1)
        }
    }

    @Test
    fun should_read_a_playerState_from_playerId() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            playerStateDao.add(PlayerState(playerId = 2, exhaustion = 1))

            assertThat(playerStateDao.findAll().size).isEqualTo(1)

            val playerState = playerStateDao.findByPlayerId(2)

            assertThat(playerState).isNotNull()
            assertThat(playerState?.exhaustion).isEqualTo(1)
        }
    }

    @Test
    fun should_read_all_playerState() {
        val playerStateToAdd = listOf(
                PlayerState(playerId = 1, maxStress = 3),
                PlayerState(playerId = 2, maxWounds = 2),
                PlayerState(playerId = 3, corruption = 4))

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            playerStateDao.addAll(playerStateToAdd)

            val allInsertedPlayerState = playerStateDao.findAll()
            assertThat(allInsertedPlayerState.size).isEqualTo(3)

            assertThat(allInsertedPlayerState[0]?.playerId).isEqualTo(1)
            assertThat(allInsertedPlayerState[0]?.maxStress).isEqualTo(3)

            assertThat(allInsertedPlayerState[1]?.playerId).isEqualTo(2)
            assertThat(allInsertedPlayerState[1]?.maxWounds).isEqualTo(2)

            assertThat(allInsertedPlayerState[2]?.playerId).isEqualTo(3)
            assertThat(allInsertedPlayerState[2]?.corruption).isEqualTo(4)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_playerState() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            // ADD
            val id = playerStateDao.add(PlayerState(playerId = 1))
            assertThat(playerStateDao.findAll().size).isEqualTo(1)

            // FIND
            val playerState = playerStateDao.findById(id)
            assertThat(playerState).isNotNull()
            assertThat(playerState?.playerId).isEqualTo(1)
            assertThat(playerState?.wounds).isEqualTo(0)

            // UPDATE
            val playerStateToUpdate = playerState?.copy(wounds = 1)
            playerStateDao.update(playerStateToUpdate!!)
            assertThat(playerStateDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newPlayerState = playerStateDao.findById(id)
            assertThat(newPlayerState).isNotNull()
            assertThat(newPlayerState?.playerId).isEqualTo(1)
            assertThat(newPlayerState?.wounds).isEqualTo(1)
        }
    }

    @Test
    fun should_update_all_playerState() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            // ADD
            val id1 = playerStateDao.add(PlayerState(playerId = 1))
            val id2 = playerStateDao.add(PlayerState(playerId = 2))
            assertThat(playerStateDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = playerStateDao.updateAll(
                    listOf(PlayerState(1, id1, maxCorruption = 2),
                            PlayerState(2, id2, maxExhaustion = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedPlayerState = playerStateDao.findAll()
            assertThat(allInsertedPlayerState.size).isEqualTo(2)
            assertThat(allInsertedPlayerState.map { it?.id }).containsExactly(id1, id2)

            assertThat(allInsertedPlayerState[0]?.playerId).isEqualTo(1)
            assertThat(allInsertedPlayerState[0]?.maxCorruption).isEqualTo(2)

            assertThat(allInsertedPlayerState[1]?.playerId).isEqualTo(2)
            assertThat(allInsertedPlayerState[1]?.maxExhaustion).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_playerState() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            assertThat(playerStateDao.findAll().size).isEqualTo(0)

            val res = playerStateDao.update(PlayerState(playerId = 1))
            assertThat(res).isEqualTo(-1)
            assertThat(playerStateDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            drop(PlayerStateTable)

            val res = playerStateDao.update(PlayerState(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_playerState() {
        val playerState1 = PlayerState(playerId = 1)
        val playerState2 = PlayerState(playerId = 2)
        val playerState3 = PlayerState(playerId = 3)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            val addAllResult = playerStateDao.addAll(listOf(playerState1, playerState2, playerState3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = playerStateDao.delete(playerState2)
            assertThat(res).isEqualTo(1)
            assertThat(playerStateDao.findAll().size).isEqualTo(2)
            assertThat(playerStateDao.findByPlayerId(1)).isNotNull()
            assertThat(playerStateDao.findByPlayerId(2)).isNull()
            assertThat(playerStateDao.findByPlayerId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_playerState_from_playerId() {
        val playerState = PlayerState(playerId = 2)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            val addedStates = playerStateDao.add(playerState)
            assertThat(addedStates).isEqualTo(1)
            assertThat(playerStateDao.findByPlayerId(2)).isNotNull()

            val res = playerStateDao.deleteByPlayerId(2)
            assertThat(res).isEqualTo(1)
            assertThat(playerStateDao.findByPlayerId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_playerState() {
        val playerState1 = PlayerState(playerId = 1)
        val playerState2 = PlayerState(playerId = 2)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            playerStateDao.add(playerState1)
            playerStateDao.add(playerState2)
            assertThat(playerStateDao.findAll().size).isEqualTo(2)

            playerStateDao.deleteAll()
            assertThat(playerStateDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_playerState() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            assertThat(playerStateDao.findAll().size).isEqualTo(0)

            val res = playerStateDao.delete(PlayerState(1, 6))
            assertThat(res).isEqualTo(0)
            assertThat(playerStateDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            drop(PlayerStateTable)

            val res = playerStateDao.delete(PlayerState(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_player_id_but_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            drop(PlayerStateTable)

            val res = playerStateDao.deleteByPlayerId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}