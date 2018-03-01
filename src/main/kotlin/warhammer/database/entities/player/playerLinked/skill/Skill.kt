package warhammer.database.entities.player.playerLinked.skill

import warhammer.database.entities.player.enums.Characteristic

data class Skill(val name: String,
                 val characteristic: Characteristic,
                 val type: SkillType = SkillType.BASIC,
                 var level: Int = 0,
                 var specializations: List<Specialization> = listOf())