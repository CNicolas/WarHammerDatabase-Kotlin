package warhammer.database.entities.player.item

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.PlayerLinkedEntity
import warhammer.database.entities.player.item.enums.ItemType
import warhammer.database.entities.player.item.enums.Quality
import warhammer.database.entities.player.item.enums.Range

interface Item : NamedEntity, PlayerLinkedEntity {
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