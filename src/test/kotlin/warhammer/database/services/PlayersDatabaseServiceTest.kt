package warhammer.database.services

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.characteristics.Characteristic.*
import warhammer.database.entities.player.characteristics.CharacteristicValue
import warhammer.database.entities.player.characteristics.PlayerCharacteristicsMap
import warhammer.database.entities.player.other.Race.*
import warhammer.database.tables.player.PlayerCharacteristicsTable
import warhammer.database.tables.PlayersTable

class PlayersDatabaseServiceTest {
    private val playersService = PlayersDatabaseService(databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
    private val playerName = "SampleName"
    private val samplePlayer
        get() = {
            val characteristics = PlayerCharacteristicsMap(
                    strengthValue = CharacteristicValue(3),
                    toughnessValue = CharacteristicValue(2, 1)
            )
            Player(playerName, DWARF, 110, 89, characteristics)
        }

    @BeforeMethod
    fun clearDatabase() {
        playersService.deleteAll()
    }

    @Test
    fun should_be_initialized() {
        transaction {
            assertThat(PlayersTable.exists()).isTrue()
            assertThat(PlayerCharacteristicsTable.exists()).isTrue()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_player() {
        val addedPlayer = playersService.add(samplePlayer())

        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(addedPlayer?.name).isEqualTo(playerName)
        assertThat(addedPlayer?.characteristics!![STRENGTH].value).isEqualTo(3)
        assertThat(addedPlayer.characteristics[TOUGHNESS].value).isEqualTo(2)
        assertThat(addedPlayer.characteristics[TOUGHNESS].fortuneValue).isEqualTo(1)
    }

    @Test
    fun should_add_all_players() {
        val playersToAdd = listOf(
                Player("Player1", characteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(1))),
                Player("Player2", characteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(2))),
                Player("Player3", characteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(3))))

        val addAllResult = playersService.addAll(playersToAdd)
        assertThat(addAllResult.size).isEqualTo(playersToAdd.size)
        assertThat(playersService.countAll()).isEqualTo(playersToAdd.size)

        assertThat(addAllResult.map { it?.name }).containsExactly("Player1", "Player2", "Player3")
        assertThat(addAllResult[0]?.characteristics!![STRENGTH].value).isEqualTo(1)
        assertThat(addAllResult[1]?.characteristics!![STRENGTH].value).isEqualTo(2)
        assertThat(addAllResult[2]?.characteristics!![STRENGTH].value).isEqualTo(3)
    }

    @Test
    fun should_add_a_player_then_fail_to_add_it_again() {
        val addedPlayer1 = playersService.add(Player(playerName))
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(addedPlayer1?.name).isEqualTo(playerName)

        val addedPlayer2 = playersService.add(Player(playerName))
        assertThat(addedPlayer2).isNull()
        assertThat(playersService.countAll()).isEqualTo(1)
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_player() {
        playersService.add(samplePlayer())
        assertThat(playersService.countAll()).isEqualTo(1)

        val player = playersService.findByName(playerName)
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo(playerName)
    }

    @Test
    fun should_find_a_player_by_id() {
        playersService.add(samplePlayer())
        assertThat(playersService.countAll()).isEqualTo(1)

        val player = playersService.findById(1)
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo(playerName)
        assertThat(player?.characteristics!![STRENGTH].value).isEqualTo(3)
        assertThat(player.characteristics[TOUGHNESS].value).isEqualTo(2)
        assertThat(player.characteristics[TOUGHNESS].fortuneValue).isEqualTo(1)
    }

    @Test
    fun should_read_all_players() {
        val playersToAdd = listOf(
                Player("Player1"),
                Player("Player2"),
                Player("Player3"))

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
        val player = playersService.add(samplePlayer())
        assertThat(playersService.countAll()).isEqualTo(1)

        // FIND
        assertThat(player).isNotNull()
        assertThat(player?.name).isEqualTo(playerName)
        assertThat(player?.race).isEqualTo(DWARF)
        assertThat(player?.characteristics!![AGILITY].value).isEqualTo(0)
        assertThat(player.characteristics[AGILITY].fortuneValue).isEqualTo(0)

        // UPDATE
        val playerToUpdate = player.copy(name = newPlayerName,
                race = WOOD_ELF,
                characteristics = PlayerCharacteristicsMap(
                        agilityValue = CharacteristicValue(5, 2)
                ))
        val newPlayer = playersService.update(playerToUpdate)

        // VERIFY
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(newPlayer).isNotNull()
        assertThat(newPlayer?.name).isEqualTo(newPlayerName)
        assertThat(newPlayer?.race).isEqualTo(WOOD_ELF)
        assertThat(newPlayer?.characteristics!![AGILITY].value).isEqualTo(5)
        assertThat(newPlayer.characteristics[AGILITY].fortuneValue).isEqualTo(2)
    }

    @Test
    fun should_update_all_players() {
        // ADD
        val player1 = playersService.add(Player("Player1", HIGH_ELF))
        val player2 = playersService.add(Player("Player2", WOOD_ELF))
        assertThat(playersService.countAll()).isEqualTo(2)

        // UPDATE
        val updatedPlayers = playersService.updateAll(
                listOf(
                        player1!!.copy(name = "Player11", race = DWARF),
                        player2!!.copy(name = "Player22", race = DWARF)
                )
        )

        // VERIFY
        assertThat(updatedPlayers.size).isEqualTo(2)
        assertThat(updatedPlayers.map { it?.name }).containsExactly("Player11", "Player22")
        assertThat(updatedPlayers.map { it?.id }).containsExactly(player1.id, player2.id)
        assertThat(updatedPlayers.map { it?.race }).containsExactly(DWARF, DWARF)
    }

    @Test
    fun should_return_false_when_update_a_inexistant_player() {
        assertThat(playersService.countAll()).isEqualTo(0)

        val updatedPlayer = playersService.update(Player("Inexistant"))
        assertThat(updatedPlayer).isNull()
        assertThat(playersService.findAll()).isEmpty()
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_player() {
        val player1 = Player("Player1", characteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(1)))
        val player2 = Player("Player2", characteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(2)))
        val player3 = Player("Player3", characteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(3)))

        val addAllResult = playersService.addAll(listOf(player1, player2, player3))
        assertThat(addAllResult.size).isEqualTo(3)
        assertThat(addAllResult.map { it?.name }).containsExactly("Player1", "Player2", "Player3")

        val isDeleted = playersService.delete(addAllResult[1]!!)

        assertThat(isDeleted).isTrue()
        assertThat(playersService.countAll()).isEqualTo(2)
        assertThat(playersService.findByName("Player1")).isNotNull()
        assertThat(playersService.findByName("Player1")?.characteristics!![STRENGTH].value).isEqualTo(1)
        assertThat(playersService.findByName("Player2")).isNull()
        assertThat(playersService.findByName("Player3")).isNotNull()
        assertThat(playersService.findByName("Player3")?.characteristics!![STRENGTH].value).isEqualTo(3)
    }

    @Test
    fun should_delete_all_players() {
        val player1 = Player("Player1")
        val player2 = Player("Player2", characteristics = PlayerCharacteristicsMap(toughnessValue = CharacteristicValue(2)))

        playersService.add(player1)
        playersService.add(player2)
        assertThat(playersService.countAll()).isEqualTo(2)

        playersService.deleteAll()
        assertThat(playersService.findAll()).isEmpty()
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_player() {
        assertThat(playersService.countAll()).isEqualTo(0)

        val res = playersService.delete(Player("Inexistant"))
        assertThat(res).isFalse()
        assertThat(playersService.findAll()).isEmpty()
    }
    // endregion
}