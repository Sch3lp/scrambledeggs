package org.scrambled

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ScrambledApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ScrambledApplication>(*args)
        }
    }
}