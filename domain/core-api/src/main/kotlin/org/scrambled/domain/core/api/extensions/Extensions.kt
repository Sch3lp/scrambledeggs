package org.scrambled.domain.core.api.extensions

import org.scrambled.domain.core.api.challenging.PlayerId
import java.util.*

fun String.toPlayerId() : PlayerId = UUID.fromString(this)