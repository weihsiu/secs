package secs

class QuerySuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component
  given ComponentMeta[Dimension] with {}

  case class Heading(angle: Double) extends Component
  given ComponentMeta[Heading] with {}

  case class Rotation(angle: Double) extends Component
  given ComponentMeta[Rotation] with {}

  val world = summon[World]

  test("query 1") {
    inline def system(using query: Query1[(EntityC, Dimension, Heading)]): Unit =
      assertEquals(query.result.length, 2)
      query.result.foreach { (_, dimension, heading) =>
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
    world.insertComponent(entity2, heading)
    system
    world.despawnEntity(entity1)
    world.despawnEntity(entity2)
  }

  test("query 2") {
    import BoolOps.*
    inline def system1(using query: Query[(EntityC, Dimension, Heading), ¬[Rotation]]): Unit =
      assertEquals(query.result.length, 1)
      query.result.foreach { (_, dimension, heading) =>
        assertEquals(dimension, Dimension(10, 20))
        assertEquals(heading, Heading(123))
      }
    inline def system2(using query: Query[(EntityC, Dimension, Heading), ¬[¬[Rotation]]]): Unit =
      assertEquals(query.result.length, 1)
      query.result.foreach { (_, dimension, heading) =>
        assertEquals(dimension, Dimension(10, 20))
        assertEquals(heading, Heading(123))
      }
    val entity1 = world.spawnEntity()
    val entity2 = world.spawnEntity()
    val dimension = Dimension(10, 20)
    val heading = Heading(123)
    val rotation = Rotation(321)
    world.insertComponent(entity1, dimension)
    world.insertComponent(entity1, heading)
    world.insertComponent(entity2, dimension)
    world.insertComponent(entity2, heading)
    world.insertComponent(entity2, rotation)
    system1
    system2
    world.despawnEntity(entity1)
    world.despawnEntity(entity2)
  }

  test("query 3") {
    import BoolOps.*
    inline def system(using query: Query[EntityC *: EmptyTuple, Dimension ∨ Rotation]): Unit =
      assertEquals(query.result.length, 2)
    val entity1 = world.spawnEntity()
    val entity2 = world.spawnEntity()
    val dimension = Dimension(10, 20)
    val heading = Heading(123)
    val rotation = Rotation(321)
    world.insertComponent(entity1, dimension)
    world.insertComponent(entity1, heading)
    world.insertComponent(entity2, dimension)
    world.insertComponent(entity2, heading)
    world.insertComponent(entity2, rotation)
    system
    world.despawnEntity(entity1)
    world.despawnEntity(entity2)
  }