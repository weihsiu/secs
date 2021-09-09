package secs

import secs.BoolOps.*

import scala.Tuple.{Filter as TFilter, *}
import scala.compiletime.*

trait Query[CS <: Tuple, OS <: BoolOps]:
  inline def result: List[CS]

type Query1[CS <: Tuple] = Query[CS, ¬[¬[EntityC]]]

object Query:
  inline given [CS <: Tuple, OS <: BoolOps](using W: World): Query[CS, OS] with
    type NotOption[C] <: Boolean = C match
      case Option[?] => false
      case ?         => true

    type NoOptionCM[CM] = CM match
      case ComponentMeta[Option[c]] => ComponentMeta[c]
      case ComponentMeta[c]         => ComponentMeta[c]

    type ToComponent[CM] = CM match
      case ComponentMeta[c] => c

    private def toEntities(cms: Tuple): List[Set[Entity]] =
      cms match
        case (cm: ComponentMeta[? <: Component]) *: cms =>
          W.entitiesWith(using cm) :: toEntities(cms)
        case x *: _ => sys.error(s"invalid type: $x")
        case _      => Nil

    inline def toComponents(entity: Entity, cms: Tuple): Tuple =
      cms.map[ToComponent](
        [cm] =>
          (x: cm) =>
            x match
              case _: ComponentMeta[Option[? <: Component]] =>
                W.componentsWithin(entity)
                  .get(summon[NoOptionCM[cm]].asInstanceOf[ComponentMeta[Component]])
                  .asInstanceOf[ToComponent[cm]]
              case _ =>
                W.componentsWithin(entity)(x.asInstanceOf[ComponentMeta[Component]])
                  .asInstanceOf[ToComponent[cm]]
      )

    private def validEntity(boolOps: BoolOps)(entity: Entity): Boolean =
      val cs = W.componentsWithin(entity)
      def hasComponent(cm: ComponentMeta[?]): Boolean =
        cs.contains(cm.asInstanceOf[ComponentMeta[Nothing]])
      boolOps match
        case ¬(cm: ComponentMeta[?]) => !hasComponent(cm)
        case ¬(os: BoolOps)          => !validEntity(os)(entity)
        case ∧(cm1: ComponentMeta[?], cm2: ComponentMeta[?]) =>
          hasComponent(cm1) && hasComponent(cm2)
        case ∧(cm: ComponentMeta[?], os: BoolOps) => hasComponent(cm) && validEntity(os)(entity)
        case ∧(os: BoolOps, cm: ComponentMeta[?]) => hasComponent(cm) && validEntity(os)(entity)
        case ∧(os1: BoolOps, os2: BoolOps) => validEntity(os1)(entity) && validEntity(os2)(entity)
        case ∨(cm1: ComponentMeta[?], cm2: ComponentMeta[?]) =>
          hasComponent(cm1) || hasComponent(cm2)
        case ∨(cm: ComponentMeta[?], os: BoolOps) => hasComponent(cm) || validEntity(os)(entity)
        case ∨(os: BoolOps, cm: ComponentMeta[?]) => hasComponent(cm) || validEntity(os)(entity)
        case ∨(os1: BoolOps, os2: BoolOps) => validEntity(os1)(entity) || validEntity(os2)(entity)
        case _                             => sys.error("invalid BoolOps")

    inline def result =
      val boolOps = summon[Filter[OS]].boolOps
      val r = toEntities(summonAll[Map[TFilter[CS, NotOption], ComponentMeta]])
      r.reduce(_.intersect(_))
        .toList
        .filter(validEntity(boolOps))
        .map(e => toComponents(e, summonAll[Map[CS, ComponentMeta]]).asInstanceOf[CS])
