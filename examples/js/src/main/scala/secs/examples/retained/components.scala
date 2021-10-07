package secs.examples.retained

import secs.*

type Vect3 = (Double, Double, Double)

case class Cube(width: Double, position: Vect3, rotation: Vect3) extends Component
    derives ComponentMeta
