package org.scrambled.ktorapi.players.registration

import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.scrambled.domain.core.api.players.FetchAllRegisteredPlayers
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.infra.cqrs.QueryExecutor
import java.util.*

fun Routing.registrationRoutes() {
    val commandExecutor by inject<CommandExecutor>()

    route("/api/register") {

        post("") {
            val registrationInfo = call.receive<RegisterPlayerJson>()
            val registerPlayer = RegisterPlayer(nickname = registrationInfo.nickname)
            val registeredPlayer = commandExecutor.execute(registerPlayer)
            call.respond(Created)
        }
    }
}
data class RegisterPlayerJson(val nickname: String)

fun Routing.playerRoutes() {
    val queryExecutor by inject<QueryExecutor>()

    route("/api/player") {
        get("") {
            val players: List<RegisteredPlayerJson> = queryExecutor.execute(FetchAllRegisteredPlayers()) {
                this.map(RegisteredPlayerRepresentation::toJson)
            }

            call.respond(OK, players)
        }
    }
}

data class RegisteredPlayerJson(val playerId: UUID, val nickname: String)
internal fun RegisteredPlayerRepresentation.toJson(): RegisteredPlayerJson = RegisteredPlayerJson(this.id, this.nickname)
