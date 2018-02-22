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
        player.items = player.items.map { it ->
            var item = itemRepository.findById(it.id)
            if (item == null) {
                item = itemRepository.findByNameAndPlayer(it.name, player)
                when (item) {
                    null -> itemRepository.add(it, player)!!
                    else -> itemRepository.updateByPlayer(it, player)!!
                }
            } else {
                itemRepository.updateByPlayer(it, player)!!
            }
        }

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

    fun deletePlayer(player: Player) = deletePlayer(player.name)

    fun deleteAllItemsOfPlayer(player: Player) {
        itemRepository.deleteAllByPlayer(player)
        player.items = listOf()
    }

    fun deleteAll() {
        playerRepository.deleteAll()
        itemRepository.deleteAll()
    }
}