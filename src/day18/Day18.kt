package day18

import common.*

const val HOLE = '#'

fun main() {
    val testinput = readInput("day18/testinput").map { Instruction.parse(it) }
    val input = readInput("day18/input").map { Instruction.parse(it) }
    part1(input).println()
}

private fun part1(instructions: List<Instruction>): Int { 
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

private fun findInner(grid: Grid<Char>): Point {
    val firstTwo = grid.allPoints()
        .groupBy { it.y }
        .values
    return firstTwo.map { row -> row.sortedBy { it.x }.take(2) }
        .first { (first, second) -> second.x > first.x + 1 }
        .let { (first, _) -> first.copy( x = first.x + 1) }
}

private fun floodFill(point: Point, grid: Grid<Char>) {
    val toCheck = ArrayDeque<Point>().apply { add(point) }
    while(toCheck.isNotEmpty()) {
        val current = toCheck.removeFirst()
        if(!grid.hasValue(current)) {
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
            val direction = when(string[0]) {
                'R' -> Direction.RIGHT
                'L' -> Direction.LEFT
                'D' -> Direction.DOWN
                'U' -> Direction.UP
                else -> throw IllegalArgumentException("Unknown direction ${string[0]}")
            }
            return Instruction(direction, string.substringAfter(" ").substringBefore(" ").toInt())
        }
    }
}