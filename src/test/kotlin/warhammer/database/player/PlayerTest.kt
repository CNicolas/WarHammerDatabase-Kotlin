package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import warhammer.database.entities.player.*
import warhammer.database.entities.player.enums.Race.DWARF
import warhammer.database.entities.player.enums.Race.WOOD_ELF
import warhammer.database.entities.player.item.Weapon
import warhammer.database.entities.player.item.enums.Quality.LOW
import warhammer.database.entities.player.item.enums.Quality.NORMAL

class PlayerTest {
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
        player.updateItem(weapon)

        assertThat(player.items.size).isEqualTo(1)
        assertThat(player.items[0].name).isEqualTo("Spear")
        assertThat(player.items[0].quality).isEqualTo(LOW)
    }
}