package org.scrambled.adapter.rdbms.core.challenges

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.domain.core.api.challenges.QueryablePendingChallenge
import org.scrambled.domain.core.api.challenges.QueryablePendingChallenges

@RegisterKotlinMapper(value = QueryablePendingChallenge::class)
interface PendingChallengesDao : QueryablePendingChallenges {

    @SqlQuery(
        """
        SELECT c.id as challengeId,
        c.gameMode as gameMode,
        p.nickname as opponentName,
        c.appointmentsuggestion as appointment
        FROM CHALLENGES c
        inner join registered_players p on p.id = c.challengerid
        where c.opponentId = :challengedPlayerId"""
    )
    override fun findPendingFor(@Bind("challengedPlayerId") challengedPlayerId: PlayerId): List<QueryablePendingChallenge>
}