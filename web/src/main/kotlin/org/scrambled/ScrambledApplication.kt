package org.scrambled

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

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

@Configuration
class WebApplicationConfig: WebMvcConfigurer {
    // Paths that do not match the pattern for a static resource (containing a '.'), will be forwarded to root (and will be served the vue frontend)
    // Since it's a forward, the url will stay the same. This makes it so vue can route to the correct page.
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/**/{spring:[^.]+}").setViewName("forward:/")
    }
}