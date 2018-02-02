package daos

import entities.NamedEntity

interface Dao<E : NamedEntity> {
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