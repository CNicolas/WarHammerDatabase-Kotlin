package warhammer.database.services

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.entities.Hand
import warhammer.database.tables.HandsTable

class HandsDatabaseServiceTest {
    private val handsService = HandsDatabaseService(databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared")
    private val handName = "SampleName"
    private val sampleHand = Hand(handName, characteristicDicesCount = 3, challengeDicesCount = 1)

    @BeforeMethod
    fun clearDatabase() {
        handsService.deleteAll()
    }

    @Test
    fun should_be_initialize() {
        transaction {
            assertThat(HandsTable.exists()).isTrue()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_hand() {
        val addedHand = handsService.add(sampleHand)
        assertThat(handsService.countAll()).isEqualTo(1)
        assertThat(addedHand?.name).isEqualTo(handName)
        assertThat(addedHand?.characteristicDicesCount).isEqualTo(sampleHand.characteristicDicesCount)
        assertThat(addedHand?.challengeDicesCount).isEqualTo(sampleHand.challengeDicesCount)
    }

    @Test
    fun should_add_all_hands() {
        val handsToAdd = listOf(
                Hand("Hand1"),
                Hand("Hand2"),
                Hand("Hand3"))

        val addAllResult = handsService.addAll(handsToAdd)
        assertThat(addAllResult.size).isEqualTo(handsToAdd.size)
        assertThat(addAllResult.map { it?.name }).containsExactly("Hand1", "Hand2", "Hand3")
        assertThat(handsService.countAll()).isEqualTo(handsToAdd.size)
    }

    @Test
    fun should_add_a_hand_then_fail_to_add_it_again() {
        val addedHand1 = handsService.add(sampleHand)
        assertThat(handsService.countAll()).isEqualTo(1)
        assertThat(addedHand1?.name).isEqualTo(handName)

        val addedHand2 = handsService.add(sampleHand)
        assertThat(addedHand2).isNull()
        assertThat(handsService.countAll()).isEqualTo(1)
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_hand() {
        handsService.add(sampleHand)
        assertThat(handsService.countAll()).isEqualTo(1)

        val hand = handsService.findByName(handName)
        assertThat(hand).isNotNull()
        assertThat(hand?.name).isEqualTo(handName)
        assertThat(hand?.characteristicDicesCount).isEqualTo(sampleHand.characteristicDicesCount)
        assertThat(hand?.challengeDicesCount).isEqualTo(sampleHand.challengeDicesCount)
    }

    @Test
    fun should_find_a_hand_by_id() {
        handsService.add(sampleHand)
        assertThat(handsService.countAll()).isEqualTo(1)

        val hand = handsService.findById(1)
        assertThat(hand).isNotNull()
        assertThat(hand?.name).isEqualTo(handName)
    }

    @Test
    fun should_read_all_hands() {
        val handsToAdd = listOf(
                Hand("Hand1"),
                Hand("Hand2"),
                Hand("Hand3"))

        handsService.addAll(handsToAdd)

        val allInsertedHands = handsService.findAll()
        assertThat(allInsertedHands.size).isEqualTo(3)
        assertThat(allInsertedHands.map { it?.name }).containsExactly("Hand1", "Hand2", "Hand3")
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_hand() {
        val newHandName = "MyHandIsNew"

        // ADD
        val hand = handsService.add(sampleHand)
        assertThat(handsService.countAll()).isEqualTo(1)

        // FIND
        assertThat(hand).isNotNull()
        assertThat(hand?.name).isEqualTo(handName)
        assertThat(hand?.characteristicDicesCount).isEqualTo(sampleHand.characteristicDicesCount)
        assertThat(hand?.challengeDicesCount).isEqualTo(sampleHand.challengeDicesCount)

        // UPDATE
        val handToUpdate = hand?.copy(name = newHandName, characteristicDicesCount = 1)
        val newHand = handsService.update(handToUpdate!!)

        // VERIFY
        assertThat(handsService.countAll()).isEqualTo(1)
        assertThat(newHand).isNotNull()
        assertThat(newHand?.name).isEqualTo(newHandName)
        assertThat(newHand?.characteristicDicesCount).isEqualTo(handToUpdate.characteristicDicesCount)
        assertThat(newHand?.challengeDicesCount).isEqualTo(sampleHand.challengeDicesCount)
    }

    @Test
    fun should_update_all_hands() {
        // ADD
        val hand1 = handsService.add(Hand("Hand1"))
        val hand2 = handsService.add(Hand("Hand2"))
        assertThat(handsService.countAll()).isEqualTo(2)

        // UPDATE
        val updatedHands = handsService.updateAll(listOf(hand1!!.copy(name = "Hand11"), hand2!!.copy(name = "Hand22")))

        // VERIFY
        assertThat(updatedHands.size).isEqualTo(2)
        assertThat(updatedHands.map { it?.name }).containsExactly("Hand11", "Hand22")
        assertThat(updatedHands.map { it?.id }).containsExactly(hand1.id, hand2.id)
    }

    @Test
    fun should_return_false_when_update_a_non_existent_hand() {
        assertThat(handsService.countAll()).isEqualTo(0)

        val updatedHand = handsService.update(Hand("Unknown"))
        assertThat(updatedHand).isNull()
        assertThat(handsService.findAll()).isEmpty()
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_hand() {
        val hand1 = Hand("Hand1")
        val hand2 = Hand("Hand2")
        val hand3 = Hand("Hand3")

        val addAllResult = handsService.addAll(listOf(hand1, hand2, hand3))
        assertThat(addAllResult.size).isEqualTo(3)
        assertThat(addAllResult.map { it?.name }).containsExactly("Hand1", "Hand2", "Hand3")

        val isDeleted = handsService.delete(hand2)
        assertThat(isDeleted).isTrue()
        assertThat(handsService.countAll()).isEqualTo(2)
        assertThat(handsService.findByName("Hand1")).isNotNull()
        assertThat(handsService.findByName("Hand2")).isNull()
        assertThat(handsService.findByName("Hand3")).isNotNull()
    }

    @Test
    fun should_delete_all_hands() {
        val hand1 = Hand("Hand1")
        val hand2 = Hand("Hand2")

        handsService.add(hand1)
        handsService.add(hand2)
        assertThat(handsService.countAll()).isEqualTo(2)

        handsService.deleteAll()
        assertThat(handsService.findAll()).isEmpty()
    }

    @Test
    fun should_return_false_when_delete_a_non_existent_hand() {
        assertThat(handsService.countAll()).isEqualTo(0)

        val res = handsService.delete(Hand("Unknown"))
        assertThat(res).isFalse()
        assertThat(handsService.findAll()).isEmpty()
    }
    // endregion
}