package secs

import scala.Tuple.Map
import scala.compiletime.*

trait Component

trait ComponentMeta[+A]:
  def name: String

trait ComponentMetaMapper:
  def map: PartialFunction[Component, ComponentMeta[Component]]

case class Dimension(width: Double, height: Double) extends Component
given ComponentMeta[Dimension] with
  val name = "Dimension"

case class Heading(angle: Double) extends Component
given ComponentMeta[Heading] with
  val name = "Heading"

given ComponentMetaMapper with
  val map =
    case _: Dimension => summon[ComponentMeta[Dimension]]
    case _: Heading   => summon[ComponentMeta[Heading]]

inline def toMetas[CS <: Tuple]: Map[CS, ComponentMeta] =
  summonAll[Map[CS, ComponentMeta]]

def toNames(cms: Tuple): List[String] =
  cms match
    case (c: ComponentMeta[?]) *: cs => c.name :: toNames(cs)
    case x *: _                      => sys.error(s"invalid type: $x")
    case _                           => Nil

inline def query[CS <: Tuple]: List[String] =
  toNames(toMetas[CS])

@main
def componentTest() =
  println(query[Dimension *: Heading *: EmptyTuple])
