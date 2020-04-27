package com.joeaustin.player

interface MessageSender {
    fun sendMessage(message: String): Boolean
}