package secs

class CommandSuite extends munit.FunSuite:

  case class Dimension(width: Double, height: Double) extends Component
  given ComponentMeta[Dimension] with
    val name = "Dimension"

  case class Heading(angle: Double) extends Component
  given ComponentMeta[Heading] with
    val name = "Heading"

  val command = summon[Command]

  test("spawnEntity and despawnEntity") {
    val entityCommand = command.spawnEntity()
    command.despawnEntity(entityCommand.entity)
  }

  test("insertComponent and removeComponent") {
    val entity = command.spawnEntity().insertComponent(Dimension(10, 20)).insertComponent(Heading(10)).entity
    command.despawnEntity(entity)
  }