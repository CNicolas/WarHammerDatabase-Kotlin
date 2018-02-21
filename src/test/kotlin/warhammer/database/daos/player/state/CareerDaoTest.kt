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
import warhammer.database.entities.player.PlayerEntity
import warhammer.database.entities.player.PlayerState
import warhammer.database.entities.player.state.Career
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerStateTable
import warhammer.database.tables.player.state.CareerTable
import java.sql.Connection

class CareerDaoTest {
    private val careerDao = CareerDao()

    @BeforeMethod
    fun createPlayersAndStates() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()
        val playerStateDao = PlayerStateDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(PlayersTable, PlayerStateTable, CareerTable)

            playersDao.deleteAll()
            playerStateDao.deleteAll()
            CareerTable.deleteAll()

            playersDao.add(PlayerEntity(id = 1, name = "PlayerName1"))
            playersDao.add(PlayerEntity(id = 2, name = "PlayerName2"))
            playersDao.add(PlayerEntity(id = 3, name = "PlayerName3"))

            playerStateDao.add(PlayerState(id = 1, playerId = 1))
            playerStateDao.add(PlayerState(id = 2, playerId = 2))
            playerStateDao.add(PlayerState(id = 3, playerId = 3))
        }
    }

    // region CREATE
    @Test
    fun should_add_a_career() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            careerDao.add(Career(stateId = 1))

            assertThat(careerDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_career() {
        val careerToAdd = listOf(
                Career(stateId = 1),
                Career(stateId = 2),
                Career(stateId = 3))

        transaction {
            logger.addLogger(StdOutSqlLogger)

            careerDao.addAll(careerToAdd)

            assertThat(careerDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_career_then_fail_to_add_it_again() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            var resOfInsert = careerDao.add(Career(stateId = 1))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(careerDao.findAll().size).isEqualTo(1)

            resOfInsert = careerDao.add(Career(stateId = 1))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(careerDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_career() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            careerDao.add(Career(1, name = "Tueur de Démons", rank = 5))

            assertThat(careerDao.findAll().size).isEqualTo(1)

            val career = careerDao.findById(1)

            assertThat(career).isNotNull()
            assertThat(career?.name).isEqualTo("Tueur de Démons")
            assertThat(career?.rank).isEqualTo(5)
        }
    }

    @Test
    fun should_read_a_career_from_stateId() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            careerDao.add(Career(stateId = 2, availableExperience = 1))

            assertThat(careerDao.findAll().size).isEqualTo(1)

            val career = careerDao.findByStateId(2)

            assertThat(career).isNotNull()
            assertThat(career?.availableExperience).isEqualTo(1)
        }
    }

    @Test
    fun should_read_all_career() {
        val careerToAdd = listOf(
                Career(stateId = 1, totalExperience = 3),
                Career(stateId = 2, name = "Forestier"),
                Career(stateId = 3, rank = 4))

        transaction {
            logger.addLogger(StdOutSqlLogger)

            careerDao.addAll(careerToAdd)

            val allInsertedCareer = careerDao.findAll()
            assertThat(allInsertedCareer.size).isEqualTo(3)

            assertThat(allInsertedCareer[0].stateId).isEqualTo(1)
            assertThat(allInsertedCareer[0].totalExperience).isEqualTo(3)

            assertThat(allInsertedCareer[1].stateId).isEqualTo(2)
            assertThat(allInsertedCareer[1].name).isEqualTo("Forestier")

            assertThat(allInsertedCareer[2].stateId).isEqualTo(3)
            assertThat(allInsertedCareer[2].rank).isEqualTo(4)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_career() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            // ADD
            val id = careerDao.add(Career(stateId = 1))
            assertThat(careerDao.findAll().size).isEqualTo(1)

            // FIND
            val career = careerDao.findById(id)
            assertThat(career).isNotNull()
            assertThat(career!!.stateId).isEqualTo(1)
            assertThat(career.name).isEqualTo("Unemployed")

            // UPDATE
            career.name = "Ratier"
            careerDao.update(career)
            assertThat(careerDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newCareer = careerDao.findById(id)
            assertThat(newCareer).isNotNull()
            assertThat(newCareer?.stateId).isEqualTo(1)
            assertThat(newCareer?.name).isEqualTo("Ratier")
        }
    }

    @Test
    fun should_update_all_career() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            // ADD
            val id1 = careerDao.add(Career(stateId = 1))
            val id2 = careerDao.add(Career(stateId = 2))
            assertThat(careerDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = careerDao.updateAll(
                    listOf(Career(id1, 1, rank = 2),
                            Career(id2, 2, totalExperience = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedCareer = careerDao.findAll()
            assertThat(allInsertedCareer.size).isEqualTo(2)
            assertThat(allInsertedCareer.map { it.id }).containsExactly(id1, id2)

            assertThat(allInsertedCareer[0].stateId).isEqualTo(1)
            assertThat(allInsertedCareer[0].rank).isEqualTo(2)

            assertThat(allInsertedCareer[1].stateId).isEqualTo(2)
            assertThat(allInsertedCareer[1].totalExperience).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_non_existent_career() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            assertThat(careerDao.findAll()).isEmpty()

            val res = careerDao.update(Career(stateId = 1))
            assertThat(res).isEqualTo(-1)
            assertThat(careerDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_non_existent_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(CareerTable)

            val res = careerDao.update(Career(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_career() {
        val career1 = Career(stateId = 1)
        val career2 = Career(stateId = 2)
        val career3 = Career(stateId = 3)

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val addAllResult = careerDao.addAll(listOf(career1, career2, career3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = careerDao.delete(career2)
            assertThat(res).isEqualTo(1)
            assertThat(careerDao.findAll().size).isEqualTo(2)
            assertThat(careerDao.findByStateId(1)).isNotNull()
            assertThat(careerDao.findByStateId(2)).isNull()
            assertThat(careerDao.findByStateId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_career_from_stateId() {
        val career = Career(stateId = 2)

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val addedCharacteristics = careerDao.add(career)
            assertThat(addedCharacteristics).isEqualTo(1)
            assertThat(careerDao.findByStateId(2)).isNotNull()

            val res = careerDao.deleteByStateId(2)
            assertThat(res).isEqualTo(1)
            assertThat(careerDao.findByStateId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_career() {
        val career1 = Career(stateId = 1)
        val career2 = Career(stateId = 2)

        transaction {
            logger.addLogger(StdOutSqlLogger)

            careerDao.add(career1)
            careerDao.add(career2)
            assertThat(careerDao.findAll().size).isEqualTo(2)

            careerDao.deleteAll()
            assertThat(careerDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_non_existent_career() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            assertThat(careerDao.findAll()).isEmpty()

            val res = careerDao.delete(Career(1, 6))
            assertThat(res).isEqualTo(0)
            assertThat(careerDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_non_existent_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(CareerTable)

            val res = careerDao.delete(Career(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_state_id_but_non_existent_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(CareerTable)

            val res = careerDao.deleteByStateId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}