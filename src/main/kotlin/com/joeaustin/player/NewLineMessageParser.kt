package com.joeaustin.player

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class NewLineMessageParser(inputFile: File) : MessageParser {

    private val pos = AtomicInteger(0)
    override val canReset: Boolean = true
    private val lines = if (inputFile.exists()) {
        inputFile.readLines()
    } else {
        emptyList()
    }

    override fun reset(): Boolean {
        pos.set(0)
        return true
    }

    override fun getNextMessage(): String? {
        val lineCount = lines.size
        val currentPos = pos.getAndIncrement()

        return if (currentPos < lineCount) {
            lines[currentPos] + "\n"
        } else {
            null
        }
    }
}