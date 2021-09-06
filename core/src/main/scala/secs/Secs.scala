package secs

import scala.Tuple.*
import scala.collection.immutable
import scala.compiletime.*

trait Secs:
  type ToOptionComponent[CM] = CM match
    case ComponentMeta[c] => Option[c]

  extension (components: immutable.Map[ComponentMeta[Component], Component])
    def getC[C <: Component: ComponentMeta]: Option[C] =
      components.get(summon[ComponentMeta[C]]).asInstanceOf[Option[C]]

    inline def getCs[CS <: Tuple]: Option[CS] =
      Tuples
        .sequenceOptions(
          summonAll[Map[CS, ComponentMeta]]
            .map[ToOptionComponent](
              [cm] =>
                (x: cm) =>
                  components
                    .get(x.asInstanceOf[ComponentMeta[Component]])
                    .asInstanceOf[ToOptionComponent[cm]]
            )
        )
        .asInstanceOf[Option[CS]]

  type Worldly = World ?=> Unit
  def init(): Worldly
  def tick(): Worldly
  def beforeRender(): Unit
  def renderEntity(
      entity: Entity,
      components: immutable.Map[ComponentMeta[Component], Component]
  ): Unit
  def afterRender(): Unit

object Secs:
  def start(secs: Secs)(using world: World): () => Unit =
    secs.init()
    () =>
      world.tick()
      secs.tick()
      secs.beforeRender()
      world.allEntities().foreach(e => secs.renderEntity(e, world.componentsWithin(e)))
      secs.afterRender()
