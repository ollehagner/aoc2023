package day09

import common.println
import common.readInput

fun main() {
    val testinput = readInput("day09/testinput")
        .map { line -> line.split(" ").map { it.trim().toInt() } }
    val input = readInput("day09/input")
        .map { line -> line.split(" ").map { it.trim().toInt() } }

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<List<Int>>): Int {
    return input.sumOf { nextInSequence(it) }
}

fun part2(input: List<List<Int>>): Int {
    return input.sumOf { nextInSequence(it.reversed()) }
}

fun nextInSequence(dataset: List<Int>): Int {
    if (dataset.all { it == 0 }) return 0

    return dataset.last() + nextInSequence(
        dataset
            .windowed(2)
            .map { (first, second) -> second - first })
}

