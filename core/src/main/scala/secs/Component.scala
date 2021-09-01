package secs

import scala.Tuple.Map
import scala.compiletime.*

trait Component extends Matchable

trait ComponentMeta[+A] extends Matchable

case class EntityC(entity: Entity) extends Component
given ComponentMeta[EntityC] with {}

case class Dimension(width: Double, height: Double) extends Component
given ComponentMeta[Dimension] with {}

case class Heading(angle: Double) extends Component
given ComponentMeta[Heading] with {}

case class Rotation(angle: Double) extends Component
given ComponentMeta[Rotation] with {}
