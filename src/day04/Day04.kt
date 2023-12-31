package day04

import common.println
import common.readInput
import java.math.BigInteger

fun main() {
    val testInput = readInput("day04/testinput")

    val input = readInput("day04/input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    return input
        .map(ScratchCard.Companion::parse)
        .sumOf { it.score() }
}

fun part2(input: List<String>): Long {
    val scratchCards = input
        .map(ScratchCard.Companion::parse)
    val initialCardCount = scratchCards.associate { it.id to 1L}.toMutableMap()
    return scratchCards
        .fold(initialCardCount) { acc, scratchCard ->
            val instancesOfCurrent = acc[scratchCard.id]
            repeat(scratchCard.matchingNumbers) { updateOffset ->
                val toUpdateId = scratchCard.id + updateOffset + 1
                acc.merge(toUpdateId, instancesOfCurrent!!) { newValue, oldValue -> newValue + oldValue }
            }
            acc
        }
        .values
        .sum()
}

fun numbersToInts(numbersAsString: String): Set<Int> {
    return numbersAsString.split("\\s+".toRegex()).map { it.toInt() }.toSet()
}

data class ScratchCard(val id: Int, val matchingNumbers: Int) {

    fun score(): Long {
        return 2L.pow(matchingNumbers - 1)
    }

    companion object {
        fun parse(line: String): ScratchCard {
            val id = line.substringBefore(":").substringAfter("Card").trim().toInt()
            val numbers = numbersToInts(line.substringAfter(":").substringBefore("|").trim())
            val winningNumbers = numbersToInts(line.substringAfter(":").substringAfter("|").trim())
            return ScratchCard(id, numbers.intersect(winningNumbers).size)
        }
    }
}

fun Long.pow(exp: Int): Long {
    return if (exp == -1) 0 else BigInteger.valueOf(2L).pow(exp).toLong()
}