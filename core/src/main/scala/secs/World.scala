package secs

trait World:

  def components(name: String): Iterable[Component]
