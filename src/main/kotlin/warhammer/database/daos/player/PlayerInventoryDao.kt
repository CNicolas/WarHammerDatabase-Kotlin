package warhammer.database.daos.player

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayerInventoryEntity
import warhammer.database.entities.player.PlayerInventory
import warhammer.database.tables.player.PlayerInventoryTable
import java.lang.Exception

class PlayerInventoryDao : AbstractDao<PlayerInventory>(), PlayerLinkedDao<PlayerInventory> {
    override val table: IntIdTable = PlayerInventoryTable

    override fun findByPlayerId(playerId: Int): PlayerInventory? {
        val result = table.select { PlayerInventoryTable.playerId eq playerId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: PlayerInventory): Int {
        return try {
            val oldInventory = when {
                entity.id == -1 -> findByPlayerId(entity.playerId)
                else -> findById(entity.id)
            }

            return when (oldInventory) {
                null -> -1
                else -> {
                    table.update({ (table.id eq oldInventory.id) }) {
                        mapEntityToTable(it, entity.copy(id = oldInventory.id))
                    }

                    oldInventory.id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: PlayerInventory): Int {
        return try {
            table.deleteWhere {
                (table.id eq entity.id) or (PlayerInventoryTable.playerId eq entity.playerId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByPlayerId(playerId: Int): Int {
        return try {
            table.deleteWhere { PlayerInventoryTable.playerId eq playerId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): PlayerInventory? =
            result.mapToPlayerInventoryEntity()

    override fun mapEntityToTable(it: UpdateStatement, entity: PlayerInventory) {
        it[table.id] = EntityID(entity.id, PlayerInventoryTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: PlayerInventory) =
            it.mapFieldsOfEntity(entity)
}