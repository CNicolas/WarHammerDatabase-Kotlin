package entities

import entities.tables.Hands
import org.assertj.core.api.Assertions.assertThat
import org.h2.jdbc.JdbcSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert
import org.testng.annotations.Test

class HandEntityTest {
    @Test
    fun should_insert_a_hand() {
        val handName = "SampleName"

        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            Hands.insert {
                it[name] = handName
            }

            assertThat(Hands.selectAll().count()).isEqualTo(1)
        }
    }

    @Test
    fun should_throw_error_when_inserting_2_Hands_with_same_name() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Hands)

            Hands.insert {
                it[name] = "Rocky"
            }

            try {
                Hands.insert {
                    it[name] = "Rocky"
                }
            } catch (e: JdbcSQLException) {
                Assert.assertTrue(true)
            }
        }
    }
}