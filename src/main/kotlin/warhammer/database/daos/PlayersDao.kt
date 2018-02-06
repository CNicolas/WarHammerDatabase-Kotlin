package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.Player
import warhammer.database.entities.player.Characteristic.*
import warhammer.database.entities.player.PlayerCharacteristicsMapper
import warhammer.database.tables.PlayerCharacteristicsTable
import warhammer.database.tables.PlayersTable
import java.lang.Exception

class PlayersDao : AbstractDao<Player>() {
    override val table = PlayersTable

    override fun add(entity: Player): Int {
        return try {
            val playerId = PlayersTable.insertAndGetId {
                mapFieldsOfEntityToTable(it, entity)
            }

            if (playerId != null) {
                PlayerCharacteristicsTable.insert {
                    it[PlayerCharacteristicsTable.playerId] = EntityID(playerId.value, PlayerCharacteristicsTable)

                    it[PlayerCharacteristicsTable.strength] = entity.characteristics[STRENGTH].value
                    it[PlayerCharacteristicsTable.toughness] = entity.characteristics[TOUGHNESS].value
                    it[PlayerCharacteristicsTable.agility] = entity.characteristics[AGILITY].value
                    it[PlayerCharacteristicsTable.intelligence] = entity.characteristics[INTELLIGENCE].value
                    it[PlayerCharacteristicsTable.willpower] = entity.characteristics[WILLPOWER].value
                    it[PlayerCharacteristicsTable.fellowship] = entity.characteristics[FELLOWSHIP].value

                    it[PlayerCharacteristicsTable.strengthFortune] = entity.characteristics[STRENGTH].fortuneValue
                    it[PlayerCharacteristicsTable.toughnessFortune] = entity.characteristics[TOUGHNESS].fortuneValue
                    it[PlayerCharacteristicsTable.agilityFortune] = entity.characteristics[AGILITY].fortuneValue
                    it[PlayerCharacteristicsTable.intelligenceFortune] = entity.characteristics[INTELLIGENCE].fortuneValue
                    it[PlayerCharacteristicsTable.willpowerFortune] = entity.characteristics[WILLPOWER].fortuneValue
                    it[PlayerCharacteristicsTable.fellowshipFortune] = entity.characteristics[FELLOWSHIP].fortuneValue
                }

                playerId.value
            } else {
                -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun findByName(name: String): Player? {
        val result = PlayersTable.select { PlayersTable.name eq name }
                .firstOrNull()

        return mapResultRowToEntity(result)
    }

    override fun update(entity: Player): Int {
        return try {
            PlayersTable.update({ (PlayersTable.id eq entity.id) or (PlayersTable.name eq entity.name) }) {
                mapEntityToTable(it, entity)
            }

            entity.id
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun delete(entity: Player): Int {
        return try {
            val playerToDelete = findByName(entity.name)
            if (playerToDelete != null) {
                PlayerCharacteristicsTable.deleteWhere { PlayerCharacteristicsTable.playerId eq playerToDelete.id }
                PlayersTable.deleteWhere { (PlayersTable.id eq playerToDelete.id) }
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun deleteAll() {
        PlayerCharacteristicsTable.deleteAll()
        PlayersTable.deleteAll()
    }

    override fun mapResultRowToEntity(result: ResultRow?): Player? = when (result) {
        null -> null
        else -> {
            val playerId = result[PlayersTable.id].value

            val characteristics =
                    PlayerCharacteristicsMapper.mapEntityToPlayerCharacteristics(
                            PlayerCharacteristicsMapper.mapResultRowToEntity(
                                    PlayerCharacteristicsTable
                                            .select { PlayerCharacteristicsTable.playerId eq playerId }
                                            .firstOrNull()
                            )
                    )

            Player(result[PlayersTable.name], playerId, characteristics)
        }
    }

    override fun mapEntityToTable(it: UpdateStatement, entity: Player) {
        it[PlayersTable.id] = EntityID(entity.id, PlayersTable)

        mapFieldsOfEntityToTable(it, entity)
    }

    override fun mapFieldsOfEntityToTable(it: UpdateBuilder<Int>, entity: Player) {
        it[PlayersTable.name] = entity.name
    }
}