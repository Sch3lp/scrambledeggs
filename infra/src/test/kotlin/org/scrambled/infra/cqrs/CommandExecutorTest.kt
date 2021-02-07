package org.scrambled.infra.cqrs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

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
    override val commandType = PlaySong::class.java
    override fun handle(cmd: PlaySong): DomainEvent {
        return SongStarted(cmd.songName)
    }
}

internal data class PlaySong(val songName: String): Command {
    override val id: AggregateId = UUID.randomUUID()
}
internal data class SongStarted(val songName: String): DomainEvent()

internal class RewindSongHandler: CommandHandler<RewindSong> {
    override val commandType = RewindSong::class.java
    override fun handle(cmd: RewindSong): DomainEvent {
        return SongStarted(cmd.songName)
    }
}

internal data class RewindSong(val songName: String): Command {
    override val id: AggregateId = UUID.randomUUID()
}
internal data class SongRestarted(val songName: String): DomainEvent()
