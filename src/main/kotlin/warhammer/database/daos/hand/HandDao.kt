package warhammer.database.daos.hand

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.daos.AbstractNameKeyDao
import warhammer.database.entities.hand.Hand
import warhammer.database.entities.hand.mapFieldsOfEntity
import warhammer.database.entities.hand.mapToHand
import warhammer.database.tables.HandsTable

class HandDao : AbstractNameKeyDao<Hand>() {
    override val table = HandsTable

    override fun mapResultRowToEntity(result: ResultRow?): Hand? = result.mapToHand()

    override fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: Hand) = statement.mapFieldsOfEntity(entity)

    override fun predicateByName(name: String): SqlExpressionBuilder.() -> Op<Boolean> = { table.name eq name }
}