package org.scrambled.infra.hashids

class HashidsForTests {
    companion object {
        fun seedHashIdsWith(seed: Int) {
            Hashids.Companion.seedSupplier = { seed }
        }
    }
}