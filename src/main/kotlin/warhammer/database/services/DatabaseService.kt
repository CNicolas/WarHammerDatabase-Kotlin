package warhammer.database.services

import warhammer.database.entities.WarHammerEntity

interface DatabaseService<E : WarHammerEntity> {
    fun add(entity: E): E?
    fun addAll(entities: List<E>): List<E?>

    fun findAll(): List<E?>
    fun findById(id: Int): E?
    fun countAll(): Int

    fun update(entity: E): E?
    fun updateAll(entities: List<E>): List<E?>

    fun delete(entity: E): Boolean
    fun deleteAll()
}