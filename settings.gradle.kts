rootProject.name = "scrambledeggs"

include(
    "infra",
    "domain:core", "domain:core-api", // the player/matches domain
    "domain:leaderboards", "domain:leaderboards-api", // the Leaderboards domain
    "adapter:eventsourcing", // contains event sourcing persistence + projections
    "adapter:rdbms", // contains DB access stuff
    "adapter:rest-api",
    "adapter:ktor-api", // alternative API in Ktor instead of spring-web
    "ui", // contains all Elm stuff
    "web", // bundles all the things in a SpringBoot container
    "ktor-web" // bundles all the things to serve in a Ktor embedded container
)