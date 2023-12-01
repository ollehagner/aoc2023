import java.lang.IllegalArgumentException

fun main() {

    val testInput = readInput("Day01_test_2")
    val input = readInput("Day01")
    part1(input).println()

    part2(input).println()

}

fun part1(input: List<String>): Int {
    return input
        .sumOf { "${firstDigit(it)}${firstDigit(it.reversed())}".toInt() }
}

fun part2(input: List<String>): Int {
    return input
        .map { textToDigits(it) }
        .sumOf { "${firstDigit(it)}${firstDigit(it.reversed())}".toInt() }
}

fun firstDigit(line: String): Char {
    return line.first { it.isDigit() }
}

fun textToDigits(line: String): String {
    val numbersInText = "^(one|two|three|four|five|six|seven|eight|nine)".toRegex()
    return line
        .windowed(5, partialWindows = true)
        .mapNotNull {
            if (it.first().isDigit()) {
                it.first()
            } else {
                numbersInText.find(it)?.let { matchResult -> matchResult!!.groupValues.first().textToInt() }
            }
        }
        .joinToString("")
}

fun String.textToInt(): String {
    return when(this) {
        "one" -> "1"
        "two" -> "2"
        "three" -> "3"
        "four" -> "4"
        "five" -> "5"
        "six" -> "6"
        "seven" -> "7"
        "eight" -> "8"
        "nine" -> "9"
        else -> {throw IllegalArgumentException("Unknown value $this")}
    }
}


