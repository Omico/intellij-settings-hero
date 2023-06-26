package me.omico.intellij.settingsHero.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.visitFileTree

val File.watchChannel: WatchChannel
    get() = toPath().watchChannel

val Path.watchChannel: WatchChannel
    get() = WatchChannel(path = this)

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalPathApi::class,
)
class WatchChannel(
    private val channel: Channel<PathWatchEvent> = Channel(),
    private val path: Path,
) : Channel<PathWatchEvent> by channel {
    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchKeys = HashSet<WatchKey>()

    private fun recollectWatchKeys() {
        clearWatchKeys()
        when {
            path.isDirectory() ->
                path.visitFileTree {
                    onPreVisitDirectory { dir, _ ->
                        watchKeys += dir.register()
                        FileVisitResult.CONTINUE
                    }
                }
            else -> watchKeys += path.toAbsolutePath().parent.register()
        }
    }

    init {
        watchScoop.launch(Dispatchers.IO) {
            var requireCollectWatchKeys = true
            while (!isClosedForSend) {
                if (requireCollectWatchKeys) {
                    recollectWatchKeys()
                    requireCollectWatchKeys = false
                }
                val watchKey = watchService.take()
                watchKey.pollEvents()
                    .filterIsInstance<WatchEvent<Path>>()
                    .map(::PathWatchEvent)
                    .forEach { watchEvent ->
                        if (path.isRegularFile() && watchEvent.context().toAbsolutePath() != path.toAbsolutePath()) {
                            return@forEach
                        }
                        if (watchEvent.isDirectoryCreateOrDelete) requireCollectWatchKeys = true
                        channel.send(watchEvent)
                    }
                if (!watchKey.reset()) {
                    watchKey.cancel()
                    close()
                }
            }
        }
    }

    override fun close(cause: Throwable?): Boolean =
        clearWatchKeys().let { channel.close(cause) }

    private fun Path.register(): WatchKey =
        register(watchService, watchEventKinds.toTypedArray())

    private fun clearWatchKeys() {
        watchKeys.forEach(WatchKey::cancel)
        watchKeys.clear()
    }
}

data class PathWatchEvent(
    private val watchEvent: WatchEvent<Path>,
) : WatchEvent<Path> by watchEvent {
    val path: Path = watchEvent.context()
}

private val watchScoop: CoroutineScope = CoroutineScope(Dispatchers.IO)

private val watchEventKinds: Set<WatchEvent.Kind<Path>> = setOf(
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_DELETE,
    StandardWatchEventKinds.ENTRY_MODIFY,
)

private val PathWatchEvent.isDirectoryCreateOrDelete: Boolean
    get() = context().isDirectory() &&
        kind() in listOf(StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)
