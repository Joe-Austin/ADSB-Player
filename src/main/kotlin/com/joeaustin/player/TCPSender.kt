package com.joeaustin.player

import kotlinx.coroutines.*
import java.io.Closeable
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class TCPSender(private val port: Int) : CoroutineScope, MessageSender, Closeable {

    private val clientMap = ConcurrentHashMap<Socket, OutputStream>()
    private val server = ServerSocket(port)

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    var firstClientConnectedListener: ((TCPSender) -> Unit)? = null

    fun start() {
        launch {
            acceptClients()
        }
    }

    fun stop() {
        close()
    }

    private suspend fun acceptClients() = withContext(Dispatchers.IO) {
        println("Listening at ${server.inetAddress.hostName}:$port")
        while (!server.isClosed) {
            try {
                val client = server.accept()
                println("Received connection from ${client.inetAddress.hostAddress}")
                client.keepAlive = true
                client.soTimeout = Int.MAX_VALUE

                val firstClient = clientMap.isEmpty()
                clientMap[client] = client.getOutputStream()

                if (firstClient) {
                    firstClientConnectedListener?.invoke(this@TCPSender)
                }
            } catch (t: Throwable) {
                close()
                throw t
            }
        }
    }


    override fun sendMessage(message: String): Boolean {
        clientMap.forEach { (client, os) ->
            try {
                os.write(message.toByteArray())
            } catch (t: Throwable) {
                println("Error writing to client (${client.inetAddress.hostName} (${t.message}), attempting to evict client.")
                try {
                    os.close()
                    client.close()
                    println("Client evicted")
                } catch (ignored: Throwable) {
                    println("Eviction unsuccessful")
                }

                clientMap.remove(client)
            }
        }

        return true
    }

    override fun close() {
        cancel()
        server.close()
        clientMap.forEach { (socket, os) ->
            try {
                os.close()
                socket.close()
            } catch (ignore: Throwable) {
            }
        }

        clientMap.clear()
    }
}