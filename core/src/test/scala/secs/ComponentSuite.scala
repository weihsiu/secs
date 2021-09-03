package secs

class ComponentSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component
  given ComponentMeta[Dimension] with
    val name = "Dimension"

  case class Heading(angle: Double) extends Component
  given ComponentMeta[Heading] with
    val name = "Heading"

  test("ComponentMeta is singleton") {
    assertEquals(summon[ComponentMeta[Dimension]], summon[ComponentMeta[Dimension]])
    assertEquals(summon[ComponentMeta[Heading]], summon[ComponentMeta[Heading]])
  }
