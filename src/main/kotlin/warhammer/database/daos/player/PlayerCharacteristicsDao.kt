package warhammer.database.daos.player

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayerCharacteristicsEntity
import warhammer.database.entities.player.PlayerCharacteristicsEntity
import warhammer.database.tables.player.PlayerCharacteristicsTable
import java.lang.Exception

class PlayerCharacteristicsDao : AbstractDao<PlayerCharacteristicsEntity>(), PlayerLinkedDao<PlayerCharacteristicsEntity> {
    override val table: IntIdTable = PlayerCharacteristicsTable

    override fun findByPlayerId(playerId: Int): PlayerCharacteristicsEntity? {
        val result = table.select { PlayerCharacteristicsTable.playerId eq playerId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: PlayerCharacteristicsEntity): Int {
        return try {
            table.update({
                (table.id eq entity.id) or (PlayerCharacteristicsTable.playerId eq entity.playerId)
            }) {
                mapEntityToTable(it, entity)
            }

            findByPlayerId(entity.playerId)?.id!!
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: PlayerCharacteristicsEntity): Int {
        return try {
            table.deleteWhere {
                (table.id eq entity.id) or (PlayerCharacteristicsTable.playerId eq entity.playerId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByPlayerId(playerId: Int): Int {
        return try {
            table.deleteWhere { PlayerCharacteristicsTable.playerId eq playerId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): PlayerCharacteristicsEntity? =
            result.mapToPlayerCharacteristicsEntity()

    override fun mapEntityToTable(it: UpdateStatement, entity: PlayerCharacteristicsEntity) {
        it[table.id] = EntityID(entity.id, PlayerCharacteristicsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: PlayerCharacteristicsEntity) =
            it.mapFieldsOfEntity(entity)
}