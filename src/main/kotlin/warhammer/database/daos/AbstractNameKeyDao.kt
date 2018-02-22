package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.NamedEntity
import java.lang.Exception

abstract class AbstractNameKeyDao<E : NamedEntity> : AbstractDao<E>(), NameKeyDao<E> {
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

    override fun findByName(name: String): E? {
        val result = table.select(predicateByName(name))
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun findAll(): List<E> {
        return table.selectAll().mapNotNull { mapResultRowToEntity(it) }
    }

    override fun update(entity: E): E? {
        return try {
            table.update({ (table.id eq entity.id) or predicateByName(entity.name).invoke(this) }) {
                it[table.id] = EntityID(entity.id, table)
                mapFieldsOfEntityToTable(it, entity)
            }

            findByName(entity.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun delete(entity: E): Int {
        return try {
            table.deleteWhere { table.id eq entity.id }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByName(name: String): Int {
        return try {
            table.deleteWhere(predicateByName(name))
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteAll(): Int = table.deleteAll()

    protected abstract fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: E)
    protected abstract fun predicateByName(name: String): SqlExpressionBuilder.() -> Op<Boolean>
}