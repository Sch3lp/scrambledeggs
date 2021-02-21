package org.scrambled.adapter.rdbms.leaderboard.projection

import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.junit.jupiter.api.Test
import org.scrambled.adapter.rdbms.JdbiConfig
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.ProjectedPlayer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.test.context.ContextConfiguration


@JdbcTest
@ContextConfiguration(classes = [JdbiConfig::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MostChallengesDoneDaoTest {

    @Autowired
    private lateinit var jdbi: Jdbi

    @Autowired
    private lateinit var projection: MostChallengesDoneDao

    @Test
    fun `MostChallengesDoneDao can store players in the correct table`() {
        val lionO = ProjectedPlayer(nickname = "Lion-O", score = 0)

        projection.store(listOf(lionO))

        val players = "select * from MOST_CHALLENGES_DONE_LEADERBOARD".runQuery<ProjectedPlayer>()

        assertThat(players).contains(lionO)
    }

    private inline fun <reified T : Any> String.runQuery(): List<T> {
        val handle = jdbi.open()
        return handle.createQuery(this).mapTo<T>().list()
    }
}