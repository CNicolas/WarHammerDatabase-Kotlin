package warhammer.database.daos

import warhammer.database.entities.WarHammerEntity

interface Dao<E : WarHammerEntity> {
    fun add(entity: E): Int
    fun addAll(entities: List<E>): List<Int>

    fun findById(id: Int): E?
    fun findAll(): List<E>

    fun update(entity: E): Int
    fun updateAll(entities: List<E>): List<Int>

    fun delete(entity: E): Int
    fun deleteAll()
}