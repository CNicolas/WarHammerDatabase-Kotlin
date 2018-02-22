package warhammer.database.hand

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.HandFacade
import warhammer.database.entities.hand.Hand

class HandFacadeTest {
    private val handFacade = HandFacade(
            databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared",
            driver = "org.sqlite.JDBC"
    )

    @BeforeMethod
    fun clearBeforeTest() {
        handFacade.deleteAll()
    }

    @Test
    fun should_add_a_simple_hand() {
        val handName = "HandName"

        val hand = handFacade.save(Hand(handName))
        assertThat(hand.name).isEqualTo(handName)
    }

    @Test
    fun should_find_an_added_simple_hand() {
        val handName = "HandName"

        val hand = handFacade.save(Hand(handName))
        assertThat(hand.name).isEqualTo(handName)

        val foundHand = handFacade.find(handName)
        assertThat(foundHand).isNotNull()
        assertThat(foundHand!!.name).isEqualTo(handName)

        assertThat(hand).isEqualToComparingFieldByField(foundHand)
    }

    @Test
    fun should_update_an_added_simple_hand() {
        val handName = "HandName"

        val hand = handFacade.save(Hand(handName))
        assertThat(hand.name).isEqualTo(handName)

        hand.characteristicDicesCount = 3
        hand.challengeDicesCount = 1

        val updatedHand = handFacade.save(hand)
        assertThat(updatedHand.name).isEqualTo(handName)
        assertThat(updatedHand.characteristicDicesCount).isEqualTo(3)
        assertThat(updatedHand.challengeDicesCount).isEqualTo(1)
        assertThat(updatedHand).isEqualToComparingFieldByField(hand)
    }

    @Test
    fun should_update_name_of_hand() {
        val hand = handFacade.save(Hand("Hand1"))
        assertThat(hand.name).isEqualTo("Hand1")

        hand.name = "Hand2"

        val updatedHand = handFacade.save(hand)
        assertThat(updatedHand.name).isEqualTo("Hand2")
        assertThat(updatedHand).isEqualToComparingFieldByField(hand)

        val allHands = handFacade.findAll()
        assertThat(allHands.size).isEqualTo(1)
    }

    @Test
    fun should_delete_a_hand() {
        val handName = "HandName"

        var hand = handFacade.save(Hand(handName))
        assertThat(hand.name).isEqualTo(handName)

        handFacade.deleteHand(hand)
        assertThat(handFacade.findAll()).isEmpty()

        hand = handFacade.save(Hand(handName))
        assertThat(hand.name).isEqualTo(handName)

        handFacade.deleteHand(handName)
        assertThat(handFacade.findAll()).isEmpty()
    }
}