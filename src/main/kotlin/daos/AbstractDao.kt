package daos

import entities.WarHammerNamedEntity
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.sqlite.SQLiteException

abstract class AbstractDao<T : WarHammerNamedEntity> : Dao<T> {
    abstract val table: IntIdTable

    override fun addAll(entities: List<T>): List<Int> {
        val addedIds = mutableListOf<Int>()

        entities.forEach {
            val addedId = add(it)
            if (addedId > -1) {
                addedIds.add(addedId)
            }
        }

        return addedIds
    }

    override fun findById(id: Int): T? {
        val result = table.select { table.id eq id }
                .firstOrNull()

        return mapResultRowToEntity(result)
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