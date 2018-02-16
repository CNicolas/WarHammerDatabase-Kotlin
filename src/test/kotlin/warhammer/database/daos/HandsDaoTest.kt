package warhammer.database.daos

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.entities.Hand
import warhammer.database.tables.HandsTable
import java.sql.Connection

class HandsDaoTest {
    private val handsDao = HandsDao()

    @BeforeMethod
    fun initializeDatabase() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(HandsTable)

            HandsTable.deleteAll()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_hand() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            handsDao.add(Hand(name = "HandName"))

            assertThat(handsDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_hands() {
        val handsToAdd = listOf(
                Hand(name = "Hand1"),
                Hand(name = "Hand2"),
                Hand(name = "Hand3"))

        transaction {
            logger.addLogger(StdOutSqlLogger)

            handsDao.addAll(handsToAdd)

            assertThat(handsDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_hand_then_fail_to_add_it_again() {
        val handName = "PlayerName"

        transaction {
            logger.addLogger(StdOutSqlLogger)

            var resOfInsert = handsDao.add(Hand(name = handName))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(handsDao.findAll().size).isEqualTo(1)

            resOfInsert = handsDao.add(Hand(name = handName))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(handsDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_hand() {
        val handName = "PlayerName"

        transaction {
            logger.addLogger(StdOutSqlLogger)

            handsDao.add(Hand(name = handName))
            assertThat(handsDao.findAll().size).isEqualTo(1)

            val hand = handsDao.findByName(handName)
            assertThat(hand).isNotNull()
            assertThat(hand?.name).isEqualTo(handName)
        }
    }

    @Test
    fun should_read_all_hands() {
        val handsToAdd = listOf(
                Hand(name = "Hand1"),
                Hand(name = "Hand2"),
                Hand(name = "Hand3"))

        transaction {
            logger.addLogger(StdOutSqlLogger)

            handsDao.addAll(handsToAdd)

            val allInsertedHands = handsDao.findAll()
            assertThat(allInsertedHands.size).isEqualTo(3)
            assertThat(allInsertedHands.map { it.name }).containsExactly("Hand1", "Hand2", "Hand3")
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_hand() {
        val handName = "HandName1"
        val newHandName = "HandName2"

        transaction {
            logger.addLogger(StdOutSqlLogger)

            // ADD
            val id = handsDao.add(Hand(name = handName))
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
        transaction {
            logger.addLogger(StdOutSqlLogger)

            // ADD
            val id1 = handsDao.add(Hand(name = "Hand1"))
            val id2 = handsDao.add(Hand(name = "Hand2"))
            assertThat(handsDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = handsDao.updateAll(listOf(Hand(name = "Hand11", id = id1), Hand(name = "Hand22", id = id2)))
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedHands = handsDao.findAll()
            assertThat(allInsertedHands.size).isEqualTo(2)
            assertThat(allInsertedHands.map { it.name }).containsExactly("Hand11", "Hand22")
            assertThat(allInsertedHands.map { it.id }).containsExactly(id1, id2)
        }
    }

    @Test
    fun should_return_false_when_update_a_non_existent_hand() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            assertThat(handsDao.findAll()).isEmpty()

            val res = handsDao.update(Hand(name = "Unknown"))
            assertThat(res).isEqualTo(-1)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_non_existent_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(HandsTable)

            val res = handsDao.update(Hand(name = "Unknown"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_hand() {
        val hand1 = Hand(name = "Hand1")
        val hand2 = Hand(name = "Hand2")
        val hand3 = Hand(name = "Hand3")

        transaction {
            logger.addLogger(StdOutSqlLogger)

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
        val hand1 = Hand(name = "Hand1")
        val hand2 = Hand(name = "Hand2")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            handsDao.add(hand1)
            handsDao.add(hand2)
            assertThat(handsDao.findAll().size).isEqualTo(2)

            handsDao.deleteAll()
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_non_existent_hand() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            HandsTable.deleteAll()
            assertThat(handsDao.findAll()).isEmpty()

            val res = handsDao.delete(Hand(name = "Unknown"))
            assertThat(res).isEqualTo(0)
            assertThat(handsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_non_existent_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)

            drop(HandsTable)

            val res = handsDao.delete(Hand(name = "Unknown"))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}