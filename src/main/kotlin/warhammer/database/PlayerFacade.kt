package warhammer.database

import warhammer.database.entities.player.Player
import warhammer.database.repositories.player.PlayerRepository
import warhammer.database.repositories.player.playerLinked.ItemsRepository
import warhammer.database.repositories.player.playerLinked.SkillsRepository

class PlayerFacade(databaseUrl: String = "jdbc:sqlite:file:warhammer", driver: String = "org.sqlite.JDBC") {
    private val playerRepository = PlayerRepository(databaseUrl, driver)
    private val itemsRepository = ItemsRepository(databaseUrl, driver)
    private val skillsRepository = SkillsRepository(databaseUrl, driver)

    // region SAVE
    fun save(player: Player): Player {
        val existingPlayer = playerRepository.findById(player.id)
        return when (existingPlayer) {
            null -> addPlayer(player)
            else -> updatePlayer(player)
        }
    }

    private fun addPlayer(player: Player): Player {
        val savedPlayer = playerRepository.add(player)
        savedPlayer!!.items = player.items

        updateItems(savedPlayer)
        skillsRepository.crateSkillsForPlayer(savedPlayer)

        return find(player.name)!!
    }

    private fun updatePlayer(player: Player): Player {
        playerRepository.update(player)
        updateItems(player)

        updateSkills(player)
//        skillsRepository.deleteAllByPlayer(player)
//        player.skills.forEach { skillsRepository.add(it, player) }

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
            skillsRepository.deleteAllByPlayer(player)
            playerRepository.deleteByName(name)
        }
    }

    fun deletePlayer(player: Player) {
        val foundPlayer = playerRepository.findById(player.id)
        if (foundPlayer != null) {
            itemsRepository.deleteAllByPlayer(foundPlayer)
            skillsRepository.deleteAllByPlayer(foundPlayer)
            playerRepository.delete(foundPlayer)
        }
    }

    fun deleteAllItemsOfPlayer(player: Player): Int {
        player.items = listOf()
        return itemsRepository.deleteAllByPlayer(player)
    }

    fun deleteAll() {
        playerRepository.deleteAll()
        itemsRepository.deleteAll()
        skillsRepository.deleteAll()
    }
    // endregion

    fun getAdvancedSkills() = skillsRepository.getAdvancedSkills()

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

    private fun updateSkills(player: Player) {
        val savedSkills = findAllSkillsByPlayer(player).toMutableList()

        player.skills.forEach { it ->
            val skill = skillsRepository.findById(it.id)
            savedSkills.remove(skill)

            if (skill == null) {
                skillsRepository.add(it, player)!!
            } else {
                skillsRepository.updateByPlayer(it, player)!!
            }
        }

        savedSkills.forEach { skillsRepository.deleteByPlayer(it, player) }
    }

    private fun findAllItemsByPlayer(player: Player) = itemsRepository.findAllByPlayer(player)
    private fun findAllSkillsByPlayer(player: Player) = skillsRepository.findAllByPlayer(player)

    private fun setPlayersLists(player: Player) {
        player.items = findAllItemsByPlayer(player)
        player.skills = findAllSkillsByPlayer(player)
    }
}