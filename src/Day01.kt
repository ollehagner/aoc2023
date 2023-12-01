import java.lang.IllegalArgumentException

val wordNumbers = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
)

val wordNumbersReversed = wordNumbers.entries.associate { (key, value) -> key.reversed() to value }

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
    return input.sumOf {
        val firstNumber = firstDigit(it, wordNumbers)
        val secondNumber = firstDigit(it.reversed(), wordNumbersReversed)
        "$firstNumber$secondNumber".toInt()
    }
}

fun firstDigit(line: String): Char {
    return line.first { it.isDigit() }
}

fun firstDigit(line: String, wordNumbers: Map<String, Int>): Int {
    val numbersInTextRegex = ("^(" + wordNumbers.keys.joinToString("|") + ")").toRegex()
    return line
        .windowed(5, partialWindows = true)
        .firstNotNullOf {
            if (it.first().isDigit()) {
                it.first().digitToInt()
            } else {
                numbersInTextRegex.find(it)?.let { matchResult -> wordNumbers[matchResult.groupValues.first()] }
            }
        }
}

fun textToDigits(line: String): String {
    val numbersInText = "^(one|two|three|four|five|six|seven|eight|nine)".toRegex()
    return line
        .windowed(5, partialWindows = true)
        .mapNotNull {
            if (it.first().isDigit()) {
                it.first()
            } else {
                numbersInText.find(it)?.let { matchResult -> matchResult.groupValues.first().textToInt() }
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


