package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.mapping.mapToPlayer
import warhammer.database.entities.player.Player
import warhammer.database.tables.PlayersTable
import java.lang.Exception

class PlayersDao : AbstractDao<Player>(), NamedDao<Player> {
    override val table = PlayersTable

    override fun add(entity: Player): Int {
        return try {
            val id = PlayersTable.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            id?.value ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun findByName(name: String): Player? {
        val result = PlayersTable.select { PlayersTable.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Player): Int {
        return try {
            PlayersTable.update({ (PlayersTable.id eq entity.id) or (PlayersTable.name eq entity.name) }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Player): Int {
        return try {
            PlayersTable.deleteWhere { (PlayersTable.id eq entity.id) or (PlayersTable.name eq entity.name) }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteAll() {
        PlayersTable.deleteAll()
    }

    override fun mapResultRowToEntity(result: ResultRow?): Player? = result.mapToPlayer()

    override fun mapEntityToTable(it: UpdateStatement, entity: Player) {
        it[PlayersTable.id] = EntityID(entity.id, PlayersTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Player) {
        it[PlayersTable.name] = entity.name
        it[PlayersTable.race] = entity.race.toString()
        it[PlayersTable.age] = entity.age
        it[PlayersTable.size] = entity.size
    }
}