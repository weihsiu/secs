package secs.examples.retained

import secs.*

type Vect3 = (Double, Double, Double)

extension (v1: Vect3) def +(v2: Vect3): Vect3 = (v1._1 + v2._1, v1._2 + v2._2, v1._3 + v2._3)

case class Cube(width: Double) extends Component derives ComponentMeta

case class Transform(scale: Double, rotation: Vect3, position: Vect3) extends Component
    derives ComponentMeta

case class TransformDelta(scale: Double, rotation: Vect3, position: Vect3) extends Component
    derives ComponentMeta
