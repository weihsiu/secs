package secs

import scala.compiletime.*

class TupleSuite extends munit.FunSuite:
  test("extractSome with Somes") {
    val t1 = (Some(1), Some('a'), Some(true))
    val t2: (Int, Char, Boolean) = t1.extractSome
    assertEquals(t2, (1, 'a', true))
    assertEquals(t1.size, t2.size)
  }

  test("extractSome with Somes and None") {
    val t1 = (Some(1), Some('a'), None)
    val t2: (Int, Char) = t1.extractSome
    assertEquals(t2, (1, 'a'))
    assertNotEquals(t1.size, t2.size)
  }

  test("sequenceOptions with Somes") {
    val t1 = (Some(1), Some('a'), Some(true))
    val t2: Option[(Int, Char, Boolean)] = t1.sequenceOptions
    val Some((i, c, b)) = t1.sequenceOptions
    assertEquals(t2, Some((1, 'a', true)))
    assertEquals(i, 1)
    assertEquals(c, 'a')
    assertEquals(b, true)
  }

  test("sequenceOptions with Somes and None") {
    val t1 = (Some(1), Some('a'), None)
    val t2: Option[(Int, Char)] = t1.sequenceOptions
    assertEquals(t2, None)
  }
