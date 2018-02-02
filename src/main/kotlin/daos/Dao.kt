package daos

import entities.WarHammerNamedEntity

interface Dao<E : WarHammerNamedEntity> {
    fun add(entity: E): Int
    fun addAll(entities: List<E>): List<Int>

    fun findById(id: Int): E?
    fun findByName(name: String): E?
    fun findAll(): List<E?>

    fun update(entity: E): Int
    fun updateAll(entities: List<E>): List<Int>

    fun delete(entity: E): Int
    fun deleteAll()
}