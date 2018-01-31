package daos

import entities.Hand
import entities.tables.Hands
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert.assertNotNull
import org.testng.annotations.Test
import java.sql.Connection

class HandsDaoTest {
    @Test
    fun should_return_one_hand_in_a_single_transaction() {
        Database.connect("jdbc:sqlite:file:testSqlite?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            logger.addLogger(StdOutSqlLogger)

            SchemaUtils.create(Hands)

            Hand.new {
                name = "SampleName"
            }

            val hand = HandsDao().findByName("SampleName")

            assertNotNull(hand)
            assertThat(hand!!.name).isEqualTo("SampleName")
        }
    }

    @Test
    fun should_return_one_hand_in_2_different_transaction() {
        Database.connect("jdbc:sqlite:file:testSqlite", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            logger.addLogger(StdOutSqlLogger)

            SchemaUtils.create(Hands)

            Hand.new {
                name = "SampleName"
            }

            Hands.selectAll().forEach { println(it) }
        }

        val hand = HandsDao().findByNameInSeparateTransaction("SampleName")

        assertNotNull(hand)
        assertThat(hand!!.name).isEqualTo("SampleName")
    }
}