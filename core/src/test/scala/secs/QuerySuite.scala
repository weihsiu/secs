package secs

class QuerySuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component
  given ComponentMeta[Dimension] with
    val name = "Dimension"

  case class Heading(angle: Double) extends Component
  given ComponentMeta[Heading] with
    val name = "Heading"

  val world = summon[World]

  test("Query1") {
    inline def system(using Q: Query1[(EntityC, Dimension, Heading)]): Unit =
      assertEquals(Q.result.length, 1)
      Q.result.foreach { (_, dimension, heading) =>
        assertEquals(dimension, Dimension(10, 20))
        assertEquals(heading, Heading(123))
      }
    val entity1 = world.spawnEntity()
    val entity2 = world.spawnEntity()
    val dimension = Dimension(10, 20)
    val heading = Heading(123)
    world.insertComponent(entity1, dimension)
    world.insertComponent(entity1, heading)
    world.insertComponent(entity2, dimension)
    system
    world.despawnEntity(entity1)
    world.despawnEntity(entity2)
  }
