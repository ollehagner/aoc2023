package day02

import common.*;
import java.lang.IllegalArgumentException

fun main() {

    val testInput = readInput("day02/testinput")
    val input = readInput("day02/input")
    part1(input).println()

    part2(input).println()

}

fun part1(input: List<String>): Int {
    val games = input
        .map { Game.parse(it) }

    val existingCubes = listOf(Cubes(Color.RED, 12), Cubes(Color.GREEN, 13), Cubes(Color.BLUE, 14))
    return games
        .filter { it.isPossible(existingCubes) }
        .sumOf { it.number }
}

fun part2(input: List<String>): Int {
    val games = input
        .map { Game.parse(it) }

    return games.sumOf {
        game ->
        Color.values()
            .map { game.max(it) }
            .reduce { acc, value -> acc * value }
    }

}

enum class Color {
    BLUE, GREEN, RED
}

class Cubes(val color: Color, val count: Int, val round: Int = 0) {
}

class Game(val number: Int, private val cubes: List<Cubes>) {

    fun isPossible(existing: List<Cubes>): Boolean {
        return existing
            .all { it.count >= max(it.color) }
    }

    fun max(color: Color): Int {
        return cubes
            .filter { it.color == color }
            .maxOf { it.count }
            .or(0)
    }

    companion object {
        fun parse(gameAsString: String): Game {
            val gameNumber = gameAsString.substringAfter("Game ").substringBefore(":").toInt()
            val cubes = gameAsString.substringAfter(": ")
                .split(";")
                .flatMapIndexed { round, cubeInfo -> parseCubeInfo(round, cubeInfo) }
            return Game(gameNumber, cubes)
        }

        private fun parseCubeInfo(round: Int, cubeInfo: String): List<Cubes> {
            return cubeInfo.split(",")
                .map { it.trim() }
                .map {
                    val count = it.substringBefore(" ").toInt()
                    val color = it.substringAfter(" ")
                        .let { colorAsString ->
                            when(colorAsString) {
                                "blue" -> Color.BLUE
                                "red" -> Color.RED
                                "green" -> Color.GREEN
                                else -> throw IllegalArgumentException("Unknown color $colorAsString")
                        } }
                    Cubes(color, count, round)
                }
        }
    }

    override fun toString(): String {
        return "Game(number=$number, cubes=$cubes)"
    }
}