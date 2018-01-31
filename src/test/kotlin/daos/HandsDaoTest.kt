package daos

import entities.Hand
import entities.tables.Hands
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
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

        val hand = handsDao.findByNameInSeparateTransaction(handName)

        assertNotNull(hand)
        assertThat(hand!!.name).isEqualTo("SampleName")
    }
}