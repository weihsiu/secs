package secs

trait Command:
  def spawnEntity(): EntityCommand
  def entity(entity: Entity): EntityCommand
  def despawnEntity(entity: Entity): Unit

trait EntityCommand:
  def entity: Entity
  def insertComponent[C <: Component](component: C)(using CM: ComponentMeta[C]): EntityCommand
  def updateComponent[C <: Component](update: C => C)(using CM: ComponentMeta[C]): EntityCommand
  def removeComponent[C <: Component]()(using CM: ComponentMeta[C]): EntityCommand

object Command:
  given (using world: World): Command with
    private def entityCommand(e: Entity): EntityCommand = new EntityCommand:
      val entity = e
      def insertComponent[C <: Component](component: C)(using CM: ComponentMeta[C]) =
        world.insertComponent(e, component)
        this
      def updateComponent[C <: Component](update: C => C)(using CM: ComponentMeta[C]) =
        world.updateComponent(e, update)
        this
      def removeComponent[C <: Component]()(using CM: ComponentMeta[C]) =
        world.removeComponent[C](e)
        this
    def spawnEntity() =
      val entity = world.spawnEntity()
      entityCommand(entity)
    def entity(entity: Entity) = entityCommand(entity)
    def despawnEntity(entity: Entity) = world.despawnEntity(entity)
