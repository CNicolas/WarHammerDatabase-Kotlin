package warhammer.database.entities.player.characteristics

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import warhammer.database.entities.DifficultyLevel
import warhammer.database.entities.mapping.mapToPlayerCharacteristics
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerCharacteristicsEntity
import warhammer.database.entities.player.characteristics.Characteristic.*

class CharacteristicsTest {
    @Test
    fun should_get_intelligence() {
        val charac = CharacteristicValue(3, 2)
        val playerCharacteristics = PlayerCharacteristics(charac, charac, charac, charac, charac, charac)

        assertThat(playerCharacteristics[INTELLIGENCE].value).isEqualTo(3)
        assertThat(playerCharacteristics[INTELLIGENCE].fortuneValue).isEqualTo(2)
        assertThat(playerCharacteristics.intelligence.compareTo(charac)).isZero()

        assertThat(playerCharacteristics[STRENGTH].compareTo(charac)).isZero()
        assertThat(playerCharacteristics[TOUGHNESS].compareTo(charac)).isZero()
        assertThat(playerCharacteristics[AGILITY].compareTo(charac)).isZero()
        assertThat(playerCharacteristics[INTELLIGENCE].compareTo(charac)).isZero()
        assertThat(playerCharacteristics[WILLPOWER].compareTo(charac)).isZero()
        assertThat(playerCharacteristics[FELLOWSHIP].compareTo(charac)).isZero()
    }

    @Test
    fun should_set_strength() {
        val playerCharacteristics = PlayerCharacteristics(strength = CharacteristicValue(4, 1))
        assertThat(playerCharacteristics.strength.value).isEqualTo(4)
        assertThat(playerCharacteristics.strength.fortuneValue).isEqualTo(1)

        playerCharacteristics.strength = CharacteristicValue(3, 2)
        assertThat(playerCharacteristics.strength.value).isEqualTo(3)
        assertThat(playerCharacteristics.strength.fortuneValue).isEqualTo(2)
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
        val playerCharacteristics = PlayerCharacteristics(strength = CharacteristicValue(4, 1))
        val player = Player(name = "SamplePlayer", characteristics = playerCharacteristics)
        val hand = player.strength.getHand("SampleHand")

        assertThat(hand).isNotNull()
        assertThat(hand.name).isEqualTo("SampleHand")
        assertThat(hand.characteristicDicesCount).isEqualTo(4)
        assertThat(hand.fortuneDicesCount).isEqualTo(1)
        assertThat(hand.challengeDicesCount).isEqualTo(0)
    }

    @Test
    fun should_create_hand_with_difficulty_level() {
        val playerCharacteristics = PlayerCharacteristics(strength = CharacteristicValue(4, 1))
        val player = Player(name = "SamplePlayer", characteristics = playerCharacteristics)
        val hand = player.strength.getHand("SampleHand", DifficultyLevel.MEDIUM)

        assertThat(hand).isNotNull()
        assertThat(hand.name).isEqualTo("SampleHand")
        assertThat(hand.characteristicDicesCount).isEqualTo(4)
        assertThat(hand.fortuneDicesCount).isEqualTo(1)
        assertThat(hand.challengeDicesCount).isEqualTo(2)
    }

    @Test
    fun should_return_empty_player_characteristics() {
        val entity: PlayerCharacteristicsEntity? = null
        val expected = PlayerCharacteristics()
        assertThat(entity.mapToPlayerCharacteristics()).isEqualToComparingFieldByField(expected)
    }

    @Test
    fun should_compare_two_values() {
        val charac1 = CharacteristicValue(5, 3)
        val charac2 = CharacteristicValue(6, 2)
        assertThat(charac1.compareTo(charac2)).isNegative()
    }
}