package secs

import scala.Tuple.*
import scala.compiletime.*

trait Query[CS <: Tuple, FS <: Tuple]:
  inline def result: List[List[Component]]

type Query1[CS <: Tuple] = Query[CS, EmptyTuple]

object Query:
  inline given [CS <: Tuple, FS <: Tuple](using world: World): Query[CS, FS]
    with
    type ToComponent[CM[_]] = CM match
      case ComponentMeta[c] => c
    private def toEntities(cms: Tuple): List[Set[Entity]] =
      cms match
        case (cm: ComponentMeta[? <: Component]) *: cms =>
          world.entitiesWith(using cm) :: toEntities(cms)
        case x *: _ => sys.error(s"invalid type: $x")
        case _      => Nil
    // private def toComponents(entity: Entity, cms: Tuple): Tuple =
    //   cms.map[ToComponent]([cm] => (x: cm) => world.componentsWithin(entity)(x.asInstanceOf[ComponentMeta[Component]]))
    private def toComponents(entity: Entity, cms: Tuple): List[Component] =
      cms match
        case (cm: ComponentMeta[? <: Component]) *: cms =>
          world.componentsWithin(entity)(cm) :: toComponents(entity, cms)
        case x *: _ => sys.error(s"invalid type: $x")
        case _      => Nil
    inline def result =
      val r = toEntities(summonAll[Map[CS, ComponentMeta]])
      r.reduce(_.intersect(_)).toList.map(e => toComponents(e, summonAll[Map[CS, ComponentMeta]]))