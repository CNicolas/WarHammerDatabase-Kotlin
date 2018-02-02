package services

import entities.WarHammerNamedEntity

interface Service<E : WarHammerNamedEntity> {
    fun add(entity: E): Int
    fun addAll(entities: List<E>): List<Int>

    fun findAll(): List<E?>
    fun findById(id: Int): E?
    fun findByName(name: String): E?
    fun countAll(): Int

    fun update(entity: E): Int
    fun updateAll(entities: List<E>): List<Int>

    fun delete(entity: E): Int
    fun deleteAll()
}