# Captain's Log

This log serves to document all design decisions and problems we ran in to.

## 2021, June 22nd - JWT in cookie or exchange JWT for session in cookie
To reduce the attack surface, we'll want to send the JWT along with the http requests in a read-only cookie.

This we'll need to do anyway, even if we're exchanging it for a `Session` object. So, the only value the `Session` would bring, is to get a nicer, abstract way of using it in the UI. But it would still need an invalidation feature somehow, and that we _automatically_ get on our JWT.

So, we'll just end up using JWT's in read-only cookies as our sessions.

## 2021, April 6th - We'll keep a single external account ref instead of multiple for one registered player
So if somebody wants to both link their google AND their epic games accounts to the same ScrambledEggs account, they won't be able to.

We'll make sure people register using Epic Games only, so we'll get easier future integration.

Migrating towards having multiple accounts isn't impossible either, so we'll cross that bridge when we get there.

## 2021, February 28th - Gradle Spring Cleaning!
Ha! Wow! Much pun!

Because we split the gradle conventions plugins into smaller bits, and because of the low dependency on spring stuff (only need @Component or whatever) it's now possible to drop that bootJar overriding hack.

Also added `java-test-fixtures` to be able to share objects like TestBuilders etc. across modules.

## 2020, December 18th - Deciding which persistence mechanism to use for storing our events
### Options

1. Just use PostgreSQL, they've got a TimeSeries plugin.
2. Use EventStore. 
3. Use FaunaDB.
4. Use MySQL, they've also got a TimeSeries plugin.

### Pro's Con's?
Ended up with PostgreSQL table because of the learning potential we'd get when trying to implement our own EventStore.

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