package day14

import common.*
import common.Direction.*
import java.math.BigDecimal
import java.math.RoundingMode

const val BALL = 'O'
const val EMPTY = '.'

fun main() {
    val testinput = readInput("day14/testinput")
    val input = readInput("day14/input")
    part1(input).println()
    part2(input).println()
}

val comparators = mapOf<Direction, Comparator<Map.Entry<Point, Char>>>(
    UP to compareByDescending { it.key.y },
    DOWN to compareBy { it.key.y },
    RIGHT to compareByDescending { it.key.x },
    LEFT to compareBy { it.key.x },

)


fun part1(input: List<String>): Int {
    val grid = Grid(input.reversed().map { line -> line.split("").filter { it.isNotEmpty() }.map { it.trim().single() }})
    grid.entries()
        .filter { it.value == BALL }
        .sortedByDescending { it.key.y }
        .forEach { move(it.key, UP, grid) }
    return grid.entries()
        .filter { it.value == BALL }
        .sumOf { (it.key.y + 1) }
}

fun part2(input: List<String>): Int {
    val totalCycles = 1_000_000_000

    val grid =
        Grid(input.reversed().map { line -> line.split("").filter { it.isNotEmpty() }.map { it.trim().single() } })

    val values = infiniteSequence(1)
        .map {
            listOf(UP, LEFT, DOWN, RIGHT)
                .forEach { direction -> performRound(direction, comparators[direction]!!, grid) }
            grid.entries()
                .filter { it.value == BALL }
                .sumOf { (it.key.y + 1) }
        }
        .take(10000)
        .toList()

    val valueMap = values
        .groupingBy { it }.eachCount()

    val min = valueMap
        .filter { it.value > 100 }
        .minOf { it.value }

    val cycleLength = valueMap
        .values.sumOf { BigDecimal(it).divide(BigDecimal(min), RoundingMode.HALF_DOWN).toLong() }

    val offset = values.windowed(cycleLength.toInt() * 2)
        .takeWhile { it.take(cycleLength.toInt()) != it.drop(cycleLength.toInt()) }
        .count() + 1

    val noInCycleAfterIterations = (totalCycles - offset) % cycleLength
    return values[offset + noInCycleAfterIterations.toInt() - 1]
}

fun performRound(direction: Direction, comparator: Comparator<Map.Entry<Point, Char>>, grid: Grid<Char>) {
    grid.entries()
        .filter { it.value == BALL }
        .sortedWith(comparator)
        .forEach { move(it.key, direction, grid) }

}

fun move(ballAt: Point, direction: Direction, grid: Grid<Char>) {
    val nextPosition = Point.sequence(ballAt, direction)
        .takeWhile { point -> point == ballAt || (grid.hasValue(point) && grid.valueOf(point) == EMPTY) }
        .last()
    grid.set(ballAt, EMPTY)
    grid.set(nextPosition, BALL)
}



