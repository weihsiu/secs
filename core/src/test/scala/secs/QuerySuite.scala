package secs

class QuerySuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component derives ComponentMeta

  case class Heading(angle: Double) extends Component derives ComponentMeta

  case class Rotation(angle: Double) extends Component derives ComponentMeta

  val world = summon[World]

  test("query 1") {
    inline def system(using
        query: Query1[(EntityC, Dimension, Option[Heading], Option[Rotation])]
    ): Unit =
      assertEquals(query.result.length, 2)
      query.result.foreach { (_, dimension, heading, rotation) =>
        assertEquals(dimension, Dimension(10, 20))
        assertEquals(heading, Some(Heading(123)))
        assertEquals(rotation, None)
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

  test("query 4") {
    import BoolOps.*
    import Decorator.*
    inline def system(using
        query: Query[EntityC *: EmptyTuple, Dimension ∧ Added[Rotation]]
    ): Unit =
      assertEquals(query.result.length, 1)
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
