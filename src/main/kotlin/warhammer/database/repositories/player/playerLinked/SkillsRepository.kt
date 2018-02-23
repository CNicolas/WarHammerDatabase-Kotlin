package warhammer.database.repositories.player.playerLinked

import com.beust.klaxon.Klaxon
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import warhammer.database.daos.player.playerLinked.SkillsDao
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.playerLinked.skill.Skill
import warhammer.database.entities.player.playerLinked.skill.SkillType.ADVANCED
import warhammer.database.entities.player.playerLinked.skill.SkillType.BASIC
import warhammer.database.repositories.player.AbstractPlayerLinkedRepository
import warhammer.database.tables.SkillsTable

class SkillsRepository(databaseUrl: String, driver: String) : AbstractPlayerLinkedRepository<Skill>(databaseUrl, driver) {
    override val dao = SkillsDao()

    init {
        connectToDatabase()

        transaction {
            logger.addLogger(StdOutSqlLogger)
            SchemaUtils.create(SkillsTable)
        }
    }

    fun crateSkillsForPlayer(player: Player) {
        loadSkills()?.filter { it.type == BASIC }!!.forEach { add(it, player) }
    }

    fun getAdvancedSkills(): List<Skill> =
            loadSkills()?.filter { it.type == ADVANCED } ?: listOf()

    private fun loadSkills(): List<Skill>? {
        return Klaxon().parseArray(this.javaClass.getResource("/skills.json").readText())
    }
}