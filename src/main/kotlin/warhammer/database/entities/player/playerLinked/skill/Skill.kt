package warhammer.database.entities.player.playerLinked.skill

import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.entities.player.playerLinked.PlayerLinkedEntity

data class Skill(override val name: String,
                 val characteristic: Characteristic,
                 val type: SkillType = SkillType.BASIC,
                 var level: Int = 0,
                 var specializations: List<Specialization> = listOf(),
                 override val id: Int = -1) : PlayerLinkedEntity