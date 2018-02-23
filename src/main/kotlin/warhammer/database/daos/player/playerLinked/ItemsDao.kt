package warhammer.database.daos.player.playerLinked

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.item.Item
import warhammer.database.entities.player.playerLinked.item.mapFieldsOfEntity
import warhammer.database.entities.player.playerLinked.item.mapToItem
import warhammer.database.tables.ItemsTable

class ItemsDao : AbstractPlayerLinkedDao<Item>() {
    override val table = ItemsTable

    override fun mapResultRowToEntity(result: ResultRow?): Item? = result.mapToItem()

    override fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: Item, player: Player) =
            statement.mapFieldsOfEntity(entity, player)

    override fun predicateByPlayer(player: Player): SqlExpressionBuilder.() -> Op<Boolean> = { table.playerName eq player.name }
}