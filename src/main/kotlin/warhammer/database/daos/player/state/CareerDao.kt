package warhammer.database.daos.player.state

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.state.mapFieldsOfEntity
import warhammer.database.entities.mapping.state.mapToCareer
import warhammer.database.entities.player.state.Career
import warhammer.database.tables.player.state.CareerTable
import java.lang.Exception

class CareerDao : AbstractDao<Career>(), PlayerStateLinkedDao<Career> {
    override val table: IntIdTable = CareerTable

    override fun findByStateId(stateId: Int): Career? {
        val result = CareerTable.select { CareerTable.stateId eq stateId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Career): Int {
        return try {
            CareerTable.update({
                (CareerTable.id eq entity.id) or (CareerTable.stateId eq entity.stateId)
            }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Career): Int {
        return try {
            CareerTable.deleteWhere {
                (CareerTable.id eq entity.id) or (CareerTable.stateId eq entity.stateId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByStateId(stateId: Int): Int {
        return try {
            CareerTable.deleteWhere { CareerTable.stateId eq stateId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Career? =
            result.mapToCareer()

    override fun mapEntityToTable(it: UpdateStatement, entity: Career) {
        it[CareerTable.id] = EntityID(entity.id, CareerTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Career) =
            it.mapFieldsOfEntity(entity)
}