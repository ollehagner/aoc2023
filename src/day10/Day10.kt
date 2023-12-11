package day10

import common.*
import common.Direction.*

fun main() {
    val testinput = readInput("day10/testinput4")
    val input = readInput("day10/input")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    return pointsInLoop(input).size / 2
}

fun part2(input: List<String>): Int {
    val pipesInLoop = pointsInLoop(input)

    val gridWithLoopPipesOnly = Grid(pipesInLoop.associateBy { it.position })

    val upperLeft = gridWithLoopPipesOnly.min()
    val lowerRight = gridWithLoopPipesOnly.max()
    val pointsToCheck = (upperLeft.x + 1..<lowerRight.x).flatMap { x ->
        (upperLeft.y + 1..<lowerRight.y).map { y ->
            Point(x, y)
        }
    }.toSet() - pipesInLoop.map { it.position }.toSet()

    return pointsToCheck.count { pointToCheck ->
        setOf(UP, DOWN, LEFT, RIGHT).all { direction ->
            isContained(
                pointToCheck,
                direction,
                gridWithLoopPipesOnly
            )
        }
    }
}

fun isContained(point: Point, direction: Direction, grid: Grid<Pipe>): Boolean {
    val complementDirections = if(direction in setOf(UP, DOWN)) setOf(LEFT, RIGHT) else setOf(UP, DOWN)
    val directionList = Point.sequence(point, direction).takeWhile { grid.withinBounds(it) }
        .flatMap { grid.maybeValue(it)?.directions ?: setOf() }
        .filter { complementDirections.contains(it) }
        .groupingBy { it }.eachCount().values
        return directionList.any { it % 2 != 0 }
}

fun pointsInLoop(input: List<String>): Set<Pipe> {
    val grid: Grid<Pipe> = Grid(input) { position, char -> Pipe.create(position, char) }
    val startPosition = findStart(input)

    val pipesConnectingToStart = grid
        .values()
        .filter { pipe -> pipe.connections().contains(startPosition) }
    val startDirections = pipesConnectingToStart.map { it.position }.map { startPosition.directionToNeighbor(it) }.toSet()
    val start = Pipe(startPosition, startDirections)
    grid.set(startPosition, start)

    var current = pipesConnectingToStart.first()
    var previous = start
    var pointsInLoop = mutableSetOf(previous)
    do {
        var next = grid.valueOf(current.next(previous))
        pointsInLoop.add(current)
        previous = current
        current = next
    } while (current != start)
    return pointsInLoop
}

fun findStart(input: List<String>): Point {
    return input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, char ->
            if (char == 'S') Point(x, y) else null
        }
    }.first()
}

data class Pipe(val position: Point, val directions: Set<Direction>) {

    fun connections(): Set<Point> {
        return directions.map { direction -> position.move(direction) }.toSet()
    }

    fun next(from: Pipe): Point {
        return (connections() - setOf(from.position)).first()
    }

    override fun toString(): String {
        return "${directionsToCharMap[directions]}"
    }

    companion object {

        val charToDirectionsMap = mapOf(
            '|' to setOf(UP, DOWN),
            '-' to setOf(LEFT, RIGHT),
            'L' to setOf(DOWN, RIGHT),
            'J' to setOf(DOWN, LEFT),
            '7' to setOf(UP, LEFT),
            'F' to setOf(UP, RIGHT)
        )

        val directionsToCharMap = charToDirectionsMap.entries.associate { (key, value) -> value to key }

        fun create(position: Point, char: Char): Pipe? {
            return when (char) {
                '|' -> Pipe(position, setOf(UP, DOWN))
                '-' -> Pipe(position, setOf(LEFT, RIGHT))
                'L' -> Pipe(position, setOf(DOWN, RIGHT))
                'J' -> Pipe(position, setOf(DOWN, LEFT))
                '7' -> Pipe(position, setOf(UP, LEFT))
                'F' -> Pipe(position, setOf(UP, RIGHT))
                else -> null
            }
        }
    }

}