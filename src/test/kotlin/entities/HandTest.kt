package entities

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.Test
import entities.tables.Hands
import java.sql.Connection

class HandTest {
    @Test
    fun should_connect_h2() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }

    @Test
    fun should_connect_sqlite() {
        Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")

        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    @Test
    fun should_insert_one_hand_by_dto() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            val oneCharHand = Hand.new {
                name = "One Characteristic"
                characteristicDicesCount = 1
            }

            println(oneCharHand.name)

            assertThat(Hand.all().count()).isEqualTo(1)
            assertThat(oneCharHand.characteristicDicesCount!!).isEqualTo(1)
        }
    }

    @Test
    fun should_select_by_name() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            Hand.new {
                name = "SampleName"
                characteristicDicesCount = 1
                expertiseDicesCount = 2
            }

            val oneCharHand = Hand.find { Hands.name eq "SampleName" }.single()

            println(oneCharHand.name)

            assertThat(oneCharHand.characteristicDicesCount!!).isEqualTo(1)
            assertThat(oneCharHand.expertiseDicesCount!!).isEqualTo(2)
        }
    }
}