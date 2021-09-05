package secs

import scala.Tuple.*
import scala.compiletime.*

type TupleOption[OT <: Tuple] <: Option[Tuple] = OT match
  case Some[x] *: rest => Some[x *: TupleOption[rest]]
  case None.type *: ?  => None.type
def tupleOption[OT <: Tuple](ot: OT): Option[OT] = ot match
  case Some(x) *: rest => Some(x *: tupleOption(rest))
  case None *: ?       => None

trait Secs:
  extension (components: Map[ComponentMeta[Component], Component])
    def getC[C <: Component: ComponentMeta]: Option[C] =
      components.get(summon[ComponentMeta[C]]).asInstanceOf[Option[C]]
    inline def getCs[CS <: Tuple]: Option[CS] =
      val ocs = summonAll[Map[CS, ComponentMeta]].map[Option]([cm] => (x: cm) => components.get(x))

  type Worldly = World ?=> Unit
  def init(): Worldly
  def tick(): Worldly
  def beforeRender(): Unit
  def renderEntity(entity: Entity, components: Map[ComponentMeta[Component], Component]): Unit
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
