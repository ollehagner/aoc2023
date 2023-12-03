package common

enum class Direction {
    NONE {
        override fun from(from: Point, steps: Int): Point {
            return from
        }
    },
    UP {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x, from.y + steps)
        }
    }, DOWN {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x, from.y - steps)
        }
    }, RIGHT {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x + steps, from.y)
        }
    }, LEFT {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x - steps, from.y)
        }
    }, UP_LEFT {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x - steps, from.y + steps)
        }
    }, UP_RIGHT {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x + steps, from.y + steps)
        }
    }, DOWN_LEFT {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x - steps, from.y - steps)
        }
    }, DOWN_RIGHT {
        override fun from(from: Point, steps: Int): Point {
            return Point(from.x + steps, from.y - steps)
        }
    };

    abstract fun from(from: Point, steps: Int = 1): Point;
}
