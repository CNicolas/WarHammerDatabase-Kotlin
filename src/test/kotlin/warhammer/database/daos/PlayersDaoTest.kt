package warhammer.database.daos

import warhammer.database.entities.PlayerEntity
import warhammer.database.tables.Players
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.Test

class PlayersDaoTest {
    private val playersDao = PlayersDao()

    // region CREATE
    @Test
    fun should_add_a_player() {
        val playerName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            playersDao.add(PlayerEntity(playerName))

            assertThat(playersDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_players() {
        val playersToAdd = listOf(
                PlayerEntity("Player1"),
                PlayerEntity("Player2"),
                PlayerEntity("Player3"))

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            playersDao.addAll(playersToAdd)

            assertThat(playersDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_player_then_fail_to_add_it_again() {
        val playerName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            var resOfInsert = playersDao.add(PlayerEntity(playerName))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(playersDao.findAll().size).isEqualTo(1)

            resOfInsert = playersDao.add(PlayerEntity(playerName))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(playersDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_player() {
        val playerName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            playersDao.add(PlayerEntity(playerName))

            assertThat(playersDao.findAll().size).isEqualTo(1)

            val player = playersDao.findByName(playerName)

            assertThat(player).isNotNull()
            assertThat(player?.name).isEqualTo(playerName)
        }
    }

    @Test
    fun should_read_all_players() {
        val playersToAdd = listOf(
                PlayerEntity("Player1"),
                PlayerEntity("Player2"),
                PlayerEntity("Player3"))

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            playersDao.addAll(playersToAdd)

            val allInsertedPlayers = playersDao.findAll()
            assertThat(allInsertedPlayers.size).isEqualTo(3)
            assertThat(allInsertedPlayers.map { it?.name }).containsExactly("Player1", "Player2", "Player3")
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_player() {
        val playerName = "TheLegend27"
        val newPlayerName = "TheLegend28"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            // ADD
            val id = playersDao.add(PlayerEntity(playerName))
            assertThat(playersDao.findAll().size).isEqualTo(1)

            // FIND
            val player = playersDao.findById(id)
            assertThat(player).isNotNull()
            assertThat(player?.name).isEqualTo(playerName)

            // UPDATE
            val playerToUpdate = player?.copy(name = newPlayerName)
            playersDao.update(playerToUpdate!!)
            assertThat(playersDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newPlayer = playersDao.findById(id)
            assertThat(newPlayer).isNotNull()
            assertThat(newPlayer?.name).isEqualTo(newPlayerName)
        }
    }

    @Test
    fun should_update_all_players() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            // ADD
            val id1 = playersDao.add(PlayerEntity("Player1"))
            val id2 = playersDao.add(PlayerEntity("Player2"))
            assertThat(playersDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = playersDao.updateAll(listOf(PlayerEntity("Player11", id1), PlayerEntity("Player22", id2)))
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedPlayers = playersDao.findAll()
            assertThat(allInsertedPlayers.size).isEqualTo(2)
            assertThat(allInsertedPlayers.map { it?.name }).containsExactly("Player11", "Player22")
            assertThat(allInsertedPlayers.map { it?.id }).containsExactly(id1, id2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_player() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            assertThat(playersDao.findAll().size).isEqualTo(0)

            val res = playersDao.update(PlayerEntity("Inexistant"))
            assertThat(res).isEqualTo(-1)
            assertThat(playersDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = playersDao.update(PlayerEntity("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_player() {
        val player1 = PlayerEntity("Player1")
        val player2 = PlayerEntity("Player2")
        val player3 = PlayerEntity("Player3")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            playersDao.add(player1)
            playersDao.add(player2)
            playersDao.add(player3)
            assertThat(playersDao.findAll().size).isEqualTo(3)

            val res = playersDao.delete(player2)
            assertThat(res).isEqualTo(1)
            assertThat(playersDao.findAll().size).isEqualTo(2)
            assertThat(playersDao.findByName("Player1")).isNotNull()
            assertThat(playersDao.findByName("Player2")).isNull()
            assertThat(playersDao.findByName("Player3")).isNotNull()
        }
    }

    @Test
    fun should_delete_all_players() {
        val player1 = PlayerEntity("Player1")
        val player2 = PlayerEntity("Player2")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            playersDao.add(player1)
            playersDao.add(player2)
            assertThat(playersDao.findAll().size).isEqualTo(2)

            playersDao.deleteAll()
            assertThat(playersDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_player() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            assertThat(playersDao.findAll().size).isEqualTo(0)

            val res = playersDao.delete(PlayerEntity("Inexistant"))
            assertThat(res).isEqualTo(0)
            assertThat(playersDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = playersDao.delete(PlayerEntity("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}