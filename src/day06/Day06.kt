package day06

import common.println
import common.readInput

fun main() {
    val testinput = RaceInfo.parse(readInput("day06/testinput"))
    val input = RaceInfo.parse(readInput("day06/input"))

    part1(input).println()
}

fun part1(races : List<RaceInfo>): Int {
    return races
        .map {
            it.recordHoldTimes().count()
        }
        .reduce { acc, value -> acc * value }
}

data class RaceInfo(val totalTime: Int, val recordDistance: Int) {

    fun recordHoldTimes(): List<Int> {
        return (0..totalTime)
            .map { elapsedTime ->
                elapsedTime * (totalTime - elapsedTime)
            }
            .filter { it > recordDistance }
    }

    companion object {
        fun parse(input: List<String>): List<RaceInfo> {
            val times = input.first().substringAfter("Time:").trim().split("\\s+".toRegex()).map { it.toInt() }
            val distances = input.last().substringAfter("Distance:").trim().split("\\s+".toRegex()).map { it.toInt() }
            return times.zip(distances)
                .map { (time, distance) -> RaceInfo(time, distance) }
        }
    }
}
