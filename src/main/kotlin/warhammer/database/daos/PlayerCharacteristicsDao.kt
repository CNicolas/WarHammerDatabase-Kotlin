package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayerCharacteristicsEntity
import warhammer.database.entities.player.PlayerCharacteristics
import warhammer.database.tables.PlayerCharacteristicsTable
import java.lang.Exception

class PlayerCharacteristicsDao : AbstractDao<PlayerCharacteristics>() {
    override val table: IntIdTable = PlayerCharacteristicsTable

    fun findByPlayerId(playerId: Int): PlayerCharacteristics? {
        val result = table.select { PlayerCharacteristicsTable.playerId eq playerId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: PlayerCharacteristics): Int {
        return try {
            table.update({
                (table.id eq entity.id) or (PlayerCharacteristicsTable.playerId eq entity.playerId)
            }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: PlayerCharacteristics): Int {
        return try {
            table.deleteWhere {
                (table.id eq entity.id) or (PlayerCharacteristicsTable.playerId eq entity.playerId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun deleteByPlayerId(playerId: Int): Int {
        return try {
            table.deleteWhere { PlayerCharacteristicsTable.playerId eq playerId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): PlayerCharacteristics? =
            result.mapToPlayerCharacteristicsEntity()

    override fun mapEntityToTable(it: UpdateStatement, entity: PlayerCharacteristics) {
        it[table.id] = EntityID(entity.id, PlayerCharacteristicsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: PlayerCharacteristics) =
            it.mapFieldsOfEntity(entity)
}