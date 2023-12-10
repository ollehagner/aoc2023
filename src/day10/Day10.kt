package day10

import common.*
import common.Direction.*

fun main() {
    val testinput = readInput("day10/testinput")
    val input = readInput("day10/input")

    part1(input).println()
}

fun part1(input: List<String>): Int {
    val grid: Grid<Pipe> = Grid(input) { char -> Pipe.parse(char) }
    val start = findStart(input)

    val connectingToStart = grid
        .entries()
        .filter { (point, pipe) -> pipe.connections(point).contains(start) }
        .map { it.key }

    var current = connectingToStart.first()
    var previous = start
    var steps = 1
    do {
        var next = grid.valueOf(current).next(previous, current)
        previous = current
        current = next
        steps++
    } while(current != start)
    return steps / 2
}



fun findStart(input: List<String>): Point {
    return input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull() { x, char ->
            if(char == 'S') Point(x, y) else null
        }
    }.first()
}

data class Pipe(val directions: Set<Direction>) {

    fun connections(position: Point): Set<Point> {
        return directions.map { direction -> position.move(direction) }.toSet()
    }

    fun next(from: Point, position: Point): Point {
        return (connections(position) - setOf(from)).first()
    }

    companion object {
        fun parse(char: Char): Pipe? {
            return when(char) {
                '|' -> Pipe(setOf(UP, DOWN))
                '-' -> Pipe(setOf(LEFT, RIGHT))
                'L' -> Pipe(setOf(DOWN, RIGHT))
                'J' -> Pipe(setOf(DOWN, LEFT))
                '7' -> Pipe(setOf(UP, LEFT))
                'F' -> Pipe(setOf(UP, RIGHT))
                else -> null
            }
        }
    }
}