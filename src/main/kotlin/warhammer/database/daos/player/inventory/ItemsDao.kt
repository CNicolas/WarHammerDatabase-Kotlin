package warhammer.database.daos.player.inventory

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.inventory.mapFieldsOfEntity
import warhammer.database.entities.mapping.inventory.mapToItem
import warhammer.database.entities.player.inventory.Item
import warhammer.database.tables.player.inventory.ItemsTable
import java.lang.Exception

class ItemsDao : AbstractDao<Item>(), PlayerInventoryLinkedDao<Item> {
    override val table: IntIdTable = ItemsTable

    override fun findByInventoryId(inventoryId: Int): Item? {
        val result = ItemsTable.select { ItemsTable.inventoryId eq inventoryId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Item): Int {
        return try {
            ItemsTable.update({
                (ItemsTable.id eq entity.id) or (ItemsTable.inventoryId eq entity.inventoryId)
            }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Item): Int {
        return try {
            ItemsTable.deleteWhere {
                (ItemsTable.id eq entity.id) or (ItemsTable.inventoryId eq entity.inventoryId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByInventoryId(inventoryId: Int): Int {
        return try {
            ItemsTable.deleteWhere { ItemsTable.inventoryId eq inventoryId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Item? = result.mapToItem()

    override fun mapEntityToTable(it: UpdateStatement, entity: Item) {
        it[ItemsTable.id] = EntityID(entity.id, ItemsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Item) = it.mapFieldsOfEntity(entity)
}