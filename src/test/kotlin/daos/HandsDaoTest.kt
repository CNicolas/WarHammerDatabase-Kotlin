package daos

import entities.HandEntity
import entities.tables.Hands
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert
import org.testng.Assert.assertNotNull
import org.testng.annotations.Test
import java.sql.Connection

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

            handsDao.add(HandEntity(handName))

            assertThat(handsDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_hands() {
        val handsToAdd = listOf(
                HandEntity("Hand1"),
                HandEntity("Hand2"),
                HandEntity("Hand3"))

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

            var resOfInsert = handsDao.add(HandEntity(handName))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(handsDao.findAll().size).isEqualTo(1)

            resOfInsert = handsDao.add(HandEntity(handName))
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

            handsDao.add(HandEntity(handName))

            assertThat(handsDao.findAll().size).isEqualTo(1)

            val hand = handsDao.findByName(handName)

            assertNotNull(hand)
            assertThat(hand?.name).isEqualTo(handName)
        }
    }

    @Test
    fun should_read_all_hands() {
        val handsToAdd = listOf(
                HandEntity("Hand1"),
                HandEntity("Hand2"),
                HandEntity("Hand3"))

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
            val id = handsDao.add(HandEntity(handName))
            assertThat(handsDao.findAll().size).isEqualTo(1)

            // FIND
            val hand = handsDao.findById(id)
            assertNotNull(hand)
            assertThat(hand?.name).isEqualTo(handName)

            // UPDATE
            val handToUpdate = hand?.copy(name = newHandName)
            handsDao.update(handToUpdate!!)
            assertThat(handsDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newHand = handsDao.findById(id)
            assertNotNull(newHand)
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
            val id1 = handsDao.add(HandEntity("Hand1"))
            val id2 = handsDao.add(HandEntity("Hand2"))
            assertThat(handsDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = handsDao.updateAll(listOf(HandEntity("Hand11", id1), HandEntity("Hand22", id2)))
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

            val res = handsDao.update(HandEntity("Inexistant"))
            assertThat(res).isEqualTo(-1)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = handsDao.update(HandEntity("Inexistant"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_hand() {
        val hand1 = HandEntity("Hand1")
        val hand2 = HandEntity("Hand2")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            handsDao.add(hand1)
            handsDao.add(hand2)

            assertThat(handsDao.findAll().size).isEqualTo(2)

            val res = handsDao.delete(hand1)
            Assert.assertTrue(res)
            assertThat(handsDao.findAll().size).isEqualTo(1)
            assertThat(handsDao.findByName("Hand1")).isNull()
            assertThat(handsDao.findByName("Hand2")).isNotNull()
        }
    }

    @Test
    fun should_delete_all_hands() {
        val hand1 = HandEntity("Hand1")
        val hand2 = HandEntity("Hand2")

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

            val res = handsDao.delete(HandEntity("Inexistant"))
            Assert.assertFalse(res)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            val res = handsDao.delete(HandEntity("Inexistant"))
            Assert.assertFalse(res)
        }
    }
    // endregion

    @Test
    fun should_return_one_hand_in_2_different_transaction() {
        val handName = "SampleName"

        Database.connect("jdbc:sqlite:file:testSqlite", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            if (Hands.exists()) {
                Hands.deleteAll()
            } else {

                create(Hands)
            }
        }

        transaction {
            logger.addLogger(StdOutSqlLogger)

            handsDao.add(HandEntity(handName))

            Hands.selectAll().forEach { println(it) }
        }

        transaction {
            val hand = handsDao.findByName(handName)

            assertNotNull(hand)
            assertThat(hand!!.name).isEqualTo("SampleName")
        }
    }
}