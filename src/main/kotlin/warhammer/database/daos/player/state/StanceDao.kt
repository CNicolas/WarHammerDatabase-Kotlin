package warhammer.database.daos.player.state

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.state.mapFieldsOfEntity
import warhammer.database.entities.mapping.state.mapToStance
import warhammer.database.entities.player.state.Stance
import warhammer.database.tables.player.state.StanceTable
import java.lang.Exception

class StanceDao : AbstractDao<Stance>(), PlayerStateLinkedDao<Stance> {
    override val table: IntIdTable = StanceTable

    override fun findByStateId(stateId: Int): Stance? {
        val result = table.select { StanceTable.stateId eq stateId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Stance): Int {
        return try {
            table.update({
                (table.id eq entity.id) or (StanceTable.stateId eq entity.stateId)
            }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Stance): Int {
        return try {
            table.deleteWhere {
                (table.id eq entity.id) or (StanceTable.stateId eq entity.stateId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByStateId(stateId: Int): Int {
        return try {
            table.deleteWhere { StanceTable.stateId eq stateId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Stance? =
            result.mapToStance()

    override fun mapEntityToTable(it: UpdateStatement, entity: Stance) {
        it[table.id] = EntityID(entity.id, table)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Stance) =
            it.mapFieldsOfEntity(entity)
}