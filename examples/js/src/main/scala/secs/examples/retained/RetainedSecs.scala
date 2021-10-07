package secs.examples.retained

import org.scalajs.dom.*
import secs.{*, given}

class RetainedSecs(renderer: Renderer) extends Secs[(Spawned.type, Alive.type, Despawned.type)]:
  inline def setup(using C: Command): Unit =
    C.spawnEntity().insertComponent(Cube(10, (0, 0, -5), (0, 0, 0)))

  inline def rotateCube(using C: Command, Q: Query1[(EntityC, Cube)]): Unit =
    Q.result.foreach((e, c) =>
      C.entity(e.entity)
        .updateComponent[Cube](
          _.copy(rotation = (c.rotation._1 + 0.01, c.rotation._2 + 0.01, c.rotation._3))
        )
    )
  def init() =
    setup

  def tick(time: Double) =
    rotateCube

  def beforeRender() = ()

  def renderEntity(
      entity: Entity,
      status: EntityStatus,
      components: Components,
      previousComponents: => Components
  ) = status match
    case Spawned =>
      components.getComponent[Cube].foreach(renderer.addCube(entity, _))
    case Alive =>
      components.getComponent[Cube].foreach(renderer.updateCube(entity, _))
    case Despawned =>
      previousComponents.getComponent[Cube].foreach(_ => renderer.removeCube(entity))

  def afterRender() = ()
