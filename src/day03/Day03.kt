package day03

import common.Grid
import common.Point
import common.println
import common.readInput

const val EMPTY = '.'
const val GEAR = '*'

fun main() {

    val testInput = readInput("day03/Day03_sample")
        .map { it.toList() }

    val input = readInput("day03/input")
        .map { it.toList() }

    part1(testInput).println()
//    part2(input).println()

}

fun part1(input: List<List<Char>>): Int {
    val grid = Grid(input)
    val foundParts = findEngineParts(grid)
    foundParts
        .groupingBy { it.enginePartNumber }.eachCount()
        .toSortedMap()
        .forEach{ (engineNumber, count) -> println("$engineNumber : $count")}
    return foundParts
        .sumOf { verifiedEnginePart -> verifiedEnginePart.enginePartNumber }

}

fun part2(input: List<List<Char>>): Int {
    val grid = Grid(input)
    val engineParts = findEngineParts(grid)
    val gears = grid.entries().filter { it.value == GEAR }

    return gears.map { it.key.neighbors() }
        .map { gearNeighbors ->
            engineParts.filter { enginePart ->
                enginePart.positions.intersect(gearNeighbors).isNotEmpty()
            }
        }
        .filter { it.size == 2 }
        .sumOf { it.first().enginePartNumber * it.last().enginePartNumber }

}

fun findEngineParts(grid: Grid<Char>): List<EnginePart> {
    return grid.entries()
        .fold(mutableListOf(mutableListOf<Pair<Point, Char>>())) { acc, (point, value) ->
            if (!value.isDigit() && acc.last().isNotEmpty()) {
                acc.add(mutableListOf())
            } else if (value.isDigit()) {
                acc.last().add(Pair(point, value))
            }
            acc
        }
        .filter { it.isNotEmpty() }
        .map { EnginePart.fromData(it) }
        .filter { possibleEnginePart -> hasAdjacentSymbol(possibleEnginePart.neighbors(), grid) }

}

fun hasAdjacentSymbol(positions: Set<Point>, grid: Grid<Char>): Boolean {
    return positions
        .map { grid.valueOrDefault(it, EMPTY)  }
        .any { value -> value != EMPTY && !value.isDigit() }

}


data class EnginePart(val enginePartNumber: Int, val positions: Set<Point>) {

    fun neighbors(): Set<Point> {
        return this.positions
            .flatMap { it.neighbors() }
            .filter { !positions.contains(it) }
            .toSet()
    }

    override fun toString(): String {
        return "$enginePartNumber | $positions"
    }

    companion object {
        fun fromData(data: List<Pair<Point, Char>>): EnginePart {
            val enginePartNumber = data.map { it.second }.joinToString("").toInt()
            val positions = data.map { it.first }.toSet()
            return EnginePart(enginePartNumber, positions)
        }
    }

}