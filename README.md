# Scrambled.gg

A project to get Diabotical players to play more Diabotical, by offering a website to host a Ladder, with a custom points system.

Built in Kotlin, and Elm.

Check [the LiveCoding With Sch3lp Twitch Channel](https://twitch.tv/livecodingwithsch3lp) to see how it's going, or watch [the past episodes on YouTube](https://www.youtube.com/watch?v=MTr35-Pt1Xg&list=PLb9Sf9ImdCXJQ6v76kOfEj2As4_DsfEw4) to see how it went.

## Experiments I'm running
### Valuing E2E tests over unit tests
With E2E I mean integration tests that hit my REST endpoints, and verify both rdbms and eventstore for correctness.

**Hypothesis**: 

* I might refactor much faster this way.
* Running E2E tests is not that much slower than running integration tests + unit tests.

**Observations**:

* I miss unit tests on my domain sometimes, so I started writing those.
* Refactoring is easier most of the time, but running the tests does take longer.
* Using types properly (aka _Lean on the compiler_) is a must.
* I miss integration/framework tests when I don't know enough about a library/framework. For example, I usually finish my refactoring quite easily and then get failing scenario tests because I did something that JDBI doesn't like. JDBI framework tests on my _DAO's_ would give me faster feedback.
* Had to provide my own JWT stubbing impl, which was both fun and not fun. Normally I would write JWT -> User mgmt stuff in unit tests and then just use basic auth in scenario tests.


## Contributing
Read about the design decisions in the [Captain's Log](./docs/CaptainsLog.md).

Unsure which GitMoji to use when committing? [Look over here](https://gitmoji.carloscuesta.me/).

### Developing locally
**Starting the _backend_:**

Navigate to `ScrambledApplication` and run it via IntelliJ IDEA; or run ./gradlew bootRun


**Starting the _frontend_ (for development):**

```
cd ui
npm install
npm start
```

The script behind `npm start` is the following:
```
elm-live src/main/elm/Main.elm --open -d src/main/resources --start-page=index.html --proxy-host=http://localhost:8080/api --proxy-prefix=/api -- --output=src/main/resources/main.js
```

`elm-live`: an [npm package](https://github.com/wking-io/elm-live) that runs a compile + server cycle so we can do hot-reloading at dev time.  
`--open`: Opens a browser after successfully running the elm-live command  
`-d src/main/resources`: Set the base-dir from where to serve from to `src/main/resources` (which contains our index.html and style.css)  
`--start-page=index.html`: So that the elm-main doesn't accidentally override our index.html  
`--proxy-host=http://localhost:8080/api`: Forward requests that are hit with the `--proxy-prefix` parameter to this exact address  
`--proxy-prefix=/api`: When requests that contain this string are hit, the webserver should forward these requests to the `--proxy-host` parameter.  
`--src/main/resources/main.js`: Make sure that the dist is built in the same directory as we're serving from (see `-d`).

## Todo
* [x] ScenarioTests that run against the REST API
* [x] Get a runnable/demoable application again (starting up ScrambledApplication requires a r2dbc url)
* [x] How to do projections? From which class/responsible thing do we initiate it?
* [x] EventSourcing: what's out there? Or do we DIY it with Postgres?
* [x] Try to use elm-ui (and/or elm-css, dunno what it's called nowadays)
* [x] Do some proper error handling
* [x] Actually parse the JSON instead of passing a string
* [x] Find a better way to develop the UI locally, because `gradlew :ui:build` is too tedious/slow (no hot-reloading)
* [x] Make an initial Elm page that calls the rest api
* [x] Make an initial rest controller
* [x] Make sure we can run the SpringBoot app from an executable jar
* [x] Make sure elm-make's build result is served via SpringBoot
* [x] Make sure we can run elm-make from Gradle
* [x] Make sure we can run a SpringBoot app
* [x] Create gradle modules
