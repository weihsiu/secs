package secs

import scala.compiletime.*

class FilterSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component derives ComponentMeta

  case class Heading(angle: Double) extends Component derives ComponentMeta

  case class Rotation(angle: Double) extends Component derives ComponentMeta

  test("filter") {
    import BoolOps.*
    assert(
      summon[Filter[¬[Heading ∧ Dimension ∨ ¬[Rotation]]]].boolOps == ¬(
        ∨(
          ∧(summon[ComponentMeta[Heading]], summon[ComponentMeta[Dimension]]),
          ¬(summon[ComponentMeta[Rotation]])
        )
      )
    )
  }
