package warhammer.database.extensions.skills

import warhammer.database.entities.player.Player
import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.entities.player.playerLinked.skill.Skill
import warhammer.database.entities.player.playerLinked.skill.Specialization

fun Player.addSkill(skill: Skill): List<Skill> {
    val mutableSkills = skills.toMutableList()
    mutableSkills.add(skill)
    skills = mutableSkills.toList()

    return skills
}

fun Player.getSkillByName(name: String): Skill? =
        skills.firstOrNull { it.name.toLowerCase() == name.toLowerCase() }

fun Player.getSkillsByCharacteristic(characteristic: Characteristic): List<Skill> =
        skills.filter { it.characteristic == characteristic }

fun Player.getSpecializations(): List<Specialization> =
        skills.flatMap {
            it.getSpecializedSpecializations()
        }

fun Player.getSpecializationByName(name: String): Specialization? =
        getSpecializations().firstOrNull { it.name == name }


fun Skill.getSpecializedSpecializations(): List<Specialization> =
        specializations.filter { it.isSpecialized }

fun Skill.getSpecializationByName(name: String): Specialization? =
        specializations.firstOrNull { it.name == name }