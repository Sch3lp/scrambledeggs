package org.scrambled

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScrambledApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ScrambledApplication>(*args)
        }
    }
}