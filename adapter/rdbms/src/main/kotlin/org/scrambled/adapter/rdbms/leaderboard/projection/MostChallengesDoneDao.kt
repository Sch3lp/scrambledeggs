package org.scrambled.adapter.rdbms.leaderboard.projection

import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardProjection
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.ProjectedPlayer
import org.springframework.transaction.annotation.Transactional


@RegisterKotlinMapper(value = ProjectedPlayer::class)
interface MostChallengesDoneDao: MostChallengesDoneLeaderboardProjection {
    @SqlQuery("SELECT rank, nickname, score FROM MOST_CHALLENGES_DONE_LEADERBOARD order by score desc")
    override fun getRanking(): List<ProjectedPlayer>

    @Transactional(transactionManager = "rdbms-tx-mgr")
    @SqlBatch("INSERT INTO MOST_CHALLENGES_DONE_LEADERBOARD(rank, nickname, score) values(:rank, :nickname, :score)")
    override fun store(@BindBean players: List<ProjectedPlayer>)

    @Transactional(transactionManager = "rdbms-tx-mgr")
    @SqlUpdate("DELETE FROM MOST_CHALLENGES_DONE_LEADERBOARD where 1=1")
    override fun wipe()
}
