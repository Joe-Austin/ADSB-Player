package com.joeaustin.player

interface MessageParser {
    fun getNextMessage(): String?
    val canReset: Boolean
    fun reset(): Boolean
}