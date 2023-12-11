package day11

import common.Point
import common.println
import common.readInput

const val GALAXY = '#'

fun main() {
    val testinput = readInput("day11/testinput").map { it.toCharArray() }.toTypedArray()

    val input = readInput("day11/input").map { it.toCharArray() }.toTypedArray()

    part1(input).println()
    part2(input).println()
}

fun part1(universe: Array<CharArray>): Int {
    val expandedUniverse = expand(universe)
    val galaxies = expandedUniverse
        .flatMapIndexed { y, row ->
            row.mapIndexed { x, char ->
                if (char == GALAXY) Point(x, y) else null
            }
        }
        .filterNotNull()
    return combineAll(galaxies).sumOf { (from, to) -> from.manhattanDistance(to) }

}

fun part2(universe: Array<CharArray>): Long {
    val emptyRows = findEmptyRows(universe)
    val emptyColumns = findEmptyRows(universe.columns())
    val galaxies = universe
        .flatMapIndexed { y, row ->
            row.mapIndexed { x, char ->
                if (char == GALAXY) Point(x, y) else null
            }
        }
        .filterNotNull()
    return combineAll(galaxies)
        .sumOf { manhattanDistanceWithExpandedUniverse(it.first, it.second, emptyRows, emptyColumns) }
}

fun manhattanDistanceWithExpandedUniverse(from: Point, to: Point, emptyRows: Set<Int>, emptyColumns: Set<Int>): Long {
    val horizontalRange = (minOf(from.x, to.x)..maxOf(from.x, to.x))
    val verticalRange = (minOf(from.y, to.y)..maxOf(from.y, to.y))
    val numOfEmptyRowsCrossed = emptyRows.count { verticalRange.contains(it) }
    val numOfEmptyColumnsCrossed = emptyColumns.count { horizontalRange.contains(it) }
    return from.manhattanDistance(to).toLong() +
            (numOfEmptyRowsCrossed * 1_000_000 - numOfEmptyRowsCrossed) +
            (numOfEmptyColumnsCrossed * 1_000_000 - numOfEmptyColumnsCrossed)
}

fun findEmptyRows(universe: Array<CharArray>): Set<Int> {
    return universe.mapIndexed { y, row ->
        Pair(y, !row.contains(GALAXY))
    }.filter { it.second }.map { it.first }.toSet()
}

fun combineAll(elements: List<Point>): List<Pair<Point, Point>> {
    return elements
        .flatMapIndexed { index, point ->
            (index + 1..<elements.size)
                .map { combineWithIndex -> Pair(point, elements[combineWithIndex]) }
        }
}

fun expand(universe: Array<CharArray>): Array<CharArray> {
    return universe.flatMap { row ->
        if (row.contains(GALAXY)) listOf(row) else listOf(row, row)
    }
        .toTypedArray()
        .rotateClockwise()
        .flatMap { column ->
            if (column.contains(GALAXY)) listOf(column) else listOf(column, column)
        }.toTypedArray()
        .rotateCounterClockwise()
}

fun Array<CharArray>.rotateClockwise(): Array<CharArray> {
    return columns().map { it.reversed().toCharArray() }.toTypedArray()
}

fun Array<CharArray>.rotateCounterClockwise(): Array<CharArray> {
    return columns().reversedArray()
}

fun Array<CharArray>.columns(): Array<CharArray> {
    val width = get(0).size
    val height = size
    return (0..<width).map { x ->
        (0..<height)
            .map { y ->
                get(y)[x]
            }.toCharArray()
    }.toTypedArray()
}

fun printMatrix(matrix: Array<CharArray>) {
    println(matrix.joinToString(System.lineSeparator()) { row -> row.joinToString(" ") })
}




