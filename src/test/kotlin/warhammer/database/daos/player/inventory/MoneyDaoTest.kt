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
import warhammer.database.entities.player.inventory.Money
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerInventoryTable
import warhammer.database.tables.player.inventory.MoneyTable
import java.sql.Connection

class MoneyDaoTest {
    private val moneyDao = MoneyDao()

    @BeforeMethod
    fun createPlayersAndInventorys() {
        Database.connect("jdbc:sqlite:testSqlite:?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val playersDao = PlayersDao()
        val playerInventoryDao = PlayerInventoryDao()

        transaction {
            logger.addLogger(StdOutSqlLogger)

            create(PlayersTable, PlayerInventoryTable, MoneyTable)

            playersDao.deleteAll()
            playerInventoryDao.deleteAll()
            MoneyTable.deleteAll()

            playersDao.add(Player(id = 1, name = "PlayerName1"))
            playersDao.add(Player(id = 2, name = "PlayerName2"))
            playersDao.add(Player(id = 3, name = "PlayerName3"))

            playerInventoryDao.add(PlayerInventory(id = 1, playerId = 1))
            playerInventoryDao.add(PlayerInventory(id = 2, playerId = 2))
            playerInventoryDao.add(PlayerInventory(id = 3, playerId = 3))
        }
    }

    // region CREATE
    @Test
    fun should_add_a_money() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            moneyDao.add(Money(inventoryId = 1))

            assertThat(moneyDao.findAll().size).isEqualTo(1)
        }
    }

    @Test
    fun should_add_all_money() {
        val moneyToAdd = listOf(
                Money(inventoryId = 1),
                Money(inventoryId = 2),
                Money(inventoryId = 3))

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            moneyDao.addAll(moneyToAdd)

            assertThat(moneyDao.findAll().size).isEqualTo(3)
        }
    }

    @Test
    fun should_add_a_money_then_fail_to_add_it_again() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            var resOfInsert = moneyDao.add(Money(inventoryId = 1))
            assertThat(resOfInsert).isEqualTo(1)
            assertThat(moneyDao.findAll().size).isEqualTo(1)

            resOfInsert = moneyDao.add(Money(inventoryId = 1))
            assertThat(resOfInsert).isEqualTo(-1)
            assertThat(moneyDao.findAll().size).isEqualTo(1)
        }
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_money() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            moneyDao.add(Money(1, brass = 2))

            assertThat(moneyDao.findAll().size).isEqualTo(1)

            val money = moneyDao.findById(1)

            assertThat(money).isNotNull()
            assertThat(money?.brass).isEqualTo(2)
        }
    }

    @Test
    fun should_read_a_money_from_inventoryId() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            moneyDao.add(Money(inventoryId = 2, silver = 1))

            assertThat(moneyDao.findAll().size).isEqualTo(1)

            val money = moneyDao.findByInventoryId(2)

            assertThat(money).isNotNull()
            assertThat(money?.silver).isEqualTo(1)
        }
    }

    @Test
    fun should_read_all_money() {
        val moneyToAdd = listOf(
                Money(inventoryId = 1, brass = 3),
                Money(inventoryId = 2, silver = 1),
                Money(inventoryId = 3, gold = 4))

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            moneyDao.addAll(moneyToAdd)

            val allInsertedMoney = moneyDao.findAll()
            assertThat(allInsertedMoney.size).isEqualTo(3)

            assertThat(allInsertedMoney[0]?.inventoryId).isEqualTo(1)
            assertThat(allInsertedMoney[0]?.brass).isEqualTo(3)

            assertThat(allInsertedMoney[1]?.inventoryId).isEqualTo(2)
            assertThat(allInsertedMoney[1]?.silver).isEqualTo(1)

            assertThat(allInsertedMoney[2]?.inventoryId).isEqualTo(3)
            assertThat(allInsertedMoney[2]?.gold).isEqualTo(4)
        }
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_money() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            // ADD
            val id = moneyDao.add(Money(inventoryId = 1))
            assertThat(moneyDao.findAll().size).isEqualTo(1)

            // FIND
            val money = moneyDao.findById(id)
            assertThat(money).isNotNull()
            assertThat(money?.inventoryId).isEqualTo(1)
            assertThat(money?.silver).isEqualTo(0)

            // UPDATE
            val moneyToUpdate = money?.copy(silver = 3)
            moneyDao.update(moneyToUpdate!!)
            assertThat(moneyDao.findAll().size).isEqualTo(1)

            // VERIFY
            val newMoney = moneyDao.findById(id)
            assertThat(newMoney).isNotNull()
            assertThat(newMoney?.inventoryId).isEqualTo(1)
            assertThat(newMoney?.silver).isEqualTo(3)
        }
    }

    @Test
    fun should_update_all_money() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            // ADD
            val id1 = moneyDao.add(Money(inventoryId = 1))
            val id2 = moneyDao.add(Money(inventoryId = 2))
            assertThat(moneyDao.findAll().size).isEqualTo(2)

            // UPDATE
            val updatedIds = moneyDao.updateAll(
                    listOf(Money(1, id1, brass = 2),
                            Money(2, id2, silver = 2))
            )
            assertThat(updatedIds).containsExactly(id1, id2)

            // VERIFY
            val allInsertedMoney = moneyDao.findAll()
            assertThat(allInsertedMoney.size).isEqualTo(2)
            assertThat(allInsertedMoney.map { it?.id }).containsExactly(id1, id2)

            assertThat(allInsertedMoney[0]?.inventoryId).isEqualTo(1)
            assertThat(allInsertedMoney[0]?.brass).isEqualTo(2)

            assertThat(allInsertedMoney[1]?.inventoryId).isEqualTo(2)
            assertThat(allInsertedMoney[1]?.silver).isEqualTo(2)
        }
    }

    @Test
    fun should_return_false_when_update_a_inexistant_money() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            assertThat(moneyDao.findAll().size).isEqualTo(0)

            val res = moneyDao.update(Money(inventoryId = 1))
            assertThat(res).isEqualTo(-1)
            assertThat(moneyDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_update_on_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            drop(MoneyTable)

            val res = moneyDao.update(Money(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_money() {
        val money1 = Money(inventoryId = 1)
        val money2 = Money(inventoryId = 2)
        val money3 = Money(inventoryId = 3)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            val addAllResult = moneyDao.addAll(listOf(money1, money2, money3))
            assertThat(addAllResult.size).isEqualTo(3)
            assertThat(addAllResult).containsExactly(1, 2, 3)

            val res = moneyDao.delete(money2)
            assertThat(res).isEqualTo(1)
            assertThat(moneyDao.findAll().size).isEqualTo(2)
            assertThat(moneyDao.findByInventoryId(1)).isNotNull()
            assertThat(moneyDao.findByInventoryId(2)).isNull()
            assertThat(moneyDao.findByInventoryId(3)).isNotNull()
        }
    }

    @Test
    fun should_delete_a_money_from_inventoryId() {
        val money = Money(inventoryId = 2)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            val addedCharacteristics = moneyDao.add(money)
            assertThat(addedCharacteristics).isEqualTo(1)
            assertThat(moneyDao.findByInventoryId(2)).isNotNull()

            val res = moneyDao.deleteByInventoryId(2)
            assertThat(res).isEqualTo(1)
            assertThat(moneyDao.findByInventoryId(2)).isNull()
        }
    }

    @Test
    fun should_delete_all_money() {
        val money1 = Money(inventoryId = 1)
        val money2 = Money(inventoryId = 2)

        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            moneyDao.add(money1)
            moneyDao.add(money2)
            assertThat(moneyDao.findAll().size).isEqualTo(2)

            moneyDao.deleteAll()
            assertThat(moneyDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_a_inexistant_money() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            assertThat(moneyDao.findAll().size).isEqualTo(0)

            val res = moneyDao.delete(Money(1, 6))
            assertThat(res).isEqualTo(0)
            assertThat(moneyDao.findAll()).isEmpty()
        }
    }

    @Test
    fun should_return_false_when_delete_on_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            drop(MoneyTable)

            val res = moneyDao.delete(Money(1, 1))
            assertThat(res).isEqualTo(-1)
        }
    }

    @Test
    fun should_return_false_when_delete_on_inventory_id_but_inexistant_table() {
        transaction {
            logger.addLogger(StdOutSqlLogger)
            
            drop(MoneyTable)

            val res = moneyDao.deleteByInventoryId(1)
            assertThat(res).isEqualTo(-1)
        }
    }
    // endregion
}