package com.joeaustin.player

import java.time.Duration
import java.util.*

private const val TIMER_NAME = "Message Ticker"

class IntervalPlayer(
    private val durationBetweenMessages: Duration,
    messageParser: MessageParser,
    messageSender: MessageSender,
    completionBehavior: CompletionBehavior
) : Player(messageParser, messageSender, completionBehavior) {

    private var messageTicker: Timer? = null
    private val syncRoot = Any()
    var onStoppedListener: ((IntervalPlayer) -> Unit)? = null

    fun start() {
        synchronized(syncRoot) {
            if (messageTicker == null) {
                messageTicker = kotlin.concurrent.timer(
                    name = TIMER_NAME,
                    daemon = false,
                    initialDelay = 0L,
                    period = durationBetweenMessages.toMillis()
                ) { onMessageTickerTick() }
            }
        }
    }

    private fun onMessageTickerTick() {
        if (!playNext()) {
            stop()
        }
    }

    fun stop() {
        synchronized(syncRoot) {
            messageTicker?.cancel()
            messageTicker = null
            onStoppedListener?.invoke(this)
        }
    }
}