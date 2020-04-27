package com.joeaustin

import com.joeaustin.args.ArgumentOption
import com.joeaustin.args.ArgumentParser
import com.joeaustin.player.CompletionBehavior
import com.joeaustin.player.IntervalPlayer
import com.joeaustin.player.NewLineMessageParser
import com.joeaustin.player.TCPSender
import java.io.File
import java.time.Duration
import kotlin.system.exitProcess

private const val OPTION_NAME_DELAY = "Message Delay"
private const val OPTION_NAME_PORT = "Port"
private const val OPTION_NAME_INPUT = "Input File"
private const val OPTION_NAME_END_BEHAVIOR = "End Behavior"
private const val OPTION_NAME_HELP = "Help"

fun main(args: Array<String>) {
    val options = getArgumentOptions()
    val argList = args.toList()

    val parsedArgs = try {
        ArgumentParser.parse(options, argList, false)
    } catch (t: Throwable) {
        println(t.message)
        println()
        null
    }

    val playbackPackage = parsedArgs?.let { getPlaybackPackage(it) }

    if (parsedArgs == null || parsedArgs.hasFlag(OPTION_NAME_HELP) || playbackPackage == null) {
        ArgumentOption.printHelp(options)
        return
    }


    val parser = NewLineMessageParser(playbackPackage.inputFile)
    val sender = TCPSender(playbackPackage.port)
    val player = IntervalPlayer(playbackPackage.interval, parser, sender, playbackPackage.completionBehavior)

    sender.firstClientConnectedListener = {
        println("Starting playback (type q to quit)")
        player.start()
    }

    player.onStoppedListener = {
        println("Playback complete")
        exitProcess(0)
    }

    sender.start()

    println("Waiting for client to connect")
    println("Type q to quit")

    val reader = System.`in`.bufferedReader()
    val quitCommands = setOf("q", "quit", "exit")

    var quit = false

    while (!quit) {
        val input = reader.readLine().trim().toLowerCase()
        if (input in quitCommands) {
            quit = true
        } else {
            println("I don't know what to do with: '$input'")
        }
    }

    sender.stop()
    player.stop()

    println("Done!")
}

private fun getArgumentOptions(): List<ArgumentOption> {
    val optionDelay = ArgumentOption(
        OPTION_NAME_DELAY,
        "Specifies the time (in milliseconds) between messages",
        setOf("-d", "--delay"),
        1,
        listOf("5000")
    )

    val optionPort = ArgumentOption(
        OPTION_NAME_PORT,
        "The port to broadcast messages to.",
        setOf("-p", "--port"),
        1,
        listOf("30003")
    )


    val optionInput = ArgumentOption(
        OPTION_NAME_INPUT,
        "Specifies the input file to play",
        setOf("-i", "--input"),
        1
    )

    val optionEndBehavior = ArgumentOption(
        OPTION_NAME_END_BEHAVIOR,
        "Specifies what to do when the end of the file is reached (r = restart; e = end)",
        setOf("-e", "--end-behavior"),
        1,
        defaultValues = listOf("r")
    )

    val optionHelp = ArgumentOption(
        OPTION_NAME_HELP,
        "Prints this text",
        setOf("-h", "--help"),
        0
    )

    return listOf(optionDelay, optionPort, optionInput, optionEndBehavior, optionHelp)
}

private fun getPlaybackPackage(parser: ArgumentParser): PlaybackPackage? {
    val port = parser.getInt(OPTION_NAME_PORT)
    val inputFilePath = parser.getString(OPTION_NAME_INPUT)
    val interval = parser.getInt(OPTION_NAME_DELAY)
    val completionBehaviorInput = parser.getString(OPTION_NAME_END_BEHAVIOR)

    if (port == null) {
        println("Port expected to be an integer")
        return null
    }

    if (inputFilePath == null) {
        println("input path required")
        return null
    }

    if (interval == null) {
        println("interval expected to be an integer")
        return null
    }

    if (completionBehaviorInput == null || completionBehaviorInput !in setOf("e", "r")) {
        println("Completion behavior expected to be either 'r' or 'e'")
        return null
    }

    val inputFile = File(inputFilePath)

    if (!inputFile.exists()) {
        println("Input file must exist")
        return null
    }

    val completionBehavior = when (completionBehaviorInput) {
        "r" -> CompletionBehavior.RESTART
        else -> CompletionBehavior.END
    }

    val duration = Duration.ofMillis(interval.toLong())

    return PlaybackPackage(inputFile, completionBehavior, duration, port)
}