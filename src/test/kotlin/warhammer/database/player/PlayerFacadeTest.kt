package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.PlayerFacade
import warhammer.database.entities.player.CharacteristicValue
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.addItem
import warhammer.database.entities.player.item.Weapon
import warhammer.database.entities.player.item.enums.Quality.LOW
import warhammer.database.entities.player.item.enums.Quality.NORMAL
import warhammer.database.entities.player.updateItem

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

        val player = playerFacade.save(Player(playerName))
        assertThat(player.name).isEqualTo(playerName)
        assertThat(player.items).isEmpty()

        player.addItem(Weapon(name = "Sword", damage = 4, criticalLevel = 3))

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo(playerName)
        assertThat(updatedPlayer.items).isNotEmpty()
        assertThat(updatedPlayer.items).isEqualTo(player.items)
        assertThat(updatedPlayer.items[0] is Weapon).isTrue()
        assertThat(updatedPlayer.items[0].name).isEqualTo("Sword")
        assertThat(updatedPlayer.items[0].damage).isEqualTo(4)
        assertThat(updatedPlayer.items[0].criticalLevel).isEqualTo(3)
        assertThat(updatedPlayer).isEqualToComparingFieldByField(player)
    }

    @Test
    fun should_update_field_of_item_of_a_player() {
        val player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        assertThat(player.name).isEqualTo("John")
        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0] is Weapon).isTrue()
        val weapon = player.items[0] as Weapon
        assertThat(weapon.name).isEqualTo("Sword")
        assertThat(weapon.damage).isEqualTo(4)
        assertThat(weapon.criticalLevel).isEqualTo(3)
        assertThat(weapon.quality).isEqualTo(NORMAL)

        weapon.quality = LOW
        player.updateItem(weapon)

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.items.size).isEqualTo(1)
        assertThat(updatedPlayer.items[0] is Weapon).isTrue()
        val newWeapon = updatedPlayer.items[0] as Weapon
        assertThat(newWeapon.name).isEqualTo("Sword")
        assertThat(newWeapon.damage).isEqualTo(4)
        assertThat(newWeapon.criticalLevel).isEqualTo(3)
        assertThat(newWeapon.quality).isEqualTo(LOW)
    }

    @Test
    fun should_update_name_of_item_of_a_player() {
        val player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        assertThat(player.name).isEqualTo("John")
        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0] is Weapon).isTrue()
        val weapon = player.items[0] as Weapon
        assertThat(weapon.name).isEqualTo("Sword")
        assertThat(weapon.damage).isEqualTo(4)
        assertThat(weapon.criticalLevel).isEqualTo(3)

        weapon.name = "Spear"
        player.updateItem(weapon)

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.items.size).isEqualTo(1)
        assertThat(updatedPlayer.items[0] is Weapon).isTrue()
        val newWeapon = updatedPlayer.items[0] as Weapon
        assertThat(newWeapon.name).isEqualTo("Spear")
        assertThat(newWeapon.damage).isEqualTo(4)
        assertThat(newWeapon.criticalLevel).isEqualTo(3)
    }

    @Test
    fun should_delete_items_of_player_then_player() {
        val player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        assertThat(player.items.size).isEqualTo(1)

        playerFacade.deleteAllItemsOfPlayer(player)
        assertThat(player.items).isEmpty()

        playerFacade.deletePlayer(player)
        assertThat(playerFacade.findAll()).isEmpty()
    }
}