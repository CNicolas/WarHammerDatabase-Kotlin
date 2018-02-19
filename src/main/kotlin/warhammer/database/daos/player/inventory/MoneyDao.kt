package warhammer.database.daos.player.inventory

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.daos.AbstractDao
import warhammer.database.entities.mapping.inventory.mapFieldsOfEntity
import warhammer.database.entities.mapping.inventory.mapToMoney
import warhammer.database.entities.player.inventory.Money
import warhammer.database.tables.player.inventory.MoneyTable
import java.lang.Exception

class MoneyDao : AbstractDao<Money>(), PlayerInventoryLinkedDao<Money> {
    override val table: IntIdTable = MoneyTable

    fun findByInventoryId(inventoryId: Int): Money? {
        val result = MoneyTable.select { MoneyTable.inventoryId eq inventoryId }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Money): Int {
        return try {
            MoneyTable.update({
                (MoneyTable.id eq entity.id) or (MoneyTable.inventoryId eq entity.inventoryId)
            }) {
                mapEntityToTable(it, entity)
            }

            findByInventoryId(entity.inventoryId)?.id!!
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Money): Int {
        return try {
            MoneyTable.deleteWhere {
                (MoneyTable.id eq entity.id) or (MoneyTable.inventoryId eq entity.inventoryId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteByInventoryId(inventoryId: Int): Int {
        return try {
            MoneyTable.deleteWhere { MoneyTable.inventoryId eq inventoryId }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun mapResultRowToEntity(result: ResultRow?): Money? = result.mapToMoney()

    override fun mapEntityToTable(it: UpdateStatement, entity: Money) {
        it[MoneyTable.id] = EntityID(entity.id, MoneyTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Money) =
            it.mapFieldsOfEntity(entity)
}