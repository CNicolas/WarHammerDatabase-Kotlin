package warhammer.database.services

import warhammer.database.entities.NamedEntity

interface DatabaseNamedService<E : NamedEntity>: DatabaseService<E> {
    fun findByName(name: String): E?
}