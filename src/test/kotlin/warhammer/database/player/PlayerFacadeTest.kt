package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.PlayerFacade
import warhammer.database.entities.player.CharacteristicValue
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.addItem
import warhammer.database.entities.player.item.Weapon

class PlayerFacadeTest {
    private val playerFacade = PlayerFacade(
            databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared",
            driver = "org.sqlite.JDBC"
    )

    @BeforeMethod
    fun clearBeforeTest() {
        playerFacade.deleteAll()
    }

    @Test
    fun should_add_a_simple_player() {
        val playerName = "PlayerName"

        val player = playerFacade.save(Player(playerName))
        assertThat(player.name).isEqualTo(playerName)
    }

    @Test
    fun should_find_an_added_simple_player() {
        val playerName = "PlayerName"

        val player = playerFacade.save(Player(playerName))
        assertThat(player.name).isEqualTo(playerName)

        val foundPlayer = playerFacade.find(playerName)
        assertThat(foundPlayer).isNotNull()
        assertThat(foundPlayer!!.name).isEqualTo(playerName)

        assertThat(player).isEqualToComparingFieldByField(foundPlayer)
    }

    @Test
    fun should_update_an_added_simple_player() {
        val playerName = "PlayerName"

        val player = playerFacade.save(Player(playerName))
        assertThat(player.name).isEqualTo(playerName)
        assertThat(player).isEqualToComparingFieldByField(player)

        player.careerName = "Soldier"
        player.toughness = CharacteristicValue(4, 1)

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo(playerName)
        assertThat(updatedPlayer.careerName).isEqualTo("Soldier")
        assertThat(updatedPlayer.toughness.value).isEqualTo(4)
        assertThat(updatedPlayer.toughness.fortuneValue).isEqualTo(1)
        assertThat(updatedPlayer).isEqualToComparingFieldByField(player)
    }

    @Test
    fun should_update_name_of_player() {
        val player = playerFacade.save(Player("John"))
        assertThat(player.name).isEqualTo("John")

        player.name = "Jack"

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("Jack")
        assertThat(updatedPlayer).isEqualToComparingFieldByField(player)

        val allPlayers = playerFacade.findAll()
        assertThat(allPlayers.size).isEqualTo(1)
    }

    @Test
    fun should_update_items_of_a_player() {
        val playerName = "PlayerName"
        val player = Player(playerName)

        val savedPlayer = playerFacade.save(player)
        assertThat(savedPlayer.name).isEqualTo(playerName)
        assertThat(savedPlayer).isEqualToComparingFieldByField(player)
        assertThat(savedPlayer.items).isEmpty()

        val sword = Weapon(name = "Sword", damage = 4, criticalLevel = 3)
        player.addItem(sword)

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo(playerName)
        assertThat(updatedPlayer.items).isNotEmpty()
        assertThat(updatedPlayer.items).isEqualTo(player.items)
        assertThat(updatedPlayer.items[0] is Weapon).isTrue()
        assertThat(updatedPlayer.items[0]).isEqualToComparingFieldByField(sword)
        assertThat(updatedPlayer).isEqualToComparingFieldByField(player)
    }
}