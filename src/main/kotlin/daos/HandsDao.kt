package daos

import entities.Hand
import entities.tables.Hands
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.sqlite.SQLiteException

class HandsDao(override val table: IntIdTable = Hands) : AbstractDao<Hand>() {
    override fun add(entity: Hand): Int {
        return try {
            val id = Hands.insertAndGetId {
                it[name] = entity.name
                it[characteristicDicesCount] = entity.characteristicDicesCount
                it[expertiseDicesCount] = entity.expertiseDicesCount
                it[fortuneDicesCount] = entity.fortuneDicesCount
                it[conservativeDicesCount] = entity.conservativeDicesCount
                it[recklessDicesCount] = entity.recklessDicesCount
                it[challengeDicesCount] = entity.challengeDicesCount
                it[misfortuneDicesCount] = entity.misfortuneDicesCount
            }

            id?.value ?: -1
        } catch (e: SQLiteException) {
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
            Hands.update({ Hands.id eq entity.id }) {
                mapEntityToTable(it, entity)
            }
        } catch (e: SQLiteException) {
            -1
        }
    }

    override fun delete(entity: Hand): Boolean {
        return try {
            Hands.deleteWhere { Hands.id eq entity.id }

            true
        } catch (e: SQLiteException) {
            false
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
        it[Hands.name] = entity.name
        it[Hands.id] = EntityID(entity.id, Hands)
        it[Hands.characteristicDicesCount] = entity.characteristicDicesCount
        it[Hands.expertiseDicesCount] = entity.expertiseDicesCount
        it[Hands.fortuneDicesCount] = entity.fortuneDicesCount
        it[Hands.conservativeDicesCount] = entity.conservativeDicesCount
        it[Hands.recklessDicesCount] = entity.recklessDicesCount
        it[Hands.challengeDicesCount] = entity.challengeDicesCount
        it[Hands.misfortuneDicesCount] = entity.misfortuneDicesCount
    }
}