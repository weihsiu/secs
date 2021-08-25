package secs

trait World:
  def entitiesWith[C <: Component: ComponentMeta]: Set[Entity]
  def componentsWithin(entity: Entity): Set[Component]
  def spawnEntity(): Entity
  def despawnEntity(entity: Entity)(using mapper: ComponentMetaMapper): Unit
  def insertComponent[C <: Component: ComponentMeta](
      entity: Entity,
      component: C
  ): Unit
  def updateComponent[C <: Component: ComponentMeta](
      entity: Entity,
      update: C => C
  ): Unit
  def removeComponent[C <: Component: ComponentMeta](
      entity: Entity,
      component: C
  ): Unit

given World with
  private var components = Map.empty[String, Set[Entity]]
  private var entities = Map.empty[Entity, Set[Component]]
  private var currentEntityId = 0
  def entitiesWith[C <: Component: ComponentMeta] =
    components.get(summon[ComponentMeta[C]].name).getOrElse(Set.empty)
  def componentsWithin(entity: Entity) =
    entities.get(entity).getOrElse(Set.empty)
  def spawnEntity() =
    val entity = Entity(currentEntityId)
    entities += entity -> Set.empty
    currentEntityId += 1
    entity
  def despawnEntity(entity: Entity)(using mapper: ComponentMetaMapper) =
    entities
      .get(entity)
      .foreach(_.foreach { c =>
        removeComponent(entity, c)(mapper.map(c))
      })
    entities -= entity
  def insertComponent[C <: Component: ComponentMeta](
      entity: Entity,
      component: C
  ) =
    entities = entities.updatedWith(entity)(_.map(_ + component))
    components =
      components.updatedWith(summon[ComponentMeta[C]].name)(_.map(_ + entity))
  def updateComponent[C <: Component: ComponentMeta](
      entity: Entity,
      update: C => C
  ) = ???
  def removeComponent[C <: Component: ComponentMeta](
      entity: Entity,
      component: C
  ) = ???
