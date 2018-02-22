package warhammer.database.repositories

import warhammer.database.daos.Dao

interface Repository<out E> {
    val dao: Dao<E>

    fun findById(id: Int): E?
}