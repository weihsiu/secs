package secs

import secs.BoolOps.*
import secs.Decorator.*

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

    // private def toEntities(cms: Tuple): List[Set[Entity]] =
    //   cms match
    //     case (cm: ComponentMeta[? <: Component]) *: cms =>
    //       W.entitiesWith(using cm) :: toEntities(cms)
    //     case x *: _ => sys.error(s"invalid type: $x")
    //     case _      => Nil

    inline def toEntities[CS <: Tuple]: List[Set[Entity]] =
      inline erasedValue[CS] match
        case _: (c *: cs) =>
          W.entitiesWith(using
            summonInline[ComponentMeta[c]].asInstanceOf[ComponentMeta[Component]]
          ) :: toEntities[cs]
        case _ => Nil

    // type ToComponent[CM] = CM match
    //   case ComponentMeta[c] => c
    // inline def toComponents(entity: Entity, cms: Tuple): Tuple =
    //   cms.map[ToComponent](
    //     [cm] =>
    //       (x: cm) =>
    //         W.componentsWithin(entity)(x.asInstanceOf[ComponentMeta[Component]])
    //           .asInstanceOf[ToComponent[cm]]
    //   )

    // type ToComponentMeta[C] = C match
    //   case Option[c] => ComponentMeta[c]
    //   case ?         => ComponentMeta[C]
    // inline def toComponents2[CS <: Tuple](entity: Entity, cms: Tuple): Tuple =
    //   inline (cms, erasedValue[CS]) match
    //     case (cm *: cms, _: (Option[?] *: cs)) =>
    //       W.componentsWithin(entity)
    //         .get(cm.asInstanceOf[ComponentMeta[Component]])
    //         .asInstanceOf[Option[Component]] *: toComponents2[cs](entity, cms)
    //     case (cm *: cms, _: (? *: cs)) =>
    //       W.componentsWithin(entity)(cm.asInstanceOf[ComponentMeta[Component]])
    //         .asInstanceOf[Component] *: toComponents2[cs](entity, cms)
    //     case (EmptyTuple, _: EmptyTuple.type) => EmptyTuple

    inline def toComponents[CS <: Tuple](entity: Entity): Tuple =
      inline erasedValue[CS] match
        case _: (Option[c] *: cs) =>
          W.componentsWithin(entity)
            .get(summonInline[ComponentMeta[c]].asInstanceOf[ComponentMeta[Component]])
            .asInstanceOf[Option[Component]] *: toComponents[cs](entity)
        case _: (c *: cs) =>
          W.componentsWithin(entity)(
            summonInline[ComponentMeta[c]].asInstanceOf[ComponentMeta[Component]]
          ).asInstanceOf[Component] *: toComponents[cs](entity)
        case _ => EmptyTuple

    private def validEntity(boolOps: BoolOps)(entity: Entity): Boolean =
      val cs = W.componentsWithin(entity)
      lazy val prevCs = W.previousComponentsWithin(entity)
      def hasComponent(cm: ComponentMeta[?]): Boolean =
        cs.contains(cm.asInstanceOf[ComponentMeta[Nothing]])
      def isAddedComponent(cm: ComponentMeta[?]): Boolean =
        !prevCs.contains(cm.asInstanceOf[ComponentMeta[Nothing]]) && hasComponent(cm)
      def isChangedComponent(cm: ComponentMeta[?]): Boolean =
        prevCs
          .get(cm.asInstanceOf[ComponentMeta[Nothing]])
          .fold(false)(prevC =>
            cs.get(cm.asInstanceOf[ComponentMeta[Nothing]]).fold(false)(_ != prevC)
          )
      boolOps match
        case ¬(d: Added[?])                  => !isAddedComponent(d.cm)
        case ¬(d: Changed[?])                => !isChangedComponent(d.cm)
        case ¬(cm: ComponentMeta[?])         => !hasComponent(cm)
        case ¬(os: BoolOps)                  => !validEntity(os)(entity)
        case ∧(d1: Added[?], d2: Added[?])   => isAddedComponent(d1.cm) && isAddedComponent(d2.cm)
        case ∧(d1: Added[?], d2: Changed[?]) => isAddedComponent(d1.cm) && isChangedComponent(d2.cm)
        case ∧(d1: Changed[?], d2: Added[?]) => isChangedComponent(d1.cm) && isAddedComponent(d2.cm)
        case ∧(d1: Changed[?], d2: Changed[?]) =>
          isChangedComponent(d1.cm) && isChangedComponent(d2.cm)
        case ∧(cm: ComponentMeta[?], d: Added[?])   => hasComponent(cm) && isAddedComponent(d.cm)
        case ∧(d: Added[?], cm: ComponentMeta[?])   => isAddedComponent(d.cm) && hasComponent(cm)
        case ∧(cm: ComponentMeta[?], d: Changed[?]) => hasComponent(cm) && isChangedComponent(d.cm)
        case ∧(d: Changed[?], cm: ComponentMeta[?]) => isChangedComponent(d.cm) && hasComponent(cm)

        case ∧(d: Added[?], os: BoolOps)   => isAddedComponent(d.cm) && validEntity(os)(entity)
        case ∧(os: BoolOps, d: Added[?])   => validEntity(os)(entity) && isAddedComponent(d.cm)
        case ∧(d: Changed[?], os: BoolOps) => isChangedComponent(d.cm) && validEntity(os)(entity)
        case ∧(os: BoolOps, d: Changed[?]) => validEntity(os)(entity) && isChangedComponent(d.cm)
        case ∧(cm1: ComponentMeta[?], cm2: ComponentMeta[?]) =>
          hasComponent(cm1) && hasComponent(cm2)
        case ∧(cm: ComponentMeta[?], os: BoolOps) => hasComponent(cm) && validEntity(os)(entity)
        case ∧(os: BoolOps, cm: ComponentMeta[?]) => validEntity(os)(entity) && hasComponent(cm)
        case ∧(os1: BoolOps, os2: BoolOps)   => validEntity(os1)(entity) && validEntity(os2)(entity)
        case ∨(d1: Added[?], d2: Added[?])   => isAddedComponent(d1.cm) || isAddedComponent(d2.cm)
        case ∨(d1: Added[?], d2: Changed[?]) => isAddedComponent(d1.cm) || isChangedComponent(d2.cm)
        case ∨(d1: Changed[?], d2: Added[?]) => isChangedComponent(d1.cm) || isAddedComponent(d2.cm)
        case ∨(d1: Changed[?], d2: Changed[?]) =>
          isChangedComponent(d1.cm) || isChangedComponent(d2.cm)
        case ∨(cm: ComponentMeta[?], d: Added[?])   => hasComponent(cm) || isAddedComponent(d.cm)
        case ∨(d: Added[?], cm: ComponentMeta[?])   => isAddedComponent(d.cm) || hasComponent(cm)
        case ∨(cm: ComponentMeta[?], d: Changed[?]) => hasComponent(cm) || isChangedComponent(d.cm)
        case ∨(d: Changed[?], cm: ComponentMeta[?]) => isChangedComponent(d.cm) || hasComponent(cm)
        case ∨(d: Added[?], os: BoolOps)   => isAddedComponent(d.cm) || validEntity(os)(entity)
        case ∨(os: BoolOps, d: Added[?])   => validEntity(os)(entity) || isAddedComponent(d.cm)
        case ∨(d: Changed[?], os: BoolOps) => isChangedComponent(d.cm) || validEntity(os)(entity)
        case ∨(os: BoolOps, d: Changed[?]) => validEntity(os)(entity) || isChangedComponent(d.cm)
        case ∨(cm1: ComponentMeta[?], cm2: ComponentMeta[?]) =>
          hasComponent(cm1) || hasComponent(cm2)
        case ∨(cm: ComponentMeta[?], os: BoolOps) => hasComponent(cm) || validEntity(os)(entity)
        case ∨(os: BoolOps, cm: ComponentMeta[?]) => validEntity(os)(entity) || hasComponent(cm)
        case ∨(os1: BoolOps, os2: BoolOps) => validEntity(os1)(entity) || validEntity(os2)(entity)
        case _                             => sys.error("invalid BoolOps")

    inline def result =
      val boolOps = summon[Filter[OS]].boolOps
      // val r = toEntities(summonAll[Map[TFilter[CS, NotOption], ComponentMeta]])
      val r = toEntities[TFilter[CS, NotOption]]
      r.reduce(_.intersect(_))
        .toList
        .filter(validEntity(boolOps))
        .map(e => toComponents[CS](e).asInstanceOf[CS])
