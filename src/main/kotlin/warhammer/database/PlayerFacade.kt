package warhammer.database

import warhammer.database.entities.player.Player
import warhammer.database.repositories.player.PlayerRepository
import warhammer.database.repositories.player.item.ItemRepository

class PlayerFacade(databaseUrl: String = "jdbc:sqlite:file:warhammer", driver: String = "org.sqlite.JDBC") {
    private val playerRepository = PlayerRepository(databaseUrl, driver)
    private val itemRepository = ItemRepository(databaseUrl, driver)

    // region SAVE
    fun save(player: Player): Player {
        val existingPlayer = playerRepository.findById(player.id)
        return when (existingPlayer) {
            null -> addPlayer(player)
            else -> updatePlayer(player)
        }
    }

    private fun addPlayer(player: Player): Player {
        playerRepository.add(player)
        player.items.forEach {
            itemRepository.add(it, player)
        }

        return find(player.name)!!
    }

    private fun updatePlayer(player: Player): Player {
        playerRepository.update(player)
        val savedItems = itemRepository.findAllByPlayer(player).toMutableList()

        player.items.map { it ->
            val item = itemRepository.findById(it.id)
            savedItems.remove(item)

            if (item == null) {
                itemRepository.add(it, player)!!
            } else {
                itemRepository.updateByPlayer(it, player)!!
            }
        }

        savedItems.forEach { itemRepository.deleteByPlayer(it, player) }

        player.items = itemRepository.findAllByPlayer(player)

        return find(player.name)!!
    }
    // endregion

    fun find(name: String): Player? {
        val player = playerRepository.findByName(name)
        if (player != null) {
            player.items = itemRepository.findAllByPlayer(player)
        }

        return player
    }

    fun findAll(): List<Player> {
        val players = playerRepository.findAll()
        players.forEach {
            it.items = itemRepository.findAllByPlayer(it)
        }

        return players
    }

    fun deletePlayer(name: String) {
        val player = playerRepository.findByName(name)
        if (player != null) {
            itemRepository.deleteAllByPlayer(player)
            playerRepository.deleteByName(name)
        }
    }

    fun deletePlayer(player: Player) {
        val foundPlayer = playerRepository.findById(player.id)
        if (foundPlayer != null) {
            itemRepository.deleteAllByPlayer(foundPlayer)
            playerRepository.delete(foundPlayer)
        }
    }

    fun deleteAllItemsOfPlayer(player: Player): Int {
        player.items = listOf()
        return itemRepository.deleteAllByPlayer(player)
    }

    fun deleteAll() {
        playerRepository.deleteAll()
        itemRepository.deleteAll()
    }
}