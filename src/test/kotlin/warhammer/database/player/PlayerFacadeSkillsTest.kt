package warhammer.database.player

import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import warhammer.database.PlayerFacade
import warhammer.database.entities.player.Player
import warhammer.database.entities.player.enums.Characteristic.*
import warhammer.database.entities.player.extensions.*
import warhammer.database.entities.player.playerLinked.skill.SkillType

class PlayerFacadeSkillsTest {
    private val playerFacade = PlayerFacade(
            databaseUrl = "jdbc:sqlite:testSqlite:?mode=memory&cache=shared",
            driver = "org.sqlite.JDBC"
    )

    @BeforeMethod
    fun clearBeforeTest() {
        playerFacade.deleteAll()
    }

    @Test
    fun should_add_advanced_skill_to_player() {
        val player = playerFacade.save(Player("John"))
        assertThat(player.name).isEqualTo("John")
        assertThat(player.skills.size).isEqualTo(18)

        val advancedSkills = playerFacade.getAdvancedSkills()
        println(advancedSkills)
        assertThat(advancedSkills.map { it.type }.distinct()).isEqualTo(listOf(SkillType.ADVANCED))
        player.addSkill(advancedSkills[0])

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.skills.size).isEqualTo(19)
    }

    @Test
    fun should_update_level_of_skill_of_a_player() {
        val player = playerFacade.save(Player("John"))
        assertThat(player.name).isEqualTo("John")
        assertThat(player.skills.size).isEqualTo(18)

        val fight = player.getSkillByName("capacité de combat")
        assertThat(fight).isNotNull()
        assertThat(fight!!.level).isEqualTo(0)
        assertThat(fight.getSpecializationByName("Armes d’Hast")?.isSpecialized).isFalse()

        fight.level = 2
        fight.getSpecializationByName("Armes d’Hast")?.isSpecialized = true

        val updatedPlayer = playerFacade.save(player)
        assertThat(updatedPlayer.name).isEqualTo("John")
        assertThat(updatedPlayer.skills.size).isEqualTo(18)

        val newSkill = updatedPlayer.getSkillByName("capacité de combat")
        assertThat(newSkill).isNotNull()
        assertThat(newSkill!!.type).isEqualTo(SkillType.BASIC)
        assertThat(newSkill.characteristic).isEqualTo(STRENGTH)
        assertThat(newSkill.level).isEqualTo(2)
        assertThat(newSkill.getSpecializationByName("Armes d’Hast")?.isSpecialized).isTrue()
        assertThat(updatedPlayer.getSpecializations()).isNotEmpty()
        assertThat(updatedPlayer.getSpecializationByName("Armes d’Hast")?.isSpecialized).isTrue()
    }

    @Test
    fun should_get_skills_by_characteristic() {
        val player = playerFacade.save(Player("John"))
        val strengthSkills = player.getSkillsByCharacteristic(STRENGTH)
        val toughnessSkills = player.getSkillsByCharacteristic(TOUGHNESS)
        val agilitySkills = player.getSkillsByCharacteristic(AGILITY)
        val intelligenceSkills = player.getSkillsByCharacteristic(INTELLIGENCE)
        val willpowerSkills = player.getSkillsByCharacteristic(WILLPOWER)
        val fellowshipSkills = player.getSkillsByCharacteristic(FELLOWSHIP)

        assertThat(strengthSkills).isNotEmpty()
        assertThat(strengthSkills.size).isEqualTo(3)

        assertThat(toughnessSkills).isNotEmpty()
        assertThat(toughnessSkills.size).isEqualTo(1)

        assertThat(agilitySkills).isNotEmpty()
        assertThat(agilitySkills.size).isEqualTo(5)

        assertThat(intelligenceSkills).isNotEmpty()
        assertThat(intelligenceSkills.size).isEqualTo(4)

        assertThat(willpowerSkills).isNotEmpty()
        assertThat(willpowerSkills.size).isEqualTo(1)

        assertThat(fellowshipSkills).isNotEmpty()
        assertThat(fellowshipSkills.size).isEqualTo(4)
    }
}