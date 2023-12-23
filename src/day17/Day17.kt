package day17

import common.*
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
    val start = Point(0, 0)

    val lowestCosts = mutableMapOf<Set<Point>, Int>()

    val goal = grid.max()
    val pathsToExplore = PriorityQueue<Path>() { a, b -> a.totalHeatLoss.compareTo(b.totalHeatLoss)}

    pathsToExplore.add(Path(setOf(Point(-1,0), start), 0, maxStraight, straightMovesBeforeTurn))
    pathsToExplore.add(Path(setOf(Point(0,-1), start), 0, maxStraight, straightMovesBeforeTurn))
    var minCost = Int.MAX_VALUE
    while(pathsToExplore.isNotEmpty()) {
        val current = pathsToExplore.poll()
        current
            .possibleDestinations()
            .filter { grid.hasValue(it) }
            .map { current.move(it, grid.valueOf(it)) }
            .forEach {path ->
                if(path.currentPosition() == goal) {
                    if (path.movesInCurrentDirection() >= path.straightMovesBeforeTurn) {
                        minCost = minOf(path.totalHeatLoss, minCost)
                        println("New mincost $minCost")
                        val total = path.lastPositions.drop(2).sumOf { grid.valueOrDefault(it, 0) }
                        println("Path total = $total")
                        Grid(path.lastPositions.associateWith { grid.valueOrDefault(it, 0) }).println()
                    }
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

    fun move(to: Point, heatLoss: Int): Path {
//        val newPositions = if(lastPositions.size == maxStraight + 1) (lastPositions.drop(1) + to).toSet() else (lastPositions + to).toSet()
        return copy(lastPositions = lastPositions + to, totalHeatLoss = totalHeatLoss + heatLoss)
    }

    fun possibleDestinations(): Set<Point> {
        val currentDirection = currentDirection()
        return when {
            movesInCurrentDirection() < straightMovesBeforeTurn -> setOf(currentPosition().move(currentDirection))
            movesInCurrentDirection() in straightMovesBeforeTurn..<maxStraight ->
                currentPosition().cardinalNeighbors().filter { it !in lastPositions }.toSet()
            else -> currentPosition().cardinalNeighbors().filter { it !in lastPositions }.toSet() - currentPosition().move(currentDirection)
        }

    }

    private fun currentDirection(): Direction {
        return lastPositions.windowed(2)
            .map { (first, second) -> first to second }
            .last().let { (secondToLast, last) -> secondToLast.directionToNeighbor(last) }
    }

    fun movesInCurrentDirection(): Int {
        val currentDirection = currentDirection()
        return lastPositions
            .reversed()
            .windowed(2)
            .map { (next, previous) -> previous.directionToNeighbor(next) }
            .takeWhile { it == currentDirection }
            .count()
    }

    fun currentPosition(): Point {
        return lastPositions.last()
    }

    fun lastMaxStraightPositions(): Set<Point> {
        if(lastPositions.size < maxStraight + 1) return lastPositions
        return lastPositions
            .windowed(maxStraight + 1, 1, false)
            .last().toSet()
    }

}

