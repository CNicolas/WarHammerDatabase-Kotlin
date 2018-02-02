package services

import entities.HandEntity
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class HandsServiceTest {
    private val handsService = HandsService(databaseUrl = "jdbc:sqlite:test:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
    private val handName = "SampleName"
    private val sampleHand = HandEntity(handName, characteristicDicesCount = 3, challengeDicesCount = 1)

    @BeforeMethod
    fun clearDatabase() {
        handsService.deleteAll()
    }

    // region CREATE
    @Test
    fun should_add_a_hand() {
        val addResult = handsService.add(sampleHand)
        assertThat(addResult).isEqualTo(1)
        assertThat(handsService.countAll()).isEqualTo(1)
    }

    @Test
    fun should_add_all_hands() {
        val handsToAdd = listOf(
                HandEntity("Hand1"),
                HandEntity("Hand2"),
                HandEntity("Hand3"))

        val addAllResult = handsService.addAll(handsToAdd)
        assertThat(addAllResult.size).isEqualTo(handsToAdd.size)
        assertThat(addAllResult).containsExactly(1, 2, 3)
        assertThat(handsService.countAll()).isEqualTo(handsToAdd.size)
    }

    @Test
    fun should_add_a_hand_then_fail_to_add_it_again() {
        var resOfInsert = handsService.add(sampleHand)
        assertThat(resOfInsert).isEqualTo(1)
        assertThat(handsService.countAll()).isEqualTo(1)

        resOfInsert = handsService.add(sampleHand)
        assertThat(resOfInsert).isEqualTo(-1)
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
    fun should_read_all_hands() {
        val handsToAdd = listOf(
                HandEntity("Hand1"),
                HandEntity("Hand2"),
                HandEntity("Hand3"))

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
        val id = handsService.add(sampleHand)
        assertThat(handsService.countAll()).isEqualTo(1)

        // FIND
        val hand = handsService.findById(id)
        assertThat(hand).isNotNull()
        assertThat(hand?.name).isEqualTo(handName)
        assertThat(hand?.characteristicDicesCount).isEqualTo(sampleHand.characteristicDicesCount)
        assertThat(hand?.challengeDicesCount).isEqualTo(sampleHand.challengeDicesCount)

        // UPDATE
        val handToUpdate = hand?.copy(name = newHandName, characteristicDicesCount = 1)
        handsService.update(handToUpdate!!)
        assertThat(handsService.countAll()).isEqualTo(1)

        // VERIFY
        val newHand = handsService.findById(id)
        assertThat(newHand).isNotNull()
        assertThat(newHand?.name).isEqualTo(newHandName)
        assertThat(newHand?.characteristicDicesCount).isEqualTo(handToUpdate.characteristicDicesCount)
        assertThat(newHand?.challengeDicesCount).isEqualTo(sampleHand.challengeDicesCount)
    }

    @Test
    fun should_update_all_hands() {
        // ADD
        val id1 = handsService.add(HandEntity("Hand1"))
        val id2 = handsService.add(HandEntity("Hand2"))
        assertThat(handsService.countAll()).isEqualTo(2)

        // UPDATE
        val updatedIds = handsService.updateAll(listOf(HandEntity("Hand11", id1), HandEntity("Hand22", id2)))
        assertThat(updatedIds).containsExactly(id1, id2)

        // VERIFY
        val allInsertedHands = handsService.findAll()
        assertThat(allInsertedHands.size).isEqualTo(2)
        assertThat(allInsertedHands.map { it?.name }).containsExactly("Hand11", "Hand22")
        assertThat(allInsertedHands.map { it?.id }).containsExactly(id1, id2)
    }

    @Test
    fun should_return_false_when_update_a_inexistant_hand() {
        assertThat(handsService.countAll()).isEqualTo(0)

        val res = handsService.update(HandEntity("Inexistant"))
        assertThat(res).isEqualTo(-1)
        assertThat(handsService.findAll()).isEmpty()
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_hand() {
        val hand1 = HandEntity("Hand1")
        val hand2 = HandEntity("Hand2")
        val hand3 = HandEntity("Hand3")

        val addAllResult = handsService.addAll(listOf(hand1, hand2, hand3))
        assertThat(addAllResult.size).isEqualTo(3)
        assertThat(addAllResult).containsExactly(1, 2, 3)


        val res = handsService.delete(hand2)
        assertThat(res).isEqualTo(2)
        assertThat(handsService.countAll()).isEqualTo(2)
        assertThat(handsService.findByName("Hand1")).isNotNull()
        assertThat(handsService.findByName("Hand2")).isNull()
        assertThat(handsService.findByName("Hand3")).isNotNull()
    }

    @Test
    fun should_delete_all_hands() {
        val hand1 = HandEntity("Hand1")
        val hand2 = HandEntity("Hand2")

        handsService.add(hand1)
        handsService.add(hand2)
        assertThat(handsService.countAll()).isEqualTo(2)

        handsService.deleteAll()
        assertThat(handsService.findAll()).isEmpty()
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_hand() {
        assertThat(handsService.countAll()).isEqualTo(0)

        val res = handsService.delete(HandEntity("Inexistant"))
        assertThat(res).isEqualTo(-1)
        assertThat(handsService.findAll()).isEmpty()
    }
    // endregion
}