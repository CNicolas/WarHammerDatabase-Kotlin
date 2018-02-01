package daos

import entities.Player
import entities.tables.Players
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert.*
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

            playersDao.add(Player(playerName))

            assertThat(playersDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_players() {
        val playersToAdd = listOf(
                Player("Player1"),
                Player("Player2"),
                Player("Player3"))

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            playersDao.addAll(playersToAdd)

            assertThat(playersDao.findAll().size).isEqualTo(3)
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

            playersDao.add(Player(playerName))

            assertThat(playersDao.findAll().size).isEqualTo(1)

            val player = playersDao.findByName(playerName)

            assertNotNull(player)
            assertThat(player?.name).isEqualTo(playerName)
        }
    }

    @Test
    fun should_read_all_players() {
        val playersToAdd = listOf(
                Player("Player1"),
                Player("Player2"),
                Player("Player3"))

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
            val id = playersDao.add(Player(playerName))
            assertThat(playersDao.findAll().size).isEqualTo(1)

            // FIND
            val player = playersDao.findById(id)
            assertNotNull(player)
            assertThat(player?.name).isEqualTo(playerName)

            // UPDATE
            val playerToUpdate = player?.copy(name = newPlayerName)
            playersDao.update(playerToUpdate!!)
            assertThat(playersDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newPlayer = playersDao.findById(id)
            assertNotNull(newPlayer)
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
            val id1 = playersDao.add(Player("Player1"))
            val id2 = playersDao.add(Player("Player2"))
            assertThat(playersDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = playersDao.updateAll(listOf(Player("Player11", id1), Player("Player22", id2)))
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedPlayers = playersDao.findAll()
            assertThat(allInsertedPlayers.size).isEqualTo(2)
            assertThat(allInsertedPlayers.map { it?.name }).containsExactly("Player11", "Player22")
            assertThat(allInsertedPlayers.map { it?.id }).containsExactly(id1, id2)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_player() {
        val player1 = Player("Player1")
        val player2 = Player("Player2")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Players)

            playersDao.add(player1)
            playersDao.add(player2)

            assertThat(playersDao.findAll().size).isEqualTo(2)

            val res = playersDao.delete(player1)
            assertTrue(res)
            assertThat(playersDao.findAll().size).isEqualTo(1)
            assertThat(playersDao.findByName("Player1")).isNull()
            assertThat(playersDao.findByName("Player2")).isNotNull()
        }
    }

    @Test
    fun should_delete_all_players() {
        val player1 = Player("Player1")
        val player2 = Player("Player2")

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

            val res = playersDao.delete(Player("Inexistant"))
            assertFalse(res)
            assertThat(playersDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = playersDao.delete(Player("Inexistant"))
            assertFalse(res)
        }
    }
    // endregion
}