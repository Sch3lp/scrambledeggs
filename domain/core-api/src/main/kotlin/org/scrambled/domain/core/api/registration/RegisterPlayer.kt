package org.scrambled.domain.core.api.registration

import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.cqrs.DomainEvent

data class RegisterPlayer(
    val nickName: String
): Command {
    override val id: AggregateId
        get() = AggregateId.randomUUID()
}

data class PlayerRegistered(
    val id: AggregateId,
    val nickName: String
) : DomainEvent()