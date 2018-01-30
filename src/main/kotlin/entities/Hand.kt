package entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tables.Hands

class Hand(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Hand>(Hands)

    var name by Hands.name
    var characteristicDicesCount by Hands.characteristicDicesCount
    var expertiseDicesCount by Hands.expertiseDicesCount
    var fortuneDicesCount by Hands.fortuneDicesCount
    var conservativeDicesCount by Hands.conservativeDicesCount
    var recklessDicesCount by Hands.recklessDicesCount
    var challengeDicesCount by Hands.challengeDicesCount
    var misfortuneDicesCount by Hands.misfortuneDicesCount
}