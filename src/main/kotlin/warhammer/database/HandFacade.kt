package warhammer.database

import warhammer.database.entities.hand.Hand
import warhammer.database.repositories.hand.HandRepository

class HandFacade(databaseUrl: String = "jdbc:sqlite:file:warhammer", driver: String = "org.sqlite.JDBC") {
    private val handRepository = HandRepository(databaseUrl, driver)

    // region SAVE
    fun save(hand: Hand): Hand {
        val existingHand = handRepository.findById(hand.id)
        when (existingHand) {
            null -> handRepository.add(hand)
            else -> handRepository.update(hand)
        }

        return find(hand.name)!!
    }

    // endregion

    fun find(name: String): Hand? = handRepository.findByName(name)

    fun findAll(): List<Hand> = handRepository.findAll()

    fun deleteHand(name: String) {
        val hand = handRepository.findByName(name)
        if (hand != null) {
            handRepository.deleteByName(name)
        }
    }

    fun deleteHand(hand: Hand) = deleteHand(hand.name)

    fun deleteAll() {
        handRepository.deleteAll()
    }
}