package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.Hand
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToHand
import warhammer.database.tables.HandsTable
import java.lang.Exception

class HandsDao : AbstractDao<Hand>(), NamedDao<Hand> {
    override val table: IntIdTable = HandsTable

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

    override fun mapResultRowToEntity(result: ResultRow?): Hand? = result.mapToHand()

    override fun mapEntityToTable(it: UpdateStatement, entity: Hand) {
        it[HandsTable.id] = EntityID(entity.id, HandsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Hand) = it.mapFieldsOfEntity(entity)
}