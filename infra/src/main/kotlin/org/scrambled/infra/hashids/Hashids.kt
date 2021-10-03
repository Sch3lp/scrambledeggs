package org.scrambled.infra.hashids

import org.hashids.Hashids
import kotlin.random.Random

class Hashids(salt: String, minLength: Int = 8) {

    private val hashids: Hashids = Hashids(salt, minLength)
    private val random: Random = Random(seedSupplier.invoke())

    fun next(): String {
        return hashids.encode(random.nextLong(10000))
    }

    companion object {
        var seedSupplier: () -> Int = { Random.nextInt() }
    }
}