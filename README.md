# Scrambled.gg

A project to get Diabotical players to play more Diabotical, by offering a website to host a Ladder, with a custom points system.

Built in Kotlin, and Elm.

See https://twitch.tv/livecodingwithsch3lp to see how it's going.

## Contributing
Read about the design decisions in the [Captain's Log](./docs/CaptainsLog.md).

Unsure which GitMoji to use when committing? [Look over here](https://gitmoji.carloscuesta.me/).

## Todo
* [ ] EventSourcing: what's out there? Or do we DIY it with Postgres?
* [ ] Find a better way to develop the UI locally, because `gradlew :ui:build` is too tedious/slow (no hot-reloading)
* [ ] Do some proper error handling
* [ ] Actually parse the JSON instead of passing a string
* [x] Make an initial Elm page that calls the rest api
* [x] Make an initial rest controller
* [x] Make sure we can run the SpringBoot app from an executable jar
* [x] Make sure elm-make's build result is served via SpringBoot
* [x] Make sure we can run elm-make from Gradle
* [x] Make sure we can run a SpringBoot app
* [x] Create gradle modules
