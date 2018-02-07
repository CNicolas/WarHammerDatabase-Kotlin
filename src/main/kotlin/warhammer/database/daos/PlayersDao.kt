package warhammer.database.daos

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import warhammer.database.entities.Player
import warhammer.database.entities.player.PlayerCharacteristicsEntity
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

                    mapPlayerCharacteristicsEntityFieldsToTable(it,
                            PlayerCharacteristicsMapper.mapPlayerCharacteristicsToEntity(entity.characteristics, playerId.value))
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

            val playerCharacteristicsToUpdate = PlayerCharacteristicsMapper.mapResultRowToEntity(
                    PlayerCharacteristicsTable.select { PlayerCharacteristicsTable.playerId eq entity.id }.firstOrNull()
            )
            println("entity:" + entity.id + "  characId" + playerCharacteristicsToUpdate?.id)

            PlayerCharacteristicsTable.update({ PlayerCharacteristicsTable.playerId eq entity.id }) {
                val entityToUpdate = PlayerCharacteristicsMapper.mapPlayerCharacteristicsToEntityKnowingId(
                        entity.characteristics,
                        entity.id,
                        playerCharacteristicsToUpdate!!.id
                )
                mapPlayerCharacteristicsEntityToTable(it, entityToUpdate)
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
                PlayersTable.deleteWhere { (PlayersTable.id eq playerToDelete.id) or (PlayersTable.name eq entity.name) }
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

    private fun mapPlayerCharacteristicsEntityToTable(updating: UpdateStatement, entity: PlayerCharacteristicsEntity) {
        updating[PlayerCharacteristicsTable.id] = EntityID(entity.id, PlayerCharacteristicsTable)

        mapPlayerCharacteristicsEntityFieldsToTable(updating, entity)
    }

    private fun mapPlayerCharacteristicsEntityFieldsToTable(updating: UpdateBuilder<Int>, entity: PlayerCharacteristicsEntity) {
        updating[PlayerCharacteristicsTable.strength] = entity.strength
        updating[PlayerCharacteristicsTable.toughness] = entity.toughness
        updating[PlayerCharacteristicsTable.agility] = entity.agility
        updating[PlayerCharacteristicsTable.intelligence] = entity.intelligence
        updating[PlayerCharacteristicsTable.willpower] = entity.willpower
        updating[PlayerCharacteristicsTable.fellowship] = entity.fellowship

        updating[PlayerCharacteristicsTable.strengthFortune] = entity.strengthFortune
        updating[PlayerCharacteristicsTable.toughnessFortune] = entity.toughnessFortune
        updating[PlayerCharacteristicsTable.agilityFortune] = entity.agilityFortune
        updating[PlayerCharacteristicsTable.intelligenceFortune] = entity.intelligenceFortune
        updating[PlayerCharacteristicsTable.willpowerFortune] = entity.willpowerFortune
        updating[PlayerCharacteristicsTable.fellowshipFortune] = entity.fellowshipFortune
    }
}