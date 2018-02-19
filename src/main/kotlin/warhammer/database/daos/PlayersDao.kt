package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.mapping.mapFieldsOfEntity
import warhammer.database.entities.mapping.mapToPlayer
import warhammer.database.entities.player.Player
import warhammer.database.tables.PlayersTable
import java.lang.Exception

class PlayersDao : AbstractDao<Player>(), NamedDao<Player> {
    override val table = PlayersTable

    override fun findByName(name: String): Player? {
        val result = table.select { PlayersTable.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Player): Int {
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

    override fun delete(entity: Player): Int {
        return try {
            table.deleteWhere { (table.id eq entity.id) or (PlayersTable.name eq entity.name) }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Player? = result.mapToPlayer()

    override fun mapEntityToTable(it: UpdateStatement, entity: Player) {
        it[table.id] = EntityID(entity.id, PlayersTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Player) = it.mapFieldsOfEntity(entity)
}