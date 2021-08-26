package secs

import scala.Tuple.*
import scala.compiletime.*

trait Query[CS <: Tuple, FS <: Tuple]:
  inline def result: String

type Query1[CS <: Tuple] = Query[CS, EmptyTuple]

object Query:
  inline given [CS <: Tuple, FS <: Tuple](using world: World): Query[CS, FS]
    with
    private def toEntities(cms: Tuple): List[Set[Entity]] =
      cms match
        case (cm: ComponentMeta[? <: Component]) *: cms =>
          world.entitiesWith(using cm) :: toEntities(cms)
        case x *: _ => sys.error(s"invalid type: $x")
        case _      => Nil
    inline def result =
      toEntities(summonAll[Map[CS, ComponentMeta]]).toString
