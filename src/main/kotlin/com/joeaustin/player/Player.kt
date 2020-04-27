package com.joeaustin.player

open class Player(
    protected val messageParser: MessageParser,
    protected val messageSender: MessageSender,
    protected val completionBehavior: CompletionBehavior
) {

    open fun playNext(): Boolean {
        val nextMessage = getNextMessage() ?: return false
        return messageSender.sendMessage(nextMessage)
    }

    protected fun getNextMessage(): String? {
        var nextMessage = messageParser.getNextMessage()

        if (nextMessage == null
            && completionBehavior == CompletionBehavior.RESTART
            && messageParser.canReset
            && messageParser.reset()
        ) {
            nextMessage = messageParser.getNextMessage()
        }

        return nextMessage
    }
}