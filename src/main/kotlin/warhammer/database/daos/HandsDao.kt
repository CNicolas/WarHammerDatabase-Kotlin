package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.Hand
import warhammer.database.tables.HandsTable
import java.lang.Exception

class HandsDao : AbstractDao<Hand>() {
    override val table: IntIdTable = HandsTable

    override fun add(entity: Hand): Int {
        return try {
            val id = HandsTable.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            id?.value ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun findByName(name: String): Hand? {
        val result = HandsTable.select { HandsTable.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Hand): Int {
        return try {
            HandsTable.update({ (HandsTable.id eq entity.id) or (HandsTable.name eq entity.name) }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Hand): Int {
        return try {
            HandsTable.deleteWhere { HandsTable.name eq entity.name }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Hand? = when (result) {
        null -> null
        else -> Hand(result[HandsTable.name],
                result[HandsTable.id].value,
                characteristicDicesCount = result[HandsTable.characteristicDicesCount],
                expertiseDicesCount = result[HandsTable.expertiseDicesCount],
                fortuneDicesCount = result[HandsTable.fortuneDicesCount],
                conservativeDicesCount = result[HandsTable.conservativeDicesCount],
                recklessDicesCount = result[HandsTable.recklessDicesCount],
                challengeDicesCount = result[HandsTable.challengeDicesCount],
                misfortuneDicesCount = result[HandsTable.misfortuneDicesCount])
    }

    override fun mapEntityToTable(it: UpdateStatement, entity: Hand) {
        it[HandsTable.id] = EntityID(entity.id, HandsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Hand) {
        it[HandsTable.name] = entity.name

        it[HandsTable.characteristicDicesCount] = entity.characteristicDicesCount
        it[HandsTable.expertiseDicesCount] = entity.expertiseDicesCount
        it[HandsTable.fortuneDicesCount] = entity.fortuneDicesCount
        it[HandsTable.conservativeDicesCount] = entity.conservativeDicesCount
        it[HandsTable.recklessDicesCount] = entity.recklessDicesCount
        it[HandsTable.challengeDicesCount] = entity.challengeDicesCount
        it[HandsTable.misfortuneDicesCount] = entity.misfortuneDicesCount
    }
}