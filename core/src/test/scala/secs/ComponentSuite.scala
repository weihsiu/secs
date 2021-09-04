package secs

class ComponentSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component derives ComponentMeta

  case class Heading(angle: Double) extends Component derives ComponentMeta

  test("ComponentMeta is singleton") {
    assertEquals(summon[ComponentMeta[Dimension]], summon[ComponentMeta[Dimension]])
    assertEquals(summon[ComponentMeta[Heading]], summon[ComponentMeta[Heading]])
  }
