package warhammer.database.mongo

import com.mongodb.ConnectionString
import org.assertj.core.api.Assertions.assertThat
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.testng.annotations.Test

class KMongoTest {
    val client = KMongo.createClient(
            ConnectionString("mongodb+srv://testingUser:testingUser@theonlycluster-5tfzf.mongodb.net/test")
    )
    val database = client.getDatabase("test")
    val collection = database.getCollection<Player>()

    @Test
    fun should_crud_player() {
        collection.insertOne(Player("Jon", rank = 3))

        val player = collection.findOne("Jon")
        assertThat(player).isNotNull()
        assertThat(player!!.name).isEqualTo("Jon")
        assertThat(player.rank).isEqualTo(3)
    }
}