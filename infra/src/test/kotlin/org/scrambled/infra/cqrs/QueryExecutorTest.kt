package org.scrambled.infra.cqrs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class QueryExecutorTest {
    private val songDb = listOf(
        Song("ABBA", "Chiquitita"),
        Song("Commodore Hits", "Lemmings")
    )

    @Test
    internal fun `handlerForQuery retrieves the QueryHandler based on the query type`() {
        val queryExecutor = QueryExecutor(listOf(FindByArtistHandler(songDb), FindByTitleHandler(songDb)))

        val result = queryExecutor.execute(FindByTitle("Chiquitita")) { "${it.title} by ${it.artist}" }

        assertThat(result).isEqualTo("Chiquitita by ABBA")
    }
}

data class Song(val artist: String, val title: String)
data class FindByArtist(val artist: String) : Query<Song> {
    override val id: AggregateId = UUID.randomUUID()
}
class FindByArtistHandler(val songDb: List<Song>) : QueryHandler<FindByArtist, Song> {
    override val queryType = FindByArtist::class.java
    override fun handle(query: FindByArtist) = songDb.firstOrNull { it.artist == query.artist }
}

data class FindByTitle(val title: String) : Query<Song> {
    override val id: AggregateId = UUID.randomUUID()
}
class FindByTitleHandler(val songDb: List<Song>) : QueryHandler<FindByTitle, Song> {
    override val queryType = FindByTitle::class.java
    override fun handle(query: FindByTitle) = songDb.firstOrNull { it.title == query.title }
}