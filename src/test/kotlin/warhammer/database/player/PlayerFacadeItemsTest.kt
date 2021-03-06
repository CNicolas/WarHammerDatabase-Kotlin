package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.PlayerFacade
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.item.Armor
import warhammer.database.entities.player.playerLinked.item.Expandable
import warhammer.database.entities.player.playerLinked.item.GenericItem
import warhammer.database.entities.player.playerLinked.item.Weapon
import warhammer.database.entities.player.playerLinked.item.enums.ArmorType.HELMET
import warhammer.database.entities.player.playerLinked.item.enums.Quality
import warhammer.database.extensions.items.*

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
        assertThat(player.name).isEqualTo(playerName)
        assertThat(player.items).isEmpty()
        assertThat(player.encumbrance).isZero()

        player.addItem(Weapon(name = "Sword", damage = 4, isEquipped = true, encumbrance = 3))

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
        assertThat(updatedPlayer.encumbrance).isEqualTo(3)
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
        assertThat(genericItem.quality).isEqualTo(Quality.NORMAL)

        genericItem.quality = Quality.LOW

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.items.size).isEqualTo(1)
        assertThat(updatedPlayer.items[0] is GenericItem).isTrue()
        val newItem = updatedPlayer.getGenericItems()[0]
        assertThat(newItem.isEquipped).isNull()
        assertThat(newItem.quality).isEqualTo(Quality.LOW)
    }

    @Test
    fun should_update_name_of_item_of_a_player() {
        val player = playerFacade.save(
                Player("John", items = listOf(Armor(name = "Helmet", soak = 2, subType = HELMET, defense = 1)))
        )
        assertThat(player.name).isEqualTo("John")
        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0] is Armor).isTrue()
        val armor = player.items[0] as Armor
        assertThat(armor.name).isEqualTo("Helmet")
        assertThat(armor.soak).isEqualTo(2)
        assertThat(armor.defense).isEqualTo(1)
        assertThat(armor.subType).isEqualTo(HELMET)

        armor.name = "Shield"

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.items.size).isEqualTo(1)
        assertThat(updatedPlayer.items[0] is Armor).isTrue()

        val newWeapon = updatedPlayer.getArmors()[0]
        assertThat(newWeapon.name).isEqualTo("Shield")
        assertThat(armor.soak).isEqualTo(2)
        assertThat(armor.defense).isEqualTo(1)
        assertThat(armor.subType).isEqualTo(HELMET)
    }

    @Test
    fun should_delete_items_of_player_then_player() {
        var player = playerFacade.save(Player("John", items = listOf(Weapon(name = "Sword", damage = 4, criticalLevel = 3))))
        assertThat(player.items.size).isEqualTo(1)

        player.removeAllItems()
        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.items).isEmpty()

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