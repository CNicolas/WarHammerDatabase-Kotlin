package warhammer.database.daos

import warhammer.database.entities.NamedEntity

interface NamedDao<E : NamedEntity> : Dao<E> {
    fun findByName(name: String): E?
}