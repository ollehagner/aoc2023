package day10

import common.*
import common.Direction.*

fun main() {
    val testinput = readInput("day10/testinput")
    val input = readInput("day10/input")

    part1(input).println()
}

fun part1(input: List<String>): Int {
    return pointsInLoop(input).size / 2
}

fun pointsInLoop(input: List<String>): Set<Point> {
    val grid: Grid<Pipe> = Grid(input) { position, char -> Pipe.create(position, char) }
    val start = findStart(input)

    val pipesConnectingToStart = grid
        .values()
        .filter { pipe -> pipe.connections().contains(start) }

    var current = pipesConnectingToStart.first().position
    var previous = start
    var pointsInLoop = mutableSetOf(previous)
    do {
        var next = grid.valueOf(current).next(previous)
        pointsInLoop.add(current)
        previous = current
        current = next
    } while(current != start)
    return pointsInLoop
}

fun findStart(input: List<String>): Point {
    return input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull() { x, char ->
            if(char == 'S') Point(x, y) else null
        }
    }.first()
}

data class Pipe(val position: Point, val directions: Set<Direction>) {

    fun connections(): Set<Point> {
        return directions.map { direction -> position.move(direction) }.toSet()
    }

    fun next(from: Point): Point {
        return (connections() - setOf(from)).first()
    }

    companion object {
        fun create(position: Point, char: Char): Pipe? {
            return when(char) {
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