package org.scrambled.infra.retry

fun <T> retry(errorMessage: String = "Retry attempts exhausted", retries: Int = 5, block: () -> T)
    : Retry<T> {
    return Retry(retries, errorMessage, block)
}

class Retry<T>(private val retries: Int, private val errorMessage: String, private val block: () -> T) {
    fun until(predicate: (t: T) -> Boolean): T {
        fun loop(attempts: Int): T {
            if (attempts == this.retries) throw RetryException(errorMessage)
            val result = block.invoke()
            return if (!predicate(result)) {
                loop(attempts + 1)
            } else {
                result
            }
        }
        return loop(0)
    }
}

class RetryException(message: String): RuntimeException(message)