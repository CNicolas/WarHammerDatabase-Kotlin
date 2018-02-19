package warhammer.database.daos.player

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayerStateEntity
import warhammer.database.entities.player.PlayerState
import warhammer.database.tables.player.PlayerStateTable
import java.lang.Exception

class PlayerStateDao : AbstractDao<PlayerState>(), PlayerLinkedDao<PlayerState> {
    override val table: IntIdTable = PlayerStateTable

    override fun findByPlayerId(playerId: Int): PlayerState? {
        val result = table.select { PlayerStateTable.playerId eq playerId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: PlayerState): Int {
        return try {
            val oldState = when {
                entity.id == -1 -> findByPlayerId(entity.playerId)
                else -> findById(entity.id)
            }

            return when (oldState) {
                null -> -1
                else -> {
                    table.update({ (table.id eq oldState.id) }) {
                        mapEntityToTable(it, entity.copy(id = oldState.id))
                    }

                    oldState.id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: PlayerState): Int {
        return try {
            table.deleteWhere {
                (table.id eq entity.id) or (PlayerStateTable.playerId eq entity.playerId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByPlayerId(playerId: Int): Int {
        return try {
            table.deleteWhere { PlayerStateTable.playerId eq playerId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): PlayerState? =
            result.mapToPlayerStateEntity()

    override fun mapEntityToTable(it: UpdateStatement, entity: PlayerState) {
        it[table.id] = EntityID(entity.id, PlayerStateTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: PlayerState) =
            it.mapFieldsOfEntity(entity)
}