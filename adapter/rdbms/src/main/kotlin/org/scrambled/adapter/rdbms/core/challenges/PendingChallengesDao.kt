package org.scrambled.adapter.rdbms.core.challenges

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.scrambled.domain.core.api.challenges.*

/**
 * Exists to join challenges with player info to immediately get the opponent name
 */
@RegisterKotlinMapper(value = QueryablePendingChallenge::class)
interface PendingChallengesDao : QueryablePendingChallenges {

    @SqlQuery(
        """
        SELECT c.id as id,
        c.challengeId as challengeId,
        c.gameMode as gameMode,
        p.nickname as opponentName,
        c.appointmentsuggestion as appointment,
        c.comment as comment
        FROM CHALLENGES c
        inner join registered_players p on p.id = c.challengerid
        where c.challengeId = :challengeId
        and c.isaccepted = false
    """
    )
    override fun getByChallengeId(@Bind("challengeId") challengeId: ChallengeId): QueryablePendingChallenge?

    @SqlQuery(
        """
        SELECT c.id as id,
        c.challengeId as challengeId,
        c.gameMode as gameMode,
        p.nickname as opponentName,
        c.appointmentsuggestion as appointment,
        c.comment as comment
        FROM CHALLENGES c
        inner join registered_players p on p.id = c.challengerid
        where c.opponentId = :challengedPlayerId
        and c.isaccepted = false
        """
    )
    override fun findPendingFor(@Bind("challengedPlayerId") challengedPlayerId: PlayerId): List<QueryablePendingChallenge>
}