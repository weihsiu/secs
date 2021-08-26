package secs

class WorldSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component
  given ComponentMeta[Dimension] with
    val name = "Dimension"

  case class Heading(angle: Double) extends Component
  given ComponentMeta[Heading] with
    val name = "Heading"

  val world = summon[World]

  test("World is singleton") {
    assertEquals(summon[World], summon[World])
  }

  test("spawn and despawn Entities") {
    val entity = world.spawnEntity()
    assertEquals(world.despawnEntity(entity), ())
  }

  test("insertComponent and removeComponent") {
    val entity = world.spawnEntity()
    val dimension = Dimension(10, 20)
    world.insertComponent(entity, dimension)
    assertEquals(world.componentsWithin(entity), Set(dimension))
    assertEquals(world.entitiesWith[Dimension], Set(entity))
    world.removeComponent[Dimension](entity)
    assertEquals(world.componentsWithin(entity), Set.empty)
    assertEquals(world.entitiesWith[Dimension], Set.empty)
    world.despawnEntity(entity)
  }

  test("updateComponent") {
    val entity = world.spawnEntity()
    val dimension = Dimension(10, 20)
    world.insertComponent(entity, dimension)
    world.updateComponent[Dimension](entity, _.copy(width = 20))
    assertEquals(world.componentsWithin(entity), Set(Dimension(20, 20)))
    world.despawnEntity(entity)
  }
