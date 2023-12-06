package day05

import common.groupUntil
import common.println
import common.readInput

fun main() {
    val input = readInput("day05/input")
    val testinput = readInput("day05/testinput")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val seeds = input.first().substringAfter(": ").split(" ").map { LongRange(it.trim().toLong(), it.trim().toLong()) }
    val chain = createChain(input)
    return chain.convert(seeds).minOf { it.first }
}

fun part2(input: List<String>): Long {
    val seeds = input
        .first().substringAfter(": ")
        .split(" ")
        .map { it.trim().toLong() }
        .windowed(2, 2)
        .map { LongRange(it.first(), it.first() + it.last() - 1) }
    val chain = createChain(input)
    return chain.convert(seeds).minOf { it.first }
}


fun createChain(input: List<String>): Stage {
    val mappers = parseMappers(input)
    return mappers
        .drop(1)
        .fold(Stage.from(mappers.first())) { next, mapper -> Stage.from(mapper, next) }
}

fun parseMappers(input: List<String>): List<Mapper> {
    return input.drop(2)
        .reversed()
        .groupUntil { it.isEmpty() }
        .map { Mapper.parse(it) }
}

interface Stage {
    fun convert(from: List<LongRange>): List<LongRange>

    companion object {
        fun from(mapper: Mapper): Stage {
            return LeafStage(mapper)
        }

        fun from(mapper: Mapper, next: Stage): Stage {
            return NodeStage(mapper, next)
        }
    }
}

class NodeStage(private val mapper: Mapper, val next: Stage) : Stage {

    override fun convert(from: List<LongRange>): List<LongRange> {
        return next.convert(mapper.get(from))
    }

}

class LeafStage(private val mapper: Mapper) : Stage {

    override fun convert(from: List<LongRange>): List<LongRange> {
        return mapper.get(from)
    }
}

class Mapper(val mappings: Map<LongRange, LongRange>) {

    fun get(from: List<LongRange>): List<LongRange> {
        val lowerUnmapped = -1..<mappings.keys.minOf { it.first }
        val upperUnmapped = mappings.keys.maxOf { it.last } + 1..Long.MAX_VALUE
        return from.flatMap { range ->
            val mapped = mappings
                .filterKeys { source -> source.overlaps(range) }
                .map { (source, destination) ->
                    val start = maxOf(range.first, source.first)
                    val end = minOf(range.last, source.last)
                    val offset = destination.first - source.first
                    LongRange(start + offset, end + offset)
                }.toMutableList()
            if(lowerUnmapped.overlaps(range)) {
                mapped.add(range.first..minOf(range.last, lowerUnmapped.last))
            }
            if(upperUnmapped.overlaps(range)) {
                mapped.add(maxOf(range.first, upperUnmapped.first)..range.last)
            }
            mapped
        }
    }

    override fun toString(): String {
        return mappings
            .map { (range, value) -> "$range -> $value" }
            .joinToString(System.lineSeparator())
    }

    companion object {
        fun parse(input: List<String>): Mapper {
            val mappings = input
                .filter { it.isNotEmpty() }
                .dropLast(1)
                .map { line -> line.split(" ").map { it.trim().toLong() } }
                .associate { (destination, source, length) ->
                    (source..<source + length) to (destination..<destination + length)
                }
                .toSortedMap(compareBy<LongRange> { it.first })
            return Mapper(mappings)
        }
    }
}

private infix fun LongRange.overlaps(other: LongRange): Boolean =
    (first <= other.last && other.first <= last)