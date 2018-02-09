package warhammer.database.daos

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.Test
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.other.Race
import warhammer.database.tables.PlayerCharacteristicsTable
import warhammer.database.tables.PlayersTable

class PlayersDaoTest {
    private val playersDao = PlayersDao()

    // region CREATE
    @Test
    fun should_add_a_player() {
        val playerName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(PlayersTable, PlayerCharacteristicsTable)

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

            create(PlayersTable)

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

            create(PlayersTable)

            var resOfInsert = playersDao.add(Player(playerName))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(playersDao.findAll().size).isEqualTo(1)

            resOfInsert = playersDao.add(Player(playerName))
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

            create(PlayersTable)

            playersDao.add(Player(playerName))

            assertThat(playersDao.findAll().size).isEqualTo(1)

            val player = playersDao.findByName(playerName)

            assertThat(player).isNotNull()
            assertThat(player?.name).isEqualTo(playerName)
        }
    }

    @Test
    fun should_read_all_players() {
        val playersToAdd = listOf(
                Player("Player1", race = Race.DWARF),
                Player("Player2", size = 92),
                Player("Player3", age = 219))

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(PlayersTable, PlayerCharacteristicsTable)

            playersDao.addAll(playersToAdd)

            val allInsertedPlayers = playersDao.findAll()
            assertThat(allInsertedPlayers).isNotNull()
            assertThat(allInsertedPlayers.size).isEqualTo(3)
            assertThat(allInsertedPlayers.map { it?.name }).containsExactly("Player1", "Player2", "Player3")
            assertThat(allInsertedPlayers[0]?.race).isEqualTo(Race.DWARF)
            assertThat(allInsertedPlayers[1]?.size).isEqualTo(92)
            assertThat(allInsertedPlayers[2]?.age).isEqualTo(219)
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
            create(PlayersTable, PlayerCharacteristicsTable)

            // ADD
            val id = playersDao.add(Player(playerName))
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
            create(PlayersTable, PlayerCharacteristicsTable)

            // ADD
            val id1 = playersDao.add(Player("Player1"))
            val id2 = playersDao.add(Player("Player2"))
            assertThat(playersDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = playersDao.updateAll(listOf(Player("Player11", id = id1), Player("Player22", id = id2)))
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
            create(PlayersTable, PlayerCharacteristicsTable)

            assertThat(playersDao.findAll().size).isEqualTo(0)

            val res = playersDao.update(Player("Inexistant"))
            assertThat(res).isEqualTo(-1)
            assertThat(playersDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = playersDao.update(Player("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_player() {
        val player1 = Player("Player1")
        val player2 = Player("Player2")
        val player3 = Player("Player3")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable)

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
        val player1 = Player("Player1")
        val player2 = Player("Player2")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable)

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
            create(PlayersTable)

            assertThat(playersDao.findAll().size).isEqualTo(0)

            val res = playersDao.delete(Player("Inexistant"))
            assertThat(res).isEqualTo(0)
            assertThat(playersDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = playersDao.delete(Player("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}