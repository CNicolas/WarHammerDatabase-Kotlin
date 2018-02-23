package warhammer.database.daos.player.playerLinked

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.skill.Skill
import warhammer.database.entities.player.playerLinked.skill.mapFieldsOfEntity
import warhammer.database.entities.player.playerLinked.skill.mapToSkill
import warhammer.database.tables.SkillsTable

class SkillsDao : AbstractPlayerLinkedDao<Skill>() {
    override val table = SkillsTable

    override fun mapResultRowToEntity(result: ResultRow?): Skill? = result.mapToSkill()

    override fun mapFieldsOfEntityToTable(statement: UpdateBuilder<Int>, entity: Skill, player: Player) =
            statement.mapFieldsOfEntity(entity, player)

    override fun predicateByPlayer(player: Player): SqlExpressionBuilder.() -> Op<Boolean> = { table.playerName eq player.name }
}