package warhammer.database.daos.player.state

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
import warhammer.database.daos.player.PlayerStateDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerState
import warhammer.database.entities.player.state.Stance
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerStateTable
import warhammer.database.tables.player.state.StanceTable
import java.sql.Connection

class StanceDaoTest {
    private val stanceDao = StanceDao()

    @BeforeMethod
    fun createPlayersAndStates() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()
        val playerStateDao = PlayerStateDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable, PlayerStateTable, StanceTable)

            playersDao.add(Player(id = 1, name = "PlayerName1"))
            playersDao.add(Player(id = 2, name = "PlayerName2"))
            playersDao.add(Player(id = 3, name = "PlayerName3"))

            playerStateDao.add(PlayerState(id = 1, playerId = 1))
            playerStateDao.add(PlayerState(id = 2, playerId = 2))
            playerStateDao.add(PlayerState(id = 3, playerId = 3))

            StanceTable.deleteAll()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_stance() {
        transaction {
            stanceDao.add(Stance(stateId = 1))

            assertThat(stanceDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_stance() {
        val stanceToAdd = listOf(
                Stance(stateId = 1),
                Stance(stateId = 2),
                Stance(stateId = 3))

        transaction {
            stanceDao.addAll(stanceToAdd)

            assertThat(stanceDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_stance_then_fail_to_add_it_again() {
        transaction {
            var resOfInsert = stanceDao.add(Stance(stateId = 1))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(stanceDao.findAll().size).isEqualTo(1)

            resOfInsert = stanceDao.add(Stance(stateId = 1))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(stanceDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_stance() {
        transaction {
            stanceDao.add(Stance(1, reckless = 1))

            assertThat(stanceDao.findAll().size).isEqualTo(1)

            val stance = stanceDao.findById(1)

            assertThat(stance).isNotNull()
            assertThat(stance?.reckless).isEqualTo(1)
        }
    }

    @Test
    fun should_read_a_stance_from_stateId() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            stanceDao.add(Stance(stateId = 2, conservative = 1))

            assertThat(stanceDao.findAll().size).isEqualTo(1)

            val stance = stanceDao.findByStateId(2)

            assertThat(stance).isNotNull()
            assertThat(stance?.conservative).isEqualTo(1)
        }
    }

    @Test
    fun should_read_all_stance() {
        val stanceToAdd = listOf(
                Stance(stateId = 1, maxReckless = 3),
                Stance(stateId = 2, conservative = 2),
                Stance(stateId = 3, maxConservative = 4))

        transaction {
            stanceDao.addAll(stanceToAdd)

            val allInsertedStance = stanceDao.findAll()
            assertThat(allInsertedStance.size).isEqualTo(3)

            assertThat(allInsertedStance[0]?.stateId).isEqualTo(1)
            assertThat(allInsertedStance[0]?.maxReckless).isEqualTo(3)

            assertThat(allInsertedStance[1]?.stateId).isEqualTo(2)
            assertThat(allInsertedStance[1]?.conservative).isEqualTo(2)

            assertThat(allInsertedStance[2]?.stateId).isEqualTo(3)
            assertThat(allInsertedStance[2]?.maxConservative).isEqualTo(4)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_stance() {
        transaction {
            // ADD
            val id = stanceDao.add(Stance(stateId = 1))
            assertThat(stanceDao.findAll().size).isEqualTo(1)

            // FIND
            val stance = stanceDao.findById(id)
            assertThat(stance).isNotNull()
            assertThat(stance?.stateId).isEqualTo(1)
            assertThat(stance?.maxReckless).isEqualTo(0)

            // UPDATE
            val stanceToUpdate = stance?.copy(maxReckless = 1)
            stanceDao.update(stanceToUpdate!!)
            assertThat(stanceDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newStance = stanceDao.findById(id)
            assertThat(newStance).isNotNull()
            assertThat(newStance?.stateId).isEqualTo(1)
            assertThat(newStance?.maxReckless).isEqualTo(1)
        }
    }

    @Test
    fun should_update_all_stance() {
        transaction {
            // ADD
            val id1 = stanceDao.add(Stance(stateId = 1))
            val id2 = stanceDao.add(Stance(stateId = 2))
            assertThat(stanceDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = stanceDao.updateAll(
                    listOf(Stance(1, id1, reckless = 2),
                            Stance(2, id2, maxConservative = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedStance = stanceDao.findAll()
            assertThat(allInsertedStance.size).isEqualTo(2)
            assertThat(allInsertedStance.map { it?.id }).containsExactly(id1, id2)

            assertThat(allInsertedStance[0]?.stateId).isEqualTo(1)
            assertThat(allInsertedStance[0]?.reckless).isEqualTo(2)

            assertThat(allInsertedStance[1]?.stateId).isEqualTo(2)
            assertThat(allInsertedStance[1]?.maxConservative).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_stance() {
        transaction {
            assertThat(stanceDao.findAll().size).isEqualTo(0)

            val res = stanceDao.update(Stance(stateId = 1))
            assertThat(res).isEqualTo(-1)
            assertThat(stanceDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        transaction {
            drop(StanceTable)

            val res = stanceDao.update(Stance(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_stance() {
        val stance1 = Stance(stateId = 1)
        val stance2 = Stance(stateId = 2)
        val stance3 = Stance(stateId = 3)

        transaction {
            val addAllResult = stanceDao.addAll(listOf(stance1, stance2, stance3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = stanceDao.delete(stance2)
            assertThat(res).isEqualTo(1)
            assertThat(stanceDao.findAll().size).isEqualTo(2)
            assertThat(stanceDao.findByStateId(1)).isNotNull()
            assertThat(stanceDao.findByStateId(2)).isNull()
            assertThat(stanceDao.findByStateId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_stance_from_stateId() {
        val stance = Stance(stateId = 2)

        transaction {
            val addedCharacteristics = stanceDao.add(stance)
            assertThat(addedCharacteristics).isEqualTo(1)
            assertThat(stanceDao.findByStateId(2)).isNotNull()

            val res = stanceDao.deleteByStateId(2)
            assertThat(res).isEqualTo(1)
            assertThat(stanceDao.findByStateId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_stance() {
        val stance1 = Stance(stateId = 1)
        val stance2 = Stance(stateId = 2)

        transaction {
            stanceDao.add(stance1)
            stanceDao.add(stance2)
            assertThat(stanceDao.findAll().size).isEqualTo(2)

            stanceDao.deleteAll()
            assertThat(stanceDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_stance() {
        transaction {
            assertThat(stanceDao.findAll().size).isEqualTo(0)

            val res = stanceDao.delete(Stance(1, 6))
            assertThat(res).isEqualTo(0)
            assertThat(stanceDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        transaction {
            drop(StanceTable)

            val res = stanceDao.delete(Stance(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_state_id_but_inexistant_table() {
        transaction {
            drop(StanceTable)

            val res = stanceDao.deleteByStateId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}