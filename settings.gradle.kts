rootProject.name = "scrambledeggs"

include(
    "infra",
    "common:domain:api:error",
    "common:domain:api:security",
    "hex-matches:domain:core", "hex-matches:domain:api", // the player/matches domain
    "hex-matches:adapters:in:rest",
    "hex-matches:adapters:out:rdbms",
    "hex-leaderboards:domain:core", "hex-leaderboards:domain:api", // the Leaderboards domain
    "hex-leaderboards:adapters:in:rest",
    "hex-leaderboards:adapters:out:eventsourcing", // contains event sourcing persistence + projections
    "hex-leaderboards:adapters:out:rdbms", // contains DB access stuff
    "common:adapter:rdbms", // contains DB access stuff
    "common:adapter:rest-api",
    "ui", // contains all Elm stuff
    "web" // bundles all the things in a SpringBoot container
)