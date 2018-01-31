package daos

import entities.WarHammerNamedEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.sqlite.SQLiteException

abstract class AbstractDao<T : WarHammerNamedEntity> : Dao<T> {
    abstract val table: Table

    override fun addAll(entities: List<T>): Boolean {
        entities.forEach {
            if (!add(it)) {
                return false
            }
        }

        return true
    }

    override fun findAll(): List<T?> {
        return table.selectAll().map { mapResultRowToEntity(it) }
    }

    override fun updateAll(entities: List<T>): Boolean {
        entities.forEach {
            if (!update(it)) {
                return false
            }
        }

        return true
    }

    override fun deleteAll(): Boolean {
        return try {
            table.deleteAll()

            true
        } catch (e: SQLiteException) {
            false
        }
    }

    protected abstract fun mapResultRowToEntity(result: ResultRow?): T?
    protected abstract fun mapEntityToTable(it: UpdateStatement, entity: T)
}