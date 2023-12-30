package day08

import common.infiniteSequence
import common.leastCommonMultiple
import common.println
import common.readInput
import java.lang.IllegalArgumentException
import java.util.function.Predicate

fun main() {
    val testinput = readInput("day08/testinput")
    val testinputpart2 = readInput("day08/testinputpart2")
    val input = readInput("day08/input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val instructions = input.first()
    val network = createNetwork(input)
    return followInstructions("AAA", { it == "ZZZ"}, instructions, network)
}

fun part2(input: List<String>): Long {
    val instructions = input.first()
    val network = createNetwork(input)

    val startingPositions = network.keys.filter { it.endsWith("A") }.toSet()
    val cycles = startingPositions.map { startingPosition ->
        followInstructions(startingPosition, { it.endsWith("Z") }, instructions, network)
    }
    return leastCommonMultiple(cycles)

}

fun createNetwork(input: List<String>): Map<String, Pair<String, String>> {
    return input
        .drop(2).associate {
            val from = it.substringBefore("=").trim()
            val destinations = it
                .substringAfter("(")
                .substringBefore(")")
                .split(",")
                .let { (left, right) -> Pair(left.trim(), right.trim()) }
            from to destinations
        }
}

fun followInstructions(start: String, endCondition: Predicate<String>, instructions: String, network: Map<String, Pair<String, String>>): Long {
    return infiniteSequence(instructions)
        .flatMap { it.toList() }
        .runningFold(start) { location, direction ->
            when (direction) {
                'L' -> network[location]!!.first
                'R' -> network[location]!!.second
                else -> throw IllegalArgumentException("Unknown direction : $direction")
            }
        }
        .takeWhile { !endCondition.test(it) }
        .count().toLong()
}

fun greatestCommonDivisor(first: Long, second: Long): Long {
    if(first == second) return first

    val min = minOf(first, second)
    val max = maxOf(first, second)
    return greatestCommonDivisor(min, max - min)
}






