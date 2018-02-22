package warhammer.database.daos.player

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.daos.AbstractNameKeyDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.mapFieldsOfEntity
import warhammer.database.entities.player.mapToPlayer
import warhammer.database.tables.PlayersTable

class PlayerDao : AbstractNameKeyDao<Player>() {
    override val table = PlayersTable

    override fun mapResultRowToEntity(result: ResultRow?): Player? = result.mapToPlayer()

    override fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: Player) =
            statement.mapFieldsOfEntity(entity)

    override fun predicateByName(name: String): SqlExpressionBuilder.() -> Op<Boolean> = { table.name eq name }
}