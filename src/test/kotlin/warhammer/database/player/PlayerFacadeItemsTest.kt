package warhammer.database.player

import org.assertj.core.api.Assertions
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.PlayerFacade
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.extensions.*
import warhammer.database.entities.player.playerLinked.item.Armor
import warhammer.database.entities.player.playerLinked.item.Expandable
import warhammer.database.entities.player.playerLinked.item.GenericItem
import warhammer.database.entities.player.playerLinked.item.Weapon
import warhammer.database.entities.player.playerLinked.item.enums.Quality

class PlayerFacadeItemsTest {
    private val playerFacade = PlayerFacade(
            databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared",
            driver = "org.sqlite.JDBC"
    )

    @BeforeMethod
    fun clearBeforeTest() {
        playerFacade.deleteAll()
    }

    @Test
    fun should_update_items_of_a_player() {
        val playerName = "PlayerName"

        val player = playerFacade.save(Player(playerName))
        Assertions.assertThat(player.name).isEqualTo(playerName)
        Assertions.assertThat(player.items).isEmpty()

        player.addItem(Weapon(name = "Sword", damage = 4, isEquipped = true))

        val updatedPlayer = playerFacade.save(player)
        Assertions.assertThat(updatedPlayer.name).isEqualTo(playerName)
        Assertions.assertThat(updatedPlayer.items).isNotEmpty()
        Assertions.assertThat(updatedPlayer.items).isEqualTo(player.items)
        Assertions.assertThat(updatedPlayer.items[0] is Weapon).isTrue()
        val weapon = updatedPlayer.getWeapons()[0]
        Assertions.assertThat(weapon.name).isEqualTo("Sword")
        Assertions.assertThat(weapon.damage).isEqualTo(4)
        Assertions.assertThat(weapon.isEquipped).isTrue()
        Assertions.assertThat(updatedPlayer).isEqualToComparingFieldByField(player)
    }

    @Test
    fun should_update_field_of_item_of_a_player() {
        val player = playerFacade.save(Player("John", items = listOf(GenericItem(name = "Rope"))))
        Assertions.assertThat(player.name).isEqualTo("John")
        Assertions.assertThat(player.items.size).isEqualTo(1)
        Assertions.assertThat(player.items[0] is GenericItem).isTrue()
        val genericItem = player.getGenericItems()[0]
        Assertions.assertThat(genericItem.name).isEqualTo("Rope")
        Assertions.assertThat(genericItem.isEquipped).isNull()
        Assertions.assertThat(genericItem.quality).isEqualTo(Quality.NORMAL)

        genericItem.quality = Quality.LOW
        player.updateItem(genericItem)

        val updatedPlayer = playerFacade.save(player)
        Assertions.assertThat(updatedPlayer.name).isEqualTo("John")
        Assertions.assertThat(updatedPlayer.items.size).isEqualTo(1)
        Assertions.assertThat(updatedPlayer.items[0] is GenericItem).isTrue()
        val newItem = updatedPlayer.getGenericItems()[0]
        Assertions.assertThat(newItem.isEquipped).isNull()
        Assertions.assertThat(newItem.quality).isEqualTo(Quality.LOW)
    }

    @Test
    fun should_update_name_of_item_of_a_player() {
        val player = playerFacade.save(Player("John", items = listOf(Armor(name = "Helmet", soak = 2, defense = 1))))
        Assertions.assertThat(player.name).isEqualTo("John")
        Assertions.assertThat(player.items.size).isEqualTo(1)
        Assertions.assertThat(player.items[0] is Armor).isTrue()
        val armor = player.items[0] as Armor
        Assertions.assertThat(armor.name).isEqualTo("Helmet")
        Assertions.assertThat(armor.soak).isEqualTo(2)
        Assertions.assertThat(armor.defense).isEqualTo(1)

        armor.name = "Shield"
        player.updateItem(armor)

        val updatedPlayer = playerFacade.save(player)
        Assertions.assertThat(updatedPlayer.name).isEqualTo("John")
        Assertions.assertThat(updatedPlayer.items.size).isEqualTo(1)
        Assertions.assertThat(updatedPlayer.items[0] is Armor).isTrue()

        val newWeapon = updatedPlayer.getArmors()[0]
        Assertions.assertThat(newWeapon.name).isEqualTo("Shield")
        Assertions.assertThat(armor.soak).isEqualTo(2)
        Assertions.assertThat(armor.defense).isEqualTo(1)
    }

    @Test
    fun should_delete_items_of_player_then_player() {
        var player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        Assertions.assertThat(player.items.size).isEqualTo(1)

        playerFacade.deleteAllItemsOfPlayer(player)
        Assertions.assertThat(player.items).isEmpty()

        playerFacade.deletePlayer(player)
        Assertions.assertThat(playerFacade.findAll()).isEmpty()

        player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        Assertions.assertThat(player.items.size).isEqualTo(1)
        playerFacade.deletePlayer("John")
        Assertions.assertThat(playerFacade.findAll()).isEmpty()
    }

    @Test
    fun should_delete_an_item_of_player() {
        val player = playerFacade.save(Player("John", items = listOf(Expandable(name = "Potion", uses = 1))))
        Assertions.assertThat(player.items.size).isEqualTo(1)
        Assertions.assertThat(player.items[0] is Expandable).isTrue()
        Assertions.assertThat(player.items[0].name).isEqualTo("Potion")

        val expandable = player.getExpandableByName("Potion")!!
        player.removeItem(expandable)
        Assertions.assertThat(player.items.size).isEqualTo(0)

        playerFacade.save(player)

        Assertions.assertThat(player.items.size).isEqualTo(0)
    }
}