package day15

import common.println
import common.readInput

const val REMOVE = '-'
const val UPSERT = '='

fun main() {

    val testinput = readInput("day15/testinput").first().split(",")
    val input = readInput("day15/input").first().split(",")

    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    return input.sumOf { hash(it) }
}

fun part2(input: List<String>): Int {
    val boxes = mutableMapOf<Int, MutableList<Lens>>()
    input
        .forEach { instruction ->
            val label = instruction.takeWhile { char -> char != REMOVE && char != UPSERT }
            if(instruction.contains(REMOVE)) {
                boxes[hash(label)]?.let { lenses -> lenses.removeAll { lens -> lens.label == label } }
            } else {
                val lenses = boxes.getOrDefault(hash(label), mutableListOf())

                val focalLength = instruction.substringAfter(UPSERT).toInt()
                if(lenses.any { it.label == label }) {
                    val lensToReplace = lenses.find { it.label == label }
                    lensToReplace!!.focalLength = focalLength
                } else {
                    lenses.add(Lens(label, focalLength))
                    boxes[hash(label)] = lenses
                }
            }
        }
    return boxes
        .entries
        .sumOf { (boxNumber, lenses) ->
        lenses.mapIndexed { index, lens ->
            (index + 1) * lens.focalLength
        }.sum() * (boxNumber + 1)
    }
}

class Lens(val label: String, var focalLength: Int) {
    override fun toString(): String {
        return "$label : $focalLength"
    }
}

fun hash(input: String): Int {
    return input
        .map { asciiValue(it) }
        .fold(0) { acc, value -> hash(acc + value) }
}


fun hash(current: Int): Int {
    return (current * 17) % 256
}

fun asciiValue(char: Char) : Int {
    return char.code
}