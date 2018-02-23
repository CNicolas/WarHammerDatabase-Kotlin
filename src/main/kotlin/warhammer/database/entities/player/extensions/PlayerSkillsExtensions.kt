package warhammer.database.entities.player.extensions

import warhammer.database.entities.player.Player
import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.entities.player.playerLinked.skill.Skill

fun Player.addSkill(skill: Skill): List<Skill> {
    val mutableSkills = skills.toMutableList()
    mutableSkills.add(skill)
    skills = mutableSkills.toList()

    return skills
}

fun Player.getSkillByName(name: String) = skills.firstOrNull { it.name.toLowerCase() == name.toLowerCase() }

fun Player.getSkillsByCharacteristic(characteristic: Characteristic) =
        skills.filter { it.characteristic == characteristic }


fun Player.updateSkillLevel(skill: Skill): List<Skill> {
    skills.forEach {
        if (it.id == skill.id) {
            it.level = skill.level
        }
    }

    return skills
}