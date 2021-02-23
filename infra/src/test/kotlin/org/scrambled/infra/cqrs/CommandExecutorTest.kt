package org.scrambled.infra.cqrs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.scrambled.infra.domainevents.DomainEvent
import org.scrambled.infra.domainevents.InMemoryDomainEventBroadcaster

internal class CommandExecutorTest {
    private val broadcaster = InMemoryDomainEventBroadcaster()

    @Test
    internal fun `handlerForCommand retrieves the CommandHandler based on the command type`() {
        val commandExecutor = CommandExecutor(listOf(RewindSongHandler(), PlaySongHandler()), broadcaster)

        commandExecutor.execute(PlaySong(songName = "Chiquitita"))

        with(broadcaster) {
            verifyEventExists(SongStarted::class.java)
        }
    }
}


fun <T> InMemoryDomainEventBroadcaster.verifyEventExists(clazz: Class<T>) {
    assertThat(this.findEvent(clazz)).isNotNull()
}


internal class PlaySongHandler: CommandHandler<Song, PlaySong> {
    override val commandType = PlaySong::class
    override fun handle(cmd: PlaySong): Pair<Song, SongStarted> {
        return Song("unknown", cmd.songName) to SongStarted(cmd.songName)
    }
}

internal data class PlaySong(val songName: String): Command<Song>
internal data class SongStarted(val songName: String): DomainEvent()

internal class RewindSongHandler: CommandHandler<Song, ReplaySong> {
    override val commandType = ReplaySong::class
    override fun handle(cmd: ReplaySong): Pair<Song, SongRestarted> {
        return Song("unknown", cmd.songName) to SongRestarted(cmd.songName)
    }
}

internal data class ReplaySong(val songName: String): Command<Song>
internal data class SongRestarted(val songName: String): DomainEvent()
