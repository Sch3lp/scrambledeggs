package org.scrambled.adapter.eventsourcing.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun scrambledObjectMapper(): ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
