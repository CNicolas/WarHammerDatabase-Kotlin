package warhammer.database.entities.player.inventory.item

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.inventory.item.enums.ItemType
import warhammer.database.entities.player.inventory.item.enums.Quality
import warhammer.database.entities.player.inventory.item.enums.Range

interface Item : NamedEntity {
    val inventoryId: Int

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