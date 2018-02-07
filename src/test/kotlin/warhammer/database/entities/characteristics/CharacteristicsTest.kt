package warhammer.database.entities.characteristics

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import warhammer.database.entities.Player

class CharacteristicsTest {
    @Test
    fun should_create_hand_from_characteristics_value() {
        val characteristicValue = CharacteristicValue(3, 2)
        val hand = characteristicValue.getHand("SampleHand")

        assertThat(hand).isNotNull()
        assertThat(hand.name).isEqualTo("SampleHand")
        assertThat(hand.characteristicDicesCount).isEqualTo(3)
        assertThat(hand.fortuneDicesCount).isEqualTo(2)
        assertThat(hand.challengeDicesCount).isEqualTo(0)
    }

    @Test
    fun should_create_hand_from_player_strength() {
        val playerCharacteristics = PlayerCharacteristics(strengthValue = CharacteristicValue(4, 1))
        val player = Player("SamplePlayer", characteristics = playerCharacteristics)
        val hand = player.characteristics.getHand(Characteristic.STRENGTH)

        assertThat(hand).isNotNull()
        assertThat(hand.name).isEqualTo("Hand")
        assertThat(hand.characteristicDicesCount).isEqualTo(4)
        assertThat(hand.fortuneDicesCount).isEqualTo(1)
        assertThat(hand.challengeDicesCount).isEqualTo(0)
    }
}