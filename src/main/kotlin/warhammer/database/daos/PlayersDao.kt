package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayer
import warhammer.database.entities.player.PlayerEntity
import warhammer.database.tables.PlayersTable
import java.lang.Exception

class PlayersDao : AbstractDao<PlayerEntity>(), NamedDao<PlayerEntity> {
    override val table = PlayersTable

    override fun findByName(name: String): PlayerEntity? {
        val result = table.select { PlayersTable.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: PlayerEntity): Int {
        return try {
            table.update({ (table.id eq entity.id) or (PlayersTable.name eq entity.name) }) {
                mapEntityToTable(it, entity)
            }

            findByName(entity.name)?.id!!
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: PlayerEntity): Int {
        return try {
            table.deleteWhere { (table.id eq entity.id) or (PlayersTable.name eq entity.name) }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): PlayerEntity? = result.mapToPlayer()

    override fun mapEntityToTable(it: UpdateStatement, entity: PlayerEntity) {
        it[table.id] = EntityID(entity.id, PlayersTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: PlayerEntity) = it.mapFieldsOfEntity(entity)
}