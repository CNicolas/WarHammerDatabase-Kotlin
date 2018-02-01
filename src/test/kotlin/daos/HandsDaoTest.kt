package daos

import entities.Hand
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

    @Test
    fun should_return_one_hand_in_a_single_transaction() {
        val handName = "SampleName"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            SchemaUtils.create(Hands)

            handsDao.add(Hand(handName))

            val hand = handsDao.findByName(handName)

            assertNotNull(hand)
            assertThat(hand!!.name).isEqualTo(handName)
        }
    }

    @Test
    fun should_insert_several_hands_with_addAll() {
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

    @Test
    fun should_insert_then_find_and_update_hand() {
        val handName = "SampleHandName"
        val newHandName = "NewSampleHandName"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            // ADD
            val id = handsDao.add(Hand(handName))
            assertThat(Hands.selectAll().count()).isEqualTo(1)

            // FIND
            val hand = handsDao.findById(id)
            assertNotNull(hand)
            assertThat(hand?.name).isEqualTo(handName)

            // UPDATE
            val handToUpdate = hand?.copy(name = newHandName)
            handsDao.update(handToUpdate!!)
            assertThat(Hands.selectAll().count()).isEqualTo(1)

            // VERIFY
            val newHand = handsDao.findById(id)
            assertNotNull(newHand)
            assertThat(newHand?.name).isEqualTo(newHandName)
        }
    }

    @Test
    fun should_updateAll_hands() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            // ADD
            val id1 = handsDao.add(Hand("Hand1"))
            val id2 = handsDao.add(Hand("Hand2"))
            assertThat(Hands.selectAll().count()).isEqualTo(2)

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
    fun should_delete_a_hand() {
        val hand1 = Hand("Hand1")
        val hand2 = Hand("Hand2")

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
        val hand1 = Hand("Hand1")
        val hand2 = Hand("Hand2")

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Hands)

            handsDao.add(hand1)
            handsDao.add(hand2)

            assertThat(handsDao.findAll().size).isEqualTo(2)

            val res = handsDao.deleteAll()
            Assert.assertTrue(res)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }
    
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

            handsDao.add(Hand(handName))

            Hands.selectAll().forEach { println(it) }
        }

        transaction {
            val hand = handsDao.findByName(handName)

            assertNotNull(hand)
            assertThat(hand!!.name).isEqualTo("SampleName")
        }
    }
}