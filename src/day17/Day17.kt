package day17

import common.*
import java.util.PriorityQueue
import kotlin.time.measureTime

fun main() {
    val testinput = readInput("day17/testinput")
    val input = readInput("day17/input")
//    part1(input).println()
    measureTime {
        part2(testinput).println()
    }.let { println("Completed in ${it.inWholeMilliseconds} ms") }

}

fun part1(input: List<String>): Int {
    return lowestHeatLoss(input, 4, 1)
}

fun part2(input: List<String>): Int {
    return lowestHeatLoss(input, 10, 4)
}

fun lowestHeatLoss(input: List<String>, maxStraight: Int, straightMovesBeforeTurn: Int): Int {
    val grid = Grid(input.map { row -> row.map { it.digitToInt() } })
    val start = Point(0, 0)
    val path = Path(setOf(start, Point(1,0)), 0, maxStraight, straightMovesBeforeTurn)

    val lowestCosts = mutableMapOf<Set<Point>, Int>()

    val goal = grid.max()
//    val pathsToExplore = PriorityQueue<Path>() { a, b -> a.currentPosition().manhattanDistance(goal).compareTo(b.currentPosition().manhattanDistance(goal))}
    val pathsToExplore = PriorityQueue<Path>() { a, b -> a.totalHeatLoss.compareTo(b.totalHeatLoss)}

//    val pathsToExplore = mutableListOf(path)
    pathsToExplore.add(path)
    var minCost = Int.MAX_VALUE
    while(pathsToExplore.isNotEmpty()) {
        val current = pathsToExplore.poll()
        current
            .possibleDestinations()
            .filter { grid.hasValue(it) }
            .filter { it !in path.lastPositions  }
            .map { current.move(it, grid.valueOf(it)) }
            .forEach {path ->
                if(path.currentPosition() == goal &&
                    (path.consecutiveHorizontalMoves() >= path.straightMovesBeforeTurn || path.consecutiveVerticalMoves() >= path.straightMovesBeforeTurn)) {
                    minCost = minOf(path.totalHeatLoss, minCost)
                } else if(path.totalHeatLoss < lowestCosts.getOrDefault(path.lastMaxStraightPositions(), Int.MAX_VALUE)) {
                    pathsToExplore.add(path)
                    lowestCosts[path.lastMaxStraightPositions()] = path.totalHeatLoss
                }
            }
        pathsToExplore.removeIf { it.totalHeatLoss >= minCost }
    }
    return minCost
}

data class Path(val lastPositions: Set<Point>, val totalHeatLoss: Int, val maxStraight: Int, val straightMovesBeforeTurn: Int) {
    init {
        assert(lastPositions.isNotEmpty())
    }

    fun move(to: Point, heatLoss: Int): Path {
        val newPositions = if(lastPositions.size == maxStraight) (lastPositions.drop(1) + to).toSet() else (lastPositions + to).toSet()
        return copy(lastPositions = newPositions, totalHeatLoss = totalHeatLoss + heatLoss)
    }

    fun possibleDestinations(): Set<Point> {
        return lastPositions.last().cardinalNeighbors()
            .filter { isAllowed(it) }
            .toSet()
    }

    fun currentPosition(): Point {
        return lastPositions.last()
    }

    fun lastMaxStraightPositions(): Set<Point> {
        if(lastPositions.size < maxStraight) return lastPositions
        return lastPositions
            .windowed(maxStraight, 1, false)
            .last().toSet()
    }

    private fun isAllowed(destination: Point): Boolean {
        return if(maxVerticalMovesStraight()) {
            destination.x != currentPosition().x && destination !in lastPositions
        } else if(maxHorizontalMovesStraight()) {
            destination.y != currentPosition().y && destination !in lastPositions
        } else if(destination.y != currentPosition().y && consecutiveHorizontalMoves() < straightMovesBeforeTurn) {
            false
        } else if(destination.x != currentPosition().x && consecutiveVerticalMoves() < straightMovesBeforeTurn) {
            false
        } else {
            true
        }
    }

    fun consecutiveHorizontalMoves(): Int {
        return lastMaxStraightPositions().reversed().takeWhile { it.y == currentPosition().y }.count()
    }

    fun consecutiveVerticalMoves(): Int {
        return lastMaxStraightPositions().reversed().takeWhile { it.x == currentPosition().x }.count()
    }

    private fun maxVerticalMovesStraight(): Boolean {
        return lastPositions.size >= maxStraight && lastPositions.toList().takeLast(maxStraight).map { it.x }.toSet().size == 1
    }

    private fun maxHorizontalMovesStraight(): Boolean {
        return lastPositions.size >= maxStraight && lastPositions.toList().takeLast(maxStraight).map { it.y }.toSet().size == 1
    }

}

