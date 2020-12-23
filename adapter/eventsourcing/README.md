# Event Sourcing module

This is the adapter to our domain so we can _persist_ the domain using some Event Sourcing library/platform.

It should only depend on our Domain's API.

## Todo

* [ ] Write an integration-test (using TestContainers)
* [ ] Set up a Postgres in a docker container to play around with
* [ ] Finish `PostgresEventStream` implementation
* [ ] Clean up dependencies (e.g. maybe we don't need `org.springframework.data:spring-data-r2dbc`)
