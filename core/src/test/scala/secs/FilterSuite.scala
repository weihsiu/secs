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
      case ¬[? <: Component] => ???
      case ¬[? <: BoolOps] => ???
    inline def toBoolOps[OS <: BoolOps]: BoolOps =
      inline erasedValue[OS] match
        case _: ¬[? <: Component] => ¬(summonInline[ComponentMeta[NotInner[OS]]])
        case _: ¬[? <: BoolOps] => toBoolOps[NotInner[OS]]
    inline def boolOps = toBoolOps[OS].asInstanceOf[OS]

  inline def filter[OS <: BoolOps](using F: Filter[OS]): Unit =
    println(F.boolOps)

  filter[¬[Heading]]
