package warhammer.database.daos

interface Dao<out E> {
    fun findById(id: Int): E?
}