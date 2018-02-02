package services

import entities.HandEntity
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

class HandsServiceTest {
    @Test
    fun should_add_a_hand() {
        val handName = "SampleName"
        val handsService = HandsService(databaseUrl = "jdbc:sqlite:mem:test?mode=memory&cache=shared", driver = "org.h2.Driver")
        val handToAdd = HandEntity(handName, characteristicDicesCount = 3)

        val addResult = handsService.add(handToAdd)
        assertThat(addResult).isEqualTo(1)

        val foundHand = handsService.findByName(handName)
        assertThat(foundHand).isNotNull()
        assertThat(foundHand?.characteristicDicesCount).isEqualTo(3)
    }
}