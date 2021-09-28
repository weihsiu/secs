package secs

import scala.Tuple.*
import scala.collection.immutable
import scala.compiletime.*

enum EntityStatus:
  case Spawned, Alive, Despawned
export EntityStatus.*

trait Components:
  def getComponent[C <: Component: ComponentMeta]: Option[C]

trait Secs[A <: Tuple]:
  type Worldly = World ?=> Unit
  def init(): Worldly
  def tick(time: Double): Worldly
  def beforeRender(): Unit
  def renderEntity(
      entity: Entity,
      status: EntityStatus,
      components: Components,
      previousComponents: Option[Components]
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

  inline def renderEntities[A <: Tuple](secs: Secs[A])(using W: World): Unit =
    inline erasedValue[A] match
      case _: (Spawned.type *: rest) => renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: (Alive.type *: rest) =>
        W
          .allPreviousEntities()
          .foreach(e =>
            secs.renderEntity(
              e,
              Alive,
              DefaultComponents(W.previousComponentsWithin(e)),
              Some(DefaultComponents(W.previous2ComponentsWithin(e)))
            )
          )
        renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: (Despawned.type *: rest) => renderEntities[rest](secs.asInstanceOf[Secs[rest]])
      case _: EmptyTuple               => ()

  inline def start[A <: Tuple](secs: Secs[A], ticker: Option[Double => () => Unit] = None)(using
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
      renderEntities[A](secs)
      secs.afterRender()
      join()

export Secs.*
