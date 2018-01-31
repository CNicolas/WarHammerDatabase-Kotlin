package daos

import entities.WarHammerNamedEntity

interface Dao<T : WarHammerNamedEntity> {
    fun add(entity: T): Boolean
    fun findByName(name: String): T?
}