package secs

class SystemSuite extends munit.FunSuite:
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

  inline def queryDimensions(using query: Query1[(EntityC, Dimension)]): Unit =
    val ds = query.result
    ds.foreach(println)
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

  test("system 1") {
    initialize
    queryDimensions
    updateDimensions
    queryDimensions
  }
