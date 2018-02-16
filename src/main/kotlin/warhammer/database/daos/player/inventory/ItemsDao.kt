package warhammer.database.daos.player.inventory

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.daos.NamedDao
import warhammer.database.entities.mapping.inventory.mapFieldsOfEntity
import warhammer.database.entities.mapping.inventory.mapToItem
import warhammer.database.entities.player.inventory.item.*
import warhammer.database.entities.player.inventory.item.enums.ItemType.*
import warhammer.database.tables.player.inventory.ItemsTable
import java.lang.Exception

class ItemsDao : AbstractDao<Item>(), PlayerInventoryLinkedDao<Item>, NamedDao<Item> {
    override val table: IntIdTable = ItemsTable

    // region Find one

    override fun findByName(name: String): Item? {
        val result = table.select { ItemsTable.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    fun findGenericItemById(id: Int): GenericItem? = findById(id) as? GenericItem
    fun findArmorById(id: Int): Armor? = findById(id) as? Armor
    fun findWeaponById(id: Int): Weapon? = findById(id) as? Weapon
    fun findExpandableById(id: Int): Expandable? = findById(id) as? Expandable

    // endregion

    // region Find all

    fun findAllGenericItemByInventoryId(inventoryId: Int): List<GenericItem> {
        return findAllByInventoryId(inventoryId)
                .filter { it.type == ITEM }
                .map { it as GenericItem }
    }

    fun findAllArmorsByInventoryId(inventoryId: Int): List<Armor> {
        return findAllByInventoryId(inventoryId)
                .filter { it.type == ARMOR }
                .map { it as Armor }
    }

    fun findAllWeaponsByInventoryId(inventoryId: Int): List<Weapon> {
        return findAllByInventoryId(inventoryId)
                .filter { it.type == WEAPON }
                .map { it as Weapon }
    }

    fun findAllExpandablesByInventoryId(inventoryId: Int): List<Expandable> {
        return findAllByInventoryId(inventoryId)
                .filter { it.type == EXPANDABLE }
                .map { it as Expandable }
    }

    fun findAllByInventoryId(inventoryId: Int): List<Item> {
        val result = ItemsTable.select { ItemsTable.inventoryId eq inventoryId }
                .toList()

        return result.mapNotNull { mapResultRowToEntity(it) }
    }
    // endregion

    override fun update(entity: Item): Int {
        return try {
            val updateCount = ItemsTable.update({ (ItemsTable.id eq entity.id) }) {
                mapEntityToTable(it, entity)
            }

            if (updateCount == 0) {
                -1
            } else {
                entity.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    // region Delete one

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

    // endregion

    // region Delete all

    fun deleteAllGenericItemsByInventoryId(inventoryId: Int) =
            ItemsTable.deleteWhere {
                (ItemsTable.inventoryId eq inventoryId) and (ItemsTable.type eq ITEM.toString())
            }

    fun deleteAllArmorsByInventoryId(inventoryId: Int) =
            ItemsTable.deleteWhere {
                (ItemsTable.inventoryId eq inventoryId) and (ItemsTable.type eq ARMOR.toString())
            }

    fun deleteAllWeaponsByInventoryId(inventoryId: Int) =
            ItemsTable.deleteWhere {
                (ItemsTable.inventoryId eq inventoryId) and (ItemsTable.type eq WEAPON.toString())
            }

    fun deleteAllExpandablesByInventoryId(inventoryId: Int) =
            ItemsTable.deleteWhere {
                (ItemsTable.inventoryId eq inventoryId) and (ItemsTable.type eq EXPANDABLE.toString())
            }


    // endregion

    override fun mapResultRowToEntity(result: ResultRow?): Item? = result.mapToItem()

    override fun mapEntityToTable(it: UpdateStatement, entity: Item) {
        it[ItemsTable.id] = EntityID(entity.id, ItemsTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Item) = it.mapFieldsOfEntity(entity)
}