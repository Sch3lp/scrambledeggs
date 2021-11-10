package org.scrambled.leaderboards.adapters.out.rdbms.projection

import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.leaderboards.domain.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardProjection
import org.scrambled.leaderboards.domain.api.mostchallengesdone.projections.ProjectedPlayer
import org.springframework.transaction.annotation.Transactional


@RegisterKotlinMapper(value = ProjectedPlayer::class)
interface MostChallengesDoneDao: MostChallengesDoneLeaderboardProjection {
    @SqlQuery("SELECT rank, nickname, score, playerId FROM MOST_CHALLENGES_DONE_LEADERBOARD order by rank asc")
    override fun getRanking(): List<ProjectedPlayer>

    @Transactional(transactionManager = "rdbms-tx-mgr")
    @SqlBatch("INSERT INTO MOST_CHALLENGES_DONE_LEADERBOARD(rank, nickname, score, playerid) values(:rank, :nickname, :score, :playerId)")
    override fun store(@BindBean players: List<ProjectedPlayer>)

    @Transactional(transactionManager = "rdbms-tx-mgr")
    @SqlUpdate("DELETE FROM MOST_CHALLENGES_DONE_LEADERBOARD where 1=1")
    override fun wipe()
}
