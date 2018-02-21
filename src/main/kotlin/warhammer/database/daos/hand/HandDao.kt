package warhammer.database.daos.hand

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.daos.AbstractNameKeyDao
import warhammer.database.entities.hand.Hand
import warhammer.database.entities.hand.mapFieldsOfEntity
import warhammer.database.entities.hand.mapToHand
import warhammer.database.tables.HandsTable
import java.lang.Exception

class HandDao : AbstractNameKeyDao<Hand>() {
    override val table = HandsTable

    override fun findByName(name: String): Hand? {
        val result = table.select(predicateByName(name))
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Hand): Hand? {
        return try {
            table.update(predicateByName(entity.name)) {
                mapFieldsOfEntityToTable(it, entity)
            }

            findByName(entity.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun delete(entity: Hand): Int = deleteByName(entity.name)

    override fun deleteByName(name: String): Int {
        return try {
            table.deleteWhere(predicateByName(name))
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Hand? = result.mapToHand()

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Hand) = it.mapFieldsOfEntity(entity)

    override fun predicateByName(name: String): SqlExpressionBuilder.() -> Op<Boolean> = { table.name eq name }
}