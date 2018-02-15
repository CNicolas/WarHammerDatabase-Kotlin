package warhammer.database.daos.player.inventory

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.daos.PlayersDao
import warhammer.database.daos.player.PlayerInventoryDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerInventory
import warhammer.database.entities.player.inventory.Armor
import warhammer.database.entities.player.inventory.Expandable
import warhammer.database.entities.player.inventory.GenericItem
import warhammer.database.entities.player.inventory.Quality.MAGIC
import warhammer.database.entities.player.inventory.Range.*
import warhammer.database.entities.player.inventory.Weapon
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerInventoryTable
import warhammer.database.tables.player.inventory.ItemsTable
import java.sql.Connection

class ItemDaoTest {
    private val itemsDao = ItemsDao()

    @BeforeMethod
    fun createPlayersAndInventorys() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()
        val playerInventoryDao = PlayerInventoryDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(PlayersTable, PlayerInventoryTable, ItemsTable)

            playersDao.add(Player(id = 1, name = "PlayerName1"))
            playersDao.add(Player(id = 2, name = "PlayerName2"))
            playersDao.add(Player(id = 3, name = "PlayerName3"))

            playerInventoryDao.add(PlayerInventory(id = 1, playerId = 1))
            playerInventoryDao.add(PlayerInventory(id = 2, playerId = 2))
            playerInventoryDao.add(PlayerInventory(id = 3, playerId = 3))

            ItemsTable.deleteAll()
        }
    }

    // region CREATE
    @Test
    fun should_add_an_item() {
        transaction {
            itemsDao.add(Weapon(inventoryId = 1))

            assertThat(itemsDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_item() {
        val itemsToAdd = listOf(
                Armor(inventoryId = 1),
                Expandable(inventoryId = 2),
                GenericItem(inventoryId = 3))

        transaction {
            itemsDao.addAll(itemsToAdd)

            assertThat(itemsDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_an_item_then_fail_to_add_it_again() {
        transaction {
            var resOfInsert = itemsDao.add(Weapon(inventoryId = 1, name = "Wea"))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(itemsDao.findAll().size).isEqualTo(1)

            resOfInsert = itemsDao.add(Weapon(inventoryId = 1, name = "Wea"))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(itemsDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_an_item() {
        transaction {
            itemsDao.add(Armor(1, name = "hello", soak = 2))

            assertThat(itemsDao.findAll().size).isEqualTo(1)

            val itemById = itemsDao.findById(1)
            assertThat(itemById).isNotNull()
            assertThat(itemById?.soak).isEqualTo(2)

            val itemByName = itemsDao.findByName("hello")
            assertThat(itemByName).isNotNull()
            assertThat(itemByName?.soak).isEqualTo(2)
        }
    }

    @Test
    fun should_read_an_item_from_inventoryId() {
        transaction {
            itemsDao.add(GenericItem(inventoryId = 2, encumbrance = 1))

            assertThat(itemsDao.findAll().size).isEqualTo(1)

            val item = itemsDao.findByInventoryId(2)

            assertThat(item).isNotNull()
            assertThat(item?.encumbrance).isEqualTo(1)
        }
    }

    @Test
    fun should_read_an_item_of_each_type_by_id() {
        transaction {
            itemsDao.add(Weapon(id = 1, inventoryId = 1, criticalLevel = 3))
            itemsDao.add(Expandable(id = 2, inventoryId = 1, uses = 10))
            itemsDao.add(Armor(id = 3, inventoryId = 1, defense = 2))
            itemsDao.add(GenericItem(id = 4, inventoryId = 1, quality = MAGIC))
            itemsDao.add(Weapon(id = 5, inventoryId = 1, name = "otherWeapon", range = MEDIUM))

            assertThat(itemsDao.findAll().size).isEqualTo(5)

            val weapon = itemsDao.findWeaponById(1)
            assertThat(weapon).isNotNull()
            assertThat(weapon is Weapon).isTrue()
            assertThat(weapon?.criticalLevel).isEqualTo(3)
            assertThat(weapon?.range).isEqualTo(ENGAGED)

            val expandable = itemsDao.findExpandableById(2)
            assertThat(expandable).isNotNull()
            assertThat(expandable is Expandable).isTrue()
            assertThat(expandable?.uses).isEqualTo(10)

            val armor = itemsDao.findArmorById(3)
            assertThat(armor).isNotNull()
            assertThat(armor is Armor).isTrue()
            assertThat(armor?.defense).isEqualTo(2)

            val genericItem = itemsDao.findGenericItemById(4)
            assertThat(genericItem).isNotNull()
            assertThat(genericItem is GenericItem).isTrue()
            assertThat(genericItem?.quality).isEqualTo(MAGIC)

            val nullItem = itemsDao.findArmorById(1)
            assertThat(nullItem).isNull()
        }
    }

    @Test
    fun should_read_all_item() {
        val itemsToAdd = listOf(
                Weapon(inventoryId = 1, criticalLevel = 3),
                Expandable(inventoryId = 2, uses = 10),
                Armor(inventoryId = 3, defense = 2))

        transaction {
            itemsDao.addAll(itemsToAdd)

            val allInsertedItems = itemsDao.findAll()
            assertThat(allInsertedItems.size).isEqualTo(3)

            assertThat(allInsertedItems[0]?.inventoryId).isEqualTo(1)
            assertThat(allInsertedItems[0] is Weapon?).isTrue()
            assertThat(allInsertedItems[0]?.criticalLevel).isEqualTo(3)

            assertThat(allInsertedItems[1]?.inventoryId).isEqualTo(2)
            assertThat(allInsertedItems[1] is Expandable?).isTrue()
            assertThat(allInsertedItems[1]?.uses).isEqualTo(10)

            assertThat(allInsertedItems[2]?.inventoryId).isEqualTo(3)
            assertThat(allInsertedItems[2] is Armor?).isTrue()
            assertThat(allInsertedItems[2]?.defense).isEqualTo(2)
        }
    }

    @Test
    fun should_read_all_items_of_inventory() {
        val itemToAdd = listOf(
                Weapon(inventoryId = 1, criticalLevel = 3),
                Expandable(inventoryId = 2, uses = 10),
                Armor(inventoryId = 3, defense = 2))

        transaction {
            itemsDao.addAll(itemToAdd)

            val allInsertedItems = itemsDao.findAllByInventoryId(2)
            assertThat(allInsertedItems.size).isEqualTo(1)

            assertThat(allInsertedItems[0]?.inventoryId).isEqualTo(2)
            assertThat(allInsertedItems[0] is Expandable?).isTrue()
            assertThat(allInsertedItems[0]?.uses).isEqualTo(10)
        }
    }

    @Test
    fun should_read_each_type_of_item_in_inventory() {
        val itemToAdd = listOf(
                Weapon(inventoryId = 1, range = LONG),
                Expandable(inventoryId = 1, uses = 10),
                Armor(inventoryId = 1, defense = 2))

        transaction {
            itemsDao.addAll(itemToAdd)

            val armors = itemsDao.findAllArmorsByInventoryId(1)
            assertThat(armors.size).isEqualTo(1)
            assertThat(armors[0].defense).isEqualTo(2)

            val weapons = itemsDao.findAllWeaponsByInventoryId(1)
            assertThat(weapons.size).isEqualTo(1)
            assertThat(weapons[0].range).isEqualTo(LONG)

            val expandables = itemsDao.findAllExpandablesByInventoryId(1)
            assertThat(expandables.size).isEqualTo(1)
            assertThat(expandables[0].uses).isEqualTo(10)

            val items = itemsDao.findAllGenericItemByInventoryId(1)
            assertThat(items.size).isEqualTo(0)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_an_item() {
        transaction {
            // ADD
            val id = itemsDao.add(Weapon(inventoryId = 1))
            assertThat(itemsDao.findAll().size).isEqualTo(1)

            // FIND
            val item = itemsDao.findById(id)
            assertThat(item).isNotNull()
            assertThat(item is Weapon).isTrue()

            val weapon = itemsDao.findWeaponById(id)
            assertThat(weapon).isNotNull()
            assertThat(weapon?.inventoryId).isEqualTo(1)
            assertThat(weapon?.damage).isEqualTo(0)

            assertThat(weapon).isEqualToComparingFieldByField(item)

            // UPDATE
            val weaponToUpdate = weapon?.copy(damage = 3)
            itemsDao.update(weaponToUpdate!!)
            assertThat(itemsDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newWeapon = itemsDao.findWeaponById(id)
            assertThat(newWeapon).isNotNull()
            assertThat(newWeapon?.inventoryId).isEqualTo(1)
            assertThat(newWeapon?.damage).isEqualTo(3)
        }
    }

    @Test
    fun should_update_all_item() {
        transaction {
            // ADD
            val id1 = itemsDao.add(Armor(inventoryId = 1))
            val id2 = itemsDao.add(Expandable(inventoryId = 2))
            assertThat(itemsDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = itemsDao.updateAll(
                    listOf(Armor(id = id1, inventoryId = 1, soak = 2),
                            Expandable(id = id2, inventoryId = 2, uses = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedItems = itemsDao.findAll()
            assertThat(allInsertedItems.size).isEqualTo(2)
            assertThat(allInsertedItems.map { it?.id }).containsExactly(id1, id2)

            assertThat(allInsertedItems[0]?.inventoryId).isEqualTo(1)
            assertThat(allInsertedItems[0] is Armor).isTrue()
            assertThat(allInsertedItems[0]?.soak).isEqualTo(2)

            assertThat(allInsertedItems[1]?.inventoryId).isEqualTo(2)
            assertThat(allInsertedItems[1] is Expandable).isTrue()
            assertThat(allInsertedItems[1]?.uses).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_item() {
        transaction {
            assertThat(itemsDao.findAll().size).isEqualTo(0)

            val res = itemsDao.update(Weapon(id = 5, inventoryId = 1))
            assertThat(res).isEqualTo(-1)
            assertThat(itemsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        transaction {
            drop(ItemsTable)

            val res = itemsDao.update(Armor(id = 1, inventoryId = 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_Item() {
        val item1 = Expandable(inventoryId = 1)
        val item2 = GenericItem(inventoryId = 2)
        val item3 = Armor(inventoryId = 3)

        transaction {
            val addAllResult = itemsDao.addAll(listOf(item1, item2, item3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = itemsDao.delete(item2)
            assertThat(res).isEqualTo(1)
            assertThat(itemsDao.findAll().size).isEqualTo(2)
            assertThat(itemsDao.findByInventoryId(1)).isNotNull()
            assertThat(itemsDao.findByInventoryId(2)).isNull()
            assertThat(itemsDao.findByInventoryId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_item_with_inventoryId() {
        val item = Armor(inventoryId = 2)

        transaction {
            val addedCharacteristics = itemsDao.add(item)
            assertThat(addedCharacteristics).isEqualTo(1)
            assertThat(itemsDao.findByInventoryId(2)).isNotNull()

            val res = itemsDao.deleteByInventoryId(2)
            assertThat(res).isEqualTo(1)
            assertThat(itemsDao.findByInventoryId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_items() {
        val item1 = Weapon(inventoryId = 1)
        val item2 = Armor(inventoryId = 2)

        transaction {
            itemsDao.add(item1)
            itemsDao.add(item2)
            assertThat(itemsDao.findAll().size).isEqualTo(2)

            itemsDao.deleteAll()
            assertThat(itemsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_delete_all_armors_of_inventory() {
        val item1 = Weapon(inventoryId = 1, name = "Baton")
        val item2 = Armor(inventoryId = 1, name = "Plates")
        val item3 = Armor(inventoryId = 2, name = "Plates")

        transaction {
            itemsDao.addAll(listOf(item1, item2, item3))
            assertThat(itemsDao.findAll().size).isEqualTo(3)
            assertThat(itemsDao.findAllByInventoryId(1).size).isEqualTo(2)

            itemsDao.deleteAllArmorsByInventoryId(1)
            assertThat(itemsDao.findAll().size).isEqualTo(2)
            val itemsOfInventory = itemsDao.findAllByInventoryId(1)
            assertThat(itemsOfInventory.size).isEqualTo(1)
            assertThat(itemsOfInventory[0] is Weapon).isTrue()
        }
    }

    @Test
    fun should_delete_all_weapons_of_inventory() {
        val item1 = Weapon(inventoryId = 1, name = "Baton")
        val item2 = Armor(inventoryId = 1, name = "Plates")
        val item3 = Weapon(inventoryId = 2, name = "Baton")

        transaction {
            itemsDao.addAll(listOf(item1, item2, item3))
            assertThat(itemsDao.findAll().size).isEqualTo(3)
            assertThat(itemsDao.findAllByInventoryId(1).size).isEqualTo(2)

            itemsDao.deleteAllWeaponsByInventoryId(1)
            assertThat(itemsDao.findAll().size).isEqualTo(2)

            val itemsOfInventory = itemsDao.findAllByInventoryId(1)
            assertThat(itemsOfInventory.size).isEqualTo(1)
            assertThat(itemsOfInventory[0] is Armor).isTrue()
        }
    }

    @Test
    fun should_delete_all_generic_items_of_inventory() {
        val item1 = GenericItem(inventoryId = 1, name = "Bout de bois")
        val item2 = Armor(inventoryId = 1, name = "Plates")
        val item3 = GenericItem(inventoryId = 2, name = "Bout de bois")

        transaction {
            itemsDao.addAll(listOf(item1, item2, item3))
            assertThat(itemsDao.findAll().size).isEqualTo(3)
            assertThat(itemsDao.findAllByInventoryId(1).size).isEqualTo(2)

            itemsDao.deleteAllGenericItemsByInventoryId(1)
            assertThat(itemsDao.findAll().size).isEqualTo(2)

            val itemsOfInventory = itemsDao.findAllByInventoryId(1)
            assertThat(itemsOfInventory.size).isEqualTo(1)
            assertThat(itemsOfInventory[0] is Armor).isTrue()
        }
    }

    @Test
    fun should_delete_all_expandables_of_inventory() {
        val item1 = Expandable(inventoryId = 1, name = "Potion")
        val item2 = Armor(inventoryId = 1, name = "Plates")
        val item3 = Expandable(inventoryId = 2, name = "Potion")

        transaction {
            itemsDao.addAll(listOf(item1, item2, item3))
            assertThat(itemsDao.findAll().size).isEqualTo(3)
            assertThat(itemsDao.findAllByInventoryId(1).size).isEqualTo(2)

            itemsDao.deleteAllExpandablesByInventoryId(1)
            assertThat(itemsDao.findAll().size).isEqualTo(2)

            val itemsOfInventory = itemsDao.findAllByInventoryId(1)
            assertThat(itemsOfInventory.size).isEqualTo(1)
            assertThat(itemsOfInventory[0] is Armor).isTrue()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_item() {
        transaction {
            assertThat(itemsDao.findAll().size).isEqualTo(0)

            val res = itemsDao.delete(Expandable(id = 6, inventoryId = 1))
            assertThat(res).isEqualTo(0)
            assertThat(itemsDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        transaction {
            drop(ItemsTable)

            val res = itemsDao.delete(GenericItem(id = 1, inventoryId = 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_inventory_id_but_inexistant_table() {
        transaction {
            drop(ItemsTable)

            val res = itemsDao.deleteByInventoryId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}