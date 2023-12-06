package day06

import common.println
import common.readInput
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun main() {
    val testinput = readInput("day06/testinput")
    val input = readInput("day06/input")

    part1(RaceInfo.parsePart1(input)).println()
    part2(RaceInfo.parsePart2(input)).println()

}

/**
 * Time:        50748685
 * Distance:   242101716911252
 *
 *5330503 ska det vara och 45418182
 *
 */

fun part1(races : List<RaceInfo>): Int {
    return races
        .map {
            it.recordHoldTimes().count()
        }
        .reduce { acc, value -> acc * value }
}

fun part2(race: RaceInfo): Int {

    val totalTime = BigDecimal(race.totalTime)
    val recordDistance = BigDecimal(race.recordDistance)

    val squareRootPart = (totalTime.pow(2) - recordDistance.multiply(BigDecimal(4))).sqrt(MathContext.DECIMAL128)
    val firstAnswer = totalTime.negate().plus(squareRootPart).div(BigDecimal(-2)).setScale(0, RoundingMode.UP).toLong()
    val secondAnswer = totalTime.negate().minus(squareRootPart).div(BigDecimal(-2)).setScale(0, RoundingMode.DOWN).toLong()

    return (firstAnswer..secondAnswer).count()
}

data class RaceInfo(val totalTime: Long, val recordDistance: Long) {

    fun recordHoldTimes(): List<Long> {
        return (0..totalTime)
            .map { elapsedTime ->
                elapsedTime * (totalTime - elapsedTime)
            }
            .filter { it > recordDistance }
    }

    companion object {
        fun parsePart1(input: List<String>): List<RaceInfo> {
            val racetimes = input.first().substringAfter("Time:").trim().split("\\s+".toRegex()).map { it.toLong() }
            val recordDistances = input.last().substringAfter("Distance:").trim().split("\\s+".toRegex()).map { it.toLong() }
            return racetimes.zip(recordDistances)
                .map { (time, distance) -> RaceInfo(time, distance) }
        }

        fun parsePart2(input: List<String>): RaceInfo {
            val racetime = input.first().filter { it.isDigit() }.toLong()
            val recordDistance = input.last().filter { it.isDigit() }.toLong()
            return RaceInfo(racetime, recordDistance)
        }
    }
}
