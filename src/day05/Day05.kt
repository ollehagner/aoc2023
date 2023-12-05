package day05

import common.groupUntil
import common.println
import common.readInput

fun main() {
    val input = readInput("day05/input")
    val testinput = readInput("day05/testinput")
    part1(input).println()
//    part2(input).println()
}

fun part1(input: List<String>): Long {
    val seeds = input.first().substringAfter(": ").split(" ").map { it.trim().toLong() }
    val chain = createChain(input)

    return seeds
        .minOf { seed -> chain.endResult(seed) }
}

fun part2(input: List<String>): Long {
    val seeds = input
        .first().substringAfter(": ")
        .split(" ")
        .map { it.trim().toLong() }
        .windowed(2, 2)
        .map { LongRange(it.first(), it.first() + it.last() - 1)}
    val chain = createChain(input)
    return seeds.flatMap { it.toList() }
        .minOf { chain.endResult(it) }

}

fun createChain(input: List<String>): Converter {
    val mappers = parseMappers(input)
    return parseMappers(input)
        .drop(1)
        .fold(Converter.from(mappers.first())) { next, mapper -> Converter.from(mapper, next) }
}

fun parseMappers(input: List<String>): List<Mapper> {
    return input.drop(2)
        .reversed()
        .groupUntil { it.isEmpty() }
        .map { Mapper.parse(it) }
}

interface Converter {
    fun convert(value: Long): List<Long>

    fun endResult(value: Long): Long {
        return convert(value).last()
    }

    companion object {
        fun from(mapper: Mapper): Converter {
            return LeafConverter(mapper)
        }

        fun from(mapper: Mapper, next: Converter) : Converter {
            return NodeConverter(mapper, next)
        }
    }
}

class NodeConverter(private val mapper: Mapper, val next: Converter): Converter {

    override fun convert(value: Long): List<Long> {
        return listOf(value) + next.convert(mapper.get(value))
    }

}

class LeafConverter(private val mapper: Mapper): Converter {
    override fun convert(from: Long): List<Long> {
        return listOf(from, mapper.get(from))
    }

}

class Mapper(private val mappings: Map<LongRange, Long>) {
    fun get(from: Long): Long {
        return mappings
            .filterKeys { it.contains(from) }
            .firstNotNullOfOrNull { from + it.value } ?: from

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
                    LongRange(source, source + length - 1) to (destination - source)
                }
            return Mapper(mappings)

        }
    }


}