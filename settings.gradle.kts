rootProject.name = "scrambledeggs"

include(
    "infra",
    "domain:core", "domain:core-api", // the player/matches domain
    "domain:leaderboards", "domain:leaderboards-api", // the Leaderboards domain
    "adapter:eventsourcing", // contains event sourcing persistence + projections
    "adapter:rdbms", // contains DB access stuff
    "adapter:rest-api",
    "ui", // contains all Elm stuff
    "web" // bundles all the things in a SpringBoot container
)