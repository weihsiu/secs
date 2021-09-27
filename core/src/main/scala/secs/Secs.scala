package secs

import scala.Tuple.*
import scala.collection.immutable
import scala.compiletime.*

enum EntityStatus:
  case Spawned, Alive, Despawned

case class EntityStatuses(statuses: Set[EntityStatus])

object EntityStatuses:
  implicit val fromStatus: Conversion[EntityStatus, EntityStatuses] = s =>
    EntityStatuses(
      Set(s)
    )
  extension (statuses: EntityStatuses)
    def +(status: EntityStatus): EntityStatuses = EntityStatuses(statuses.statuses + status)

trait Components:
  def getComponent[C <: Component: ComponentMeta]: Option[C]

trait Secs:
  type Worldly = World ?=> Unit
  def init(): Worldly
  def tick(time: Double): Worldly
  def beforeRender(): Unit
  def renderEntity(entity: Entity, components: Components): Unit
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

  def start(secs: Secs, ticker: Option[Double => () => Unit] = None)(using
      world: World
  ): Double => Unit =
    secs.init()
    time =>
      synchronized(world.tick(time))
      val join = ticker match
        case Some(t) => t(time)
        case None =>
          secs.tick(time)
          () => ()
      secs.beforeRender()
      world
        .allPreviousEntities()
        .foreach(e => secs.renderEntity(e, DefaultComponents(world.previousComponentsWithin(e))))
      secs.afterRender()
      join()

export Secs.*
