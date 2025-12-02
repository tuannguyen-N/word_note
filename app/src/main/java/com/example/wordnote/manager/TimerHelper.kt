package com.example.wordnote.manager

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerHelper(
    private val scope: CoroutineScope
) {
    private var job: Job? = null
    private var remainingSeconds: Long = 0

    fun start(
        totalSeconds: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        remainingSeconds = totalSeconds
        runTimer(onTick, onFinish)
    }

    fun pause() {
        job?.cancel()
    }

    fun resume(
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        runTimer(onTick, onFinish)
    }

    fun reset() {
        job?.cancel()
        remainingSeconds = 0
    }

    private fun runTimer(
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        job?.cancel()
        val endTime = SystemClock.elapsedRealtime() + remainingSeconds * 1000
        job = scope.launch {
            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val leftMs = endTime - now
                if (leftMs <= 0) {
                    remainingSeconds = 0
                    onTick(0)
                    onFinish()
                    break
                }

                remainingSeconds = leftMs / 1000
                onTick(remainingSeconds)
                delay(300)
            }
        }
    }
}
