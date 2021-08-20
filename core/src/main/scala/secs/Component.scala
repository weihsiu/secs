package secs

import scala.compiletime.*

trait Component

trait ComponentMeta[A <: Component]:
  def name: String

case class Dimension(width: Double, height: Double) extends Component
given ComponentMeta[Dimension] with
  val name = "Dimension"

case class Heading(angle: Double) extends Component
given ComponentMeta[Heading] with
  val name = "Heading"

def test[A <: Component: ComponentMeta]: String =
  summon[ComponentMeta[A]].name

@main
def componentTest() =
  println(test[Heading])
