package secs

import scala.Tuple.*
import scala.collection.immutable
import scala.compiletime.*

enum EntityStatus:
  case Spawned, SpawnedAndAlive, Alive, Despawned
export EntityStatus.*

trait Components:
  def getComponent[C <: Component: ComponentMeta]: Option[C]

trait Secs[SS <: Tuple]:
  type Worldly = World ?=> Unit
  def init(): Worldly
  def tick(time: Double): Worldly
  def beforeRender(): Unit
  def renderEntity(
      entity: Entity,
      status: EntityStatus,
      components: Components,
      previousComponents: => Components
  ): Unit
  def afterRender(): Unit

object Secs:
  class DefaultComponents(cs: immutable.Map[ComponentMeta[Component], Component])
      extends Components:
    val components = cs
    def getComponent[C <: Component: ComponentMeta]: Option[C] =
      cs.get(summon[ComponentMeta[C]]).asInstanceOf[Option[C]]

  type ToOptionComponent[CM] = CM match
    case ComponentMeta[c] => Option[c]

  extension (components: Components)
    inline def getComponents[CS <: Tuple]: Option[CS] =
      Tuples
        .sequenceOptions(
          summonAll[Map[CS, ComponentMeta]]
            .map[ToOptionComponent](
              [cm] =>
                (x: cm) =>
                  components
                    .asInstanceOf[DefaultComponents]
                    .components
                    .get(x.asInstanceOf[ComponentMeta[Component]])
                    .asInstanceOf[ToOptionComponent[cm]]
            )
        )
        .asInstanceOf[Option[CS]]

  inline def renderEntitiesWithStatus[SS <: Tuple](
      secs: Secs[SS],
      status: EntityStatus,
      entities: Set[Entity]
  )(using
      W: World
  ): Unit =
    entities.foreach(e =>
      secs.renderEntity(
        e,
        status,
        DefaultComponents(W.previousComponentsWithin(e)),
        DefaultComponents(W.previous2ComponentsWithin(e))
      )
    )

  inline def renderEntities[SS <: Tuple](secs: Secs[SS])(using W: World): Unit =
    inline erasedValue[SS] match
      case _: (Spawned.type *: rest) =>
        renderEntitiesWithStatus[SS](
          secs,
          Spawned,
          W.allPreviousEntities().diff(W.allPrevious2Entities())
        )
        renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: (SpawnedAndAlive.type *: rest) =>
        renderEntitiesWithStatus[SS](
          secs,
          SpawnedAndAlive,
          W.allPreviousEntities()
        )
        renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: (Alive.type *: rest) =>
        renderEntitiesWithStatus[SS](
          secs,
          Alive,
          W.allPreviousEntities().intersect(W.allPrevious2Entities())
        )
        renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: (Despawned.type *: rest) =>
        renderEntitiesWithStatus[SS](
          secs,
          Despawned,
          W.allPrevious2Entities().diff(W.allPreviousEntities())
        )
        renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: EmptyTuple => ()

  inline def start[SS <: Tuple](secs: Secs[SS], ticker: Option[Double => () => Unit] = None)(using
      W: World
  ): Double => Unit =
    secs.init()
    time =>
      synchronized(W.tick(time))
      val join = ticker match
        case Some(t) => t(time)
        case None =>
          secs.tick(time)
          () => ()
      secs.beforeRender()
      renderEntities[SS](secs)
      secs.afterRender()
      join()

export Secs.*
