package secs

import scala.Tuple.Map
import scala.compiletime.*

trait Component

trait ComponentMeta[+A]

case class EntityC(entity: Entity) extends secs.Component
given ComponentMeta[EntityC] with {}

case class Dimension(width: Double, height: Double) extends Component
given ComponentMeta[Dimension] with {}

case class Heading(angle: Double) extends Component
given ComponentMeta[Heading] with {}