package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import warhammer.database.entities.hand.DifficultyLevel.HARD
import warhammer.database.entities.player.CharacteristicValue
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.entities.player.enums.Characteristic.AGILITY
import warhammer.database.entities.player.enums.Race.DWARF
import warhammer.database.entities.player.enums.Race.WOOD_ELF
import warhammer.database.entities.player.extensions.*
import warhammer.database.entities.player.playerLinked.item.Armor
import warhammer.database.entities.player.playerLinked.item.Expandable
import warhammer.database.entities.player.playerLinked.item.GenericItem
import warhammer.database.entities.player.playerLinked.item.Weapon
import warhammer.database.entities.player.playerLinked.item.enums.Quality.LOW
import warhammer.database.entities.player.playerLinked.item.enums.Quality.NORMAL

class PlayerTest {
    @Test
    fun should_get_agility_hand_HARD() {
        val player = Player("John", agility = CharacteristicValue(5, 2))
        Characteristic.values().forEach {
            if (it != AGILITY) {
                assertThat(player[it]).isEqualToComparingFieldByField(CharacteristicValue(0, 0))
            }
        }

        val hand = player[AGILITY].getHand("HandName", HARD)

        assertThat(hand.characteristicDicesCount).isEqualTo(5)
        assertThat(hand.fortuneDicesCount).isEqualTo(2)
        assertThat(hand.challengeDicesCount).isEqualTo(HARD.challengeDicesCount)
    }

    @Test
    fun should_compare_characteristic_value() {
        val charac1 = CharacteristicValue(3, 2)
        val charac2 = CharacteristicValue(2, 3)
        val charac3 = CharacteristicValue(3, 3)

        assertThat(charac1.compareTo(charac2)).isPositive()
        assertThat(charac1.compareTo(charac3)).isNegative()
        assertThat(charac2.compareTo(charac3)).isNegative()
    }

    @Test
    fun should_get_automatic_maximum_values() {
        val woodElf = Player(
                "John",
                WOOD_ELF,
                strength = CharacteristicValue(3, 1),
                toughness = CharacteristicValue(2, 1),
                willpower = CharacteristicValue(3)
        )
        assertThat(woodElf.maxExhaustion).isEqualTo(4)
        assertThat(woodElf.maxStress).isEqualTo(6)
        assertThat(woodElf.maxEncumbrance).isEqualTo(21)

        val dwarf = Player(
                "Jack",
                DWARF,
                strength = CharacteristicValue(4),
                toughness = CharacteristicValue(5),
                willpower = CharacteristicValue(3)
        )
        assertThat(dwarf.maxExhaustion).isEqualTo(10)
        assertThat(dwarf.maxStress).isEqualTo(6)
        assertThat(dwarf.maxEncumbrance).isEqualTo(30)
    }

    @Test
    fun should_update_item() {
        val weapon = Weapon(name = "Sword", damage = 4, criticalLevel = 3)
        val player = Player("John", items = listOf(weapon))

        assertThat(weapon.name).isEqualTo("Sword")
        assertThat(weapon.quality).isEqualTo(NORMAL)

        weapon.name = "Spear"
        weapon.quality = LOW

        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0].name).isEqualTo("Spear")
        assertThat(player.items[0].quality).isEqualTo(LOW)
    }

    @Test
    fun should_get_items_by_type() {
        val armor = Armor("Helmet")
        val expandable = Expandable("Potion")
        val genericItem = GenericItem("Rope")
        val weapon = Weapon("Spear")

        val player = Player("John", items = listOf(armor, expandable, genericItem, weapon))
        assertThat(player.items.size).isEqualTo(4)

        assertThat(player.getArmors().size).isEqualTo(1)
        assertThat(player.getArmorByName("Helmet")).isEqualToComparingFieldByField(armor)

        assertThat(player.getExpandables().size).isEqualTo(1)
        assertThat(player.getExpandableByName("Potion")).isEqualToComparingFieldByField(expandable)

        assertThat(player.getGenericItems().size).isEqualTo(1)
        assertThat(player.getGenericItemByName("Rope")).isEqualToComparingFieldByField(genericItem)

        assertThat(player.getWeapons().size).isEqualTo(1)
        assertThat(player.getWeaponByName("Spear")).isEqualToComparingFieldByField(weapon)
    }
}