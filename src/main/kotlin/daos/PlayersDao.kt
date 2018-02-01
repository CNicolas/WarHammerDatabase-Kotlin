package daos

import entities.Player
import entities.tables.Players
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.sqlite.SQLiteException

class PlayersDao(override val table: IntIdTable = Players) : AbstractDao<Player>() {
    override fun add(entity: Player): Boolean {
        return try {
            Players.insert {
                it[name] = entity.name
            }

            true
        } catch (e: SQLiteException) {
            false
        }
    }

    override fun findByName(name: String): Player? {
        val result = Players.select { Players.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Player): Boolean {
        return try {
            Players.update({ Players.id eq entity.id }) {
                mapEntityToTable(it, entity)
            }

            true
        } catch (e: SQLiteException) {
            false
        }
    }

    override fun delete(entity: Player): Boolean {
        return try {
            Players.deleteWhere { Players.id eq entity.id }

            true
        } catch (e: SQLiteException) {
            false
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Player? = when (result) {
        null -> null
        else -> Player(result[Players.name],
                result[Players.id].value)
    }

    override fun mapEntityToTable(it: UpdateStatement, entity: Player) {
        it[Players.name] = entity.name
        it[Players.id] = EntityID(entity.id, Players)
    }
}