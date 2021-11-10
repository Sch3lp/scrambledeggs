package org.scrambled.adapter.rdbms.core.challenges

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMappers
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.domain.core.api.challenges.*
import java.util.*

@RegisterKotlinMappers(
    RegisterKotlinMapper(value = QueryableChallenge::class),
    RegisterKotlinMapper(value = QueryablePendingChallenge::class)
)
interface ChallengesDao : QueryableChallenges {

    @SqlQuery("SELECT * FROM CHALLENGES where challengeId = :challengeId")
    override fun getByChallengeId(@Bind("challengeId") challengeId: String): QueryableChallenge?

    @SqlQuery(
        """
        SELECT * FROM CHALLENGES 
        where challengerId = :challengerId and opponentId = :opponentId
        """
    )
    fun findChallenge(
        @Bind("challengerId") challengerId: PlayerId,
        @Bind("opponentId") opponentId: PlayerId
    ): List<QueryableChallenge>

    //TODO: fix duplication of concept (querying into a QueryablePendingChallenge)
    @SqlQuery(
        """
        SELECT c.id as id,
        c.challengeId as challengeId,
        c.challengerId as challengerId,
        challenger.nickname as challengerName,
        c.opponentId as opponentId,
        opponent.nickname as opponentName,
        c.gameMode as gameMode,
        c.appointmentsuggestion as appointment,
        c.comment as comment,
        c.isaccepted as isAccepted
        FROM CHALLENGES c
        inner join registered_players challenger on challenger.id = c.challengerid
        inner join registered_players opponent on opponent.id = c.opponentid
        where c.isaccepted = false
        and c.challengeId = :challengeId
    """
    )
    override fun getByChallengeId(@Bind("challengeId") challengeId: ChallengeId): QueryablePendingChallenge?

    //TODO: fix duplication of concept (querying into a QueryablePendingChallenge)
    @SqlQuery(
        """
        SELECT c.id as id,
        c.challengeId as challengeId,
        c.challengerId as challengerId,
        challenger.nickname as challengerName,
        c.opponentId as opponentId,
        opponent.nickname as opponentName,
        c.gameMode as gameMode,
        c.appointmentsuggestion as appointment,
        c.comment as comment,
        c.isaccepted as isAccepted
        FROM CHALLENGES c
        inner join registered_players challenger on challenger.id = c.challengerid
        inner join registered_players opponent on opponent.id = c.opponentid
        where c.isaccepted = false
        and c.opponentId = :challengedPlayerId
        or c.challengerId = :challengedPlayerId
        """
    )
    override fun findPendingFor(@Bind("challengedPlayerId") challengedPlayerId: PlayerId): List<QueryablePendingChallenge>

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
    override fun storePendingChallenge(@BindKotlin queryableChallenge: QueryableChallenge)

    @SqlUpdate(
        """UPDATE CHALLENGES
            set isaccepted = true
            where id = :id
        """
    )
    override fun storeAcceptedChallenge(@Bind("id") id: UUID)

}