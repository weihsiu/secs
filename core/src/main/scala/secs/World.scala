package secs

trait World:
  def entitiesWithComponent(componentName: String): Iterable[Entity]
  def componentsInEntity(entity: Entity): Iterable[Component]
