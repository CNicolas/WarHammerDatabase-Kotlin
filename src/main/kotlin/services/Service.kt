package services

import entities.WarHammerNamedEntity

interface Service<E : WarHammerNamedEntity> {
    fun add(entity: E): E?
    fun addAll(entities: List<E>): List<E?>

    fun findAll(): List<E?>
    fun findById(id: Int): E?
    fun findByName(name: String): E?
    fun countAll(): Int

    fun update(entity: E): E?
    fun updateAll(entities: List<E>): List<E?>

    fun delete(entity: E): Boolean
    fun deleteAll()
}