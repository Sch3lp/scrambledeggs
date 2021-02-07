package org.scrambled.domain.core.api

import org.scrambled.infra.cqrs.AggregateId

interface Repository<Aggregate> {
    fun getById(id: AggregateId): Aggregate
    fun save(aggregate: Aggregate)
}