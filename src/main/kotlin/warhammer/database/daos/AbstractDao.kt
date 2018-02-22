package warhammer.database.daos

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

abstract class AbstractDao<E> : Dao<E> {
    abstract val table: IntIdTable

    override fun findById(id: Int): E? {
        val result = table.select { table.id eq id }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    protected abstract fun mapResultRowToEntity(result: ResultRow?): E?
}