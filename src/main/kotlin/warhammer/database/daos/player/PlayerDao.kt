package warhammer.database.daos.player

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.daos.AbstractNameKeyDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.mapFieldsOfEntity
import warhammer.database.entities.player.mapToPlayer
import warhammer.database.tables.PlayersTable
import java.lang.Exception

class PlayerDao : AbstractNameKeyDao<Player>() {
    override val table = PlayersTable

    override fun findByName(name: String): Player? {
        val result = table.select(predicateByName(name))
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Player): Player? {
        return try {
            table.update({ (PlayersTable.id eq entity.id) or predicateByName(entity.name).invoke(this) }) {
                it[table.id] = EntityID(entity.id, table)
                mapFieldsOfEntityToTable(it, entity)
            }

            findByName(entity.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun delete(entity: Player): Int = deleteByName(entity.name)

    override fun deleteByName(name: String): Int {
        return try {
            table.deleteWhere(predicateByName(name))
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Player? =
            result.mapToPlayer()

    override fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: Player) =
            statement.mapFieldsOfEntity(entity)

    override fun predicateByName(name: String): SqlExpressionBuilder.() -> Op<Boolean> =
            { PlayersTable.name eq name }
}