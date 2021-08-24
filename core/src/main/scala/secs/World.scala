package secs

trait World:
  def entitiesWithComponent[C <: Component]: Set[Entity]
  def componentsInEntity(entity: Entity): Set[Component]
  def spawnEntity(): Entity
  def despawnEntity(entity: Entity): Unit
  def insertComponent(entity: Entity, component: Component): Unit
  def updateComponent(entity: Entity, update: Component => Component): Unit
  def removeComponent(entity: Entity, component: Component): Unit
