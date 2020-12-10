package org.scrambled

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScrambledApplication

fun main(args: Array<String>) {
    runApplication<ScrambledApplication>(*args)
}
