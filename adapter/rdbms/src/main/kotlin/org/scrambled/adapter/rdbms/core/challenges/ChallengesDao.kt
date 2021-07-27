package org.scrambled.adapter.rdbms.core.challenges

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.domain.core.api.challenging.ChallengeId
import org.scrambled.domain.core.api.challenging.QueryableChallenge
import org.scrambled.domain.core.api.challenging.QueryableChallenges

@RegisterKotlinMapper(value = QueryableChallenge::class)
interface ChallengesDao: QueryableChallenges {
    @SqlQuery("SELECT id, challengerId, opponentId FROM CHALLENGES where id = :id")
    override fun getById(@Bind("id") id: ChallengeId): QueryableChallenge?

    @SqlUpdate("INSERT INTO CHALLENGES(id, challengerId, opponentId) values(:id, :challengerId, :opponentId)")
    override fun store(@BindKotlin queryableChallenge: QueryableChallenge)
}