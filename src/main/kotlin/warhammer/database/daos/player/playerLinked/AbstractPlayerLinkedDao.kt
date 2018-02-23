package warhammer.database.daos.player.playerLinked

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.PlayerLinkedEntity

abstract class AbstractPlayerLinkedDao<E : PlayerLinkedEntity> : AbstractDao<E>(), PlayerLinkedDao<E> {
    override fun add(entity: E, player: Player): E? {
        table.insert {
            mapFieldsOfEntityToTable(it, entity, player)
        }

        return findByNameAndPlayer(entity.name, player)
    }

    override fun findByNameAndPlayer(name: String, player: Player): E? {
        return findAllByPlayer(player).firstOrNull { it.name == name }
    }

    override fun findAllByPlayer(player: Player): List<E> {
        val result = table.select(predicateByPlayer(player))
                .toList()

        return result.mapNotNull { mapResultRowToEntity(it) }
    }

    override fun updateByPlayer(entity: E, player: Player): E? {
        table.update({ (table.id eq entity.id) and predicateByPlayer(player).invoke(this) }) {
            it[table.id] = EntityID(entity.id, table)
            mapFieldsOfEntityToTable(it, entity, player)
        }

        return findByNameAndPlayer(entity.name, player)
    }

    override fun deleteAllByPlayer(player: Player): Int = table.deleteWhere(predicateByPlayer(player))

    override fun deleteByPlayer(entity: E, player: Player): Int = table.deleteWhere {
        (table.id eq entity.id) and predicateByPlayer(player).invoke(this)
    }

    override fun deleteAll() = table.deleteAll()

    protected abstract fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: E, player: Player)
    protected abstract fun predicateByPlayer(player: Player): SqlExpressionBuilder.() -> Op<Boolean>
}