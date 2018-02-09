package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayerCharacteristicsEntity
import warhammer.database.entities.player.PlayerCharacteristicsEntity
import warhammer.database.tables.PlayerCharacteristicsTable
import java.lang.Exception

class PlayerCharacteristicsDao : AbstractDao<PlayerCharacteristicsEntity>() {
    override val table: IntIdTable = PlayerCharacteristicsTable

    override fun add(entity: PlayerCharacteristicsEntity): Int {
        return try {
            val id = PlayerCharacteristicsTable.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            id?.value ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun findByPlayerId(playerId: Int): PlayerCharacteristicsEntity? {
        val result = PlayerCharacteristicsTable.select { PlayerCharacteristicsTable.playerId eq playerId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: PlayerCharacteristicsEntity): Int {
        return try {
            PlayerCharacteristicsTable.update({
                (PlayerCharacteristicsTable.id eq entity.id) or (PlayerCharacteristicsTable.playerId eq entity.playerId)
            }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: PlayerCharacteristicsEntity): Int {
        return try {
            PlayerCharacteristicsTable.deleteWhere {
                (PlayerCharacteristicsTable.id eq entity.id) or (PlayerCharacteristicsTable.playerId eq entity.playerId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun deleteByPlayerId(playerId: Int): Int {
        return try {
            PlayerCharacteristicsTable.deleteWhere { PlayerCharacteristicsTable.playerId eq playerId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): PlayerCharacteristicsEntity? =
            result.mapToPlayerCharacteristicsEntity()

    override fun mapEntityToTable(it: UpdateStatement, entity: PlayerCharacteristicsEntity) {
        it[PlayerCharacteristicsTable.id] = EntityID(entity.id, PlayerCharacteristicsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: PlayerCharacteristicsEntity) =
            it.mapFieldsOfEntity(entity)
}