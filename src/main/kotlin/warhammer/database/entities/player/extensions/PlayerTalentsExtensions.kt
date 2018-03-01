package warhammer.database.entities.player.extensions

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

fun Player.getTalentsByType(talentType: TalentType): List<Talent> =
        talents.filterByType(talentType)

fun Player.getPassiveTalents(): List<Talent> =
        talents.filterPassive()

fun Player.getEquippedTalents(): List<Talent> =
        talents.filter { it.isEquipped }

fun Player.equipTalent(talent: Talent): List<Talent> {
    talents.firstOrNull { it == talent }?.isEquipped = true

    return getEquippedTalents()
}

fun List<Talent>.filterPassive() =
        filter { it.cooldown == PASSIVE }

fun List<Talent>.filterExhaustible() =
        filter { it.cooldown == TALENT }

fun List<Talent>.filterByType(talentType: TalentType) =
        filter { it.type == talentType }
