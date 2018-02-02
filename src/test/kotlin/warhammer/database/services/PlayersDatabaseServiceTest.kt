package warhammer.database.services

import warhammer.database.entities.PlayerEntity
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class PlayersDatabaseServiceTest {
    private val playersService = PlayersDatabaseService(databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
    private val playerName = "SampleName"
    private val samplePlayer = PlayerEntity(playerName)

    @BeforeMethod
    fun clearDatabase() {
        playersService.deleteAll()
    }

    // region CREATE
    @Test
    fun should_add_a_player() {
        val addedPlayer = playersService.add(samplePlayer)
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(addedPlayer?.name).isEqualTo(playerName)
    }

    @Test
    fun should_add_all_players() {
        val playersToAdd = listOf(
                PlayerEntity("Player1"),
                PlayerEntity("Player2"),
                PlayerEntity("Player3"))

        val addAllResult = playersService.addAll(playersToAdd)
        assertThat(addAllResult.size).isEqualTo(playersToAdd.size)
        assertThat(addAllResult.map { it?.name }).containsExactly("Player1", "Player2", "Player3")
        assertThat(playersService.countAll()).isEqualTo(playersToAdd.size)
    }

    @Test
    fun should_add_a_player_then_fail_to_add_it_again() {
        val addedPlayer1 = playersService.add(samplePlayer)
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(addedPlayer1?.name).isEqualTo(playerName)

        val addedPlayer2 = playersService.add(samplePlayer)
        assertThat(addedPlayer2).isNull()
        assertThat(playersService.countAll()).isEqualTo(1)
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_player() {
        playersService.add(samplePlayer)
        assertThat(playersService.countAll()).isEqualTo(1)

        val player = playersService.findByName(playerName)
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo(playerName)
    }

    @Test
    fun should_find_a_player_by_id() {
        playersService.add(samplePlayer)
        assertThat(playersService.countAll()).isEqualTo(1)

        val player = playersService.findById(1)
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo(playerName)
    }

    @Test
    fun should_read_all_players() {
        val playersToAdd = listOf(
                PlayerEntity("Player1"),
                PlayerEntity("Player2"),
                PlayerEntity("Player3"))

        playersService.addAll(playersToAdd)

        val allInsertedPlayers = playersService.findAll()
        assertThat(allInsertedPlayers.size).isEqualTo(3)
        assertThat(allInsertedPlayers.map { it?.name }).containsExactly("Player1", "Player2", "Player3")
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_player() {
        val newPlayerName = "MyPlayerIsNew"

        // ADD
        val player = playersService.add(samplePlayer)
        assertThat(playersService.countAll()).isEqualTo(1)

        // FIND
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo(playerName)

        // UPDATE
        val playerToUpdate = player?.copy(name = newPlayerName)
        val newPlayer = playersService.update(playerToUpdate!!)

        // VERIFY
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(newPlayer).isNotNull()
        assertThat(newPlayer?.name).isEqualTo(newPlayerName)
    }

    @Test
    fun should_update_all_players() {
        // ADD
        val player1 = playersService.add(PlayerEntity("Player1"))
        val player2 = playersService.add(PlayerEntity("Player2"))
        assertThat(playersService.countAll()).isEqualTo(2)

        // UPDATE
        val updatedPlayers = playersService.updateAll(listOf(player1!!.copy(name = "Player11"), player2!!.copy(name = "Player22")))

        // VERIFY
        assertThat(updatedPlayers.size).isEqualTo(2)
        assertThat(updatedPlayers.map { it?.name }).containsExactly("Player11", "Player22")
        assertThat(updatedPlayers.map { it?.id }).containsExactly(player1.id, player2.id)
    }

    @Test
    fun should_return_false_when_update_a_inexistant_player() {
        assertThat(playersService.countAll()).isEqualTo(0)

        val updatedPlayer = playersService.update(PlayerEntity("Inexistant"))
        assertThat(updatedPlayer).isNull()
        assertThat(playersService.findAll()).isEmpty()
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_player() {
        val player1 = PlayerEntity("Player1")
        val player2 = PlayerEntity("Player2")
        val player3 = PlayerEntity("Player3")

        val addAllResult = playersService.addAll(listOf(player1, player2, player3))
        assertThat(addAllResult.size).isEqualTo(3)
        assertThat(addAllResult.map { it?.name }).containsExactly("Player1", "Player2", "Player3")

        val isDeleted = playersService.delete(player2)
        assertThat(isDeleted).isTrue()
        assertThat(playersService.countAll()).isEqualTo(2)
        assertThat(playersService.findByName("Player1")).isNotNull()
        assertThat(playersService.findByName("Player2")).isNull()
        assertThat(playersService.findByName("Player3")).isNotNull()
    }

    @Test
    fun should_delete_all_players() {
        val player1 = PlayerEntity("Player1")
        val player2 = PlayerEntity("Player2")

        playersService.add(player1)
        playersService.add(player2)
        assertThat(playersService.countAll()).isEqualTo(2)

        playersService.deleteAll()
        assertThat(playersService.findAll()).isEmpty()
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_player() {
        assertThat(playersService.countAll()).isEqualTo(0)

        val res = playersService.delete(PlayerEntity("Inexistant"))
        assertThat(res).isFalse()
        assertThat(playersService.findAll()).isEmpty()
    }
    // endregion
}