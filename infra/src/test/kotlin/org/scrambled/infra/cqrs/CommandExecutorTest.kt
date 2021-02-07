package org.scrambled.infra.cqrs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CommandExecutorTest {
    private val broadcaster = DomainEventBroadcaster()

    @Test
    internal fun `handlerForCommand retrieves the CommandHandler based on the command type`() {
        val commandExecutor = CommandExecutor(listOf(RewindSongHandler(), PlaySongHandler()), broadcaster)

        commandExecutor.execute(PlaySong(songName = "Chiquitita"))

        with(broadcaster) {
            verifyEventExists(SongStarted::class.java)
        }
    }
}


fun <T> DomainEventBroadcaster.verifyEventExists(clazz: Class<T>) {
    assertThat(this.findEvent(clazz)).isNotNull()
}


internal class PlaySongHandler: CommandHandler<PlaySong> {
    override val commandType = PlaySong::class
    override fun handle(cmd: PlaySong): DomainEvent {
        return SongStarted(cmd.songName)
    }
}

internal data class PlaySong(val songName: String): Command
internal data class SongStarted(val songName: String): DomainEvent()

internal class RewindSongHandler: CommandHandler<RewindSong> {
    override val commandType = RewindSong::class
    override fun handle(cmd: RewindSong): DomainEvent {
        return SongStarted(cmd.songName)
    }
}

internal data class RewindSong(val songName: String): Command
internal data class SongRestarted(val songName: String): DomainEvent()
