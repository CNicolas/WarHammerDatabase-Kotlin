package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.Hand
import warhammer.database.entities.tables.Hands
import java.lang.Exception

class HandsDao : AbstractDao<Hand>() {
    override val table: IntIdTable = Hands

    override fun add(entity: Hand): Int {
        return try {
            val id = Hands.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            id?.value ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun findByName(name: String): Hand? {
        val result = Hands.select { Hands.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Hand): Int {
        return try {
            Hands.update({ (Hands.id eq entity.id) or (Hands.name eq entity.name) }) {
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
            Hands.deleteWhere { Hands.name eq entity.name }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Hand? = when (result) {
        null -> null
        else -> Hand(result[Hands.name],
                result[Hands.id].value,
                characteristicDicesCount = result[Hands.characteristicDicesCount],
                expertiseDicesCount = result[Hands.expertiseDicesCount],
                fortuneDicesCount = result[Hands.fortuneDicesCount],
                conservativeDicesCount = result[Hands.conservativeDicesCount],
                recklessDicesCount = result[Hands.recklessDicesCount],
                challengeDicesCount = result[Hands.challengeDicesCount],
                misfortuneDicesCount = result[Hands.misfortuneDicesCount])
    }

    override fun mapEntityToTable(it: UpdateStatement, entity: Hand) {
        it[Hands.id] = EntityID(entity.id, Hands)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Hand) {
        it[Hands.name] = entity.name

        it[Hands.characteristicDicesCount] = entity.characteristicDicesCount
        it[Hands.expertiseDicesCount] = entity.expertiseDicesCount
        it[Hands.fortuneDicesCount] = entity.fortuneDicesCount
        it[Hands.conservativeDicesCount] = entity.conservativeDicesCount
        it[Hands.recklessDicesCount] = entity.recklessDicesCount
        it[Hands.challengeDicesCount] = entity.challengeDicesCount
        it[Hands.misfortuneDicesCount] = entity.misfortuneDicesCount
    }
}