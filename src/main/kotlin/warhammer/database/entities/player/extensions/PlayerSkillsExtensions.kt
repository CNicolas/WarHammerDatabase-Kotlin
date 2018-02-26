package warhammer.database.entities.player.extensions

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

fun Player.getSkillByName(name: String) =
        skills.firstOrNull { it.name.toLowerCase() == name.toLowerCase() }

fun Player.getSkillsByCharacteristic(characteristic: Characteristic) =
        skills.filter { it.characteristic == characteristic }

fun Player.getSpecializationByName(name: String): Specialization? =
        getSpecializations().firstOrNull { it.name == name }

fun Skill.getSpecializationByName(name: String): Specialization? =
        specializations.firstOrNull { it.name == name }

fun Player.getSpecializations(): List<Specialization> =
        skills.flatMap {
            it.getSpecializations()
        }

fun Skill.getSpecializations(): List<Specialization> =
        specializations.filter { it.isSpecialized }
