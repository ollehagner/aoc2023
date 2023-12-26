package day19

import common.println
import common.readInput

fun main() {
    val testinput = readInput("day19/testinput")
    val input = readInput("day19/input")
    part1(input).println()
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

private interface IWorkflow {
    fun run(part: Part, workFlows: Map<String, IWorkflow>)
    fun totalRating(): Long
}

private class Workflow(val id: String, val rules: List<Rule>, val defaultDestination: String): IWorkflow {

    override fun run(part: Part, workFlows: Map<String, IWorkflow>) {
        val destination = rules.firstOrNull { it.test(part) }?.destination ?: defaultDestination
        workFlows[destination]!!.run(part, workFlows)
    }

    override fun totalRating(): Long {
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
    override fun run(part: Part, workFlows: Map<String, IWorkflow>) {
        parts.add(part)
    }

    override fun totalRating(): Long {
        return parts
            .sumOf { it.values.values.sum() }.toLong()
    }

}

private class Rule(val expr: (Part) -> Boolean, val destination: String) {
    fun test(part: Part): Boolean {
        return expr(part)
    }

    companion object {
        fun parse(string: String): Rule {
            val partRating = string.first()
            val operator = string[1]
            val checkValue = string.substring(2).substringBefore(":").toInt()
            val destination = string.substringAfter(":")
            val expression = if(operator == '<')
                { part: Part -> part.valueOf(partRating) < checkValue } else { part: Part -> part.valueOf(partRating) > checkValue }
            return Rule(expression, destination)
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