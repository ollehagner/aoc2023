package common

import day08.greatestCommonDivisor
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.function.Predicate
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String): List<String> = Path("src/${name}.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Group values until predicate is fulfilled
 */
inline fun<R> List<R>.groupUntil(predicate: Predicate<R>): List<List<R>> {
    return fold(
        LinkedList<List<R>>()
    ) {
            acc, value ->
        if(acc.isEmpty() || predicate.test(value)) {
            acc.addLast(listOf(value))
        } else {
            val currentList = acc.removeLast()
            acc.addLast(listOf(currentList, listOf( value)).flatten())
        }
        acc
    }
}

fun <T> infiniteSequence(value: T): Sequence<T> = sequence {
    while(true) {
        yield(value)
    }
}

fun Array<CharArray>.printMatrix() {
    println(joinToString(System.lineSeparator()) { row -> row.joinToString(" ") })
}

fun LongRange.intersect(other: LongRange): LongRange {
    return maxOf(first, other.first)..minOf(last, other.last)
}

fun leastCommonMultiple(values: List<Long>): Long {
    if(values.size == 1) return values.first()

    val remaining = values.drop(2)
    val leastCommonMultiple = values[0] * (values[1] / greatestCommonDivisor(values[0], values[1]))
    return leastCommonMultiple(remaining + listOf(leastCommonMultiple))
}