package day22

import common.Point3D
import common.println
import common.readInput

val DOWN = Point3D(0, 0, -1)

fun main() {
    val testinput = readInput("day22/testinput").map { Brick.parse(it) }
    val input = readInput("day22/input").map { Brick.parse(it) }
//    part1(input).println()
    part2(testinput).println()
}

private fun part1(bricks: List<Brick>): Int {
    val stack = drop(Stack(bricks))
    return stack.bricks.count {
        stack.supportedBy(it).all { supported -> stack.numOfSupportingBricks(supported) >= 2 }
    }
}

private fun part2(bricks: List<Brick>): Int {
    val stack = drop(Stack(bricks))
    return stack.bricks.sumOf {
        stack.supportedBy(it).filter { supported -> stack.numOfSupportingBricks(supported) <= 1 }
            .map { brick -> println("Supporting: $it, supports $brick")
                brick
            }
            .count()
    }
}

private fun drop(stack: Stack): Stack {
    val possibleToDrop = stack.bricks
        .filter { brick -> stack.bricks.none { it.supports(brick) } }
        .filter { brick -> brick.bottom().none { it.z == 1 } }
    return if (possibleToDrop.isEmpty()) stack
    else drop(Stack(stack.bricks
        .map { brick ->
            if (brick in possibleToDrop) {
//                println("Dropping brick: $brick")
                brick.drop()
            } else brick
        }
    )
    )
}

private class Stack(val bricks: List<Brick>) {

    fun supportedBy(brick: Brick): Set<Brick> {
        return bricks
            .filter { it.supportedBy(brick) }
            .toSet()
    }

    fun numOfSupportingBricks(brick: Brick): Int {
        return bricks
            .count { it.supports(brick) }
    }

}

private data class Brick(val from: Point3D, val to: Point3D) {

    fun supports(other: Brick): Boolean {
        val otherBottom = other.bottom()
        return otherBottom.map { it.move(DOWN) }.any { it in top() }
    }

    fun supportedBy(other: Brick): Boolean {
        val otherTop = other.top()
        return this.bottom().map { it.move(DOWN) }.any { it in otherTop }
    }

    fun top(): Set<Point3D> {
        val maxZ = maxOf(from.z, to.z)
        return coordinates()
            .filter { it.z == maxZ }
            .toSet()
    }

    fun bottom(): Set<Point3D> {
        val minZ = minOf(from.z, to.z)
        return coordinates()
            .filter { it.z == minZ }
            .toSet()
    }

    fun drop(): Brick {
        return Brick(from.move(DOWN), to.move(DOWN))
    }

    fun coordinates(): List<Point3D> {
        return (from.x..to.x).flatMap { x ->
            (from.y..to.y).flatMap { y ->
                (from.z..to.z).map { z ->
                    Point3D(x, y, z)
                }
            }
        }
    }

    companion object {
        fun parse(row: String): Brick {
            val from = row.substringBefore("~").split(",").let { (x, y, z) -> Point3D(x.toInt(), y.toInt(), z.toInt()) }
            val to = row.substringAfter("~").split(",").let { (x, y, z) -> Point3D(x.toInt(), y.toInt(), z.toInt()) }
            return Brick(from, to)
        }
    }

}