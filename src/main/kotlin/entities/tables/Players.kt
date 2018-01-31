package entities.tables

import org.jetbrains.exposed.sql.Table

object Players : Table() {
    val name = varchar("name", length = 50).primaryKey()
}