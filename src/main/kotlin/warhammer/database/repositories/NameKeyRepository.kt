package warhammer.database.repositories

import warhammer.database.daos.NameKeyDao
import warhammer.database.entities.NamedEntity

interface NameKeyRepository<E : NamedEntity> {
    val dao: NameKeyDao<E>

    fun add(entity: E): E?

    fun findAll(): List<E>
    fun findByName(name: String): E?

    fun update(entity: E): E?

    fun delete(entity: E): Boolean
    fun deleteByName(name: String): Int
    fun deleteAll()
}