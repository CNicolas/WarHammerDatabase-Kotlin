package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.Player
import warhammer.database.tables.Players
import java.lang.Exception

class PlayersDao : AbstractDao<Player>() {
    override val table = Players

    override fun add(entity: Player): Int {
        return try {
            val id = Players.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            id?.value ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    override fun findByName(name: String): Player? {
        val result = Players.select { Players.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Player): Int {
        return try {
            Players.update({ (Players.id eq entity.id) or (Players.name eq entity.name) }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            -1
        }
    }

    override fun delete(entity: Player): Int {
        return try {
            Players.deleteWhere { (Players.id eq entity.id) or (Players.name eq entity.name) }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Player? = when (result) {
        null -> null
        else -> Player(result[Players.name],
                result[Players.id].value)
    }

    override fun mapEntityToTable(it: UpdateStatement, entity: Player) {
        it[Players.id] = EntityID(entity.id, Players)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Player) {
        it[Players.name] = entity.name
    }
}