# Captain's Log

This log serves to document all design decisions and problems we ran in to.

## 2020, December 18th - Deciding which persistence mechanism to use for storing our events
### Options

1. Just use PostgreSQL, they've got a TimeSeries plugin.
2. Use EventStore. 
3. Use FaunaDB.
4. Use MySQL, they've also got a TimeSeries plugin.

### Pro's Con's?


## 2020, December 16th - Screw css combinations, just use elm-ui
Because, reasons. Actually just because it's the simplest thing. And we get free typesafety in our CSS. <3

## 2020, December 15th - Springboot's Gradle Plugin being a troll
We had the problem that when we built an executable jar, it didn't contain our rest-api.jar.

The reason was that the springboot plugin was overriding the regular jar task with its own bootJar task, and thereby the :web module couldn't include it.

Now every module that uses the spring-conventions plugin also builds a separate _-boot.jar_.

Normally only gradle modules that use the `java` plugin, will be targeted by the springboot-gradle plugin, but it appears all our kotlin modules get targeted when they're combined with the springboot-gradle plugin.

So, as a "work-around" we [re-enable the regular "jar" task that springboot-gradle disables](https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/gradle-plugin/reference/html/#packaging-executable), so we get a regular library that the other modules can depend on.

Another part of the work-around is to also suffix the created bootjar with -boot, so the regular jar doesn't get overridden with the bootjar. This we do by configuring the BootJar task with `classifier = "boot"`.

We also need to set the "mainClassName", otherwise the gradle build complains on the BootJar task execution, telling us there's not mainClassName configured.

Ideally we figure out WHY our kotlin modules are being recognized as regular `java` modules, or we figure out how we can disable the springboot gradle plugin execution, which we initially tried by using `id("org.springframework.boot") apply false` and doesn't work.

Our kotlin-conventions do contain the `java-library` plugin, which we need so we can use dependency management tasks like `implementation` etc.

## 2020, December 9th - Go!
Started this project on stream y'all!