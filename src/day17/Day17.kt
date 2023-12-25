package day17

import common.*
import common.Direction.*
import java.util.PriorityQueue

fun main() {
    val testinput = readInput("day17/testinput")
    val testinput2 = readInput("day17/testinput2")
    val input = readInput("day17/input")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    return lowestHeatLoss(input, 3, 1)
}

fun part2(input: List<String>): Int {
    return lowestHeatLoss(input, 10, 4)
}

fun lowestHeatLoss(input: List<String>, maxStraight: Int, straightMovesBeforeTurn: Int): Int {
    val grid = Grid(input.map { row -> row.map { it.digitToInt() } })

    val seen = mutableSetOf<State>()

    val goal = grid.max()
    val pathsToExplore = PriorityQueue<Path>() { a, b -> a.totalHeatLoss.compareTo(b.totalHeatLoss) }
    val start = Path(setOf(Point(0, 0)), 0, maxStraight, straightMovesBeforeTurn)
    listOf(RIGHT, UP)
        .map { start.currentPosition().move(it) }
        .map { start.move(it, grid.valueOf(it)) }
        .forEach {
            pathsToExplore.add(it)
            seen.add(it.toState())
        }


    while (pathsToExplore.isNotEmpty()) {
        val current = pathsToExplore.poll()
        current.possibleDestinations().filter { grid.hasValue(it) }.map { current.move(it, grid.valueOf(it)) }
            .filter { it.toState() !in seen }.forEach { path ->
                if (path.currentPosition() == goal) {
                    if (path.movesInCurrentDirection() >= path.straightMovesBeforeTurn) {
                        return path.totalHeatLoss
                        Grid(path.lastPositions.associateWith { grid.valueOrDefault(it, 0) }).println()
                    }
                } else {
                    pathsToExplore.add(path)
                    seen.add(path.toState())
                }
            }
    }

    return 0
}

data class State(val position: Point, val direction: Direction, val steps: Int) {}

data class Path(
    val lastPositions: Set<Point>, val totalHeatLoss: Int, val maxStraight: Int, val straightMovesBeforeTurn: Int
) {

    fun toState(): State {
        return State(currentPosition(), currentDirection(), movesInCurrentDirection())
    }

    fun move(to: Point, heatLoss: Int): Path {
        val newPositions = if(lastPositions.size == maxStraight + 1) (lastPositions.drop(1) + to).toSet() else (lastPositions + to).toSet()
        return copy(lastPositions = newPositions + to, totalHeatLoss = totalHeatLoss + heatLoss)
    }

    fun possibleDestinations(): Set<Point> {
        val currentDirection = currentDirection()
        return when {
            movesInCurrentDirection() < straightMovesBeforeTurn -> setOf(currentPosition().move(currentDirection))
            movesInCurrentDirection() in straightMovesBeforeTurn..<maxStraight -> currentPosition().cardinalNeighbors()
                .filter { it !in lastPositions }.toSet()

            else -> currentPosition().cardinalNeighbors().filter { it !in lastPositions }
                .toSet() - currentPosition().move(currentDirection)
        }

    }

    private fun currentDirection(): Direction {
        return lastPositions.windowed(2).map { (first, second) -> first to second }.last()
            .let { (secondToLast, last) -> secondToLast.directionToNeighbor(last) }
    }

    fun movesInCurrentDirection(): Int {
        val currentDirection = currentDirection()
        return lastPositions.reversed().windowed(2).map { (next, previous) -> previous.directionToNeighbor(next) }
            .takeWhile { it == currentDirection }.count()
    }

    fun currentPosition(): Point {
        return lastPositions.last()
    }

}

