package common

import java.util.*
import java.util.function.Predicate


class Grid<T> {


    private val data = mutableMapOf<Point, T>()

    constructor() {
    }

    constructor(values: List<List<T>>) {
        values.forEachIndexed { y, row ->
            row.forEachIndexed { x, value ->
                data[Point(x, y)] = value
            }
        }
    }

    constructor(points: Collection<Point>, value: T) {
        points.forEach { data[it] = value }
    }

    fun size(): Int {
        return data.size
    }

    fun entries(): Set<Map.Entry<Point, T>> {
        return data.entries
    }

    fun allPoints() : Set<Point> {
        return data.keys.toSet()
    }

    fun values(): MutableCollection<T> {
        return data.values
    }

    fun valueOf(point: Point): T {
        return data[point]!!
    }

    fun valueOrDefault(point: Point, defaultValue: T): T {
        return data.getOrDefault(point, defaultValue)
    }

    fun maybeValue(point: Point): T? {
        return data[point]
    }

    fun hasValue(point: Point): Boolean {
        return data.containsKey(point)
    }

    fun remove(predicate: Predicate<Point>) {
        data.keys.filter { predicate.test(it) }
            .forEach { data.remove(it) }
    }

    fun valueOfExistingPoints(points: List<Point>) : List<T> {
        return points
            .mapNotNull { maybeValue(it) }
    }

    fun set(point: Point, value: T) {
        data[point] = value
    }

    fun rows(): List<List<T>> {
        val min = min()
        val max = max()
        return IntRange(min.y, max.y)
            .map { y ->
                IntRange(min.x, max.x)
                    .map { x -> valueOf(Point(x, y)) }

            }
    }

    fun rowsWithDefault(defaultValue: T): List<List<T>> {
        val min = min()
        val max = max()
        return IntRange(min.y, max.y)
            .map { y ->
                IntRange(min.x, max.x)
                    .map { x -> valueOrDefault(Point(x, y), defaultValue) }

            }
    }

    fun columns(): List<List<T>> {
        val min = min()
        val max = max()
        return IntRange(min.x, max.x)
            .map { x ->
                IntRange(min.y, max.y)
                    .map { y -> valueOf(Point(x, y)) }

            }
    }

    fun min(): Point {
        val minY = Optional.ofNullable(data.keys.map { it.y }.minOf { it }).orElse(0)
        val minX = Optional.ofNullable(data.keys.map { it.x }.minOf { it }).orElse(0)
        return Point(minX, minY)
    }

    fun max(): Point {
        val maxX = Optional.ofNullable(data.keys.map { it.x }.maxOf { it }).orElse(0)
        val maxY = Optional.ofNullable(data.keys.map { it.y }.maxOf { it }).orElse(0)
        return Point(maxX, maxY)
    }

    override fun toString(): String {
        return toString() { it }
    }

    fun <R> toString(transform: (T) -> R): String {
        val sb = StringBuilder()
        val minY = Optional.ofNullable(data.keys.map { it.y }.minOf { it }).orElse(0)
        val minX = Optional.ofNullable(data.keys.map { it.x }.minOf { it }).orElse(0)
        val maxX = Optional.ofNullable(data.keys.map { it.x }.maxOf { it }).orElse(0)
        val maxY = Optional.ofNullable(data.keys.map { it.y }.maxOf { it }).orElse(0)
        (minY)!!.rangeTo(maxY!!).forEach { y ->
            (minX)!!.rangeTo(maxX!!).forEach { x ->
                if(hasValue(Point(x,y))) {
                    sb.append(transform.invoke(valueOf(Point(x,y))).toString())
                } else {
                    sb.append(".")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun <R> toStringInvertedVertical(transform: (T) -> R): String {
        val sb = StringBuilder()
        val minY = Optional.ofNullable(data.keys.map { it.y }.minOf { it }).orElse(0)
        val minX = Optional.ofNullable(data.keys.map { it.x }.minOf { it }).orElse(0)
        val maxX = Optional.ofNullable(data.keys.map { it.x }.maxOf { it }).orElse(0)
        val maxY = Optional.ofNullable(data.keys.map { it.y }.maxOf { it }).orElse(0)
        (maxY)!!.downTo(minY!!).forEach { y ->
            (minX)!!.rangeTo(maxX!!).forEach { x ->
                if(hasValue(Point(x,y))) {
                    sb.append(transform.invoke(valueOf(Point(x,y))).toString())
                } else {
                    sb.append(".")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

}
