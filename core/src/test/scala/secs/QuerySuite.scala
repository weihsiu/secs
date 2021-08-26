package secs

class QuerySuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component
  given ComponentMeta[Dimension] with
    val name = "Dimension"

  case class Heading(angle: Double) extends Component
  given ComponentMeta[Heading] with
    val name = "Heading"

  val world = summon[World]

  test("testing") {
    inline def system(using Q: Query1[(Dimension, Heading)]): Unit =
      println(Q.result)
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
