package secs.examples.retained

import secs.{*, given}

class RetainedSecs extends Secs[(Spawned.type, Alive.type, Despawned.type)]:

  def init() = ()
  def tick(time: Double) = ()
  def beforeRender() = ()
  def renderEntity(
      entity: Entity,
      status: EntityStatus,
      components: Components,
      previousComponents: => Components
  ) = ???
  def afterRender() = ???
