package warhammer.database.daos

import warhammer.database.entities.Hand
import warhammer.database.tables.Hands
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.Test

class HandsDaoTest {
    private val handsDao = HandsDao()

    // region CREATE
    @Test
    fun should_add_a_hand() {
        val handName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            handsDao.add(Hand(handName))

            assertThat(handsDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_hands() {
        val handsToAdd = listOf(
                Hand("Hand1"),
                Hand("Hand2"),
                Hand("Hand3"))

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            handsDao.addAll(handsToAdd)

            assertThat(handsDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_hand_then_fail_to_add_it_again() {
        val handName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            var resOfInsert = handsDao.add(Hand(handName))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(handsDao.findAll().size).isEqualTo(1)

            resOfInsert = handsDao.add(Hand(handName))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(handsDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_hand() {
        val handName = "TheLegend27"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            handsDao.add(Hand(handName))

            assertThat(handsDao.findAll().size).isEqualTo(1)

            val hand = handsDao.findByName(handName)

            assertThat(hand).isNotNull()
            assertThat(hand?.name).isEqualTo(handName)
        }
    }

    @Test
    fun should_read_all_hands() {
        val handsToAdd = listOf(
                Hand("Hand1"),
                Hand("Hand2"),
                Hand("Hand3"))

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            handsDao.addAll(handsToAdd)

            val allInsertedHands = handsDao.findAll()
            assertThat(allInsertedHands.size).isEqualTo(3)
            assertThat(allInsertedHands.map { it?.name }).containsExactly("Hand1", "Hand2", "Hand3")
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_hand() {
        val handName = "TheLegend27"
        val newHandName = "TheLegend28"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            // ADD
            val id = handsDao.add(Hand(handName))
            assertThat(handsDao.findAll().size).isEqualTo(1)

            // FIND
            val hand = handsDao.findById(id)
            assertThat(hand).isNotNull()
            assertThat(hand?.name).isEqualTo(handName)

            // UPDATE
            val handToUpdate = hand?.copy(name = newHandName)
            handsDao.update(handToUpdate!!)
            assertThat(handsDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newHand = handsDao.findById(id)
            assertThat(newHand).isNotNull()
            assertThat(newHand?.name).isEqualTo(newHandName)
        }
    }

    @Test
    fun should_update_all_hands() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            // ADD
            val id1 = handsDao.add(Hand("Hand1"))
            val id2 = handsDao.add(Hand("Hand2"))
            assertThat(handsDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = handsDao.updateAll(listOf(Hand("Hand11", id1), Hand("Hand22", id2)))
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedHands = handsDao.findAll()
            assertThat(allInsertedHands.size).isEqualTo(2)
            assertThat(allInsertedHands.map { it?.name }).containsExactly("Hand11", "Hand22")
            assertThat(allInsertedHands.map { it?.id }).containsExactly(id1, id2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_hand() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            assertThat(handsDao.findAll().size).isEqualTo(0)

            val res = handsDao.update(Hand("Inexistant"))
            assertThat(res).isEqualTo(-1)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = handsDao.update(Hand("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_hand() {
        val hand1 = Hand("Hand1")
        val hand2 = Hand("Hand2")
        val hand3 = Hand("Hand3")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            val addAllResult = handsDao.addAll(listOf(hand1, hand2, hand3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = handsDao.delete(hand2)
            assertThat(res).isEqualTo(1)
            assertThat(handsDao.findAll().size).isEqualTo(2)
            assertThat(handsDao.findByName("Hand1")).isNotNull()
            assertThat(handsDao.findByName("Hand2")).isNull()
            assertThat(handsDao.findByName("Hand3")).isNotNull()
        }
    }

    @Test
    fun should_delete_all_hands() {
        val hand1 = Hand("Hand1")
        val hand2 = Hand("Hand2")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            handsDao.add(hand1)
            handsDao.add(hand2)
            assertThat(handsDao.findAll().size).isEqualTo(2)

            handsDao.deleteAll()
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_hand() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            assertThat(handsDao.findAll().size).isEqualTo(0)

            val res = handsDao.delete(Hand("Inexistant"))
            assertThat(res).isEqualTo(0)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = handsDao.delete(Hand("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}