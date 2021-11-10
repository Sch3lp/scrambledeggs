package org.scrambled.domain.leaderboards.api.infra

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