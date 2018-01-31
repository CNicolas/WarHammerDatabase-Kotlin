package daos

import entities.Hand
import entities.tables.Hands
import org.jetbrains.exposed.sql.transactions.transaction

class HandsDao {
    fun findByNameInSeparateTransaction(name: String): Hand? {
        var hand: Hand? = null

        transaction {
            hand = Hand.find { Hands.name eq name }.singleOrNull()
        }

        return hand
    }

    fun findByName(name: String): Hand? = Hand.find { Hands.name eq name }.singleOrNull()
}