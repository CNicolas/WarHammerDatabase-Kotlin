package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.PlayerFacade
import warhammer.database.entities.player.*
import warhammer.database.entities.player.item.Armor
import warhammer.database.entities.player.item.Expandable
import warhammer.database.entities.player.item.GenericItem
import warhammer.database.entities.player.item.Weapon
import warhammer.database.entities.player.item.enums.Quality.LOW
import warhammer.database.entities.player.item.enums.Quality.NORMAL

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

        player.addItem(Weapon(name = "Sword", damage = 4, isEquipped = true))

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo(playerName)
        assertThat(updatedPlayer.items).isNotEmpty()
        assertThat(updatedPlayer.items).isEqualTo(player.items)
        assertThat(updatedPlayer.items[0] is Weapon).isTrue()
        val weapon = updatedPlayer.getWeapons()[0]
        assertThat(weapon.name).isEqualTo("Sword")
        assertThat(weapon.damage).isEqualTo(4)
        assertThat(weapon.isEquipped).isTrue()
        assertThat(updatedPlayer).isEqualToComparingFieldByField(player)
    }

    @Test
    fun should_update_field_of_item_of_a_player() {
        val player = playerFacade.save(Player("John", items = listOf(GenericItem(name = "Rope"))))
        assertThat(player.name).isEqualTo("John")
        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0] is GenericItem).isTrue()
        val genericItem = player.getGenericItems()[0]
        assertThat(genericItem.name).isEqualTo("Rope")
        assertThat(genericItem.isEquipped).isNull()
        assertThat(genericItem.quality).isEqualTo(NORMAL)

        genericItem.quality = LOW
        player.updateItem(genericItem)

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.items.size).isEqualTo(1)
        assertThat(updatedPlayer.items[0] is GenericItem).isTrue()
        val newItem = updatedPlayer.getGenericItems()[0]
        assertThat(newItem.isEquipped).isNull()
        assertThat(newItem.quality).isEqualTo(LOW)
    }

    @Test
    fun should_update_name_of_item_of_a_player() {
        val player = playerFacade.save(Player("John", items = listOf(Armor(name = "Helmet", soak = 2, defense = 1))))
        assertThat(player.name).isEqualTo("John")
        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0] is Armor).isTrue()
        val armor = player.items[0] as Armor
        assertThat(armor.name).isEqualTo("Helmet")
        assertThat(armor.soak).isEqualTo(2)
        assertThat(armor.defense).isEqualTo(1)

        armor.name = "Shield"
        player.updateItem(armor)

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.items.size).isEqualTo(1)
        assertThat(updatedPlayer.items[0] is Armor).isTrue()

        val newWeapon = updatedPlayer.getArmors()[0]
        assertThat(newWeapon.name).isEqualTo("Shield")
        assertThat(armor.soak).isEqualTo(2)
        assertThat(armor.defense).isEqualTo(1)
    }

    @Test
    fun should_delete_items_of_player_then_player() {
        var player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        assertThat(player.items.size).isEqualTo(1)

        playerFacade.deleteAllItemsOfPlayer(player)
        assertThat(player.items).isEmpty()

        playerFacade.deletePlayer(player)
        assertThat(playerFacade.findAll()).isEmpty()

        player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        assertThat(player.items.size).isEqualTo(1)
        playerFacade.deletePlayer("John")
        assertThat(playerFacade.findAll()).isEmpty()
    }

    @Test
    fun should_delete_an_item_of_player() {
        val player = playerFacade.save(Player("John", items = listOf(Expandable(name = "Potion", uses = 1))))
        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0] is Expandable).isTrue()
        assertThat(player.items[0].name).isEqualTo("Potion")

        val expandable = player.getExpandableByName("Potion")!!
        player.removeItem(expandable)
        assertThat(player.items.size).isEqualTo(0)

        playerFacade.save(player)

        assertThat(player.items.size).isEqualTo(0)
    }
}