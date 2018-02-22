package warhammer.database.daos.player.item

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import warhammer.database.daos.AbstractDao
import warhammer.database.daos.player.PlayerLinkedDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.item.Item
import warhammer.database.entities.player.item.mapFieldsOfEntity
import warhammer.database.entities.player.item.mapToItem
import warhammer.database.tables.ItemsTable

class ItemDao : AbstractDao<Item>(), PlayerLinkedDao<Item> {
    override val table = ItemsTable

    override fun add(entity: Item, player: Player): Item? {
        table.insert { it.mapFieldsOfEntity(entity, player) }

        return findByNameAndPlayer(entity.name, player)
    }

    override fun findByNameAndPlayer(name: String, player: Player): Item? {
        return findAllByPlayer(player).firstOrNull { it.name == name }
    }

    override fun findAllByPlayer(player: Player): List<Item> {
        val result = table.select { table.playerName eq player.name }
                .toList()

        return result.mapNotNull { it.mapToItem() }
    }

    override fun updateByPlayer(entity: Item, player: Player): Item? {
        table.update({ (table.id eq entity.id) and (ItemsTable.playerName eq player.name) }) {
            it[table.id] = EntityID(entity.id, table)
            it.mapFieldsOfEntity(entity, player)
        }

        return findByNameAndPlayer(entity.name, player)
    }

    override fun deleteByPlayer(entity: Item, player: Player): Int = table.deleteWhere {
        (ItemsTable.id eq entity.id) and (ItemsTable.playerName eq player.name)
    }

    override fun deleteAllByPlayer(player: Player): Int = table.deleteWhere { ItemsTable.playerName eq player.name }

    override fun deleteAll() = table.deleteAll()

    override fun mapResultRowToEntity(result: ResultRow?): Item? = result.mapToItem()
}