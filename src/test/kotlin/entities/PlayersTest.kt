package entities

import entities.tables.Players
import org.h2.jdbc.JdbcSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

class PlayersTest {
    @Test
    fun should_throw_error() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(Players)

            Players.insert {
                it[name] = "Rocky"
            }

            try {
                Players.insert {
                    it[name] = "Rocky"
                }
            } catch (e: JdbcSQLException) {
                assertTrue(true)
            }
        }
    }
}