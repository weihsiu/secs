package secs

import scala.compiletime.*

class FilterSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component derives ComponentMeta

  case class Heading(angle: Double) extends Component derives ComponentMeta

  case class Rotation(angle: Double) extends Component derives ComponentMeta

  test("filter 1") {
    import BoolOps.*
    import Decorator.*
    assert(
      summon[Filter[¬[Heading ∧ Dimension ∨ ¬[Rotation]]]].boolOps == ¬(
        ∨(
          ∧(ComponentMeta[Heading], ComponentMeta[Dimension]),
          ¬(ComponentMeta[Rotation])
        )
      )
    )
  }

  test("filter decorator") {
    assert(
      summon[Filter[¬[Added[Dimension]]]].boolOps == ¬(Added(ComponentMeta[Dimension]))
    )
    assert(
      summon[Filter[¬[Added[Dimension]]]].boolOps != ¬(Added(ComponentMeta[Heading]))
    )
    assert(
      summon[Filter[¬[Changed[Dimension]]]].boolOps == ¬(Changed(ComponentMeta[Dimension]))
    )
    assert(
      summon[Filter[¬[Changed[Dimension]]]].boolOps != ¬(Changed(ComponentMeta[Heading]))
    )
    assert(
      summon[Filter[∧[Added[Dimension], Changed[Heading]]]].boolOps == ∧(
        Added(ComponentMeta[Dimension]),
        Changed(ComponentMeta[Heading])
      )
    )
    assert(
      summon[Filter[∧[¬[Heading], Changed[Dimension]]]].boolOps == ∧(
        ¬(ComponentMeta[Heading]),
        Changed(ComponentMeta[Dimension])
      )
    )
    assert(
      summon[Filter[∨[Added[Dimension], Changed[Heading]]]].boolOps == ∨(
        Added(ComponentMeta[Dimension]),
        Changed(ComponentMeta[Heading])
      )
    )
    assert(
      summon[Filter[∨[¬[Heading], Changed[Dimension]]]].boolOps == ∨(
        ¬(ComponentMeta[Heading]),
        Changed(ComponentMeta[Dimension])
      )
    )
  }
