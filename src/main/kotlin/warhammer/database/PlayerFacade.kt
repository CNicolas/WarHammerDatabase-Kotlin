package warhammer.database

import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.skill.SkillType
import warhammer.database.repositories.player.PlayerRepository
import warhammer.database.repositories.player.playerLinked.ItemsRepository
import warhammer.database.staticData.getAllSkills

class PlayerFacade(databaseUrl: String = "jdbc:sqlite:file:warhammer", driver: String = "org.sqlite.JDBC") {
    private val playerRepository = PlayerRepository(databaseUrl, driver)
    private val itemsRepository = ItemsRepository(databaseUrl, driver)

    // region SAVE
    fun save(player: Player): Player {
        val existingPlayer = playerRepository.findById(player.id)
        return when (existingPlayer) {
            null -> add(player)
            else -> update(player)
        }
    }

    fun add(player: Player): Player {
        createSkillsForPlayer(player)
        val savedPlayer = playerRepository.add(player)

        savedPlayer!!.items = player.items
        updateItems(savedPlayer)

        return find(player.name)!!
    }

    fun update(player: Player): Player {
        playerRepository.update(player)
        updateItems(player)

        setPlayersLists(player)

        return find(player.name)!!
    }
    // endregion

    fun find(name: String): Player? {
        val player = playerRepository.findByName(name)
        if (player != null) {
            setPlayersLists(player)
        }

        return player
    }

    fun findAll(): List<Player> {
        val players = playerRepository.findAll()
        players.forEach {
            setPlayersLists(it)
        }

        return players
    }

    // region DELETE
    fun deletePlayer(name: String) {
        val player = playerRepository.findByName(name)
        if (player != null) {
            itemsRepository.deleteAllByPlayer(player)
            playerRepository.deleteByName(name)
        }
    }

    fun deletePlayer(player: Player) {
        val foundPlayer = playerRepository.findById(player.id)
        if (foundPlayer != null) {
            itemsRepository.deleteAllByPlayer(foundPlayer)
            playerRepository.delete(foundPlayer)
        }
    }

    fun deleteAll() {
        playerRepository.deleteAll()
        itemsRepository.deleteAll()
    }
    // endregion

    private fun updateItems(player: Player) {
        val savedItems = findAllItemsByPlayer(player).toMutableList()

        player.items.forEach { it ->
            val item = itemsRepository.findById(it.id)
            savedItems.remove(item)

            if (item == null) {
                itemsRepository.add(it, player)!!
            } else {
                itemsRepository.updateByPlayer(it, player)!!
            }
        }

        savedItems.forEach { itemsRepository.deleteByPlayer(it, player) }
    }

    private fun findAllItemsByPlayer(player: Player) = itemsRepository.findAllByPlayer(player)

    private fun setPlayersLists(player: Player) {
        player.items = findAllItemsByPlayer(player)
    }

    private fun createSkillsForPlayer(player: Player) {
        getAllSkills()
                .filter { it.type == SkillType.BASIC }
                .forEach {
                    val mutableSkills = player.skills.toMutableList()
                    mutableSkills.add(it)
                    player.skills = mutableSkills
                }
    }
}