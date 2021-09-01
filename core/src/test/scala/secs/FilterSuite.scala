package secs

import scala.compiletime.*

class FilterSuite extends munit.FunSuite:
  enum BoolOps:
    case ¬[A <: BoolOps | Component](x: A | ComponentMeta[?])
    case ∧[A <: BoolOps | Component, B <: BoolOps | Component](
        x: A | ComponentMeta[?],
        y: B | ComponentMeta[?]
    )
    case ∨[A <: BoolOps | Component, B <: BoolOps | Component](
        x: A | ComponentMeta[?],
        y: B | ComponentMeta[?]
    )
  import BoolOps.*

  trait Filter[OS <: BoolOps]:
    inline def boolOps: OS

  inline given [OS <: BoolOps]: Filter[OS] with
    type NotInner[OS] = OS match
      case ¬[x] => x
    type AndFirstInner[OS] = OS match
      case ∧[x, ?] => x
    type AndSecondInner[OS] = OS match
      case ∧[?, x] => x
    type OrFirstInner[OS] = OS match
      case ∨[x, ?] => x
    type OrSecondInner[OS] = OS match
      case ∨[?, x] => x
    inline def toBoolOps[OS]: BoolOps =
      inline erasedValue[OS] match
        case _: ¬[? <: Component] =>
          ¬(summonInline[ComponentMeta[NotInner[OS]]])
        case _: ¬[x] => ¬(toBoolOps[x])
        case _: ∧[? <: Component, ? <: Component] =>
          ∧(
            summonInline[ComponentMeta[AndFirstInner[OS]]],
            summonInline[ComponentMeta[AndSecondInner[OS]]]
          )
        case _: ∧[? <: Component, x] =>
          ∧(summonInline[ComponentMeta[AndFirstInner[OS]]], toBoolOps[x])
        case _: ∧[x, ? <: Component] =>
          ∧(toBoolOps[x], summonInline[ComponentMeta[AndSecondInner[OS]]])
        case _: ∧[x, y] => ∧(toBoolOps[x], toBoolOps[y])
        case _: ∨[? <: Component, ? <: Component] =>
          ∨(
            summonInline[ComponentMeta[OrFirstInner[OS]]],
            summonInline[ComponentMeta[OrSecondInner[OS]]]
          )
        case _: ∨[? <: Component, x] =>
          ∨(summonInline[ComponentMeta[OrFirstInner[OS]]], toBoolOps[x])
        case _: ∨[x, ? <: Component] =>
          ∨(toBoolOps[x], summonInline[ComponentMeta[OrSecondInner[OS]]])
        case _: ∨[x, y] => ∨(toBoolOps[x], toBoolOps[y])
    inline def boolOps = toBoolOps[OS].asInstanceOf[OS]

  inline def filter[OS <: BoolOps](using F: Filter[OS]): Unit =
    println(F.boolOps)

  filter[¬[Heading ∧ Dimension ∨ ¬[Rotation]]]
