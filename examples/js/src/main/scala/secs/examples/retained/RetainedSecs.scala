package secs.examples.retained

import org.scalajs.dom.*
import secs.{*, given}

class RetainedSecs(renderer: Renderer)
    extends Secs[(Spawned.type, AliveAndChanged.type, Despawned.type)]:
  inline def setup(using C: Command): Unit =
    for x <- 0 to 9 do
      for y <- 0 to 9 do
        for z <- 0 to 9 do
          C.spawnEntity()
            .insertComponent(Cube(1))
            .insertComponent(Transform(1, (0, 0, 0), (-10 + 2 * x, -10 + 2 * y, -10 + 2 * z)))
            .insertComponent(TransformDelta(0.0, (0.01, 0.01, 0), (0, 0, 0)))

  inline def transform(using C: Command, Q: Query1[(EntityC, Transform, TransformDelta)]): Unit =
    Q.result.foreach((e, t, td) =>
      C.entity(e.entity)
        .updateComponent[Transform](t =>
          Transform(
            scale = t.scale + td.scale,
            rotation = t.rotation + td.rotation,
            position = t.position + td.position
          )
        )
    )
  def init() =
    setup

  def tick(time: Double) =
    transform

  def beforeRender() = ()

  def renderEntity(
      entity: Entity,
      status: EntityStatus,
      components: Components,
      previousComponents: => Components
  ) = status match
    case Spawned =>
      components
        .getComponents[(Cube, Transform)]
        .foreach((c, t) => renderer.addCube(entity, c, t))
    case AliveAndChanged =>
      components
        .getComponents[(Cube, Transform)]
        .foreach((c, t) => renderer.updateCube(entity, c, t))
    case Despawned =>
      previousComponents.getComponent[Cube].foreach(_ => renderer.removeCube(entity))

  def afterRender() = ()
