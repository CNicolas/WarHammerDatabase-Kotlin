package warhammer.database.entities.player

import warhammer.database.entities.player.enums.Characteristic
import warhammer.database.entities.player.enums.Characteristic.*
import warhammer.database.entities.player.enums.Race
import warhammer.database.entities.player.item.*
import warhammer.database.entities.player.item.enums.ItemType

val Player.maxStress: Int
    get() = willpower.value * 2
val Player.maxExhaustion: Int
    get() = toughness.value * 2

val Player.maxEncumbrance: Int
    get() = strength.value * 5 + strength.fortuneValue + 5 + when (race) {
        Race.DWARF -> 5
        else -> 0
    }

operator fun Player.get(characteristic: Characteristic): CharacteristicValue = when (characteristic) {
    STRENGTH -> strength
    TOUGHNESS -> toughness
    AGILITY -> agility
    INTELLIGENCE -> intelligence
    WILLPOWER -> willpower
    FELLOWSHIP -> fellowship
}

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

fun Player.updateItem(item: Item): List<Item> {
    items.forEach {
        if (it.id == item.id) {
            it.merge(item)
        }
    }

    return items
}

fun Player.removeItem(item: Item): List<Item> {
    val mutableItems = items.toMutableList()
    mutableItems.remove(item)
    items = mutableItems.toList()

    return items
}