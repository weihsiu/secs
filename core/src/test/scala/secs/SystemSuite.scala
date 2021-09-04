package secs

class SystemSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component derives ComponentMeta

  case class Heading(angle: Double) extends Component derives ComponentMeta

  case class Rotation(angle: Double) extends Component derives ComponentMeta

  inline def initialize(using command: Command): Unit =
    command
      .spawnEntity()
      .insertComponent(Dimension(10, 20))
      .insertComponent(Heading(10))
    command
      .spawnEntity()
      .insertComponent(Dimension(20, 30))
      .insertComponent(Heading(20))
    command.spawnEntity().insertComponent(Dimension(30, 40))
    command.spawnEntity().insertComponent(Heading(30))

  inline def queryDimensions(dimensions: Set[Dimension])(using
      query: Query1[Dimension *: EmptyTuple]
  ): Unit =
    val ds = query.result
    ds.foreach(d => assert(dimensions(d.head)))
    assertEquals(ds.length, 3)

  inline def updateDimensions(using
      command: Command,
      query: Query1[(EntityC, Dimension)]
  ): Unit =
    val css = query.result
    css.foreach((entityC, dimension) =>
      command
        .entity(entityC.entity)
        .updateComponent[Dimension](d => d.copy(width = d.width * 2))
    )

  inline def queryHeadings(heading: Set[Heading])(using query: Query1[(EntityC, Heading)]): Unit =
    ???

  test("system 1") {
    initialize
    queryDimensions(Set(Dimension(10, 20), Dimension(20, 30), Dimension(30, 40)))
    updateDimensions
    queryDimensions(Set(Dimension(20, 20), Dimension(40, 30), Dimension(60, 40)))
  }

  test("system 2") {
    initialize

  }
