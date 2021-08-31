package secs

class FilterSuite extends munit.FunSuite:
  enum BoolOps:
    case ¬[A <: BoolOps | Component]()
    case ∧[A <: BoolOps | Component, B <: BoolOps | Component]()
    case ∨[A <: BoolOps | Component, B <: BoolOps | Component]()
  import BoolOps.*

  ¬[Dimension ∧ (Heading ∨ ¬[Rotation])]()

  trait Filter[OS <: BoolOps]:
    def boolOps: OS

  given [OS <: BoolOps]: Filter[OS] with
    def boolOps = ???
