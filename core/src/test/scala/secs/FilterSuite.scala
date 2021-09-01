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

  given [A <: Component: ComponentMeta]: ¬[A] =
    ¬[A](summon[ComponentMeta[A]])

  given [A <: Component: ComponentMeta, B <: Component: ComponentMeta]
      : ∧[A, B] =
    ∧[A, B](summon[ComponentMeta[A]], summon[ComponentMeta[B]])

  given [A <: Component: ComponentMeta, B <: Component: ComponentMeta]
      : ∨[A, B] =
    ∨[A, B](summon[ComponentMeta[A]], summon[ComponentMeta[B]])

  trait Filter[OS <: BoolOps]:
    inline def boolOps: OS

  inline given [OS <: BoolOps]: Filter[OS] with
    inline def toBoolOps[OS <: BoolOps]: BoolOps =
      inline erasedValue[OS] match
        case _: ¬[Component] => ¬(summon[ComponentMeta[]])
    inline def boolOps = ???

  inline def filter[OS <: BoolOps](using F: Filter[OS]): Unit = ???

  filter[¬[Dimension ∧ (Heading ∨ ¬[Rotation])]]
