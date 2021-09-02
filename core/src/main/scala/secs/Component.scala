package secs

import scala.Tuple.Map
import scala.compiletime.*

trait Component

trait ComponentMeta[+A]

case class EntityC(entity: Entity) extends Component
given ComponentMeta[EntityC] with {}

