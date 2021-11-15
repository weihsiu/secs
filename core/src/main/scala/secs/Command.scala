package secs

trait Command:
  def spawnEntity(): EntityCommand
  def entity(entity: Entity): EntityCommand
  def despawnEntity(entity: Entity): Command

trait EntityCommand:
  def entity: Entity
  def insertComponent[C <: Component](component: C)(using CM: ComponentMeta[C]): EntityCommand
  def updateComponent[C <: Component](update: C => C)(using CM: ComponentMeta[C]): EntityCommand
  def removeComponent[C <: Component]()(using CM: ComponentMeta[C]): EntityCommand

object Command:
  given (using W: World): Command with
    private def entityCommand(e: Entity): EntityCommand = new EntityCommand:
      val entity = e

      def insertComponent[C <: Component](component: C)(using CM: ComponentMeta[C]) =
        W.insertComponent(e, component)
        this

      def updateComponent[C <: Component](update: C => C)(using CM: ComponentMeta[C]) =
        W.updateComponent(e, update)
        this

      def removeComponent[C <: Component]()(using CM: ComponentMeta[C]) =
        W.removeComponent[C](e)
        this

    def spawnEntity() =
      val entity = W.spawnEntity()
      entityCommand(entity)

    def entity(entity: Entity) = entityCommand(entity)

    def despawnEntity(entity: Entity) =
      W.despawnEntity(entity)
      this
