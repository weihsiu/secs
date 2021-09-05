package secs

import scala.Tuple.Map
import scala.compiletime.*
import scala.deriving.*

trait Component

trait ComponentMeta[+A]
object ComponentMeta:
  def apply[A: ComponentMeta]: ComponentMeta[A] = summon[ComponentMeta[A]]
  def derived[A]: ComponentMeta[A] = new ComponentMeta[A] {}

case class EntityC(entity: Entity) extends Component derives ComponentMeta
