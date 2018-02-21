package warhammer.database.daos.player.item

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import warhammer.database.daos.player.PlayerLinkedDao
import warhammer.database.entities.player.item.mapToItem
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.item.*
import warhammer.database.entities.player.item.enums.ItemType.*
import warhammer.database.tables.ItemsTable
import java.lang.Exception

class ItemDao : PlayerLinkedDao<Item> {
    override fun add(entity: Item, player: Player): Item? {
        return try {
            ItemsTable.insert { it.mapFieldsOfEntity(entity) }

            findByNameAndPlayer(entity.name, player)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun findByNameAndPlayer(name: String, player: Player): Item? {
        return findAllByPlayer(player).firstOrNull { it.name == name }
    }

    // region Find all

    override fun findAllByPlayer(player: Player): List<Item> {
        val result = ItemsTable.select { ItemsTable.playerName eq player.name }
                .toList()

        return result.mapNotNull { it.mapToItem() }
    }

    fun findAllArmorsByInventoryId(player: Player): List<Armor> {
        return findAllByPlayer(player)
                .filter { it.type == ARMOR }
                .map { it as Armor }
    }

    fun findAllExpandablesByInventoryId(player: Player): List<Expandable> {
        return findAllByPlayer(player)
                .filter { it.type == EXPANDABLE }
                .map { it as Expandable }
    }

    fun findAllGenericItemByPlayer(player: Player): List<GenericItem> {
        return findAllByPlayer(player)
                .filter { it.type == ITEM }
                .map { it as GenericItem }
    }

    fun findAllWeaponsByInventoryId(player: Player): List<Weapon> {
        return findAllByPlayer(player)
                .filter { it.type == WEAPON }
                .map { it as Weapon }
    }
    // endregion

    override fun updateByPlayer(entity: Item, player: Player): Item? {
        return try {
            ItemsTable.update({ (ItemsTable.id eq entity.id) and (ItemsTable.playerName eq player.name) }) {
                it[ItemsTable.id] = EntityID(entity.id, ItemsTable)
                it.mapFieldsOfEntity(entity)
            }

            findByNameAndPlayer(entity.name, player)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteByPlayer(entity: Item, player: Player): Int = deleteByNameAndPlayer(entity.name, player)

    override fun deleteByNameAndPlayer(name: String, player: Player): Int {
        return try {
            ItemsTable.deleteWhere {
                (ItemsTable.name eq name) and (ItemsTable.playerName eq player.name)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteAllByPlayer(player: Player): Int {
        return try {
            ItemsTable.deleteWhere { ItemsTable.playerName eq player.name }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteAll() {
        ItemsTable.deleteAll()
    }
}