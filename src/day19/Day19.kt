package day19

import common.println
import common.readInput

fun main() {
    val testinput = readInput("day19/testinput")
    val input = readInput("day19/input")
    part1(input).println()
    part2(input).println()

}

fun part1(input: List<String>): Long {
    val workflows: MutableMap<String, IWorkflow> = input.takeWhile { it.isNotEmpty() }.map { Workflow.parse(it) }
        .associateBy { it.id }.toMutableMap()
    workflows["A"] = EndpointWorkflow()
    workflows["R"] = EndpointWorkflow()

    val parts = input.dropWhile { it.isNotEmpty() }
        .drop(1).map { Part.parse(it) }

    val startWorkflow = workflows["in"]
    parts.forEach { startWorkflow!!.run(it, workflows) }

    return workflows["A"]!!.totalRating()
}

fun part2(input: List<String>): Long {
    val workflows: MutableMap<String, IWorkflow> = input.takeWhile { it.isNotEmpty() }.map { Workflow.parse(it) }
        .associateBy { it.id }.toMutableMap()
    workflows["A"] = EndpointWorkflow()
    workflows["R"] = EndpointWorkflow()

    val startWorkflow = workflows["in"]
    startWorkflow!!.run(listOf('x', 'm', 'a', 's').associateWith { 1L..4000 }, workflows)
    return workflows["A"]!!.totalCombinations()
}

private interface IWorkflow {
    fun run(part: Part, workFlows: Map<String, IWorkflow>)
    fun totalRating(): Long

    fun run(ranges: Map<Char, LongRange>, workFlows: Map<String, IWorkflow>)
    fun totalCombinations(): Long
}

private class Workflow(val id: String, val rules: List<Rule>, val defaultDestination: String): IWorkflow {

    override fun run(part: Part, workFlows: Map<String, IWorkflow>) {
        val destination = rules.firstOrNull { it.test(part) }?.destination ?: defaultDestination
        workFlows[destination]!!.run(part, workFlows)
    }

    override fun run(ranges: Map<Char, LongRange>, workFlows: Map<String, IWorkflow>) {
        val remaining = rules.runningFold(ranges) { acc, rule ->
            val toSendAndToNextRule = rule.split(acc)
            workFlows[rule.destination]!!.run(toSendAndToNextRule.first, workFlows)
            toSendAndToNextRule.second
        }.last()
        workFlows[defaultDestination]!!.run(remaining, workFlows)
    }

    override fun totalRating(): Long {
        TODO("Not yet implemented")
    }

    override fun totalCombinations(): Long {
        TODO("Not yet implemented")
    }

    companion object {
        fun parse(string: String): Workflow {
            val id = string.substringBefore("{")
            val rules = string.substringAfter("{").substringBefore("}")
                .split(",").dropLast(1)
                .map { Rule.parse(it) }
            val defaultDestination = string.substringAfter("{").substringBefore("}")
                .split(",").last()
            return Workflow(id, rules, defaultDestination)
        }
    }
}

private class EndpointWorkflow(): IWorkflow {
    val parts = mutableListOf<Part>()
    val possibleCombinations = mutableListOf<Map<Char, LongRange>>()
    override fun run(part: Part, workFlows: Map<String, IWorkflow>) {
        parts.add(part)
    }

    override fun run(ranges: Map<Char, LongRange>, workFlows: Map<String, IWorkflow>) {
        possibleCombinations.add(ranges)
    }


    override fun totalRating(): Long {
        return parts
            .sumOf { it.values.values.sum() }.toLong()
    }

    override fun totalCombinations(): Long {
        return possibleCombinations
            .sumOf { it.values.map { partRange -> partRange.size() }.reduce(Long::times)}
    }

}

private class Rule(val partRating: Char, val operation: Operation, val checkValue: Int, val destination: String) {
    fun test(part: Part): Boolean {
        return operation.test(part.valueOf(partRating), checkValue)
    }

    fun split(ranges: Map<Char, LongRange>): Pair<Map<Char, LongRange>, Map<Char, LongRange>> {
        val rangeToSplit = ranges.entries.first { it.key == partRating }
        val otherRanges = ranges.filter { it.key != partRating }

        operation.split(rangeToSplit.value, checkValue)
            .let { (toSend, toNextInWorkflow) ->
                val rangesToSend = otherRanges + (partRating to toSend)
                val rangesToNextInWorkflow = otherRanges + (partRating to toNextInWorkflow)
                return Pair(rangesToSend, rangesToNextInWorkflow)
            }
    }

    companion object {
        fun parse(string: String): Rule {
            val partRating = string.first()
            val operator = string[1]
            val checkValue = string.substring(2).substringBefore(":").toInt()
            val destination = string.substringAfter(":")
            val operation = Operation.from(operator)

            return Rule(partRating, operation, checkValue, destination)
        }
    }

}

private enum class Operation(val symbol: Char, private val expr: (Int, Int) -> Boolean) {
    GREATER_THAN('>', { a, b -> a > b }),
    LESS_THAN('<', { a, b -> a < b });

    fun test(a: Int, b: Int): Boolean {
        return expr(a, b)
    }

    fun split(range: LongRange, value: Int): Pair<LongRange, LongRange> {
        return if(this == GREATER_THAN) {
            Pair(LongRange(value + 1L, range.last), LongRange(range.min(), value.toLong()))
        } else {
            Pair(LongRange(range.first, value - 1L), LongRange(value.toLong(), range.last))
        }
    }

    companion object {
        fun from(symbol: Char): Operation {
            return entries.first { it.symbol == symbol }
        }
    }

}

private class Part(val values: Map<Char, Int>) {
    fun valueOf(char: Char): Int {
        return values[char]!!
    }

    companion object {
        fun parse(string: String): Part {
            return Part(
                string.substringBefore("}").substringAfter("{")
                    .split(",")
                    .associate { it.substringBefore("=").first() to it.substringAfter("=").toInt() })
        }
    }
}

inline fun LongRange.size(): Long {
    return this.last - this.first + 1
}
