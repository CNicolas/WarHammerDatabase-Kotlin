package daos

import entities.Player
import entities.tables.Players
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import java.lang.Exception

class PlayersDao(override val table: IntIdTable = Players) : AbstractDao<Player>() {
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

    override fun delete(entity: Player): Boolean {
        return try {
            val numberOfDeletions = Players.deleteWhere { (Players.id eq entity.id) or (Players.name eq entity.name) }
            numberOfDeletions == 1
        } catch (e: Exception) {
            false
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