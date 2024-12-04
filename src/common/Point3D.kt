package common

import kotlin.math.abs

data class Point3D(val x: Int, val y: Int, val z: Int) {

    fun move(vector: Point3D): Point3D {
        return Point3D(this.x + vector.x, this.y + vector.y, this.z + vector.z)
    }

    fun relativeTo(other: Point3D): Point3D {
        return Point3D(other.x - this.x, other.y - this.y, other.z - this.z)
    }

    fun rotateRoundZAxis(): Point3D {
        return Point3D(this.y * -1, this.x, this.z)
    }

    fun rotateRoundYAxis(): Point3D {
        return Point3D(this.z, this.y, this.x * -1)
    }

    fun rotateRoundXAxis(): Point3D {
        return Point3D(this.x, this.z * -1, this.y)
    }

    fun manhattanDistance(other: Point3D): Int {
        return abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
    }

    fun adjacent(): Set<Point3D> {
        return setOf(
            this.copy(x = this.x - 1),
            this.copy(x = this.x + 1),
            this.copy(y = this.y - 1),
            this.copy(y = this.y + 1),
            this.copy(z = this.z - 1),
            this.copy(z = this.z + 1),
        )
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

}