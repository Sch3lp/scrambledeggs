package org.scrambled.adapter.rdbms.core.challenges

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.domain.core.api.challenges.QueryableChallenge
import org.scrambled.domain.core.api.challenges.QueryableChallenges
import java.util.*

@RegisterKotlinMapper(value = QueryableChallenge::class)
interface ChallengesDao : QueryableChallenges {

    @SqlQuery("SELECT * FROM CHALLENGES where id = :id")
    override fun getById(@Bind("id") id: UUID): QueryableChallenge?

    @SqlQuery("SELECT * FROM CHALLENGES where challengeId = :challengeId")
    override fun getByChallengeId(@Bind("challengeId") challengeId: String): QueryableChallenge?

    @SqlQuery("""
        SELECT * FROM CHALLENGES 
        where challengerId = :challengerId and opponentId = :opponentId
        """)
    fun findChallenge(
        @Bind("challengerId") challengerId: PlayerId,
        @Bind("opponentId") opponentId: PlayerId
    ): List<QueryableChallenge>

    @SqlUpdate(
        """INSERT INTO CHALLENGES(
            id,
            challengeId,
            challengerId,
            opponentId,
            comment,
            appointmentsuggestion,
            gamemode,
            isAccepted
            ) values (
            :id,
            :challengeId,
            :challengerId,
            :opponentId,
            :comment,
            :appointmentSuggestion,
            :gameMode,
            :isAccepted
            )
        """
    )
    override fun store(@BindKotlin queryableChallenge: QueryableChallenge)

}