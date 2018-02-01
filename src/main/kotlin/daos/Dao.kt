package daos

import entities.WarHammerNamedEntity

interface Dao<T : WarHammerNamedEntity> {
    fun add(entity: T): Int
    fun addAll(entities: List<T>): List<Int>

    fun findById(id: Int): T?
    fun findByName(name: String): T?
    fun findAll(): List<T?>

    fun update(entity: T): Int
    fun updateAll(entities: List<T>): List<Int>

    fun delete(entity: T): Boolean
    fun deleteAll(): Boolean
}