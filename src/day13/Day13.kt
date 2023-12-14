package day13

import common.*
import day11.rotateClockwise

fun main() {
    val testinput = readInput("day13/testinput")
    val input = readInput("day13/input")

    part1(toPatterns(input)).println()
//    part2(toPatterns(input)).println()
}

fun toPatterns(input: List<String>): List<Array<CharArray>> {
    return input
        .groupUntil { row -> row.isEmpty() }
        .map { pattern -> pattern.filter { it.isNotEmpty() }.map { it.toCharArray() }.toTypedArray() }
}

fun part1(patterns: List<Array<CharArray>>): Int {
    return patterns.sumOf { pattern ->
        maxOf(findReflection(pattern, 100)?.first?: 0, findReflection(pattern.rotateClockwise())?.first ?: 0)
    }
}

/*
Svaret mellan 34141 och 58586

 */
fun part2(patterns: List<Array<CharArray>>): Int {
    return patterns.sumOf { originalPattern ->
        val oldResult = listOfNotNull(findReflection(originalPattern, 100), findReflection(originalPattern.rotateClockwise()))
            .first()
        patternSequence(originalPattern)
            .firstNotNullOfOrNull { pattern ->
                val result = listOfNotNull(findReflection(pattern, 100), findReflection(pattern.rotateClockwise()))
                    .firstOrNull()
                if(result?.second contentEquals oldResult.second) null else result?.first
            } ?: 0
    }
}

fun patternSequence(pattern: Array<CharArray>): Sequence<Array<CharArray>> {
    val maxX = pattern.first().size - 1
    val maxY = pattern.size - 1
    return generateSequence(Point(0,0)) { point ->
        when {
            point == Point(maxX, maxY) -> null
            point.x == maxX -> Point(0, point.y + 1)
            else -> point.copy(x = point.x + 1)
        }
    }.map { nextPoint ->
        val copy = pattern.map { it.copyOf() }.toTypedArray()
        val currentValue = copy[nextPoint.y][nextPoint.x]
        copy[nextPoint.y][nextPoint.x] = if(currentValue == '.') '#' else '.'
        copy
    }
}

fun findReflection(pattern: Array<CharArray>, factor: Int = 1): Pair<Int, CharArray>? {
    val potentialReflections =
            pattern.asSequence().toList()
                .windowed(2)
                .mapIndexed { index, (firstRow, secondRow) ->
                    index to (firstRow contentEquals  secondRow)
                }
                .filter { it.second }
                .map { it.first }.toList()

    return potentialReflections
        .map { index -> index + 1 }
        .filter { pivot ->
            pattern.take(pivot).reversed().zip(pattern.drop(pivot))
                .all { (firstRow, secondRow) ->
                    firstRow contentEquals secondRow
                }
        }
        .map { Pair(it * factor, pattern[it]) }
        .firstOrNull()
}




