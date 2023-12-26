package day18

import common.*
import common.Direction.*
import kotlin.math.abs

fun main() {
    val testinput = readInput("day18/testinput")
    val input = readInput("day18/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    val instructions = input.map { Instruction.parse(it) }
    val points = instructions
        .fold(listOf(Point(0, 0))) { digLocations, instruction ->
            digLocations + Point.sequence(digLocations.last(), instruction.direction)
                .drop(1)
                .take(instruction.length)
                .toSet()
        }.associateWith { '#' }
    val grid = Grid(points)

    val inner = findInner(grid)
    floodFill(inner, grid)
    grid.toStringInvertedVertical { it }.println()
    return grid.size()
}

private fun part2(input: List<String>): Long {
    val instructions = input.map { Instruction.parsePart2(it) }
    val innerArea = instructions
        .runningFold(Point(0, 0)) { previous, instruction ->
            previous.move(instruction.direction, instruction.length)
        }
        .zipWithNext()
        .sumOf { (a, b) -> (a.x.toLong() * b.y.toLong()) - (a.y.toLong() * b.x.toLong()) }
        .div(2)
    val outer = instructions.sumOf { it.length }
    return abs(innerArea) + (outer / 2) + 1
}

private fun findInner(grid: Grid<Char>): Point {
    val firstTwo = grid.allPoints()
        .groupBy { it.y }
        .values
    return firstTwo.map { row -> row.sortedBy { it.x }.take(2) }
        .first { (first, second) -> second.x > first.x + 1 }
        .let { (first, _) -> first.copy(x = first.x + 1) }
}

private fun floodFill(point: Point, grid: Grid<Char>) {
    val toCheck = ArrayDeque<Point>().apply { add(point) }
    while (toCheck.isNotEmpty()) {
        val current = toCheck.removeFirst()
        if (!grid.hasValue(current)) {
            grid.set(current, '#')
            current.neighbors()
                .filter { !grid.hasValue(it) }
                .forEach { toCheck.add(it) }
        }
    }

}


private data class Instruction(val direction: Direction, val length: Int) {
    companion object {
        fun parse(string: String): Instruction {
            val direction = when (string[0]) {
                'R' -> RIGHT
                'L' -> LEFT
                'D' -> DOWN
                'U' -> UP
                else -> throw IllegalArgumentException("Unknown direction ${string[0]}")
            }
            return Instruction(direction, string.substringAfter(" ").substringBefore(" ").toInt())
        }

        fun parsePart2(string: String): Instruction {
            //0 means R, 1 means D, 2 means L, and 3 means U
            val hex = string.substringAfter("(#").substringBefore(")")
            val length = hex.take(5).toInt(16)
            val direction = when(hex.last()) {
                '0' -> RIGHT
                '1' -> DOWN
                '2' -> LEFT
                '3' -> UP
                else -> throw IllegalArgumentException("Unknown direction ${hex.last()}")
            }
            return Instruction(direction, length)
        }
    }
}