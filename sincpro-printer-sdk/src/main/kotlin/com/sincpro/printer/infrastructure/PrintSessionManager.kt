package com.sincpro.printer.infrastructure

import com.sincpro.printer.domain.IPrinter
import com.sincpro.printer.domain.MediaConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PrintSessionManager(private val printer: IPrinter) {

    private val sessionMutex = Mutex()

    suspend fun <T> executeSession(
        media: MediaConfig = MediaConfig.continuous80mm(),
        copies: Int = 1,
        block: suspend PrintSession.() -> T
    ): Result<T> = sessionMutex.withLock {
        val session = PrintSession(printer, media)
        try {
            session.begin()
            val result = session.block()
            session.end(copies)
            Result.success(result)
        } catch (e: Exception) {
            session.rollback()
            Result.failure(e)
        }
    }

    fun isLocked(): Boolean = sessionMutex.isLocked
}

class PrintSession(private val printer: IPrinter, private val media: MediaConfig) {
    internal suspend fun begin() {
        printer.beginTransaction(media).getOrThrow()
    }

    internal suspend fun end(copies: Int) {
        printer.endTransaction(copies).getOrThrow()
    }

    internal suspend fun rollback() {
        try { printer.endTransaction(1) } catch (_: Exception) {}
    }

    fun getMedia(): MediaConfig = media
    fun getPrinter(): IPrinter = printer
}
