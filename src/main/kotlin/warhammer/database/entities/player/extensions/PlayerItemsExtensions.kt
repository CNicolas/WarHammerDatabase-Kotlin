package warhammer.database.entities.player.extensions

import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.item.*
import warhammer.database.entities.player.playerLinked.item.enums.ItemType
import warhammer.database.entities.player.playerLinked.item.enums.Range

fun Player.addItem(item: Item): List<Item> {
    val mutableItems = items.toMutableList()
    mutableItems.add(item)
    items = mutableItems.toList()

    return items
}

fun Player.getArmors() =
        items.filter { it.type == ItemType.ARMOR }
                .map { it as Armor }

fun Player.getExpandables() =
        items.filter { it.type == ItemType.EXPANDABLE }
                .map { it as Expandable }

fun Player.getGenericItems() =
        items.filter { it.type == ItemType.GENERIC_ITEM }
                .map { it as GenericItem }

fun Player.getWeapons() =
        items.filter { it.type == ItemType.WEAPON }
                .map { it as Weapon }

fun Player.getArmorByName(name: String): Armor? = getArmors().firstOrNull { it.name == name }
fun Player.getExpandableByName(name: String): Expandable? = getExpandables().firstOrNull { it.name == name }
fun Player.getGenericItemByName(name: String): GenericItem? = getGenericItems().firstOrNull { it.name == name }
fun Player.getWeaponByName(name: String): Weapon? = getWeapons().firstOrNull { it.name == name }

fun Player.removeItem(item: Item): List<Item> {
    val mutableItems = items.toMutableList()
    mutableItems.remove(item)
    items = mutableItems.toList()

    return items
}

fun Player.removeAllItems() {
    items = listOf()
}

fun Player.getWeaponDamage(weapon: Weapon): Int {
    val weaponDamage = weapon.damage ?: 0
    return when (weapon.range) {
        null -> weaponDamage
        Range.ENGAGED -> strength.value + weaponDamage
        else -> agility.value + weaponDamage
    }
}