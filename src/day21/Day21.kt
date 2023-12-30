package day21

import common.Point
import common.printMatrix
import common.println
import common.readInput

const val ROCK = '#'
const val GARDEN = '.'
const val VALID = '0'
const val INVALID = 'X'

fun main() {
    val testinput = readInput("day21/testinput")
    val input = readInput("day21/input")
    part1(testinput, 10).println()
}

fun part1(input: List<String>, maxSteps: Int): Int {
    val grid = input.map { row -> row.toCharArray() }.toTypedArray()
    val start = grid.indices.flatMap { y ->
        grid[0].indices.map { x ->
            Point(x, y)
        }
    }.first { grid.get(it) == 'S' }

    val toExplore = mutableListOf(Pair(start, 0))
    grid.set(start, VALID)
    while(toExplore.isNotEmpty() && toExplore.any { it.second <= maxSteps }) {
        val current = toExplore.removeFirst()
        val nextPoints = current.first.cardinalNeighbors()
            .filter { grid.isValid(it) }
            .map { Pair(it, current.second + 1) }
        nextPoints.forEach { point -> if(point.second.mod(2) == 0) grid.set(point.first, VALID) else grid.set(point.first, INVALID) }
        nextPoints
            .forEach { toExplore.add(it) }
    }
//    grid.printMatrix()
    return grid.sumOf { chars -> chars.count { it == VALID } }
}

fun Array<CharArray>.get(point: Point): Char {
    return this[point.y][point.x]
}

fun Array<CharArray>.set(point: Point, value: Char) {
    this[point.y][point.x] = value
}

fun Array<CharArray>.isValid(point: Point): Boolean {
    return point.y in indices && point.x in this[0].indices && this.get(point) == GARDEN
}