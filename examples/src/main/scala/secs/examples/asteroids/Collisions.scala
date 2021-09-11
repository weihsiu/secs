package secs.examples.asteroids

object Collisions:
  type Point = (Double, Double)
  def intersectSegments(p1: Point, q1: Point, p2: Point, q2: Point): Boolean =
    def onSegment(p: Point, q: Point, r: Point): Boolean =
      q._1 <= math.max(p._1, r._1) && q._1 >= math.min(p._1, r._1) &&
        q._2 <= math.max(p._2, r._2) && q._2 >= math.min(p._2, r._2)
    def orientation(p: Point, q: Point, r: Point): Int =
      val o = (q._2 - p._2) * (r._1 - q._1) - (q._1 - p._1) * (r._2 - q._2)
      if o == 0 then 0 else if o > 0 then 1 else 2
    val o1 = orientation(p1, q1, p2)
    val o2 = orientation(p1, q1, q2)
    val o3 = orientation(p2, q2, p1)
    val o4 = orientation(p2, q2, q1)
    (o1 != o2 && o3 != o4) ||
    (o1 == 0 && onSegment(p1, p2, q1)) ||
    (o2 == 0 && onSegment(p1, q2, q1)) ||
    (o3 == 0 && onSegment(p2, p1, q2)) ||
    (o4 == 0 && onSegment(p2, q1, q2))

  def intersectRect(rect: (Double, Double, Double, Double), p: Point, q: Point): Boolean =
    val r1 = (rect._1, rect._2) // bottom left
    val r2 = (rect._1, rect._4) // top left
    val r3 = (rect._3, rect._4) // top right
    val r4 = (rect._3, rect._2) // bottom right
    intersectSegments(r1, r2, p, q) ||
    intersectSegments(r2, r3, p, q) ||
    intersectSegments(r3, r4, p, q) ||
    intersectSegments(r4, r1, p, q) ||
    (p._1 >= rect._1 && p._2 >= rect._2 && p._1 <= rect._3 && p._2 <= rect._4 &&
      q._1 >= rect._1 && q._2 >= rect._2 && q._1 <= rect._3 && q._2 <= rect._4)
