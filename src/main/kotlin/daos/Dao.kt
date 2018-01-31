package daos

import entities.WarHammerNamedEntity

interface Dao<T : WarHammerNamedEntity> {
    fun add(entity: T): Boolean
    fun addAll(entities: List<T>): Boolean

    fun findByName(name: String): T?
    fun findAll(): List<T?>

    fun update(entity: T): Boolean
    fun updateAll(entities: List<T>): Boolean

    fun delete(entity: T): Boolean
    fun deleteAll(): Boolean
}