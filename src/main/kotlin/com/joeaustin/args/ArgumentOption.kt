package com.joeaustin.args

data class ArgumentOption(
    val name: String,
    val description: String,
    val markers: Set<String>,
    val inputCount: Int,
    val defaultValues: List<String> = emptyList()
) {
    companion object {
        fun printHelp(arguments: List<ArgumentOption>) {
            arguments.forEach { arg ->
                val flags = arg.markers.joinToString()
                val defaultValueText = if (arg.defaultValues.isNotEmpty()) {
                    "(Default value(s): ${arg.defaultValues.joinToString()}) "
                } else {
                    ""
                }

                println("[$flags] ${arg.name} $defaultValueText ${arg.description}")
            }
        }
    }
}