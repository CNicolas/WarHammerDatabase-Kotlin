package daos

import entities.Player
import entities.tables.Players
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert.assertNotNull
import org.testng.annotations.Test

class PlayersDaoTest {
    private val playersDao = PlayersDao()

    @Test
    fun should_insert_player_and_find_by_name() {
        val playerName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            playersDao.add(Player(playerName))

            assertThat(Players.selectAll().count()).isEqualTo(1)

            val player = playersDao.findByName(playerName)

            assertNotNull(player)
            assertThat(player?.name).isEqualTo(playerName)
        }
    }
}