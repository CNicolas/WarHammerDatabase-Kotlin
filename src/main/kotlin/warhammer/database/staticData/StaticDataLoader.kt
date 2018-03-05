package warhammer.database.staticData

import com.beust.klaxon.Klaxon
import warhammer.database.entities.player.playerLinked.skill.Skill
import warhammer.database.entities.player.playerLinked.skill.SkillType
import warhammer.database.entities.player.playerLinked.skill.Specialization
import warhammer.database.entities.player.playerLinked.talent.Talent
import warhammer.database.entities.player.playerLinked.talent.TalentCooldown
import warhammer.database.entities.player.playerLinked.talent.TalentType
import warhammer.database.extensions.talents.findByType

fun getAllSkills(): List<Skill> =
        loadSkills() ?: listOf()

fun getAdvancedSkills(): List<Skill> =
        getAllSkills().filter { it.type == SkillType.ADVANCED }


fun getAllSpecializations(): Map<Skill, List<Specialization>> =
        getAllSkills().map { it to it.specializations }.toMap()


fun getAllTalents(): List<Talent> =
        loadTalents() ?: listOf()

fun getPassiveTalents(): List<Talent> =
        getAllTalents().filter { it.cooldown == TalentCooldown.PASSIVE }

fun getExhaustibleTalents(): List<Talent> =
        getAllTalents().filter { it.cooldown == TalentCooldown.TALENT }

fun getTalentsByType(talentType: TalentType): List<Talent> =
        getAllTalents().findByType(talentType)


private fun loadSkills(): List<Skill>? {
    return Klaxon().parseArray({}::class.java.getResource("/skills.json").readText())
}

private fun loadTalents(): List<Talent>? {
    return Klaxon().parseArray({}::class.java.getResource("/talents.json").readText())
}