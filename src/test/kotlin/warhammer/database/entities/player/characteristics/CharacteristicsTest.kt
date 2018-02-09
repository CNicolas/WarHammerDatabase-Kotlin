package warhammer.database.entities.player.characteristics

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.characteristics.Characteristic.STRENGTH

class CharacteristicsTest {
    @Test
    fun should_set_strength() {
        val playerCharacteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(4, 1))
        assertThat(playerCharacteristics[STRENGTH].value).isEqualTo(4)
        assertThat(playerCharacteristics[STRENGTH].fortuneValue).isEqualTo(1)

        playerCharacteristics[STRENGTH] = CharacteristicValue(3, 2)
        assertThat(playerCharacteristics[STRENGTH].value).isEqualTo(3)
        assertThat(playerCharacteristics[STRENGTH].fortuneValue).isEqualTo(2)
    }

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
        val playerCharacteristics = PlayerCharacteristicsMap(strengthValue = CharacteristicValue(4, 1))
        val player = Player("SamplePlayer", characteristics = playerCharacteristics)
        val hand = player.characteristics[STRENGTH].getHand("SampleHand")

        assertThat(hand).isNotNull()
        assertThat(hand.name).isEqualTo("SampleHand")
        assertThat(hand.characteristicDicesCount).isEqualTo(4)
        assertThat(hand.fortuneDicesCount).isEqualTo(1)
        assertThat(hand.challengeDicesCount).isEqualTo(0)
    }
}