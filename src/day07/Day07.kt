package day07

import common.println
import common.readInput
import java.lang.IllegalArgumentException

fun main() {
    val testinput = parseInput(readInput("day07/testinput"))


    val input = parseInput(readInput("day07/input"))

    part1(input).println()
}

fun parseInput(input: List<String>): List<Pair<Hand, Int>> {
    return input
        .map { it.split(" ") }
        .map { (hand, bid) -> Hand.parse(hand) to bid.toInt() }
}

fun part1(handsAndBids: List<Pair<Hand, Int>>): Int {
    return handsAndBids
        .sortedBy { it.first }
        .map { it.second }
        .mapIndexed { index, bid -> (index + 1) * bid }
        .sum()
}

class Hand(private val cards: List<Card>) : Comparable<Hand> {

    private val comparator: Comparator<Hand> = compareBy<Hand> { it.points() }
        .thenBy { it.cards[0].rank }
        .thenBy { it.cards[1].rank }
        .thenBy { it.cards[2].rank }
        .thenBy { it.cards[3].rank }
        .thenBy { it.cards[4].rank }

    private fun points(): Int {
        val cardCount = cards.groupingBy { it }.eachCount().values
        return when {
            cardCount.contains(5) -> 7
            cardCount.contains(4) -> 6
            cardCount.contains(2) && cardCount.contains(3) -> 5
            cardCount.contains(3) && !cardCount.contains(2) -> 4
            cardCount.count { it == 2 } == 2 -> 3
            cardCount.count { it == 2 } == 1 -> 2
            else -> 1
        }
    }

    override fun compareTo(other: Hand): Int {
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return cards.joinToString("")
    }

    companion object {
        fun parse(cardsAsString: String): Hand {
            return Hand(cardsAsString.map { Card.parse(it) })
        }
    }

}

enum class Card(val rank: Int, val char: Char) : Comparable<Card> {
    ACE(13, 'A'),
    KING(12, 'K'),
    QUEEN(11, 'Q'),
    JACK(10, 'J'),
    TEN(9, 'T'),
    NINE(8, '9'),
    EIGHT(7, '8'),
    SEVEN(6, '7'),
    SIX(5, '6'),
    FIVE(4, '5'),
    FOUR(3, '4'),
    THREE(2, '3'),
    TWO(1, '2');

    companion object {
        fun parse(char: Char): Card {
            return entries
                .find { it.char == char }
                ?: throw IllegalArgumentException("Unknown char $char")
        }
    }

    override fun toString(): String {
        return "$char"
    }


}