package org.scrambled.domain.leaderboards.impl.mostchallengesdone

import org.scrambled.domain.leaderboards.api.infra.BroadcastEvent
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvents

class InMemoryBroadcastEvents(
    private val _events: MutableList<BroadcastEvent> = emptyList<BroadcastEvent>().toMutableList())
    : List<BroadcastEvent> by _events, BroadcastEvents {

    override fun keep(event: BroadcastEvent) {
        _events.add(event)
    }

    override fun findAll(): List<BroadcastEvent> {
        return _events
    }
}
