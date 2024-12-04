package day21

import common.*

const val GARDEN = '.'
const val VALID = '0'
const val INVALID = 'X'

fun main() {
    val testinput = readInput("day21/testinput")
    val input = readInput("day21/input")
    part1(input, 10).println()
    part2(input, 26501365).println()
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
    return grid.sumOf { chars -> chars.count { it == VALID } }
}


fun part2(input: List<String>, maxSteps: Int): Long {


    val grid = input.map { row -> row.toCharArray() }.toTypedArray()
    val start = grid.indices.flatMap { y ->
        grid[0].indices.map { x ->
            Point(x, y)
        }
    }.first { grid.get(it) == 'S' }

    val toExplore = mutableListOf(Pair(start, 0))
    val visited = mutableMapOf<Point, Int>()
    grid.set(start, GARDEN)
1
    visited[start] = 0
    while(toExplore.isNotEmpty() && toExplore.any { it.second <= maxSteps }) {
        val current = toExplore.removeFirst()
        val nextPoints = current.first.cardinalNeighbors()
            .filter { !visited.contains(it) }
            .filter { grid.isValid(it) }
            .map { Pair(it, current.second + 1) }
        nextPoints.forEach { visited[it.first] = it.second }
        nextPoints
            .forEach { toExplore.add(it) }
    }
    val even = visited.values.count { it.mod(2) == 0 }
    val evenCorners = visited.values.count { it.mod(2) == 0 && it > 65 }
    val odd = visited.values.count { it.mod(2) != 0 }
    val oddCorners = visited.values.count { it.mod(2) != 0 && it > 65 }

    val n = (maxSteps.toLong() - start.x) / grid.size
    val noOfEvenBlocks: Long = n * n
    val noOfOddBlocks: Long = (n + 1) * (n + 1)
    return (odd * noOfOddBlocks) + (even * noOfEvenBlocks) - ((n + 1) * oddCorners) + (n * evenCorners)

}

fun Array<CharArray>.getInfinite(point: Point): Char {
    val x = point.x.mod(this[0].size)
    val y = point.y.mod(this.size)
    return this[y][x]
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