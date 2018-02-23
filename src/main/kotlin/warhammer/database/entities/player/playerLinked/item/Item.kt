package warhammer.database.entities.player.playerLinked.item

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.playerLinked.PlayerLinkedEntity
import warhammer.database.entities.player.playerLinked.item.enums.ItemType
import warhammer.database.entities.player.playerLinked.item.enums.Quality
import warhammer.database.entities.player.playerLinked.item.enums.Range

interface Item : NamedEntity, PlayerLinkedEntity {
    override var name: String

    var description: String?
    var encumbrance: Int
    var quantity: Int
    var quality: Quality
    var type: ItemType

    var uses: Int?

    var isEquipped: Boolean?

    var soak: Int?
    var defense: Int?

    var damage: Int?
    var criticalLevel: Int?
    var range: Range?
}

fun Item.merge(newItem: Item): Item {
    if (name != newItem.name) name = newItem.name

    if (description != newItem.description) description = newItem.description
    if (encumbrance != newItem.encumbrance) encumbrance = newItem.encumbrance
    if (quantity != newItem.quantity) quantity = newItem.quantity
    if (quality != newItem.quality) quality = newItem.quality
    if (type != newItem.type) type = newItem.type

    if (uses != newItem.uses) uses = newItem.uses

    if (isEquipped != newItem.isEquipped) isEquipped = newItem.isEquipped

    if (soak != newItem.soak) soak = newItem.soak
    if (defense != newItem.defense) defense = newItem.defense

    if (damage != newItem.damage) damage = newItem.damage
    if (criticalLevel != newItem.criticalLevel) criticalLevel = newItem.criticalLevel
    if (range != newItem.range) range = newItem.range

    return this
}