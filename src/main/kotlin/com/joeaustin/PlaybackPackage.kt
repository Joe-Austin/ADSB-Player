package com.joeaustin

import com.joeaustin.player.CompletionBehavior
import java.io.File
import java.time.Duration

data class PlaybackPackage(
    val inputFile: File,
    val completionBehavior: CompletionBehavior,
    val interval: Duration,
    val port: Int
)