package day12

import common.println
import common.readInput

const val WORKING = '.'
const val DAMAGED = '#'
const val UNKNOWN = '?'

var globalCache = mutableMapOf<Record, Long>()

fun main() {
    val testinput = readInput("day12/testinput")
    val input = readInput("day12/input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    return input.map { Record.parse(it) }.sumOf { record ->
        calculateVariants(record)
    }

}

fun part2(input: List<String>): Long {
    return input.map { line -> Record.parse(line, 5) }
        .mapIndexed { index, record ->
            globalCache = mutableMapOf()
            calculateVariants(record)
        }
        .sum()
}

fun calculateVariants(record: Record): Long {
    globalCache[record]?.let {
        return it
    }

    //Om det är slut på # och ?: om damages är tom, returnera 1 annars 0
    if (record.springs.none { it == DAMAGED || it == UNKNOWN }) {
        return if (record.damagedGroups.isEmpty()) {
            1L
        } else {
            0L
        }
    }

    return if (record.springs.first() == WORKING) {
        val newRecord = record.copy(springs = record.springs.dropWhile { it == WORKING })
        calculateVariants(newRecord)
            .apply { globalCache[newRecord] = this }
    } else if (record.springs.first() == UNKNOWN) {
            val firstRecord = record.copy(springs = record.springs.drop(1))
            val secondRecord = record.copy(springs = DAMAGED + record.springs.drop(1))
            calculateVariants(firstRecord).apply { globalCache[firstRecord] = this } +
                    calculateVariants(secondRecord).apply { globalCache[secondRecord] = this }
    } else if (record.springs.first() == DAMAGED) {
        if (record.damagedGroups.isEmpty()) {
            return 0
        }

        val currentDamageLength = record.damagedGroups.first()
        if (record.springs.length < currentDamageLength) {
            return 0
        }

        //Om vi kan ta längden
        val remainingAfterDamageLengthRemoved = record.springs.drop(currentDamageLength)
        val nextPotentialDamageGroup = record.springs.take(currentDamageLength)
        return if (nextPotentialDamageGroup.none { it == WORKING }) {

            return if (remainingAfterDamageLengthRemoved.isEmpty()) {
                return if (record.damagedGroups.drop(1).isEmpty()) {
                    1
                } else {
                    0
                }
            } else {

                return when (remainingAfterDamageLengthRemoved.first()) {
                    DAMAGED -> {
                        0
                    }

                    UNKNOWN -> {
                        val newRecord = Record(remainingAfterDamageLengthRemoved.drop(1),
                            record.damagedGroups.drop(1))
                        calculateVariants(newRecord).apply { globalCache[newRecord] = this }
                    }

                    WORKING -> {
                        val newRecord = Record(remainingAfterDamageLengthRemoved.dropWhile { it == WORKING },
                            record.damagedGroups.drop(1))
                        calculateVariants(newRecord)
                            .apply { globalCache[newRecord] = this }
                    }

                    else -> throw IllegalStateException("Unknown char")
                }
            }
        } else {
            return 0
        }
    } else {
        throw IllegalStateException("Missed something")
    }

}



data class Record(val springs: String, val damagedGroups: List<Int>) {

    companion object {
        fun parse(input: String, multiplicationFactor: Int = 1): Record {
            val springs = (0..<multiplicationFactor).joinToString("$UNKNOWN") { input.substringBefore(" ") }
            val damagedGroups =
                (0..<multiplicationFactor).flatMap { input.substringAfter(" ").split(",").map { it.trim().toInt() } }
            return Record(springs, damagedGroups)
        }
    }

    override fun toString(): String {
        return "($springs | $damagedGroups)"
    }
}

