package secs

import scala.collection.immutable.Iterable
import scala.collection.immutable.Queue

trait World:
  def tick(time: Double): Unit
  def allEntities(): Set[Entity]
  def allPreviousEntities(): Set[Entity]
  def allPrevious2Entities(): Set[Entity]
  def entitiesWith[C <: Component](using CM: ComponentMeta[C]): Set[Entity]
  def componentsWithin[C <: Component](entity: Entity): Map[ComponentMeta[C], C]
  def previousComponentsWithin[C <: Component](entity: Entity): Map[ComponentMeta[C], C]
  def previous2ComponentsWithin[C <: Component](entity: Entity): Map[ComponentMeta[C], C]
  def spawnEntity(): Entity
  def despawnEntity(entity: Entity): Unit
  def insertComponent[C <: Component](entity: Entity, component: C)(using
      CM: ComponentMeta[C]
  ): Unit
  def updateComponent[C <: Component](entity: Entity, update: C => C)(using
      CM: ComponentMeta[C]
  ): Unit
  def removeComponent[C <: Component](entity: Entity)(using
      CM: ComponentMeta[C]
  ): Unit
  def sendEvent[E](event: E)(using CM: ComponentMeta[EventSender[E]]): Unit
  def receiveEvents[E](using CM: ComponentMeta[EventSender[E]]): Iterable[E]

given World with
  private var components = Map.empty[ComponentMeta[Component], Set[Entity]]
  private var previousComponents = Map.empty[ComponentMeta[Component], Set[Entity]]
  private var previous2Components = Map.empty[ComponentMeta[Component], Set[Entity]]
  private var entities = Map.empty[Entity, Map[ComponentMeta[Component], Component]]
  private var previousEntities = Map.empty[Entity, Map[ComponentMeta[Component], Component]]
  private var previous2Entities = Map.empty[Entity, Map[ComponentMeta[Component], Component]]
  private var currentEntityId = 0
  private var events = Map.empty[ComponentMeta[EventSender[?]], Queue[?]]

  def tick(time: Double) =
    previous2Components = previousComponents
    previousComponents = components
    previous2Entities = previousEntities
    previousEntities = entities
    events = Map.empty

  def allEntities() = entities.keySet

  def allPreviousEntities() = previousEntities.keySet

  def allPrevious2Entities() = previous2Entities.keySet

  def entitiesWith[C <: Component](using CM: ComponentMeta[C]) =
    components.get(CM).getOrElse(Set.empty)

  def componentsWithin[C <: Component](entity: Entity) =
    entities
      .get(entity)
      .getOrElse(Map.empty)
      .asInstanceOf[Map[ComponentMeta[C], C]]

  def previousComponentsWithin[C <: Component](entity: Entity) =
    previousEntities
      .get(entity)
      .getOrElse(Map.empty)
      .asInstanceOf[Map[ComponentMeta[C], C]]

  def previous2ComponentsWithin[C <: Component](entity: Entity) =
    previous2Entities
      .get(entity)
      .getOrElse(Map.empty)
      .asInstanceOf[Map[ComponentMeta[C], C]]

  def spawnEntity() =
    val entity = Entity(currentEntityId)
    entities += entity -> Map.empty
    currentEntityId += 1
    insertComponent(entity, EntityC(entity))
    entity

  def despawnEntity(entity: Entity) =
    entities
      .get(entity)
      .foreach(
        _.keys.foreach(cm =>
          components = components.updatedWith(cm)(_.flatMap(es =>
            val es1 = es - entity
            Option.when(es1.nonEmpty)(es1)
          ))
        )
      )
    entities -= entity

  def insertComponent[C <: Component](entity: Entity, component: C)(using
      CM: ComponentMeta[C]
  ) =
    entities
      .get(entity)
      .foreach(cs =>
        entities += entity -> (cs + (CM -> component))
        components = components.updatedWith(CM)(
          _.fold(Some(Set(entity)))(es => Some(es + entity))
        )
      )

  def updateComponent[C <: Component](entity: Entity, update: C => C)(using
      CM: ComponentMeta[C]
  ) =
    entities
      .get(entity)
      .foreach(cs =>
        entities += entity -> cs.updatedWith(CM)(
          _.map(update.asInstanceOf[Component => Component])
        )
      )

  def removeComponent[C <: Component](
      entity: Entity
  )(using CM: ComponentMeta[C]) =
    entities
      .get(entity)
      .foreach(cs =>
        entities += entity -> (cs - CM)
        components = components.updatedWith(CM)(_.flatMap(es =>
          val es1 = es - entity
          Option.when(es1.nonEmpty)(es1)
        ))
      )

  def sendEvent[E](event: E)(using CM: ComponentMeta[EventSender[E]]) =
    events = events.updatedWith(CM)(
      _.fold(Some(Queue(event)))(es => Some(es.enqueue(event)))
    )

  def receiveEvents[E](using CM: ComponentMeta[EventSender[E]]) =
    events.get(CM).fold(Iterable.empty)(_.asInstanceOf[Queue[E]].toIterable)
