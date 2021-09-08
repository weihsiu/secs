package secs

class MacrosSuite extends munit.FunSuite:
  test("isCaseClass") {
    case class CC()
    trait T
    class C
    assert(isCaseClass[CC])
    assert(!isCaseClass[T])
    assert(!isCaseClass[C])
  }

  test("companionObject") {
    case class CC()
    println(companionObject[CC])
  }
