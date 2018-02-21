package warhammer.database.daos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.NamedEntity
import java.lang.Exception

abstract class AbstractNameKeyDao<E : NamedEntity> : NameKeyDao<E> {
    abstract val table: Table

    override fun add(entity: E): E? {
        return try {
            table.insert {
                mapFieldsOfEntityToTable(it, entity)
            }

            findByName(entity.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun findAll(): List<E> {
        return table.selectAll().mapNotNull { mapResultRowToEntity(it) }
    }

    override fun deleteAll() {
        table.deleteAll()
    }

    protected abstract fun mapResultRowToEntity(result: ResultRow?): E?
    protected abstract fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: E)

    protected abstract fun predicateByName(name: String): SqlExpressionBuilder.() -> Op<Boolean>
}