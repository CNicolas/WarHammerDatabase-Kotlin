package warhammer.database.extensions.talents

import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.talent.Talent
import warhammer.database.entities.player.playerLinked.talent.TalentCooldown.PASSIVE
import warhammer.database.entities.player.playerLinked.talent.TalentCooldown.TALENT
import warhammer.database.entities.player.playerLinked.talent.TalentType

fun Player.addTalent(talent: Talent): List<Talent> {
    val mutableTalents = talents.toMutableList()
    mutableTalents.add(talent)
    talents = mutableTalents.toList()

    return talents
}

fun Player.getTalentsByType(talentType: TalentType) = talents.findByType(talentType)
fun Player.getPassiveTalents() = talents.filter { it.cooldown == PASSIVE }
fun Player.getExhaustibleTalents() = talents.filter { it.cooldown == TALENT }
fun Player.getEquippedTalents() = talents.filter { it.isEquipped }

fun Player.equipTalent(talent: Talent): List<Talent> {
    talents.firstOrNull { it == talent }?.isEquipped = true

    return getEquippedTalents()
}


fun List<Talent>.findByType(talentType: TalentType) =
        filter { it.type == talentType }
