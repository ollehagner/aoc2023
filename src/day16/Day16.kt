package day16

import common.*
import common.Direction.*

fun main() {
    val testinput = readInput("day16/testinput")
    val input = readInput("day16/input")
//    part1(input).println()
    part2(input).println()
}

var seen = mutableSetOf<Beam>()

fun part1(input: List<String>): Int {
    val grid = input.map { row -> row.map { BeamManipulator.parse(it) }.toTypedArray() }.toTypedArray()
    val startBeam = Beam(Point(0, 0), RIGHT)

    val path = Path(startBeam)

    explorePaths(path, grid)
    return seen.map { it.point }.toSet().size
}

fun part2(input: List<String>): Int {
    val grid = input.map { row -> row.map { BeamManipulator.parse(it) }.toTypedArray() }.toTypedArray()

    val startBeams = listOf(
            grid.indices.map { Beam(Point(0, it), RIGHT) }.toList(),
            grid.indices.map { Beam(Point(grid.first().indices.last, it), LEFT) }.toList(),
            grid.first().indices.map { Beam(Point(it, 0), UP) }.toList(),
            grid.first().indices.map { Beam(Point(it, grid.indices.last), DOWN) }.toList()
        ).flatten()
    return startBeams
        .mapIndexed { index, startBeam ->
            seen = mutableSetOf()
            explorePaths(Path(startBeam), grid)
            println("${index + 1} of ${startBeams.size} complete")
            seen.map { it.point }.toSet().size
        }.max()
}

fun explorePaths(path: Path, grid: Array<Array<BeamManipulator>>): Set<Beam> {
    val nextPaths = grid.next(path)

    if(nextPaths.isEmpty()) {
        return path.steps
    }
    nextPaths.forEach { seen.add(it.last()) }
    return nextPaths
        .flatMap { explorePaths(it, grid) }
        .toSet()
}

data class Path(val steps: Set<Beam>) {
    constructor(start: Beam) : this(setOf(start)) {
    }

    fun add(step: Beam): Path {
        return Path(steps + step)
    }

    fun last(): Beam {
        return steps.last()
    }
}

data class Beam(val point: Point, val direction: Direction) {
    override fun toString(): String {
        return "($point, $direction)"
    }
}

enum class BeamManipulator {

    LEFT_UP { // '/'
        override fun next(beam: Beam): Set<Beam> {
            return when(beam.direction) {
                UP -> setOf(Beam(beam.point.move(LEFT), LEFT))
                DOWN -> setOf(Beam(beam.point.move(RIGHT), RIGHT))
                RIGHT -> setOf(Beam(beam.point.move(DOWN), DOWN))
                LEFT -> setOf(Beam(beam.point.move(UP), UP))
                else -> throw IllegalArgumentException("Invalid direction ${beam.direction}")
            }
        }
    },
    RIGHT_UP { // '\'
        override fun next(beam: Beam): Set<Beam> {
            return when(beam.direction) {
                UP -> setOf(Beam(beam.point.move(RIGHT), RIGHT))
                DOWN -> setOf(Beam(beam.point.move(LEFT), LEFT))
                RIGHT -> setOf(Beam(beam.point.move(UP), UP))
                LEFT -> setOf(Beam(beam.point.move(DOWN), DOWN))
                else -> throw IllegalArgumentException("Invalid direction ${beam.direction}")
            }
        }
    },
    HORIZONTAL_SPLITTER { // '-'
        override fun next(beam: Beam): Set<Beam> {
            return when(beam.direction) {
                UP, DOWN -> setOf(Beam(beam.point.move(RIGHT), RIGHT), Beam(beam.point.move(LEFT), LEFT))
                RIGHT, LEFT -> setOf(Beam(beam.point.move(beam.direction), beam.direction))
                else -> throw IllegalArgumentException("Invalid direction ${beam.direction}")
            }
        }
    },
    VERTICAL_SPLITTER { // '|'
        override fun next(beam: Beam): Set<Beam> {
            return when(beam.direction) {
                UP, DOWN -> setOf(Beam(beam.point.move(beam.direction), beam.direction))
                RIGHT, LEFT -> setOf(Beam(beam.point.move(UP), UP), Beam(beam.point.move(DOWN), DOWN))
                else -> throw IllegalArgumentException("Invalid direction ${beam.direction}")
            }
        }
    },
    EMPTY {
        override fun next(beam: Beam): Set<Beam> {
            return setOf(beam.copy(point = beam.point.move(beam.direction)))
        }

    };

    abstract fun next(beam: Beam): Set<Beam>

    companion object {
        fun parse(char: Char): BeamManipulator {
            return when(char) {
                '/' -> LEFT_UP
                '\\' -> RIGHT_UP
                '-' -> HORIZONTAL_SPLITTER
                '|' -> VERTICAL_SPLITTER
                '.' -> EMPTY
                else -> throw IllegalArgumentException("Unknown value : $char")
            }
        }
    }
}

inline fun Array<Array<BeamManipulator>>.get(point: Point): BeamManipulator {
    return this[point.y][point.x]!!
}

inline fun Array<Array<BeamManipulator>>.isValid(beam: Beam): Boolean {
    return indices.contains(beam.point.y) && first().indices.contains(beam.point.x)
}

inline fun Array<Array<BeamManipulator>>.next(path: Path): Set<Path> {
    return get(path.last().point).next(path.last())
        .filter { isValid(it) }
        .filter { !seen.contains(it) }
        .map { path.add(it) }.toSet()
}