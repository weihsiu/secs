package secs

import scala.Tuple.*
import scala.collection.immutable
import scala.compiletime.*

sealed trait EntityStatus
trait Spawned extends EntityStatus
trait Alive extends EntityStatus
trait Despawned extends EntityStatus

trait Components:
  def getComponent[C <: Component: ComponentMeta]: Option[C]

trait Secs[A <: Tuple]:
  type Worldly = World ?=> Unit
  def init(): Worldly
  def tick(time: Double): Worldly
  def beforeRender(): Unit
  def renderEntity(entity: Entity, status: EntityStatus, components: Components, previousComponents: Option[Components]): Unit
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

  inline def start[A <: Tuple](secs: Secs[A], ticker: Option[Double => () => Unit] = None)(using
      world: World
  ): Double => Unit =
    inline def renderEntities[A <: Tuple]: Unit =
      inline erasedValue[A] match
        case _: Spawned *: rest
    secs.init()
    time =>
      synchronized(world.tick(time))
      val join = ticker match
        case Some(t) => t(time)
        case None =>
          secs.tick(time)
          () => ()
      secs.beforeRender()
      inline erasedValue[A] match
        case _
      world
        .allPreviousEntities()
        .foreach(e => secs.renderEntity(e, DefaultComponents(world.previousComponentsWithin(e))))
      secs.afterRender()
      join()

export Secs.*
