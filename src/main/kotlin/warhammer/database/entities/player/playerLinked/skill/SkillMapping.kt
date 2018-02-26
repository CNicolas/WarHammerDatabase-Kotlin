package warhammer.database.entities.player.playerLinked.skill

import com.beust.klaxon.Klaxon
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.tables.SkillsTable


fun ResultRow?.mapToSkill(): Skill? = when (this) {
    null -> null
    else -> {
        Skill(name = this[SkillsTable.name],
                characteristic = Characteristic.valueOf(this[SkillsTable.characteristic]),
                type = SkillType.valueOf(this[SkillsTable.type]),
                level = this[SkillsTable.level],
                specializations = Klaxon().parseArray(this[SkillsTable.specializations])!!,
                id = this[SkillsTable.id].value
        )
    }
}

fun UpdateBuilder<Int>.mapFieldsOfEntity(entity: Skill, player: Player) {
    this[SkillsTable.playerId] = player.id

    this[SkillsTable.name] = entity.name
    this[SkillsTable.characteristic] = entity.characteristic.toString()
    this[SkillsTable.type] = entity.type.toString()
    this[SkillsTable.level] = entity.level
    this[SkillsTable.specializations] = Klaxon().toJsonString(entity.specializations)
}