package secs

class TupleSuite extends munit.FunSuite:
  type Inner[F] = F match
    case Option[x] => x

  test("construct tuple") {
    assertEquals(1 *: 'a' *: true *: EmptyTuple, (1, 'a', true))
  }

  test("mapping tuples") {
    val t1 = (1, 'a', true)
    val t2 = t1.map[Option]([t] => (x: t) => Some(x))
    val t3 = t2.map[Inner]([t] => (x: t) => x.asInstanceOf[Option[?]].get)
    assertEquals(t2, (Some(1), Some('a'), Some(true)))
    assertEquals(t1, t3)
  }