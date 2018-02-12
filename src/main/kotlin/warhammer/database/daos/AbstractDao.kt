package warhammer.database.daos

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.WarHammerEntity
import java.lang.Exception

abstract class AbstractDao<E : WarHammerEntity> : Dao<E> {
    abstract val table: IntIdTable

    override fun add(entity: E): Int {
        return try {
            val id = table.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            id?.value ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun addAll(entities: List<E>): List<Int> {
        val addedIds = mutableListOf<Int>()

        entities.forEach {
            val addedId = add(it)
            if (addedId > -1) {
                addedIds.add(addedId)
            }
        }

        return addedIds
    }

    override fun findById(id: Int): E? {
        val result = table.select { table.id eq id }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun findAll(): List<E?> {
        return table.selectAll().map { mapResultRowToEntity(it) }
    }

    override fun updateAll(entities: List<E>): List<Int> {
        val updatedIds = mutableListOf<Int>()

        entities.forEach {
            val updatedId = update(it)
            if (updatedId > -1) {
                updatedIds.add(updatedId)
            }
        }

        return updatedIds
    }

    override fun deleteAll() {
        table.deleteAll()
    }

    protected abstract fun mapResultRowToEntity(result: ResultRow?): E?
    protected abstract fun mapEntityToTable(it: UpdateStatement, entity: E)
    protected abstract fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: E)
}