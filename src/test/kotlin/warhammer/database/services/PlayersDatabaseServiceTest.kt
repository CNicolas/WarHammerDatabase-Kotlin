package warhammer.database.services

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.PlayerInventory
import warhammer.database.entities.player.PlayerState
import warhammer.database.entities.player.characteristics.CharacteristicValue
import warhammer.database.entities.player.characteristics.PlayerCharacteristics
import warhammer.database.entities.player.inventory.Money
import warhammer.database.entities.player.inventory.item.Armor
import warhammer.database.entities.player.inventory.item.Expandable
import warhammer.database.entities.player.inventory.item.GenericItem
import warhammer.database.entities.player.inventory.item.Weapon
import warhammer.database.entities.player.inventory.item.enums.Quality.*
import warhammer.database.entities.player.inventory.item.enums.Range
import warhammer.database.entities.player.other.Race.*
import warhammer.database.entities.player.state.Career
import warhammer.database.entities.player.state.Stance
import warhammer.database.tables.PlayersTable
import warhammer.database.tables.player.PlayerCharacteristicsTable
import warhammer.database.tables.player.PlayerInventoryTable
import warhammer.database.tables.player.PlayerStateTable

class PlayersDatabaseServiceTest {
    private val playersService = PlayersDatabaseService(
            databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared",
            driver = "org.sqlite.JDBC"
    )
    private val playerName = "SampleName"
    private val samplePlayer
        get() = {
            val characteristics = PlayerCharacteristics(
                    strength = CharacteristicValue(3),
                    toughness = CharacteristicValue(2, 1)
            )
            val state = PlayerState(career = Career(name = "Librelame"), maxWounds = 10)
            val inventory = PlayerInventory(
                    maxEncumbrance = 15,
                    encumbrance = 14,
                    money = Money(brass = 2, silver = 3, gold = 4),
                    items = mutableListOf(
                            GenericItem(name = "Corde 1m", quality = LOW),
                            Expandable(name = "Potion", uses = 1, quantity = 2),
                            Weapon(name = "Lamedor", quality = SUPERIOR, damage = 5, criticalLevel = 2, encumbrance = 3),
                            Armor(name = "Heaume de la foi", quality = MAGIC, soak = 3, defense = 1, encumbrance = 4),
                            Armor(name = "Armure de la loi", quality = MAGIC, defense = 2, soak = 5, encumbrance = 7)
                    )
            )

            Player(
                    name = playerName,
                    race = DWARF,
                    age = 110,
                    size = 89,
                    characteristics = characteristics,
                    state = state,
                    inventory = inventory
            )
        }

    @BeforeMethod
    fun clearDatabase() {
        playersService.deleteAll()
    }

    @Test
    fun should_be_initialized() {
        transaction {
            assertThat(PlayersTable.exists()).isTrue()
            assertThat(PlayerCharacteristicsTable.exists()).isTrue()
            assertThat(PlayerStateTable.exists()).isTrue()
            assertThat(PlayerInventoryTable.exists()).isTrue()
        }
    }

    // region CREATE
    @Test
    fun should_add_a_player() {
        val addedPlayer = playersService.add(samplePlayer())

        assertThat(playersService.countAll()).isEqualTo(1)
        assertSamplePlayer(addedPlayer)
    }

    @Test
    fun should_add_all_players() {
        val playersToAdd = listOf(
                Player(name = "Player1",
                        characteristics = PlayerCharacteristics(strength = CharacteristicValue(1)),
                        state = PlayerState(exhaustion = 1, wounds = 1, maxCorruption = 3, stance = Stance(reckless = 1, maxConservative = 3)),
                        inventory = PlayerInventory(maxEncumbrance = 20)
                ),
                Player(name = "Player2",
                        characteristics = PlayerCharacteristics(intelligence = CharacteristicValue(2)),
                        state = PlayerState(corruption = 1, stress = 1, stance = Stance(conservative = 1, maxReckless = 2)),
                        inventory = PlayerInventory(money = Money(silver = 2))
                ),
                Player(name = "Player3",
                        characteristics = PlayerCharacteristics(fellowship = CharacteristicValue(3)),
                        state = PlayerState(maxStress = 3, maxExhaustion = 4, career = Career(rank = 1)),
                        inventory = PlayerInventory(items = mutableListOf(
                                GenericItem(name = "Cordage", description = "48 mètres de corde, c'est bien !")
                        ))
                )
        )

        val addAllResult = playersService.addAll(playersToAdd)
        assertThat(addAllResult.size).isEqualTo(playersToAdd.size)
        assertThat(playersService.countAll()).isEqualTo(playersToAdd.size)

        assertThat(addAllResult.map { it?.name }).containsExactly("Player1", "Player2", "Player3")
        assertThat(addAllResult[0]).isNotNull()
        assertThat(addAllResult[0]!!.strength.value).isEqualTo(1)
        assertThat(addAllResult[0]!!.maxCorruption).isEqualTo(3)
        assertThat(addAllResult[0]!!.exhaustion).isEqualTo(1)
        assertThat(addAllResult[0]!!.wounds).isEqualTo(1)
        assertThat(addAllResult[0]!!.reckless).isEqualTo(1)
        assertThat(addAllResult[0]!!.maxConservative).isEqualTo(3)
        assertThat(addAllResult[0]!!.maxEncumbrance).isEqualTo(20)

        assertThat(addAllResult[1]).isNotNull()
        assertThat(addAllResult[1]!!.intelligence.value).isEqualTo(2)
        assertThat(addAllResult[1]!!.corruption).isEqualTo(1)
        assertThat(addAllResult[1]!!.stress).isEqualTo(1)
        assertThat(addAllResult[1]!!.conservative).isEqualTo(1)
        assertThat(addAllResult[1]!!.stance.conservative).isEqualTo(1)
        assertThat(addAllResult[1]!!.maxReckless).isEqualTo(2)
        assertThat(addAllResult[1]!!.money.brass).isEqualTo(0)
        assertThat(addAllResult[1]!!.money.silver).isEqualTo(2)
        assertThat(addAllResult[1]!!.money.gold).isEqualTo(0)

        assertThat(addAllResult[2]).isNotNull()
        assertThat(addAllResult[2]!!.fellowship.value).isEqualTo(3)
        assertThat(addAllResult[2]!!.maxStress).isEqualTo(3)
        assertThat(addAllResult[2]!!.maxExhaustion).isEqualTo(4)
        assertThat(addAllResult[2]!!.rank).isEqualTo(1)
        assertThat(addAllResult[2]!!.items.size).isEqualTo(1)
        assertThat(addAllResult[2]!!.items[0] is GenericItem).isTrue()
        assertThat(addAllResult[2]!!.items[0].name).isEqualTo("Cordage")
        assertThat(addAllResult[2]!!.items[0].description).isEqualTo("48 mètres de corde, c'est bien !")

    }

    @Test
    fun should_add_a_player_then_fail_to_add_it_again() {
        val addedPlayer1 = playersService.add(Player(name = playerName))
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(addedPlayer1?.name).isEqualTo(playerName)

        val addedPlayer2 = playersService.add(Player(name = playerName))
        assertThat(addedPlayer2).isNull()
        assertThat(playersService.countAll()).isEqualTo(1)
    }
    // endregion

    // region READ
    @Test
    fun should_read_a_player() {
        playersService.add(samplePlayer())
        assertThat(playersService.countAll()).isEqualTo(1)

        assertSamplePlayer(playersService.findByName(playerName))
    }

    @Test
    fun should_find_a_player_by_id() {
        playersService.add(samplePlayer())
        assertThat(playersService.countAll()).isEqualTo(1)

        val player = playersService.findById(1)
        assertSamplePlayer(player)
    }

    @Test
    fun should_read_all_players() {
        val playersToAdd = listOf(
                Player(name = "Player1"),
                Player(name = "Player2"),
                Player(name = "Player3"))

        playersService.addAll(playersToAdd)

        val allInsertedPlayers = playersService.findAll()
        assertThat(allInsertedPlayers.size).isEqualTo(3)
        assertThat(allInsertedPlayers.map { it.name }).containsExactly("Player1", "Player2", "Player3")
    }
    // endregion

    // region UPDATE
    @Test
    fun should_update_a_player() {
        val newPlayerName = "MyPlayerIsNew"

        // ADD
        val player = playersService.add(samplePlayer())
        assertThat(playersService.countAll()).isEqualTo(1)

        // FIND
        assertSamplePlayer(player)

        // UPDATE
        player!!.name = newPlayerName
        player.race = WOOD_ELF
        player.agility = CharacteristicValue(5, 2)
        player.availableExperience = 1
        player.careerName = "Retired"
        player.money.brass = 52
        val newPlayer = playersService.update(player)

        // VERIFY
        assertThat(playersService.countAll()).isEqualTo(1)
        assertThat(newPlayer).isNotNull()
        assertThat(newPlayer!!.name).isEqualTo(newPlayerName)
        assertThat(newPlayer.race).isEqualTo(WOOD_ELF)
        assertThat(newPlayer.strength.value).isEqualTo(3)
        assertThat(newPlayer.agility.value).isEqualTo(5)
        assertThat(newPlayer.agility.fortuneValue).isEqualTo(2)
        assertThat(newPlayer.availableExperience).isEqualTo(1)
        assertThat(newPlayer.money.brass).isEqualTo(52)
        assertThat(newPlayer.careerName).isEqualTo("Retired")
    }

    @Test
    fun should_update_by_id() {
        val player = Player(name = "PlayerName")
        val addedPlayer = playersService.add(player)
        assertThat(addedPlayer).isNotNull()
        assertThat(addedPlayer!!.id).isEqualTo(1)
        assertThat(addedPlayer.name).isEqualTo("PlayerName")
        assertThat(addedPlayer.willpower.value).isEqualTo(0)
        assertThat(addedPlayer.willpower.fortuneValue).isEqualTo(0)

        val updatedPlayer = playersService.update(Player(id = 1, name = "John"))
        assertThat(updatedPlayer).isNotNull()
        assertThat(updatedPlayer!!.id).isEqualTo(1)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.willpower.value).isEqualTo(0)
        assertThat(updatedPlayer.willpower.fortuneValue).isEqualTo(0)

        val updatedPlayerWithCharacteristics = playersService.update(Player(
                id = 1,
                name = "Bob",
                characteristics = PlayerCharacteristics(willpower = CharacteristicValue(4, 1))
        ))
        assertThat(updatedPlayerWithCharacteristics).isNotNull()
        assertThat(updatedPlayerWithCharacteristics!!.id).isEqualTo(1)
        assertThat(updatedPlayerWithCharacteristics.name).isEqualTo("Bob")
        assertThat(updatedPlayerWithCharacteristics.willpower.value).isEqualTo(4)
        assertThat(updatedPlayerWithCharacteristics.willpower.fortuneValue).isEqualTo(1)

        val updatedPlayerWithOtherCharacteristics = playersService.update(Player(
                id = 1,
                name = "Dave",
                characteristics = PlayerCharacteristics(willpower = CharacteristicValue(1, 0))
        ))
        assertThat(updatedPlayerWithOtherCharacteristics).isNotNull()
        assertThat(updatedPlayerWithOtherCharacteristics!!.id).isEqualTo(1)
        assertThat(updatedPlayerWithOtherCharacteristics.name).isEqualTo("Dave")
        assertThat(updatedPlayerWithOtherCharacteristics.willpower.value).isEqualTo(1)
        assertThat(updatedPlayerWithOtherCharacteristics.willpower.fortuneValue).isEqualTo(0)
    }

    @Test
    fun should_update_all_players() {
        // ADD
        val player1 = playersService.add(Player(name = "Player1", race = HIGH_ELF))
        val player2 = playersService.add(Player(name = "Player2", race = WOOD_ELF))
        assertThat(playersService.countAll()).isEqualTo(2)

        // UPDATE
        val updatedPlayers = playersService.updateAll(
                listOf(
                        player1!!.copy(name = "Player11", race = DWARF),
                        player2!!.copy(name = "Player22", race = DWARF)
                )
        )

        // VERIFY
        assertThat(updatedPlayers.size).isEqualTo(2)
        assertThat(updatedPlayers.map { it?.name }).containsExactly("Player11", "Player22")
        assertThat(updatedPlayers.map { it?.id }).containsExactly(player1.id, player2.id)
        assertThat(updatedPlayers.map { it?.race }).containsExactly(DWARF, DWARF)
    }

    @Test
    fun should_return_false_when_update_a_non_existent_player() {
        assertThat(playersService.countAll()).isEqualTo(0)

        val updatedPlayer = playersService.update(Player(name = "Unknown"))
        assertThat(updatedPlayer).isNull()
        assertThat(playersService.findAll()).isEmpty()
    }
    // endregion

    // region DELETE
    @Test
    fun should_delete_a_player() {
        val player1 = Player(name = "Player1", characteristics = PlayerCharacteristics(strength = CharacteristicValue(1)))
        val player2 = Player(name = "Player2",
                characteristics = PlayerCharacteristics(strength = CharacteristicValue(2)),
                state = PlayerState(stance = Stance(maxReckless = 2)),
                inventory = PlayerInventory(money = Money(0, 0, 2))
        )
        val player3 = Player(name = "Player3",
                characteristics = PlayerCharacteristics(strength = CharacteristicValue(3)),
                state = PlayerState(career = Career(totalExperience = 3)),
                inventory = PlayerInventory(maxEncumbrance = 50)
        )

        val addAllResult = playersService.addAll(listOf(player1, player2, player3))
        assertThat(addAllResult.size).isEqualTo(3)
        assertThat(addAllResult.map { it?.name }).containsExactly("Player1", "Player2", "Player3")

        val isDeleted = playersService.delete(addAllResult[1]!!)

        assertThat(isDeleted).isTrue()
        assertThat(playersService.countAll()).isEqualTo(2)
        val foundPlayer1 = playersService.findByName("Player1")
        assertThat(foundPlayer1).isNotNull()
        assertThat(playersService.findById(1)).isNotNull()
        assertThat(foundPlayer1!!.strength.value).isEqualTo(1)
        assertThat(foundPlayer1.state.stance.reckless).isEqualTo(0)

        assertThat(playersService.findByName("Player2")).isNull()
        assertThat(playersService.findById(2)).isNull()

        val foundPlayer3 = playersService.findByName("Player3")
        assertThat(foundPlayer3).isNotNull()
        assertThat(playersService.findById(3)).isNotNull()
        assertThat(foundPlayer3!!.strength.value).isEqualTo(3)
        assertThat(foundPlayer3.totalExperience).isEqualTo(3)
        assertThat(foundPlayer3.maxEncumbrance).isEqualTo(50)

        transaction {
            assertThat(PlayerCharacteristicsTable.selectAll().count()).isEqualTo(2)
            assertThat(PlayerStateTable.selectAll().count()).isEqualTo(2)
        }
    }

    @Test
    fun should_delete_all_players() {
        val player1 = Player(name = "Player1")
        val player2 = Player(name = "Player2",
                characteristics = PlayerCharacteristics(toughness = CharacteristicValue(2)),
                state = PlayerState(wounds = 2),
                inventory = PlayerInventory(encumbrance = 2)
        )

        playersService.add(player1)
        playersService.add(player2)
        assertThat(playersService.countAll()).isEqualTo(2)

        playersService.deleteAll()
        assertThat(playersService.findAll()).isEmpty()

        transaction {
            assertThat(PlayerCharacteristicsTable.selectAll().count()).isEqualTo(0)
            assertThat(PlayerStateTable.selectAll().count()).isEqualTo(0)
        }
    }

    @Test
    fun should_return_false_when_delete_a_non_existent_player() {
        assertThat(playersService.countAll()).isEqualTo(0)

        val res = playersService.delete(Player(name = "Unknown"))
        assertThat(res).isFalse()
        assertThat(playersService.findAll()).isEmpty()
    }
    // endregion

    private fun assertSamplePlayer(player: Player?) {
        assertThat(player).isNotNull()
        assertThat(player!!.name).isEqualTo(playerName)
        assertThat(player.race).isEqualTo(DWARF)
        assertThat(player.age).isEqualTo(110)
        assertThat(player.size).isEqualTo(89)

        assertThat(player.characteristics).isNotNull()
        assertThat(player.strength.value).isEqualTo(3)
        assertThat(player.toughness.value).isEqualTo(2)
        assertThat(player.toughness.fortuneValue).isEqualTo(1)

        assertThat(player.careerName).isEqualTo("Librelame")
        assertThat(player.maxWounds).isEqualTo(10)

        assertThat(player.encumbrance).isEqualTo(14)
        assertThat(player.maxEncumbrance).isEqualTo(15)
        assertThat(player.money.brass).isEqualTo(2)
        assertThat(player.money.silver).isEqualTo(3)
        assertThat(player.money.gold).isEqualTo(4)

        assertThat(player.items).isNotEmpty
        assertThat(player.items.size).isEqualTo(5)

        assertThat(player.items[0] is GenericItem).isTrue()
        assertThat(player.items[0].quality).isEqualTo(LOW)
        assertThat(player.items[0].damage).isNull()

        assertThat(player.items[1] is Expandable).isTrue()
        assertThat(player.items[1].uses).isEqualTo(1)
        assertThat(player.items[1].quantity).isEqualTo(2)
        assertThat(player.items[1].soak).isNull()

        assertThat(player.items[2] is Weapon).isTrue()
        assertThat(player.items[2].encumbrance).isEqualTo(3)
        assertThat(player.items[2].quantity).isEqualTo(1)
        assertThat(player.items[2].quality).isEqualTo(SUPERIOR)
        assertThat(player.items[2].range).isEqualTo(Range.ENGAGED)
        assertThat(player.items[2].damage).isEqualTo(5)
        assertThat(player.items[2].criticalLevel).isEqualTo(2)
        assertThat(player.items[2].uses).isNull()

        assertThat(player.items[3] is Armor).isTrue()
        assertThat(player.items[3].name).isEqualTo("Heaume de la foi")
        assertThat(player.items[3].encumbrance).isEqualTo(4)
        assertThat(player.items[3].quantity).isEqualTo(1)
        assertThat(player.items[3].quality).isEqualTo(MAGIC)
        assertThat(player.items[3].defense).isEqualTo(1)
        assertThat(player.items[3].soak).isEqualTo(3)
        assertThat(player.items[3].range).isNull()

        assertThat(player.items[4] is Armor).isTrue()
        assertThat(player.items[4].name).isEqualTo("Armure de la loi")
        assertThat(player.items[4].encumbrance).isEqualTo(7)
        assertThat(player.items[4].quantity).isEqualTo(1)
        assertThat(player.items[4].quality).isEqualTo(MAGIC)
        assertThat(player.items[4].defense).isEqualTo(2)
        assertThat(player.items[4].soak).isEqualTo(5)
        assertThat(player.items[4].uses).isNull()
    }
}