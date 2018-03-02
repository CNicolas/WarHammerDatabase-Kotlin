package warhammer.database.entities.player.playerLinked.item

import warhammer.database.entities.NamedEntity
import warhammer.database.entities.player.playerLinked.PlayerLinkedEntity
import warhammer.database.entities.player.playerLinked.item.enums.ItemSubType
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
    var subType: ItemSubType?

    var uses: Int?

    var isEquipped: Boolean?

    var soak: Int?
    var defense: Int?

    var damage: Int?
    var criticalLevel: Int?
    var range: Range?
}