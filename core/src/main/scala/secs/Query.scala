package secs

trait Query[CS <: Tuple, FS <: Tuple] extends Iterable[CS]

trait Query1[CS <: Tuple] extends Query[CS, EmptyTuple]

object Query:
  given [CS <: Tuple, FS <: Tuple](using world: World): Query[CS, FS] with
    def iterator: Iterator[CS] = ???