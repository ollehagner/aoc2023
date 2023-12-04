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
    val numOfCards = scratchCards.associate { it.id to 1L}.toMutableMap()
    scratchCards
        .forEach { scratchCard ->
            val instancesOfCurrent = numOfCards.getOrDefault(scratchCard.id, 0)
            repeat(scratchCard.matchingNumbers) { offset ->
                val toUpdate = scratchCard.id + offset + 1
                val instancesOfCardToAdd = numOfCards.getOrDefault(toUpdate, 0)
                numOfCards[toUpdate] = instancesOfCurrent + instancesOfCardToAdd
            }
        }
    return numOfCards.values.sum()
}

fun numbersToInts(numbersAsString: String): Set<Int> {
    return numbersAsString.split("\\s+".toRegex()).map { it.toInt() }.toSet()
}

data class ScratchCard(val id: Int, val numbers: Set<Int>, val matchingNumbers: Int) {

    fun score(): Long {
        return 2L.pow(matchingNumbers - 1)
    }

    companion object {
        fun parse(line: String): ScratchCard {
            val id = line.substringBefore(":").substringAfter("Card").trim().toInt()
            val numbers = numbersToInts(line.substringAfter(":").substringBefore("|").trim())
            val winningNumbers = numbersToInts(line.substringAfter(":").substringAfter("|").trim())
            return ScratchCard(id - 1, numbers, numbers.intersect(winningNumbers).size)
        }
    }
}

fun Long.pow(exp: Int): Long {
    return if (exp == -1) 0 else BigInteger.valueOf(2L).pow(exp).toLong()
}